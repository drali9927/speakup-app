import type { Database as Db } from 'better-sqlite3'
import { nowSec } from '../db/index.js'
import { PLANS } from './purchase.js'
import { normalizePhone } from './auth.js'

/**
 * پرس‌وجوهای پنل ادمین.
 *
 * همه‌شان فقط خواندنی‌اند جز [grantSubscription]. عمداً در یک فایل جدا
 * جمع شده‌اند تا مسیرهای اپ و مسیرهای ادمین با هم قاطی نشوند: اگر روزی
 * پنل جدا شد، این فایل با خودش می‌رود.
 *
 * کاربران نمایشی (`is_demo`) از همه شمارش‌ها بیرون‌اند. آن‌ها را خودمان
 * برای پر کردن جدول لیگ ساخته‌ایم و اگر در آمار بیایند، هر عددی که به
 * تصمیم محصول می‌خورد دروغ می‌شود.
 */

const DAY = 86_400

export interface Overview {
  users: { total: number; new1: number; new7: number; new30: number }
  active: { d1: number; d7: number; d30: number }
  subs: { active: number; byPlan: { plan: string; count: number }[] }
  revenue: { totalRial: number; d30Rial: number; manualActive: number }
  usage: { activitiesDone: number; answers: number; leitnerCards: number; accuracy: number | null }
  daily: { date: string; users: number; answers: number }[]
}

const one = (db: Db, sql: string, ...args: unknown[]): number =>
  ((db.prepare(sql).get(...args) as { n: number } | undefined)?.n ?? 0)

export function overview(db: Db): Overview {
  const t = nowSec()
  const real = `is_demo = 0`

  const byPlan = db
    .prepare(
      `SELECT plan_code AS plan, COUNT(*) AS count
         FROM subscriptions s JOIN users u ON u.id = s.user_id
        WHERE u.${real} AND s.status = 'active'
          AND (s.expires_at IS NULL OR s.expires_at > ?)
        GROUP BY plan_code ORDER BY count DESC`,
    )
    .all(t) as { plan: string; count: number }[]

  // نمودار ۱۴ روزه — روزها از خودِ SQLite می‌آیند تا روزهای خالی هم
  // ردیف داشته باشند؛ وگرنه نمودار روزهای بی‌فعالیت را حذف می‌کند و
  // افت مصرف شبیه یک هفته عادی دیده می‌شود.
  const daily: Overview['daily'] = []
  for (let i = 13; i >= 0; i--) {
    const from = Math.floor((t - i * DAY) / DAY) * DAY
    const to = from + DAY
    daily.push({
      date: new Date(from * 1000).toISOString().slice(0, 10),
      users: one(db, `SELECT COUNT(*) n FROM users WHERE ${real} AND created_at >= ? AND created_at < ?`, from, to),
      answers: one(
        db,
        `SELECT COUNT(*) n FROM answer_log a JOIN users u ON u.id = a.user_id
          WHERE u.${real} AND a.answered_at >= ? AND a.answered_at < ?`,
        from, to,
      ),
    })
  }

  const correct = one(
    db,
    `SELECT COUNT(*) n FROM answer_log a JOIN users u ON u.id = a.user_id
      WHERE u.${real} AND a.is_productive = 1 AND a.is_correct = 1`,
  )
  const productive = one(
    db,
    `SELECT COUNT(*) n FROM answer_log a JOIN users u ON u.id = a.user_id
      WHERE u.${real} AND a.is_productive = 1`,
  )

  return {
    users: {
      total: one(db, `SELECT COUNT(*) n FROM users WHERE ${real}`),
      new1: one(db, `SELECT COUNT(*) n FROM users WHERE ${real} AND created_at >= ?`, t - DAY),
      new7: one(db, `SELECT COUNT(*) n FROM users WHERE ${real} AND created_at >= ?`, t - 7 * DAY),
      new30: one(db, `SELECT COUNT(*) n FROM users WHERE ${real} AND created_at >= ?`, t - 30 * DAY),
    },
    active: {
      d1: one(db, `SELECT COUNT(*) n FROM users WHERE ${real} AND last_seen_at >= ?`, t - DAY),
      d7: one(db, `SELECT COUNT(*) n FROM users WHERE ${real} AND last_seen_at >= ?`, t - 7 * DAY),
      d30: one(db, `SELECT COUNT(*) n FROM users WHERE ${real} AND last_seen_at >= ?`, t - 30 * DAY),
    },
    subs: {
      active: byPlan.reduce((a, b) => a + b.count, 0),
      byPlan,
    },
    revenue: {
      // فقط خریدهای واقعی: اشتراک دستی پول نیست و اگر در درآمد بیاید،
      // هر گزارشی که به آن نگاه کند اشتباه تصمیم می‌گیرد.
      totalRial: one(
        db,
        `SELECT COALESCE(SUM(price_rial),0) n FROM subscriptions s JOIN users u ON u.id = s.user_id
          WHERE u.${real} AND s.gateway <> 'manual'`,
      ),
      d30Rial: one(
        db,
        `SELECT COALESCE(SUM(price_rial),0) n FROM subscriptions s JOIN users u ON u.id = s.user_id
          WHERE u.${real} AND s.gateway <> 'manual' AND s.created_at >= ?`,
        t - 30 * DAY,
      ),
      manualActive: one(
        db,
        `SELECT COUNT(*) n FROM subscriptions s JOIN users u ON u.id = s.user_id
          WHERE u.${real} AND s.gateway = 'manual' AND s.status = 'active'
            AND (s.expires_at IS NULL OR s.expires_at > ?)`,
        t,
      ),
    },
    usage: {
      activitiesDone: one(
        db,
        `SELECT COUNT(*) n FROM user_progress p JOIN users u ON u.id = p.user_id
          WHERE u.${real} AND p.status = 'COMPLETED'`,
      ),
      answers: one(
        db,
        `SELECT COUNT(*) n FROM answer_log a JOIN users u ON u.id = a.user_id WHERE u.${real}`,
      ),
      leitnerCards: one(
        db,
        `SELECT COUNT(*) n FROM leitner_cards c JOIN users u ON u.id = c.user_id WHERE u.${real}`,
      ),
      accuracy: productive > 0 ? correct / productive : null,
    },
    daily,
  }
}

