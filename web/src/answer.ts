/**
 * تصحیح پاسخ کاربر — ترجمه مستقیم domain/AnswerChecker.kt.
 *
 * اصل طراحی: کاربر نباید بابت تفاوت‌های بی‌اهمیت جریمه شود.
 * "I'm Ali" ، "I'm Ali." و "i'm ali" هر سه درست‌اند.
 * اما "I am Ali" وقتی سوال «مخفف کن» است، نادرست است — چون آن نکته
 * آموزشی درس است.
 *
 * ⚠️ این قواعد باید با نسخه اندروید یکی بمانند. اگر یک نسخه پاسخی را
 * بپذیرد که دیگری رد می‌کند، همان کاربر روی دو دستگاه نتیجه متفاوت
 * می‌گیرد و هیچ توضیحی هم برایش ندارد.
 */

/** انواع آپاستروف که کیبوردهای مختلف تولید می‌کنند */
const APOSTROPHES = /[’ʼ´`]/g

/** نشانه‌های پایانی که در مقایسه نادیده گرفته می‌شوند */
const TRAILING = /[.!?؟۔]+$/

export function normalize(raw: string): string {
  let s = raw.trim().toLowerCase()
  s = s.replace(APOSTROPHES, "'")
  s = s.replace(/\s+/g, ' ')
  s = s.replace(TRAILING, '').trim()
  // فاصله پیش از نشانه‌گذاری حذف می‌شود: "hello ,"  →  "hello,"
  s = s.replace(/\s+([,;:])/g, '$1')
  return s
}

export type Verdict = {
  correct: boolean
  expected: string
  userAnswer: string
}

/** پاسخ‌های جایگزین در محتوا با « | » جدا شده‌اند */
export const splitAlternatives = (alt: string | null | undefined): string[] =>
  (alt ?? '')
    .split('|')
    .map((x) => x.trim())
    .filter(Boolean)

export function check(
  expected: string | null | undefined,
  alternatives: string | null | undefined,
  userAnswer: string,
): Verdict {
  const exp = expected ?? ''
  // آیتمی که پاسخ درست ندارد (مثل فلش‌کارت) همیشه درست است
  if (!exp.trim()) return { correct: true, expected: '', userAnswer }

  const user = normalize(userAnswer)
  const accepted = [normalize(exp), ...splitAlternatives(alternatives).map(normalize)]

  return {
    correct: user.length > 0 && accepted.includes(user),
    expected: exp,
    userAnswer,
  }
}
