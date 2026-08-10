/**
 * زمان معتبر — تنها مرجع تاریخ برای زنجیره مطالعه.
 *
 * زنجیره تنها چیزی در محصول است که ارزش تقلب دارد: کاربر می‌تواند ساعت
 * گوشی را جلو ببرد و زنجیره بسازد. رقیب برای همین NTP اختصاصی دارد
 * (سند ۰۵ بخش ۵.۲). بنابراین کلاینت هرگز تاریخ نمی‌فرستد و هرگز طول
 * زنجیره را اعلام نمی‌کند؛ فقط می‌گوید «الان فعالیت کردم».
 *
 * زمان سرور با NTP سیستم‌عامل همگام می‌ماند؛ مسئولیت آن با زیرساخت است،
 * نه کد برنامه. اگر بعداً به دقت بیشتری نیاز بود، اینجا تنها جایی است
 * که باید عوض شود.
 */

/** منطقه زمانی محصول — همه محاسبات روز بر همین مبناست */
export const APP_TIMEZONE = 'Asia/Tehran'

/** تاریخ امروز به وقت تهران، به شکل yyyy-mm-dd */
export function todayInAppTz(at: Date = new Date()): string {
  // en-CA خروجی ISO می‌دهد: 2026-08-05
  return new Intl.DateTimeFormat('en-CA', {
    timeZone: APP_TIMEZONE,
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(at)
}

/** تعداد روز اختلاف بین دو تاریخ yyyy-mm-dd */
export function daysBetween(from: string, to: string): number {
  const a = Date.parse(`${from}T00:00:00Z`)
  const b = Date.parse(`${to}T00:00:00Z`)
  if (Number.isNaN(a) || Number.isNaN(b)) return 0
  return Math.round((b - a) / 86_400_000)
}

/** تاریخ yyyy-mm-dd با جابه‌جایی چند روز */
export function shiftDays(date: string, delta: number): string {
  const t = Date.parse(`${date}T00:00:00Z`)
  return new Date(t + delta * 86_400_000).toISOString().slice(0, 10)
}
