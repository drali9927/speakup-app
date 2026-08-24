/**
 * کلاینت API — تنها جایی که با سرور حرف می‌زند.
 *
 * نشانی همیشه نسبی است (`/v1/...`). در توسعه، Vite آن را به بک‌اند لوکال
 * پروکسی می‌کند؛ در تولید، nginx همین کار را می‌کند. یعنی هیچ نسخه‌ای از
 * کد نشانی سرور را داخل خودش ندارد و اشتباهِ «انتشار نسخه‌ای که به لپ‌تاپ
 * برنامه‌نویس وصل است» اصلاً ممکن نیست.
 */

const TOKEN_KEY = 'speakup.token'

export const getToken = () => localStorage.getItem(TOKEN_KEY)
export const setToken = (t: string) => localStorage.setItem(TOKEN_KEY, t)
export const clearToken = () => localStorage.removeItem(TOKEN_KEY)

export class ApiError extends Error {
  constructor(
    readonly status: number,
    readonly code: string,
    message: string,
  ) {
    super(message)
  }
}

/**
 * پیام‌های خطای سرور به فارسی — کاربر نباید کد انگلیسی ببیند.
 *
 * ⚠️ کلیدها باید دقیقاً همان رشته‌هایی باشند که `backend/src/lib/auth.ts`
 * برمی‌گرداند. اولین‌بار حدسشان زدم و نتیجه این شد که کاربرِ کدِ اشتباه،
 * به‌جای «کد درست نیست»، پیام مبهم «مشکلی پیش آمد» می‌دید — یعنی
 * نمی‌فهمید باید کد را دوباره تایپ کند یا کد تازه بخواهد.
 */
const MESSAGES: Record<string, string> = {
  // ورود
  wrong_code: 'کد وارد‌شده درست نیست',
  expired: 'کد منقضی شده — کد تازه بگیر',
  no_code: 'کدی برای این شماره نفرستادیم — دوباره درخواست بده',
  too_many_attempts: 'چند بار اشتباه زدی. کد تازه بگیر',
  too_soon: 'کمی صبر کن، بعد دوباره کد بخواه',
  daily_limit: 'امروز چند بار کد خواستی. فردا دوباره امتحان کن',
  invalid_phone: 'شماره موبایل درست نیست',
  sms_failed: 'پیامک فرستاده نشد. کمی بعد دوباره تلاش کن',
  // عمومی
  invalid_body: 'اطلاعات فرستاده‌شده درست نیست',
  unauthorized: 'باید دوباره وارد شوی',
  not_found: 'پیدا نشد',
  internal_error: 'خطای سرور. کمی بعد دوباره تلاش کن',
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const token = getToken()
  const res = await fetch(path, {
    ...init,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...init?.headers,
    },
  })

  if (!res.ok) {
    let code = 'unknown'
    try {
      code = (await res.json()).error ?? 'unknown'
    } catch {
      /* پاسخ JSON نبود — کد پیش‌فرض می‌ماند */
    }
    // ۴۰۱ یعنی توکن دیگر معتبر نیست؛ نگه‌داشتنش فقط خطای بعدی می‌سازد
    if (res.status === 401) clearToken()
    throw new ApiError(res.status, code, MESSAGES[code] ?? 'مشکلی پیش آمد. دوباره تلاش کن')
  }

  return res.json() as Promise<T>
}

// --- احراز هویت

export type RequestCodeResult = {
  ok: true
  expiresIn: number
  codeLength: number
  /** فقط در حالت توسعه — در تولید هرگز نمی‌آید */
  devCode?: string
}

export const requestCode = (phone: string) =>
  request<RequestCodeResult>('/v1/auth/request-code', {
    method: 'POST',
    body: JSON.stringify({ phone }),
  })

export type VerifyResult = { token: string; isNewUser: boolean }

export const verifyCode = (phone: string, code: string) =>
  request<VerifyResult>('/v1/auth/verify', {
    method: 'POST',
    body: JSON.stringify({ phone, code }),
  })

// --- کاربر

export type Subscription = {
  plan: string
  expiresAt: number
  isActive: boolean
} | null

export type Me = {
  phone: string
  displayName: string | null
  referralCode: string
  currentLevel: string
  subscription: Subscription
}

export const getMe = () => request<Me>('/v1/me')

// --- محتوا

export type Lesson = {
  id: string
  levelCode: string
  number: number
  titleEn: string
  grammarTopicFa: string
  themeFa: string
  isFree: boolean
  estimatedMinutes: number
  colorHex: string
}

export type Section = {
  id: string
  lessonId: string
  type: string
  sortOrder: number
  estimatedMinutes: number
}

export type Activity = {
  id: string
  sectionId: string
  title: string
  descriptionFa: string
  activityType: string
  sortOrder: number
}