export interface UserRow {
  id: number
  phone: string
  createdAt: number
  lastSeenAt: number | null
  level: string
  streak: number
  activitiesDone: number
  plan: string | null
  expiresAt: number | null
  gateway: string | null
}

export function users(db: Db, q: string, limit: number, offset: number): { rows: UserRow[]; total: number } {
  // جست‌وجو روی شماره؛ رقم‌های فارسی پیش از رسیدن به اینجا عادی شده‌اند
  const like = `%${q}%`
  const where = q ? `u.is_demo = 0 AND u.phone LIKE ?` : `u.is_demo = 0`
  const args = q ? [like] : []

  const total = ((db.prepare(`SELECT COUNT(*) n FROM users u WHERE ${where}`).get(...args) as { n: number }).n)

  const rows = db
    .prepare(
      `SELECT u.id, u.phone, u.created_at AS createdAt, u.last_seen_at AS lastSeenAt,
              u.current_level AS level,
              COALESCE(st.current_length, 0) AS streak,
              (SELECT COUNT(*) FROM user_progress p
                WHERE p.user_id = u.id AND p.status = 'COMPLETED') AS activitiesDone,
              s.plan_code AS plan, s.expires_at AS expiresAt, s.gateway AS gateway
         FROM users u
         LEFT JOIN streaks st ON st.user_id = u.id
         LEFT JOIN subscriptions s ON s.id = (
              SELECT id FROM subscriptions
               WHERE user_id = u.id AND status = 'active'
                 AND (expires_at IS NULL OR expires_at > ${nowSec()})
               ORDER BY expires_at DESC LIMIT 1)
        WHERE ${where}
        ORDER BY COALESCE(u.last_seen_at, u.created_at) DESC
        LIMIT ? OFFSET ?`,
    )
    .all(...args, limit, offset) as UserRow[]

  return { rows, total }
}

