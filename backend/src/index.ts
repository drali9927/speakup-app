import express from 'express'
import type { Request, Response, NextFunction } from 'express'
import { z } from 'zod'
import { loadConfig } from './config.js'
import { openDb, nowSec, type Db } from './db/index.js'
import {
  CODE_LENGTH,
  discardCode,
  normalizePhone,
  requestCode,
  signToken,
  verifyCode,
  verifyToken,
  ensureUser,
} from './lib/auth.js'
import { XP_PER_ACTIVITY, addXp, seedDemoUsers, standings } from './lib/league.js'
import { createSmsSender } from './lib/sms.js'
import {
  PLANS,
  activeSubscription,
  bazaarVerifier,
  rejectingVerifier,
  redeemPurchase,
  trustingVerifier,
} from './lib/purchase.js'
import { ContentStore } from './lib/content.js'
import {
  overview as adminOverview,
  users as adminUsers,
  grantSubscription,
  revokeSubscriptions,
} from './lib/admin.js'
import { checkIn, getStreak } from './lib/streak.js'
import { productiveAccuracy, pull, push } from './lib/sync.js'
import { todayInAppTz } from './lib/time.js'
import { dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'

/** صفحه پنل ادمین — کنار سورس می‌ماند و در build کپی می‌شود */
const adminPagePath = join(dirname(fileURLToPath(import.meta.url)), 'admin.html')

export function createApp(db: Db, config = loadConfig()) {
  const app = express()

  // لاگ درخواست‌ها — فقط متد، مسیر، کد و زمان. هیچ بدنه‌ای لاگ نمی‌شود
  // چون شماره موبایل و کد تأیید داخل بدنه‌اند.
  if (config.requestLog) {
    app.use((req, res, next) => {
      const started = process.hrtime.bigint()
      res.on('finish', () => {
        const ms = Number(process.hrtime.bigint() - started) / 1e6
        // originalUrl و نه path — داخل روتر سوارشده روی /images، مقدار
        // path پیشوند را ندارد و لاگ گمراه‌کننده می‌شود.
        console.log(`${req.method} ${req.originalUrl} ${res.statusCode} ${ms.toFixed(0)}ms`)
      })
      next()
    })
  }

  app.use(express.json({ limit: '1mb' }))

  const sms = createSmsSender(config.sms)
  const receipts =
    config.bazaar.verifyReceipts
      ? bazaarVerifier({
          packageName: config.bazaar.packageName,
          accessToken: async () => config.bazaar.accessToken,
        })
      : config.env === 'production'
        ? rejectingVerifier
        : trustingVerifier

  // ---------------------------------------------------------- کمکی‌ها

  type AuthedRequest = Request & { userId?: number }

  function requireAuth(req: AuthedRequest, res: Response, next: NextFunction) {
    const header = req.header('authorization') ?? ''
    const token = header.startsWith('Bearer ') ? header.slice(7) : ''
    const payload = token ? verifyToken(config.jwtSecret, token) : null
    if (!payload) {
      res.status(401).json({ error: 'unauthorized' })
      return
    }
    req.userId = payload.sub
    next()
  }

  /** اعتبارسنجی بدنه با zod — خطای یکدست به‌جای کرش */
  function body<T extends z.ZodTypeAny>(schema: T, req: Request, res: Response): z.infer<T> | null {
    const parsed = schema.safeParse(req.body)
    if (!parsed.success) {
      res.status(400).json({ error: 'invalid_body', details: parsed.error.flatten() })
      return null
    }
    return parsed.data
  }

  // ---------------------------------------------------------- سلامت و زمان

  app.get('/health', (_req, res) => {
    res.json({ ok: true, env: config.env })
  })

  /**
   * زمان معتبر سرور.
   * کلاینت برای زنجیره هرگز به ساعت گوشی تکیه نمی‌کند.
   */
  app.get('/v1/time', (_req, res) => {
    res.json({ epochSeconds: nowSec(), today: todayInAppTz(), timezone: 'Asia/Tehran' })
  })

  /** آیا این شماره در فهرست حساب‌های تست است؟ */
  const isTestPhone = (raw: string) => {
    const n = normalizePhone(raw)
    return n != null && config.testPhones.some(p => normalizePhone(p) === n)
  }

  // ---------------------------------------------------------- احراز هویت

  app.post('/v1/auth/request-code', async (req, res) => {
    const data = body(z.object({ phone: z.string().min(10).max(15) }), req, res)
    if (!data) return

    // حساب تست: بدون پیامک و بدون کد.
    //
    // برای اینکه در توسعه هر بار منتظر کد نمانیم. عمداً پیش از ساختن کد
    // برمی‌گردد تا سهمیه پیامک را هم مصرف نکند.
    if (isTestPhone(data.phone)) {
      res.json({ ok: true, expiresIn: 0, codeLength: CODE_LENGTH, skipCode: true })
      return
    }

    const result = requestCode(db, data.phone, config.smsDailyLimitPerPhone)
    if (!result.ok) {
      const status = result.error === 'invalid_phone' ? 400 : 429
      res.status(status).json({ error: result.error, retryAfter: result.retryAfter })
      return
    }

    const sent = await sms.send(normalizePhone(data.phone)!, result.code)
    if (!sent.ok) {
      // اگر پیامک نرفت، «موفق» جواب ندهیم؛ وگرنه کاربر دو دقیقه به صفحه
      // کد خیره می‌ماند و بعد فکر می‌کند اپ خراب است. کد ساخته‌شده را هم
      // پاک می‌کنیم تا خطای ما سهمیه و ۸۰ ثانیه انتظارش را نسوزاند.
      discardCode(db, result.codeId)
      console.error(`sms send failed via ${sms.name}: ${sent.error}`)
      res.status(502).json({ error: 'sms_failed' })
      return
    }

    res.json({
      ok: true,
      expiresIn: result.expiresIn,
      codeLength: CODE_LENGTH,
      ...(config.exposeOtpInResponse ? { devCode: result.code } : {}),
    })
  })

  app.post('/v1/auth/verify', (req, res) => {
    const data = body(
      z.object({ phone: z.string().min(10).max(15), code: z.string().min(1).max(10) }),
      req,
      res,
    )
    if (!data) return

    if (isTestPhone(data.phone)) {
      const u = ensureUser(db, normalizePhone(data.phone)!)
      res.json({
        token: signToken(config.jwtSecret, { sub: u.userId, phone: u.phone }),
        user: { phone: u.phone, referralCode: u.referralCode, isNew: u.isNew },
      })
      return
    }

    const result = verifyCode(db, data.phone, data.code)
    if (!result.ok) {
      const status = result.error === 'too_many_attempts' ? 429 : 400
      res.status(status).json({ error: result.error })
      return
    }

    res.json({
      token: signToken(config.jwtSecret, { sub: result.userId, phone: result.phone }),
      user: { phone: result.phone, referralCode: result.referralCode, isNew: result.isNew },
    })
  })

  // کاربران نمایشی لیگ — فقط توسعه.
  //
  // با همان منطق TEST_PHONES: چیزی که در production نباید وجود داشته
  // باشد، اصلاً ساخته نمی‌شود، نه اینکه به تنظیمات سپرده شود.
  if (config.env !== 'production' && process.env.SEED_DEMO_USERS === '1') {
    const made = seedDemoUsers(db)
    if (made > 0) console.log(`seeded ${made} demo users for the league`)
  }

  /** شمار فعالیت‌های تمام‌شده کاربر — مبنای امتیاز لیگ */
  const countCompleted = (database: typeof db, userId: number): number =>
    (database
      .prepare(`SELECT COUNT(*) AS n FROM user_progress WHERE user_id = ? AND status = 'COMPLETED'`)
      .get(userId) as { n: number }).n

  // ---------------------------------------------------------------- لیگ

  app.get('/v1/league', requireAuth, (req: AuthedRequest, res) => {
    res.json(standings(db, req.userId!))
  })

  app.get('/v1/me', requireAuth, (req: AuthedRequest, res) => {
    const row = db
      .prepare(`SELECT phone, display_name, referral_code, current_level FROM users WHERE id = ?`)
      .get(req.userId!) as
      | { phone: string; display_name: string | null; referral_code: string; current_level: string }
      | undefined
    if (!row) {
      res.status(404).json({ error: 'not_found' })
      return
    }
    // activeSubscription تاریخ انقضا را هم می‌سنجد. پرس‌وجوی قبلی فقط
    // status را نگاه می‌کرد و اشتراک منقضی‌شده را هم فعال گزارش می‌داد.
    res.json({
      phone: row.phone,
      displayName: row.display_name,
      referralCode: row.referral_code,
      currentLevel: row.current_level,
      subscription: activeSubscription(db, req.userId!),
    })
  })

  // ---------------------------------------------------------- خرید

  app.get('/v1/plans', (_req, res) => {
    // قیمت و مدت از سرور می‌آید تا تغییر قیمت نیازمند انتشار نسخه نباشد
    res.json({
      plans: PLANS.map((p) => ({
        code: p.code,
        sku: p.sku,
        title: p.title,
        days: p.days,
        priceRial: p.priceRial,
        badge: p.badge ?? null,
        note: p.note ?? null,
      })),
    })
  })

  app.post('/v1/purchase/verify', requireAuth, async (req: AuthedRequest, res) => {
    const data = body(
      z.object({ sku: z.string().min(1).max(64), purchaseToken: z.string().min(1).max(256) }),
      req,
      res,
    )
    if (!data) return

    const result = await redeemPurchase(db, receipts, req.userId!, data.sku, data.purchaseToken)
    if (!result.ok) {
      const status = result.error === 'unknown_sku' ? 400 : 402
      res.status(status).json({ error: result.error })
      return
    }

    res.json({
      plan: result.plan,
      expiresAt: result.expiresAt,
      alreadyRedeemed: result.alreadyRedeemed,
    })
  })

  // ---------------------------------------------------------- زنجیره

  app.get('/v1/streak', requireAuth, (req: AuthedRequest, res) => {
    res.json({ ...getStreak(db, req.userId!), today: todayInAppTz() })
  })

  /**
   * ثبت فعالیت امروز.
   * بدنه عمداً خالی است: کلاینت نه تاریخ می‌فرستد نه طول زنجیره.
   */
  app.post('/v1/streak/check-in', requireAuth, (req: AuthedRequest, res) => {
    const result = checkIn(db, req.userId!)
    res.json({ result: result.kind, ...result.state, today: todayInAppTz() })
  })

  // ---------------------------------------------------------- محتوا

  const content = new ContentStore(config.contentDir)

  /**
   * فهرست بسته‌ها با نسخه.
   * کلاینت نسخه محلی را با این مقایسه می‌کند و فقط تفاوت را دانلود می‌کند.
   * بدون احراز هویت است تا اپ پیش از ورود هم بتواند محتوا بگیرد.
   */
  app.get('/v1/content/manifest', (_req, res) => {
    res.json({ bundles: content.manifest(), imagesBaseUrl: '/images/' })
  })

  app.get('/v1/content/:level', (req, res) => {
    const b = content.bundle(String(req.params.level))
    if (!b) {
      res.status(404).json({ error: 'not_found' })
      return
    }
    // ETag یعنی اگر محتوا عوض نشده باشد، پاسخ ۳۰۴ و بدون بدنه می‌آید —
    // روی شبکه ایران این تفاوت محسوسی دارد.
    res.setHeader('ETag', `"${b.version}"`)
    res.setHeader('Cache-Control', 'no-cache')
    if (req.header('if-none-match') === `"${b.version}"`) {
      res.status(304).end()
      return
    }
    // نسخه فشرده فقط وقتی که کلاینت گفته باشد می‌پذیرد
    const wantsGzip = /\bgzip\b/.test(req.header('accept-encoding') ?? '')
    if (wantsGzip && b.gzip) {
      res.setHeader('Content-Encoding', 'gzip')
      res.setHeader('Vary', 'Accept-Encoding')
      res.type('application/json').send(b.gzip)
      return
    }
    res.type('application/json').send(b.body)
  })

  // تصاویر — کش طولانی چون نام فایل با محتوا عوض می‌شود
  app.use(
    '/images',
    express.static(config.imagesDir, {
      maxAge: '30d',
      immutable: true,
      fallthrough: false,
    }),
  )

  // ---------------------------------------------------------- همگام‌سازی

  const progressSchema = z.object({
    activityId: z.string().min(1).max(64),
    status: z.enum(['NOT_STARTED', 'IN_PROGRESS', 'COMPLETED']),
    score: z.number().nullable().optional(),
    lastItem: z.number().int().min(0).optional(),
    completedAt: z.number().int().nullable().optional(),
    updatedAt: z.number().int(),
  })

  const leitnerSchema = z.object({
    entryId: z.string().min(1).max(64),
    word: z.string().min(1).max(64),
    box: z.number().int().min(1).max(5),
    dueAt: z.number().int(),
    correctStreak: z.number().int().min(0).optional(),
    totalReviews: z.number().int().min(0).optional(),
    source: z.string().min(1).max(32),
    updatedAt: z.number().int(),
  })

  const answerSchema = z.object({
    itemId: z.string().min(1).max(64),
    activityId: z.string().min(1).max(64),
    isProductive: z.boolean(),
    userAnswer: z.string().max(500),
    isCorrect: z.boolean(),
    targetWord: z.string().max(64).nullable().optional(),
    answeredAt: z.number().int(),
  })

  /**
   * یک رفت‌وبرگشت برای هر دو جهت.
   * اپ آفلاین‌محور است؛ هرچه تعداد درخواست کمتر باشد، روی شبکه ضعیف
   * احتمال موفقیت بیشتر است.
   *
   * سقف‌ها عمدی‌اند: یک کلاینت خراب نباید بتواند حافظه سرور را پر کند.
   */
  app.post('/v1/sync', requireAuth, (req: AuthedRequest, res) => {
    const data = body(
      z.object({
        since: z.number().int().min(0).default(0),
        progress: z.array(progressSchema).max(2000).optional(),
        leitner: z.array(leitnerSchema).max(2000).optional(),
        answers: z.array(answerSchema).max(2000).optional(),
      }),
      req,
      res,
    )
    if (!data) return

    // امتیاز لیگ را سرور حساب می‌کند، نه دستگاه.
    //
    // شمار فعالیت‌های تمام‌شده پیش و پس از همگام‌سازی مقایسه می‌شود و
    // برای هر فعالیت تازه امتیاز داده می‌شود. اگر به عددی که اپ
    // می‌فرستد اعتماد می‌کردیم، هر کسی با دستکاری دیتابیس محلی صدر
    // جدول را می‌خرید.
    //
    // مقدارش با XpRules.ACTIVITY سمت اپ یکی است تا عددی که کاربر در
    // صفحه اصلی می‌بیند با رتبه‌اش در لیگ نخواند نداشته باشد.
    const doneBefore = countCompleted(db, req.userId!)
    const stats = push(db, req.userId!, data)
    const gained = (countCompleted(db, req.userId!) - doneBefore) * XP_PER_ACTIVITY
    if (gained > 0) addXp(db, req.userId!, gained)

    const server = pull(db, req.userId!, data.since)
    res.json({ ...server, applied: stats, streak: getStreak(db, req.userId!) })
  })

  /** متریک داوری محصول — سند ۰۷ بخش ۷.۷ */
  app.get('/v1/stats', requireAuth, (req: AuthedRequest, res) => {
    const since = Number(req.query.since ?? 0)
    res.json({
      productiveAccuracy: productiveAccuracy(db, req.userId!, Number.isFinite(since) ? since : 0),
      streak: getStreak(db, req.userId!),
    })
  })

  // ---------------------------------------------------------- پنل ادمین
  //
  // اگر ADMIN_TOKEN تنظیم نشده باشد، هیچ‌کدام از این مسیرها ثبت نمی‌شوند.
  // پیش‌فرضِ «خاموش» عمدی است: پنلی که ناخواسته با توکن خالی بالا بیاید،
  // یعنی آمار کاربران و ثبت اشتراک برای همه باز است.
  if (config.adminToken) {
    const adminOnly = (req: Request, res: Response, next: NextFunction) => {
      const header = req.header('authorization') ?? ''
      const bearer = header.startsWith('Bearer ') ? header.slice(7) : ''
      const token = bearer || String(req.query.token ?? '')
      // مقایسه با طول ثابت لازم نیست چون توکن تصادفی و بلند است، اما
      // مقایسه ساده هم نباید زودتر از موعد true بدهد
      if (token !== config.adminToken) {
        res.status(401).json({ error: 'unauthorized' })
        return
      }
      next()
    }

    app.get('/admin/api/overview', adminOnly, (_req, res) => {
      res.json(adminOverview(db))
    })

    app.get('/admin/api/users', adminOnly, (req, res) => {
      const q = String(req.query.q ?? '').replace(/[^0-9]/g, '').slice(0, 15)
      const page = Math.max(0, Number(req.query.page ?? 0) || 0)
      const size = 25
      res.json({ ...adminUsers(db, q, size, page * size), page, size })
    })

    app.post('/admin/api/subscription', adminOnly, (req, res) => {
      const data = body(
        z.object({
          phone: z.string().min(10).max(15),
          plan: z.string().max(32).default(''),
          days: z.number().int().min(1).max(3650).nullable().default(null),
          note: z.string().max(200).default(''),
        }),
        req, res,
      )
      if (!data) return
      const phone = data.phone.replace(/[^0-9]/g, '')
      res.json(grantSubscription(db, phone, data.plan, data.days, data.note))
    })

    app.post('/admin/api/subscription/revoke', adminOnly, (req, res) => {
      const data = body(z.object({ phone: z.string().min(10).max(15) }), req, res)
      if (!data) return
      res.json(revokeSubscriptions(db, data.phone.replace(/[^0-9]/g, '')))
    })

    // خودِ صفحه — بدون توکن باز می‌شود و توکن را از کاربر می‌گیرد،
    // وگرنه باید توکن در نوار آدرس بیاید و در تاریخچه مرورگر بماند.
    app.get('/admin', (_req, res) => {
      res.sendFile(adminPagePath)
    })
  }

  // ---------------------------------------------------------- خطاها

  app.use((_req, res) => {
    res.status(404).json({ error: 'not_found' })
  })

  app.use((err: unknown, _req: Request, res: Response, _next: NextFunction) => {
    // express.static با fallthrough:false برای فایل نبود، خطای دارای status
    // پرتاب می‌کند. اگر همه را ۵۰۰ بدهیم، تصویر گمشده مثل خرابی سرور
    // به‌نظر می‌رسد و لاگ را هم بی‌دلیل شلوغ می‌کند.
    const status = (err as { status?: number; statusCode?: number })?.status
      ?? (err as { statusCode?: number })?.statusCode
    if (typeof status === 'number' && status >= 400 && status < 500) {
      res.status(status).json({ error: status === 404 ? 'not_found' : 'bad_request' })
      return
    }
    console.error('unhandled', err)
    res.status(500).json({ error: 'internal_error' })
  })

  return app
}

// اجرای مستقیم — هنگام import در تست، سرور بالا نمی‌آید
if (process.argv[1] && import.meta.url.endsWith(process.argv[1].split('/').pop() ?? '')) {
  const config = loadConfig()
  const db = openDb(config.dbPath)
  createApp(db, config).listen(config.port, () => {
    console.log(`SpeakUp API on :${config.port} (${config.env})`)
  })
}
