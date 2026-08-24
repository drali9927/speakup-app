/**
 * تلفظ متن انگلیسی — معادل SpeechService اپ.
 *
 * از موتور خودِ مرورگر استفاده می‌کند، پس نه فایل صوتی لازم دارد نه
 * ترافیک. کیفیتش به سیستم‌عامل کاربر بستگی دارد و همیشه عالی نیست، اما
 * جایگزینش — ساخت و میزبانی هزاران فایل صوتی — هم هزینه دارد هم حجم.
 *
 * اگر مرورگر پشتیبانی نکند، بی‌صدا رد می‌شود: تمرین بدون صدا هنوز قابل
 * انجام است، ولی خطای بی‌دلیل کاربر را گیج می‌کند.
 */

let voice: SpeechSynthesisVoice | null = null

/** صدای انگلیسی را یک بار پیدا و نگه می‌دارد */
function pickVoice(): SpeechSynthesisVoice | null {
  if (voice) return voice
  const all = window.speechSynthesis?.getVoices() ?? []
  voice =
    all.find((v) => v.lang === 'en-GB') ??
    all.find((v) => v.lang.startsWith('en')) ??
    null
  return voice
}

export function speak(text: string) {
  if (!text || !('speechSynthesis' in window)) return
  try {
    // پخش قبلی قطع می‌شود، وگرنه با زدن پشت‌سرهم صداها روی هم می‌افتند
    window.speechSynthesis.cancel()
    const u = new SpeechSynthesisUtterance(text)
    u.lang = 'en-GB'
    const v = pickVoice()
    if (v) u.voice = v
    // کمی کندتر از عادی — زبان‌آموز باید واژه‌ها را از هم تشخیص دهد
    u.rate = 0.9
    window.speechSynthesis.speak(u)
  } catch {
    /* مرورگر پشتیبانی نمی‌کند — تمرین بدون صدا ادامه پیدا می‌کند */
  }
}

// فهرست صداها در بعضی مرورگرها با تأخیر پر می‌شود
if (typeof window !== 'undefined' && 'speechSynthesis' in window) {
  window.speechSynthesis.onvoiceschanged = () => {
    voice = null
  }
}
