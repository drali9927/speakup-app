import type { Db } from '../db/index.js'
import { nowSec } from '../db/index.js'
import { daysBetween, shiftDays, todayInAppTz } from './time.js'

/** سقف فریز ذخیره‌شدنی — سند ۰۳ / F-10 */
export const MAX_FREEZES = 3

export type StreakState = {
  currentLength: number
  longestLength: number
  lastActiveDate: string | null
  freezeCount: number
}

export type CheckInResult =
  | { kind: 'already_today'; state: StreakState }
  | { kind: 'extended'; state: StreakState }
  | { kind: 'frozen'; freezesUsed: number; state: StreakState }
  | { kind: 'broken'; state: StreakState }

export function getStreak(db: Db, userId: number): StreakState {
  const row = db
    .prepare(
      `SELECT current_length, longest_length, last_active_date, freeze_count
       FROM streaks WHERE user_id = ?`,
    )
    .get(userId) as
    | { current_length: number; longest_length: number; last_active_date: string | null; freeze_count: number }
    | undefined

  if (!row) return { currentLength: 0, longestLength: 0, lastActiveDate: null, freezeCount: 0 }
  return {
    currentLength: row.current_length,
    longestLength: row.longest_length,
    lastActiveDate: row.last_active_date,
    freezeCount: row.freeze_count,
  }
}

/**
 * ثبت فعالیت امروز.
 *
 * قاعده عمداً ساده است: اتمام یک فعالیت = یک روز زنجیره. دوولینگو پس از
 * بیش از ۶۰۰ آزمایش روی همین قابلیت به این نتیجه رسید که قاعده ساده و
 * قابل فهم بهترین نتیجه را می‌دهد (سند ۰۷ بخش ۷.۴).
 *
 * تاریخ همیشه از سرور می‌آید — هیچ ورودی‌ای از کلاینت در محاسبه دخالت ندارد.
 */
export function checkIn(db: Db, userId: number): CheckInResult {
  const today = todayInAppTz()
  const s = getStreak(db, userId)
  const ts = nowSec()

  if (s.lastActiveDate === today) return { kind: 'already_today', state: s }

  const gap = s.lastActiveDate === null ? null : daysBetween(s.lastActiveDate, today)
  // ساعت سرور عقب رفته یا داده ناسازگار — زنجیره نباید بی‌دلیل پاره شود
  const missed = gap === null || gap <= 0 ? 0 : gap - 1

  const covered = Math.min(missed, s.freezeCount)
  const broken = missed > covered

  const newLength = s.lastActiveDate === null ? 1 : broken ? 1 : s.currentLength + 1

  const writeDay = db.prepare(
    `INSERT INTO streak_days (user_id, date, status) VALUES (?, ?, ?)
     ON CONFLICT(user_id, date) DO UPDATE SET status = excluded.status`,
  )
  for (let i = 1; i <= covered; i++) writeDay.run(userId, shiftDays(today, -i), 'FROZEN')
  if (broken) {
    for (let i = covered + 1; i <= missed; i++) writeDay.run(userId, shiftDays(today, -i), 'MISSED')
  }
  writeDay.run(userId, today, 'ACTIVE')

  const next: StreakState = {
    currentLength: newLength,
    longestLength: Math.max(s.longestLength, newLength),
    lastActiveDate: today,
    freezeCount: s.freezeCount - covered,
  }

  db.prepare(
    `INSERT INTO streaks
       (user_id, current_length, longest_length, last_active_date, freeze_count, freezes_used_total, updated_at)
     VALUES (?, ?, ?, ?, ?, ?, ?)
     ON CONFLICT(user_id) DO UPDATE SET
       current_length = excluded.current_length,
       longest_length = excluded.longest_length,
       last_active_date = excluded.last_active_date,
       freeze_count = excluded.freeze_count,
       freezes_used_total = streaks.freezes_used_total + ?,
       updated_at = excluded.updated_at`,
  ).run(
    userId, next.currentLength, next.longestLength, next.lastActiveDate,
    next.freezeCount, covered, ts, covered,
  )

  if (broken) return { kind: 'broken', state: next }
  if (covered > 0) return { kind: 'frozen', freezesUsed: covered, state: next }
  return { kind: 'extended', state: next }
}

/** پاداش فریز — تا سقف MAX_FREEZES */
export function earnFreeze(db: Db, userId: number): boolean {
  const s = getStreak(db, userId)
  if (s.freezeCount >= MAX_FREEZES) return false
  db.prepare(
    `INSERT INTO streaks (user_id, freeze_count, updated_at) VALUES (?, 1, ?)
     ON CONFLICT(user_id) DO UPDATE SET freeze_count = streaks.freeze_count + 1, updated_at = excluded.updated_at`,
  ).run(userId, nowSec())
  return true
}
