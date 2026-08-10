import type { Db } from '../db/index.js'
import { nowSec } from '../db/index.js'

/**
 * همگام‌سازی دوطرفه.
 *
 * قواعد حل تعارض از سند ۰۵ بخش ۵.۸ می‌آیند و هرکدام دلیل مشخصی دارند:
 *
 *   پیشرفت درس  → بیشترین مقدار برنده. پیشرفت هرگز عقب نمی‌رود؛ اگر کاربر
 *                 روی گوشی دوم درس را ناتمام رها کرده باشد، نباید وضعیت
 *                 تمام‌شده گوشی اول را پاک کند.
 *   کارت لایتنر → آخرین updated_at برنده. جعبه و سررسید هر دو با هم
 *                 حرکت می‌کنند، پس باید یک نسخه کامل برنده شود نه ترکیب.
 *   زنجیره      → همیشه سرور. کلاینت اصلاً نمی‌فرستدش.
 *   اشتراک      → همیشه سرور.
 *
 * گزارش پاسخ‌ها فقط افزودنی است و تعارضی ندارد.
 */

export type ProgressRow = {
  activityId: string
  status: string
  score?: number | null
  lastItem?: number
  completedAt?: number | null
  updatedAt: number
}

export type LeitnerRow = {
  entryId: string
  word: string
  box: number
  dueAt: number
  correctStreak?: number
  totalReviews?: number
  source: string
  updatedAt: number
}

export type AnswerRow = {
  itemId: string
  activityId: string
  isProductive: boolean
  userAnswer: string
  isCorrect: boolean
  targetWord?: string | null
  answeredAt: number
}

export type PushPayload = {
  progress?: ProgressRow[]
  leitner?: LeitnerRow[]
  answers?: AnswerRow[]
}

/** رتبه وضعیت پیشرفت — برای «بیشترین مقدار برنده» */
const RANK: Record<string, number> = { NOT_STARTED: 0, IN_PROGRESS: 1, COMPLETED: 2 }
const rank = (s: string) => RANK[s] ?? 0

export type PushStats = {
  progressApplied: number
  progressRejected: number
  leitnerApplied: number
  leitnerRejected: number
  answersStored: number
}

export function push(db: Db, userId: number, payload: PushPayload): PushStats {
  const stats: PushStats = {
    progressApplied: 0, progressRejected: 0,
    leitnerApplied: 0, leitnerRejected: 0,
    answersStored: 0,
  }

  const tx = db.transaction(() => {
    for (const p of payload.progress ?? []) {
      const cur = db
        .prepare(`SELECT status, last_item, updated_at FROM user_progress WHERE user_id = ? AND activity_id = ?`)
        .get(userId, p.activityId) as { status: string; last_item: number; updated_at: number } | undefined

      // وضعیت هرگز عقب نمی‌رود، و در وضعیت برابر جدیدترین برنده است
      if (cur) {
        const better = rank(p.status) > rank(cur.status)
        const sameButNewer = rank(p.status) === rank(cur.status) && p.updatedAt > cur.updated_at
        if (!better && !sameButNewer) { stats.progressRejected++; continue }
      }

      db.prepare(
        `INSERT INTO user_progress (user_id, activity_id, status, score, last_item, completed_at, updated_at)
         VALUES (?, ?, ?, ?, ?, ?, ?)
         ON CONFLICT(user_id, activity_id) DO UPDATE SET
           status = excluded.status,
           score = excluded.score,
           -- last_item عقب نمی‌رود حتی وقتی رکورد جدیدتر است
           last_item = MAX(user_progress.last_item, excluded.last_item),
           completed_at = COALESCE(excluded.completed_at, user_progress.completed_at),
           updated_at = excluded.updated_at`,
      ).run(
        userId, p.activityId, p.status, p.score ?? null,
        p.lastItem ?? 0, p.completedAt ?? null, p.updatedAt,
      )
      stats.progressApplied++
    }

    for (const c of payload.leitner ?? []) {
      const cur = db
        .prepare(`SELECT updated_at FROM leitner_cards WHERE user_id = ? AND entry_id = ?`)
        .get(userId, c.entryId) as { updated_at: number } | undefined

      if (cur && cur.updated_at >= c.updatedAt) { stats.leitnerRejected++; continue }

      db.prepare(
        `INSERT INTO leitner_cards
           (user_id, entry_id, word, box, due_at, correct_streak, total_reviews, source, updated_at)
         VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
         ON CONFLICT(user_id, entry_id) DO UPDATE SET
           box = excluded.box,
           due_at = excluded.due_at,
           correct_streak = excluded.correct_streak,
           total_reviews = excluded.total_reviews,
           updated_at = excluded.updated_at`,
      ).run(
        userId, c.entryId, c.word, c.box, c.dueAt,
        c.correctStreak ?? 0, c.totalReviews ?? 0, c.source, c.updatedAt,
      )
      stats.leitnerApplied++
    }

    const insertAnswer = db.prepare(
      `INSERT INTO answer_log
         (user_id, item_id, activity_id, is_productive, user_answer, is_correct, target_word, answered_at)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
    )
    for (const a of payload.answers ?? []) {
      insertAnswer.run(
        userId, a.itemId, a.activityId, a.isProductive ? 1 : 0,
        a.userAnswer, a.isCorrect ? 1 : 0, a.targetWord ?? null, a.answeredAt,
      )
      stats.answersStored++
    }
  })

  tx()
  return stats
}

export type PullResult = {
  now: number
  progress: ProgressRow[]
  leitner: LeitnerRow[]
}

/** همه‌چیزی که از `since` به بعد سمت سرور تغییر کرده */
export function pull(db: Db, userId: number, since: number): PullResult {
  const progress = db
    .prepare(
      `SELECT activity_id, status, score, last_item, completed_at, updated_at
       FROM user_progress WHERE user_id = ? AND updated_at > ?`,
    )
    .all(userId, since) as Array<{
      activity_id: string; status: string; score: number | null
      last_item: number; completed_at: number | null; updated_at: number
    }>

  const leitner = db
    .prepare(
      `SELECT entry_id, word, box, due_at, correct_streak, total_reviews, source, updated_at
       FROM leitner_cards WHERE user_id = ? AND updated_at > ?`,
    )
    .all(userId, since) as Array<{
      entry_id: string; word: string; box: number; due_at: number
      correct_streak: number; total_reviews: number; source: string; updated_at: number
    }>

  return {
    now: nowSec(),
    progress: progress.map((r) => ({
      activityId: r.activity_id, status: r.status, score: r.score,
      lastItem: r.last_item, completedAt: r.completed_at, updatedAt: r.updated_at,
    })),
    leitner: leitner.map((r) => ({
      entryId: r.entry_id, word: r.word, box: r.box, dueAt: r.due_at,
      correctStreak: r.correct_streak, totalReviews: r.total_reviews,
      source: r.source, updatedAt: r.updated_at,
    })),
  }
}

/**
 * متریک داوری محصول — نرخ تولید صحیح (سند ۰۷ بخش ۷.۷).
 * فقط تمرین‌های تولیدی شمرده می‌شوند؛ چندگزینه‌ای یادگیری واقعی را نشان نمی‌دهد.
 */
export function productiveAccuracy(db: Db, userId: number, since: number): number | null {
  const row = db
    .prepare(
      `SELECT COUNT(*) AS total, SUM(is_correct) AS correct
       FROM answer_log WHERE user_id = ? AND is_productive = 1 AND answered_at >= ?`,
    )
    .get(userId, since) as { total: number; correct: number | null }
  if (!row.total) return null
  return (row.correct ?? 0) / row.total
}