export type GrantResult =
  | { ok: true; plan: string; days: number; expiresAt: number }
  | { ok: false; error: string }

/**
 * ثبت دستی اشتراک برای یک کاربر.
 *
 * از انقضای فعلی ادامه می‌دهد و نه از امروز: اگر کاربری یک ماه اعتبار
 * دارد و ما یک ماه هدیه می‌دهیم، باید دو ماه شود نه یک ماه.
 *
 * `gateway = 'manual'` می‌ماند تا در گزارش درآمد شمرده نشود و بعداً هم
 * معلوم باشد این اشتراک فروش نبوده.
 */
export function grantSubscription(
  db: Db,
  phone: string,
  planCode: string,
  daysOverride: number | null,
  note: string,
): GrantResult {
  const user = db.prepare(`SELECT id FROM users WHERE phone = ?`).get(phone) as { id: number } | undefined
  if (!user) return { ok: false, error: 'کاربری با این شماره پیدا نشد' }

  const plan = PLANS.find((p) => p.code === planCode)
  const days = daysOverride ?? plan?.days
  if (!days || days < 1 || days > 3650) return { ok: false, error: 'مدت اشتراک نامعتبر است' }

  const t = nowSec()
  const current = db
    .prepare(
      `SELECT expires_at FROM subscriptions
        WHERE user_id = ? AND status = 'active' AND (expires_at IS NULL OR expires_at > ?)
        ORDER BY expires_at DESC LIMIT 1`,
    )
    .get(user.id, t) as { expires_at: number } | undefined

  const startFrom = current?.expires_at && current.expires_at > t ? current.expires_at : t
  const expiresAt = startFrom + days * DAY

  // توکن باید یکتا باشد و ثانیه کافی نیست: دو ثبت پشت‌سرهم در یک ثانیه،
  // به قید یکتاییِ purchase_token می‌خورد و کل درخواست ۵۰۰ می‌شود.
  const token = `manual-${user.id}-${t}-${Math.random().toString(36).slice(2, 8)}`

  db.prepare(
    `INSERT INTO subscriptions
       (user_id, plan_code, status, started_at, expires_at, price_rial, gateway, purchase_token, created_at)
     VALUES (?, ?, 'active', ?, ?, 0, 'manual', ?, ?)`,
  ).run(user.id, planCode || 'manual', startFrom, expiresAt, token, t)

  if (note) {
    // یادداشت در همان توکن ذخیره نمی‌شود چون یکتاست؛ فعلاً فقط لاگ
    console.log(`[admin] اشتراک دستی برای ${phone}: ${days} روز — ${note}`)
  }
  return { ok: true, plan: planCode || 'manual', days, expiresAt }
}

/** لغو اشتراک‌های فعالِ یک کاربر — برای اصلاح اشتباه */
export function revokeSubscriptions(db: Db, phone: string): { ok: boolean; changed: number } {
  const user = db.prepare(`SELECT id FROM users WHERE phone = ?`).get(phone) as { id: number } | undefined
  if (!user) return { ok: false, changed: 0 }
  const r = db
    .prepare(`UPDATE subscriptions SET status = 'expired' WHERE user_id = ? AND status = 'active'`)
    .run(user.id)
  return { ok: true, changed: r.changes }
}

// ---------------------------------------------------------------- خروجی CSV

/**
 * همه کاربران، برای خروجی CSV.
 *
 * بدون صفحه‌بندی: خروجی گرفتن یعنی می‌خواهی **همه** را داشته باشی، و
 * جدول کاربران در این مقیاس (ده‌ها هزار ردیف) در یک پرس‌وجو جا می‌شود.
 */
