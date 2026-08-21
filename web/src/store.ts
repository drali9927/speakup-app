/**
 * انبار محلی — پیشرفت، لایتنر، و صف پاسخ‌ها.
 *
 * چرا محلی و نه مستقیم روی سرور: کاربر باید بتواند وسط درس اینترنتش قطع
 * شود و کارش از دست نرود. هر تغییر اول این‌جا می‌نشیند و پرچم `dirty`
 * می‌گیرد؛ همگام‌سازی بعداً آن را می‌برد.
 *
 * ⚠️ زمان‌ها **میلی‌ثانیه**‌اند، دقیقاً مثل اپ اندروید
 * (`System.currentTimeMillis()`). سرور همان عددی را که می‌فرستیم ذخیره
 * می‌کند و مقایسه‌ها روی همان انجام می‌شود — اگر وب ثانیه بفرستد، کارت
 * لایتنری که روی گوشی ساخته شده همیشه «جدیدتر» به نظر می‌رسد و پیشرفت
 * وب هیچ‌وقت برنده نمی‌شود.
 */

export type ProgressRow = {
  activityId: string
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED'
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

const KEY = {
  progress: 'speakup.progress',
  leitner: 'speakup.leitner',
  answers: 'speakup.answers',
  dirtyP: 'speakup.dirty.progress',
  dirtyL: 'speakup.dirty.leitner',
  since: 'speakup.since',
}

function read<T>(key: string, fallback: T): T {
  try {
    const raw = localStorage.getItem(key)
    return raw ? (JSON.parse(raw) as T) : fallback
  } catch {
    // داده خراب بهتر است دور ریخته شود تا اینکه اپ بالا نیاید
    return fallback
  }
}

const write = (key: string, v: unknown) => localStorage.setItem(key, JSON.stringify(v))

// --- پیشرفت

export const allProgress = () => read<Record<string, ProgressRow>>(KEY.progress, {})

/** رتبه وضعیت — برای قاعده «بیشترین مقدار برنده» (همان قاعده سرور) */
const RANK = { NOT_STARTED: 0, IN_PROGRESS: 1, COMPLETED: 2 } as const
const rank = (s: string) => RANK[s as keyof typeof RANK] ?? 0

export function putProgress(row: ProgressRow) {
  const all = allProgress()
  const cur = all[row.activityId]
  // پیشرفت هرگز عقب نمی‌رود — همان قاعده‌ای که سرور هم اعمال می‌کند
  if (cur && rank(row.status) < rank(cur.status)) return
  all[row.activityId] = { ...cur, ...row, lastItem: Math.max(cur?.lastItem ?? 0, row.lastItem ?? 0) }
  write(KEY.progress, all)
  const dirty = new Set(read<string[]>(KEY.dirtyP, []))
  dirty.add(row.activityId)
  write(KEY.dirtyP, [...dirty])
}

// --- لایتنر

export const allLeitner = () => read<Record<string, LeitnerRow>>(KEY.leitner, {})

/**
 * فاصله مرور هر جعبه بر حسب روز — سند ۰۳ / F-07.
 * عیناً همان اعداد data/model/Enums.kt؛ اگر این دو از هم جدا شوند، کارتی
 * که روی گوشی مرور شده روی وب در زمان دیگری سررسید می‌شود.
 */
export const MAX_BOX = 5
const INTERVAL_DAYS = [0, 1, 3, 7, 21]
export const intervalDays = (box: number) =>
  INTERVAL_DAYS[Math.min(Math.max(box, 1), MAX_BOX) - 1]
export const nextBox = (box: number, correct: boolean) =>
  correct ? Math.min(box + 1, MAX_BOX) : 1
export const isLearned = (box: number) => box >= MAX_BOX

const DAY_MS = 86_400_000

function markLeitnerDirty(entryId: string) {
  const dirty = new Set(read<string[]>(KEY.dirtyL, []))
  dirty.add(entryId)
  write(KEY.dirtyL, [...dirty])
}

/**
 * افزودن خودکار واژه‌ای که کاربر غلط جواب داده — سند ۰۷، تمایز ۲.
 *
 * این مهم‌ترین تفاوت با رقیب است: آن‌جا کاربر باید خودش دکمه بزند، یعنی
 * همان کسی که واژه را بلد نیست باید تشخیص بدهد که بلد نیست. عملاً اتفاق
 * نمی‌افتد. این‌جا خودکار است.
 */
export function addWrongWord(word: string, source = 'AUTO_WRONG') {
  const all = allLeitner()
  const entryId = `w:${word.toLowerCase()}`
  const now = Date.now()
  // اگر از قبل هست، دست نمی‌خورد: کارتی که در جعبه ۴ است نباید با یک
  // اشتباه در تمرینِ دیگر به ابتدا برگردد — مرورِ خودِ کارت این کار را می‌کند
  if (all[entryId]) return
  all[entryId] = {
    entryId,
    word,
    box: 1,
    dueAt: now,
    correctStreak: 0,
    totalReviews: 0,
    source,
    updatedAt: now,
  }
  write(KEY.leitner, all)
  markLeitnerDirty(entryId)
}

/** ثبت نتیجه یک مرور — درست: جعبه بعد · نادرست: بازگشت به جعبه ۱ */
export function reviewCard(entryId: string, correct: boolean) {
  const all = allLeitner()
  const c = all[entryId]
  if (!c) return
  const box = nextBox(c.box, correct)
  const now = Date.now()
  all[entryId] = {
    ...c,
    box,
    dueAt: now + intervalDays(box) * DAY_MS,
    correctStreak: correct ? (c.correctStreak ?? 0) + 1 : 0,
    totalReviews: (c.totalReviews ?? 0) + 1,
    updatedAt: now,
  }
  write(KEY.leitner, all)
  markLeitnerDirty(entryId)
}

/** کارت‌هایی که سررسیدشان رسیده و هنوز آموخته نشده‌اند */
export const dueCards = (now = Date.now()) =>
  Object.values(allLeitner())
    .filter((c) => c.dueAt <= now && !isLearned(c.box))
    .sort((a, b) => a.dueAt - b.dueAt)

// --- صف پاسخ‌ها (فقط افزودنی، تعارضی ندارد)

export function logAnswer(a: AnswerRow) {
  const q = read<AnswerRow[]>(KEY.answers, [])
  q.push(a)
  // سرور هر بار حداکثر ۲۰۰۰ تا می‌پذیرد؛ قدیمی‌ها اول می‌روند
  write(KEY.answers, q.slice(-2000))
}

export const pendingAnswers = () => read<AnswerRow[]>(KEY.answers, [])

// --- وضعیت همگام‌سازی

export const dirtyProgress = () => {
  const all = allProgress()
  return read<string[]>(KEY.dirtyP, []).map((id) => all[id]).filter(Boolean)
}

export const dirtyLeitner = () => {
  const all = allLeitner()
  return read<string[]>(KEY.dirtyL, []).map((id) => all[id]).filter(Boolean)
}

export const getSince = () => read<number>(KEY.since, 0)

/** پس از همگام‌سازی موفق: پرچم‌ها پاک و مرزِ زمانی جابه‌جا می‌شود */
export function commitSync(now: number, pushedAnswers: number) {
  write(KEY.dirtyP, [])
  write(KEY.dirtyL, [])
  write(KEY.since, now)
  // فقط آن‌هایی که واقعاً رفتند حذف می‌شوند؛ اگر وسط کار پاسخ تازه‌ای
  // اضافه شده باشد، نباید بی‌آنکه فرستاده شود پاک شود
  const q = read<AnswerRow[]>(KEY.answers, [])
  write(KEY.answers, q.slice(pushedAnswers))
}

/** ادغام آنچه سرور فرستاده در انبار محلی */
export function mergeFromServer(progress: ProgressRow[], leitner: LeitnerRow[]) {
  const p = allProgress()
  for (const row of progress) {
    const cur = p[row.activityId]
    const better = !cur || rank(row.status) > rank(cur.status)
    const sameButNewer =
      cur && rank(row.status) === rank(cur.status) && row.updatedAt > cur.updatedAt
    if (better || sameButNewer) {
      p[row.activityId] = { ...row, lastItem: Math.max(cur?.lastItem ?? 0, row.lastItem ?? 0) }
    }
  }
  write(KEY.progress, p)

  const l = allLeitner()
  for (const row of leitner) {
    // کارت لایتنر: نسخه کامل جدیدتر برنده است، نه ترکیب دو نسخه
    if (!l[row.entryId] || row.updatedAt > l[row.entryId].updatedAt) l[row.entryId] = row
  }
  write(KEY.leitner, l)
}

/** خروج کاربر — داده‌ی حساب قبلی نباید برای حساب بعدی بماند */
export function clearAll() {
  Object.values(KEY).forEach((k) => localStorage.removeItem(k))
}
