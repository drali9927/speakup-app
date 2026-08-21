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

/** پیام‌های خطای سرور به فارسی — کاربر نباید کد انگلیسی ببیند */
const MESSAGES: Record<string, string> = {
  invalid_code: 'کد وارد‌شده درست نیست',
  code_expired: 'کد منقضی شده — دوباره درخواست بده',
  too_many_requests: 'تعداد درخواست‌ها زیاد بود؛ کمی صبر کن',
  sms_daily_limit: 'امروز چند بار کد خواستی. فردا دوباره امتحان کن',
  unauthorized: 'باید دوباره وارد شوی',
  invalid_phone: 'شماره موبایل درست نیست',
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

export type Streak = {
  current: number
  longest: number
  todayDone: boolean
}

export const getStreak = () => request<Streak>('/v1/streak')

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