export function allUsers(db: Db): UserRow[] {
  return db
    .prepare(
      `SELECT u.id, u.phone, u.created_at AS createdAt, u.last_seen_at AS lastSeenAt,
              u.current_level AS level,
              COALESCE(st.current_length, 0) AS streak,
              (SELECT COUNT(*) FROM user_progress p
                WHERE p.user_id = u.id AND p.status = 'COMPLETED') AS activitiesDone,
              s.plan_code AS plan, s.expires_at AS expiresAt, s.gateway AS gateway
         FROM users u
         LEFT JOIN streaks st ON st.user_id = u.id
         LEFT JOIN subscriptions s ON s.id = (
              SELECT id FROM subscriptions
               WHERE user_id = u.id AND status = 'active'
                 AND (expires_at IS NULL OR expires_at > ${nowSec()})
               ORDER BY expires_at DESC LIMIT 1)
        WHERE u.is_demo = 0
        ORDER BY u.created_at DESC`,
    )
    .all() as UserRow[]
}

const iso = (t: number | null): string => (t ? new Date(t * 1000).toISOString().slice(0, 19).replace('T', ' ') : '')

/**
 * CSV برای اکسل.
 *
 * دو نکته که بدون آن‌ها فایل در اکسل خراب باز می‌شود:
 *
 * **۱. BOM لازم است.** بدون آن اکسل ویندوز فایل را با کدگذاری محلی
 * می‌خواند و ستون‌های فارسی به هم می‌ریزند.
 *
 * **۲. شماره تلفن باید متن بماند.** «09121234567» بدون محافظت به عدد
 * تبدیل و صفرِ اول حذف می‌شود؛ کل ستون شماره خراب می‌شود. با `="..."`
 * اکسل مجبور می‌شود متن نگهش دارد.
 */
export function usersCsv(db: Db): string {
  const head = [
    'شماره', 'سطح', 'زنجیره', 'فعالیت انجام‌شده',
    'اشتراک', 'انقضای اشتراک', 'درگاه', 'آخرین بازدید', 'تاریخ عضویت',
  ]
  const esc = (v: string): string => `"${v.replace(/"/g, '""')}"`
  const lines = [head.map(esc).join(',')]

  for (const u of allUsers(db)) {
    lines.push([
      `="${u.phone}"`,
      esc(u.level),
      String(u.streak),
      String(u.activitiesDone),
      esc(u.plan ?? ''),
      esc(iso(u.expiresAt)),
      esc(u.gateway ?? ''),
      esc(iso(u.lastSeenAt)),
      esc(iso(u.createdAt)),
    ].join(','))
  }
  // \r\n و نه \n: اکسل ویندوز با خط‌شکن یونیکسی گاهی همه را یک ردیف می‌بیند
  return '﻿' + lines.join('\r\n') + '\r\n'
}

// --------------------------------------------------------- ساخت گروهی کاربر

export interface BulkLineResult {
  line: number
  phone: string
  status: 'created' | 'existed' | 'granted' | 'error'
  detail?: string
}

export interface BulkResult {
  created: number
  existed: number
  granted: number
  errors: number
  rows: BulkLineResult[]
}

/**
 * ساخت گروهی کاربر از یک فایل.
 *
 * هر خط: `شماره` یا `شماره,کد اشتراک` یا `شماره,کد اشتراک,روز`
 *
 * سه تصمیم که ارزش توضیح دارند:
 *
 * **۱. `last_seen_at` خالی می‌ماند.** کاربری که ما دستی ساخته‌ایم هنوز
 * اپ را باز نکرده. اگر مثل ورود عادی مهر زمان بخورد، همان لحظه در آمار
 * «فعال امروز» می‌نشیند و عدد فعال‌ها را به اندازه کل فایل باد می‌کند.
 *
 * **۲. تکراری خطا نیست.** اجرای دوباره همان فایل نباید نصفش را خطا
 * بدهد؛ شماره‌ای که هست رد می‌شود و اگر اشتراک خواسته شده، همان اعمال
 * می‌شود. یعنی می‌شود فایل را با خیال راحت دوباره فرستاد.
 *
 * **۳. خطای یک خط بقیه را نمی‌خواباند.** در فایل صد نفره، یک شماره
 * غلط نباید ۹۹ نفر دیگر را عقب بیندازد. گزارش خط‌به‌خط برمی‌گردد.
 */
