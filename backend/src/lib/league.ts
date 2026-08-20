import type { Db } from '../db/index.js'

/**
 * لیگ هفتگی.
 *
 * الگو از دولینگو گرفته شده اما ساده‌تر است: دوره از دوشنبه شروع
 * می‌شود، کاربران در گروه‌های سی‌نفره رقابت می‌کنند، و رتبه بر پایه
 * امتیاز همان هفته است.
 *
 * چرا امتیاز سمت سرور نگه داشته می‌شود و از دستگاه جمع نمی‌شود: اگر
 * رتبه به عددی که خود اپ گزارش می‌کند اعتماد کند، هر کسی با دستکاری
 * دیتابیس محلی صدر جدول را می‌خرد و کل سازوکار بی‌معنا می‌شود. اپ فقط
 * «این فعالیت تمام شد» را می‌فرستد؛ امتیازش را سرور حساب می‌کند.
 */

export const COHORT_SIZE = 30

/** امتیاز هر فعالیت تمام‌شده — برابر با XpRules.ACTIVITY سمت اپ */
export const XP_PER_ACTIVITY = 10

/**
 * چند نفر از بالای گروه صعود می‌کنند و چند نفر از پایین سقوط.
 *
 * از سی نفر، پنج و پنج: به‌اندازه‌ای بزرگ که رسیدن بهش شدنی به نظر برسد،
 * و به‌اندازه‌ای کوچک که رقابت معنا داشته باشد.
 */
export const PROMOTE_COUNT = 5
export const RELEGATE_COUNT = 5

/** رده‌ها — از پایین به بالا */
export const TIERS = [
  'برنز', 'نقره', 'طلا', 'یاقوت', 'زمرد', 'کهربا', 'مروارید', 'الماس',
] as const

/** دوشنبه همان هفته، به شکل YYYY-MM-DD */
export function weekStart(now = new Date()): string {
  const d = new Date(Date.UTC(now.getUTCFullYear(), now.getUTCMonth(), now.getUTCDate()))
  // getUTCDay: یکشنبه ۰ … دوشنبه ۱
  const shift = (d.getUTCDay() + 6) % 7
  d.setUTCDate(d.getUTCDate() - shift)
  return d.toISOString().slice(0, 10)
}

/** شناسه دوره جاری؛ اگر نبود ساخته می‌شود */
export function currentWeekId(db: Db, now = new Date()): number {
  const starts = weekStart(now)
  const row = db.prepare(`SELECT id FROM league_weeks WHERE starts_on = ?`).get(starts) as
    | { id: number } | undefined
  if (row) return row.id
  const info = db
    .prepare(`INSERT INTO league_weeks (starts_on, created_at) VALUES (?, ?)`)
    .run(starts, Math.floor(now.getTime() / 1000))
  return Number(info.lastInsertRowid)
}

/**
 * عضویت کاربر در دوره جاری را تضمین می‌کند.
 *
 * گروه‌بندی ساده است: کاربر به آخرین گروهِ پرنشده می‌رود. تخصیص
 * تصادفی یا بر اساس مهارت، وقتی تعداد کاربران کم است فایده‌ای ندارد و
 * فقط گروه‌های خلوت می‌سازد.
 */
export function ensureMember(db: Db, userId: number, now = new Date()): number {
  const weekId = currentWeekId(db, now)
  const existing = db
    .prepare(`SELECT cohort FROM league_members WHERE week_id = ? AND user_id = ?`)
    .get(weekId, userId) as { cohort: number } | undefined
  if (existing) return weekId

  const last = db
    .prepare(`
      SELECT cohort, COUNT(*) AS n FROM league_members
      WHERE week_id = ? GROUP BY cohort ORDER BY cohort DESC LIMIT 1
    `)
    .get(weekId) as { cohort: number; n: number } | undefined

  const cohort = !last ? 1 : last.n >= COHORT_SIZE ? last.cohort + 1 : last.cohort

  // رده تازه از **نتیجه** هفته پیش درمی‌آید، نه از کپی کردن رده قبلی.
  //
  // تا امروز همین کپی می‌شد و هیچ‌کس هرگز از برنز بالاتر نمی‌رفت؛ هشت
  // رده وجود داشت اما هیچ‌کدام قابل رسیدن نبود. لیگی که صعود ندارد،
  // جدول است نه رقابت.
  //
  // محاسبه همین‌جا و تنبل انجام می‌شود و نه با کرون: رده هر کاربر دقیقاً
  // وقتی لازم است که خودش وارد دوره تازه می‌شود.
  const prev = db
    .prepare(`
      SELECT m.tier, m.cohort, m.xp, m.week_id AS weekId
        FROM league_members m
        JOIN league_weeks w ON w.id = m.week_id
       WHERE m.user_id = ? AND m.week_id <> ?
       ORDER BY w.starts_on DESC LIMIT 1
    `)
    .get(userId, weekId) as
    | { tier: number; cohort: number; xp: number; weekId: number }
    | undefined

  let tier = prev?.tier ?? 0
  if (prev) {
    const rank = ((db
      .prepare(`
        SELECT COUNT(*) + 1 AS n FROM league_members
         WHERE week_id = ? AND cohort = ?
           AND (xp > ? OR (xp = ? AND user_id < ?))
      `)
      .get(prev.weekId, prev.cohort, prev.xp, prev.xp, userId) as { n: number }).n)

    const size = ((db
      .prepare(`SELECT COUNT(*) AS n FROM league_members WHERE week_id = ? AND cohort = ?`)
      .get(prev.weekId, prev.cohort) as { n: number }).n)

    // بدون امتیاز، صعود بی‌معناست: کسی که کل هفته کار نکرده نباید فقط
    // به‌خاطر خالی بودن گروه بالا برود.
    if (rank <= PROMOTE_COUNT && prev.xp > 0) tier = Math.min(tier + 1, TIERS.length - 1)
    else if (rank > size - RELEGATE_COUNT) tier = Math.max(tier - 1, 0)
  }

  db.prepare(`
    INSERT INTO league_members (week_id, user_id, cohort, tier, xp)
    VALUES (?, ?, ?, ?, 0)
  `).run(weekId, userId, cohort, tier)

  return weekId
}

