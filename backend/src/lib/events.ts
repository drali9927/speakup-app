import type { Db } from '../db/index.js'
import { nowSec } from '../db/index.js'

/**
 * رویدادهای محصول — قیف.
 *
 * خودمیزبان و نه Firebase. سه دلیل:
 *
 * **۱. کاربر ایرانی.** Firebase Analytics بدون Google Play Services کار
 * نمی‌کند و روی بخشی از گوشی‌های بازار هدف ما یا نصب نیست یا در دسترس
 * نیست. یعنی دقیقاً همان کاربرانی که باید بشماریم، شمرده نمی‌شوند و
 * قیف سوگیری پیدا می‌کند — که از نداشتنِ قیف بدتر است، چون به عددِ غلط
 * اعتماد می‌کنیم.
 *
 * **۲. سرور از قبل هست.** جدول رویداد روی همان SQLite، هزینه‌اش در این
 * مقیاس تقریباً صفر است.
 *
 * **۳. داده از دست ما بیرون نمی‌رود.**
 */

/** رویدادهای قیف اصلی، به ترتیب. نام‌ها ثابت‌اند و نباید عوض شوند. */
export const FUNNEL = [
  'app_open',
  'onboarding_done',
  'auth_done',
  'placement_done',
  'activity_start',
  'activity_done',
  'lesson_done',
  'paywall_view',
  'purchase_done',
] as const

/** سقف طول نام و اندازه props — ورودی از کلاینت می‌آید و باید مهار شود */
const MAX_NAME = 40
const MAX_PROPS = 2000

export interface IncomingEvent {
  name: string
  at: number
  props?: Record<string, unknown>
}

export function record(
  db: Db,
  installId: string,
  userId: number | null,
  events: IncomingEvent[],
): number {
  const t = nowSec()
  const stmt = db.prepare(
    `INSERT INTO events (install_id, user_id, name, props, at, received_at)
     VALUES (?, ?, ?, ?, ?, ?)`,
  )
  const tx = db.transaction((rows: IncomingEvent[]) => {
    let n = 0
    for (const e of rows) {
      const name = String(e.name).slice(0, MAX_NAME)
      if (!name) continue
      let props: string | null = null
      if (e.props) {
        const j = JSON.stringify(e.props)
        props = j.length > MAX_PROPS ? null : j
      }
      // ساعت دستگاه ممکن است غلط باشد؛ اگر بی‌معنا بود، ساعت سرور
      const at = Number.isFinite(e.at) && e.at > 1_600_000_000 && e.at < t + 86_400 ? e.at : t
      stmt.run(installId, userId, name, props, at, t)
      n++
    }
    return n
  })
  return tx(events)
}

export interface FunnelStep {
  name: string
  installs: number
  /** درصد نسبت به گام اول */
  pctOfFirst: number
  /** درصد نسبت به گام قبل — اینجاست که ریزش دیده می‌شود */
  pctOfPrev: number
}

/**
 * قیف بر حسب **دستگاه یکتا** و نه تعداد رویداد.
 *
 * اگر رویداد بشماریم، کاربری که ده بار اپ را باز کند قیف را باد می‌کند
 * و نرخ عبور بی‌معنا می‌شود. پرسش واقعی این است: «از هر صد نفری که نصب
 * کردند، چند نفر به اینجا رسیدند؟»
 */
export function funnel(db: Db, sinceDays: number): FunnelStep[] {
  const since = nowSec() - sinceDays * 86_400
  const counts = FUNNEL.map((name) => {
    const r = db
      .prepare(`SELECT COUNT(DISTINCT install_id) n FROM events WHERE name = ? AND at >= ?`)
      .get(name, since) as { n: number }
    return { name, installs: r.n }
  })

  const first = counts[0]?.installs ?? 0
  return counts.map((c, i) => {
    const prev = i === 0 ? c.installs : (counts[i - 1]?.installs ?? 0)
    return {
      name: c.name,
      installs: c.installs,
      pctOfFirst: first ? Math.round((c.installs / first) * 100) : 0,
      pctOfPrev: prev ? Math.round((c.installs / prev) * 100) : 0,
    }
  })
}

/**
 * بازگشت روز دوم و هفتم — سنجه واقعیِ نگهداشت.
 *
 * «کاربر فعال» عدد آرامش‌بخشی است که رشد را پنهان می‌کند. آنچه به تصمیم
 * می‌خورد این است: از کسانی که N روز پیش نصب کردند، چند درصد فردایش
 * برگشتند.
 */
export function retention(db: Db, cohortDays: number): { d1: number; d7: number; cohort: number } {
  const t = nowSec()
  const from = t - cohortDays * 86_400
  const rows = db
    .prepare(
      `SELECT install_id, MIN(at) AS first_at FROM events
        WHERE at >= ? GROUP BY install_id`,
    )
    .all(from) as { install_id: string; first_at: number }[]

  let d1 = 0
  let d7 = 0
  const back = db.prepare(
    `SELECT 1 FROM events WHERE install_id = ? AND at >= ? AND at < ? LIMIT 1`,
  )
  for (const r of rows) {
    const day = 86_400
    if (back.get(r.install_id, r.first_at + day, r.first_at + 2 * day)) d1++
    if (back.get(r.install_id, r.first_at + 6 * day, r.first_at + 8 * day)) d7++
  }
  return { d1, d7, cohort: rows.length }
}