export function bulkCreateUsers(db: Db, text: string): BulkResult {
  const out: BulkResult = { created: 0, existed: 0, granted: 0, errors: 0, rows: [] }
  const ts = nowSec()

  const lines = text.split(/\r?\n/)
  lines.forEach((raw, i) => {
    const line = raw.trim()
    if (!line) return
    // سطر عنوان فایل‌های اکسل، اگر بود، رد شود
    if (i === 0 && !/[0-9۰-۹]/.test(line)) return

    const parts = line.split(/[,;\t]/).map((p) => p.trim().replace(/^="?|"?$/g, ''))
    const phone = normalizePhone(toLatinDigits(parts[0] ?? ''))
    const n = i + 1

    if (!phone) {
      out.errors++
      out.rows.push({ line: n, phone: parts[0] ?? '', status: 'error', detail: 'شماره نامعتبر' })
      return
    }

    try {
      const existing = db.prepare(`SELECT id, is_demo FROM users WHERE phone = ?`).get(phone) as
        | { id: number; is_demo: number } | undefined

      // کاربر نمایشی را دست نزن.
      //
      // شماره‌های نمایشیِ جدول لیگ در بازه‌ای هستند که با پیش‌شماره
      // واقعی هم می‌تواند برخورد کند. اگر ادمین چنین شماره‌ای را وارد
      // کند و ما در سکوت اشتراک را روی حساب قلابی بنشانیم، کاربر واقعی
      // اشتراکش را نمی‌گیرد و هیچ‌کس هم نمی‌فهمد چرا.
      if (existing?.is_demo) {
        out.errors++
        out.rows.push({
          line: n, phone, status: 'error',
          detail: 'این شماره به حساب نمایشی لیگ خورده — دستی بررسی کن',
        })
        return
      }

      if (existing) {
        out.existed++
        out.rows.push({ line: n, phone, status: 'existed' })
      } else {
        // last_seen_at عمداً NULL — این کاربر هنوز اپ را باز نکرده
        db.prepare(
          `INSERT INTO users (phone, referral_code, created_at, last_seen_at) VALUES (?, ?, ?, NULL)`,
        ).run(phone, makeReferralCode(db), ts)
        out.created++
        out.rows.push({ line: n, phone, status: 'created' })
      }

      const planCode = parts[1] || ''
      const row = out.rows[out.rows.length - 1]!
      if (planCode) {
        const days = parts[2] ? Number(toLatinDigits(parts[2])) : null
        const g = grantSubscription(db, phone, planCode, days && days > 0 ? days : null, 'ورود گروهی')
        if (g.ok) {
          out.granted++
          row.detail = `${g.days} روز ${g.plan}`
        } else {
          out.errors++
          row.status = 'error'
          row.detail = g.error
        }
      }
    } catch (e) {
      out.errors++
      out.rows.push({ line: n, phone, status: 'error', detail: String((e as Error).message) })
    }
  })

  return out
}

/** رقم فارسی و عربی به لاتین — شماره‌ها از اکسل فارسی می‌آیند */
function toLatinDigits(s: string): string {
  return s.replace(/[۰-۹]/g, (d) => String('۰۱۲۳۴۵۶۷۸۹'.indexOf(d)))
    .replace(/[٠-٩]/g, (d) => String('٠١٢٣٤٥٦٧٨٩'.indexOf(d)))
}

/** همان الگوریتم auth، چون ساخت کاربر اینجا از مسیر ورود نمی‌گذرد */
function makeReferralCode(db: Db): string {
  const alphabet = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
  for (let attempt = 0; attempt < 40; attempt++) {
    let code = ''
    for (let i = 0; i < 6; i++) code += alphabet[Math.floor(Math.random() * alphabet.length)]
    const taken = db.prepare(`SELECT 1 FROM users WHERE referral_code = ?`).get(code)
    if (!taken) return code
  }
  throw new Error('ساخت کد معرف یکتا ممکن نشد')
}