/** افزودن امتیاز به دوره جاری */
export function addXp(db: Db, userId: number, amount: number, now = new Date()): void {
  if (amount <= 0) return
  const weekId = ensureMember(db, userId, now)
  db.prepare(`
    UPDATE league_members SET xp = xp + ? WHERE week_id = ? AND user_id = ?
  `).run(amount, weekId, userId)
}

export type LeagueRow = {
  rank: number
  userId: number
  name: string
  xp: number
  isMe: boolean
  isDemo: boolean
}

/** جدول گروهِ کاربر در دوره جاری */
export function standings(db: Db, userId: number, now = new Date()): {
  tier: number
  tierName: string
  nextTierName: string | null
  cohort: number
  weekStart: string
  /** پایان دوره به ثانیه — کاربر باید بداند چقدر وقت دارد */
  endsAt: number
  promoteCount: number
  relegateCount: number
  rows: LeagueRow[]
} {
  const weekId = ensureMember(db, userId, now)
  const me = db
    .prepare(`SELECT cohort, tier FROM league_members WHERE week_id = ? AND user_id = ?`)
    .get(weekId, userId) as { cohort: number; tier: number }

  const raw = db.prepare(`
    SELECT m.user_id AS userId, m.xp AS xp, u.phone AS phone, u.is_demo AS isDemo
    FROM league_members m
    JOIN users u ON u.id = m.user_id
    WHERE m.week_id = ? AND m.cohort = ?
    ORDER BY m.xp DESC, m.user_id ASC
  `).all(weekId, me.cohort) as Array<{ userId: number; xp: number; phone: string; isDemo: number }>

  const starts = weekStart(now)
  return {
    tier: me.tier,
    tierName: TIERS[Math.min(me.tier, TIERS.length - 1)] ?? TIERS[0],
    nextTierName: TIERS[me.tier + 1] ?? null,
    cohort: me.cohort,
    weekStart: starts,
    // دوره از دوشنبه تا دوشنبه؛ پایانش هفت روز بعد از شروع
    endsAt: Math.floor(Date.parse(`${starts}T00:00:00Z`) / 1000) + 7 * 86_400,
    promoteCount: PROMOTE_COUNT,
    relegateCount: RELEGATE_COUNT,
    rows: raw.map((r, i) => ({
      rank: i + 1,
      userId: r.userId,
      // نام واقعی نداریم و شماره تلفن هم نباید به بقیه نشان داده شود.
      // چهار رقم آخر برای اینکه کاربر خودش را پیدا کند کافی است.
      name: r.isDemo ? demoName(r.userId) : `کاربر ${r.phone.slice(-4)}`,
      xp: r.xp,
      isMe: r.userId === userId,
      isDemo: r.isDemo === 1,
    })),
  }
}

const DEMO_NAMES = [
  'سارا', 'نیما', 'مریم', 'رضا', 'شیما', 'امیر', 'الهام', 'کاوه',
  'نگار', 'بابک', 'پریسا', 'حسین', 'مینا', 'سامان', 'لیلا',
]

function demoName(userId: number): string {
  return DEMO_NAMES[userId % DEMO_NAMES.length] ?? 'کاربر'
}

/**
 * ساخت کاربران نمایشی تا لیگ در روزهای اول خالی نباشد.
 *
 * ⚠️ فقط توسعه. در production فراخوانی نمی‌شود، چون کاربر جعلی هم آمار
 * را آلوده می‌کند و هم اگر کاربر واقعی بفهمد رقیبش ربات است، اعتمادش
 * به کل اپ می‌رود.
 *
 * امتیازها ثابت‌اند و نه تصادفی: با هر بار بالا آمدن سرور نباید رتبه‌ها
 * جابه‌جا شوند، وگرنه در تست به‌نظر می‌رسد چیزی خراب است.
 */
export function seedDemoUsers(db: Db, count = 12, now = new Date()): number {
  const ts = Math.floor(now.getTime() / 1000)
  let made = 0
  for (let i = 0; i < count; i++) {
    const phone = `0900000${String(i).padStart(4, '0')}`
    const existing = db.prepare(`SELECT id FROM users WHERE phone = ?`).get(phone) as
      | { id: number } | undefined
    let id: number
    if (existing) {
      id = existing.id
    } else {
      const info = db.prepare(`
        INSERT INTO users (phone, referral_code, created_at, last_seen_at, is_demo)
        VALUES (?, ?, ?, ?, 1)
      `).run(phone, `DEMO${String(i).padStart(2, '0')}`, ts, ts)
      id = Number(info.lastInsertRowid)
      made++
    }
    ensureMember(db, id, now)
    const weekId = currentWeekId(db, now)
    db.prepare(`UPDATE league_members SET xp = ? WHERE week_id = ? AND user_id = ?`)
      .run(20 + i * 35, weekId, id)
  }
  return made
}