export type Item = {
  id: string
  activityId: string
  sortOrder: number
  prompt: string
  promptFa: string
  hintFa: string
  imageFile: string
  ttsText: string
  targetWord?: string
  exampleEn?: string
  exampleFa?: string
  correctAnswer?: string | null
  /** پاسخ‌های جایگزین — کاربر بابت تفاوت جزئی جریمه نمی‌شود (سند ۰۷) */
  alternatives?: string | null
  /** گزینه‌ها به‌صورت یک رشته با جداکننده « | » می‌آیند، نه آرایه */
  options?: string | null
  /** در گفت‌وگو: F یا M — تعیین می‌کند حباب کدام سمت باشد */
  voice?: string | null
}

export type DictionaryEntry = {
  id: string
  word: string
  lemma: string
  pos: string
  ipaUk: string
  ipaUs: string
  translationFa: string
}

export type Bundle = {
  version: number
  levelCode: string
  lessons: Lesson[]
  sections: Section[]
  activities: Activity[]
  items: Item[]
  dictionary: DictionaryEntry[]
}

export const getBundle = (level: string) => request<Bundle>(`/v1/content/${level}`)

// --- زنجیره

/**
 * زنجیره — نام میدان‌ها عیناً همان چیزی است که سرور می‌دهد
 * (`lib/streak.ts`). حدس زدنشان باعث شده بود شمارنده همیشه صفر بماند
 * بی‌آنکه خطایی دیده شود.
 */
export type Streak = {
  currentLength: number
  longestLength: number
  lastActiveDate: string | null
  freezeCount: number
  /** فقط در پاسخ خودِ سرور می‌آید؛ برای مقایسه با lastActiveDate */
  today?: string
}

export const getStreak = () => request<Streak>('/v1/streak')

/** آیا امروز فعالیتی ثبت شده — سرور تاریخ را به وقت اپ می‌دهد */
export const isTodayDone = (s: Streak | null) =>
  !!s?.today && s.lastActiveDate === s.today

// --- اشتراک

export type Plan = {
  code: string
  sku: string
  title: string
  days: number
  priceRial: number
  badge: string | null
  note: string | null
}

export const getPlans = () => request<{ plans: Plan[] }>('/v1/plans')

/** نشانی تصویر یک آیتم؛ تصاویر را سرور بک‌اند سرو می‌کند نه وب‌اپ */
export const imageUrl = (file: string) => `/images/${file.replace(/\.png$/, '.webp')}`

// --- همگام‌سازی

import type { AnswerRow, LeitnerRow, ProgressRow } from './store'

export type SyncPayload = {
  /**
   * مرزِ زمانی آخرین همگام‌سازی — همان عددی که سرور در پاسخ قبلی داد.
   * سرور آن را بر حسب ثانیه می‌دهد، در حالی که `updatedAt` سطرها بر حسب
   * میلی‌ثانیه است (اپ اندروید هم همین‌طور رفتار می‌کند).
   */
  since: number
  progress?: ProgressRow[]
  leitner?: LeitnerRow[]
  answers?: AnswerRow[]
}

export type SyncResult = {
  now: number
  progress: ProgressRow[]
  leitner: LeitnerRow[]
  applied: Record<string, number>
  streak: Streak
}

export const sync = (body: SyncPayload) =>
  request<SyncResult>('/v1/sync', { method: 'POST', body: JSON.stringify(body) })

/**
 * ثبت فعالیت امروز.
 *
 * ⚠️ پاسخ این اندپوینت **تخت** است، نه پیچیده‌شده در `{streak}` — برخلاف
 * ‏`/v1/sync` که میدان `streak` دارد. یک‌بار همین را اشتباه فرض کردم و
 * شمارنده زنجیره بی‌آنکه خطایی بدهد صفر ماند.
 */
export type CheckInResult = Streak & {
  /** چه اتفاقی افتاد: ثبت عادی، استفاده از فریز، ترمیم، یا شکستن */
  result: string
}

export const checkIn = () =>
  request<CheckInResult>('/v1/streak/check-in', { method: 'POST', body: '{}' })

export type Stats = {
  productiveAccuracy: number | null
  [k: string]: unknown
}

export const getStats = (since = 0) => request<Stats>(`/v1/stats?since=${since}`)

// --- لیگ

export type LeagueRow = {
  rank: number
  userId: number
  name: string
  xp: number
  isMe: boolean
  /** کاربر نمایشی — جدولِ خالی در هفته اول انگیزه‌ای نمی‌سازد */
  isDemo: boolean
}

export type LeagueStandings = {
  tier: number
  tierName: string
  nextTierName: string | null
  cohort: number
  weekStart: string
  /** ثانیه — پایان هفته لیگ */
  endsAt: number
  promoteCount: number
  relegateCount: number
  rows: LeagueRow[]
}

export const getLeague = () => request<LeagueStandings>('/v1/league')
