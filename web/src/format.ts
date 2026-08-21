/**
 * ارقام فارسی — معادل toPersianDigits در اپ اندروید.
 *
 * عدد لاتین در میان متن فارسی ناجور دیده می‌شود. اپ همه‌جا این کار را
 * می‌کند، پس وب هم باید بکند وگرنه دو نسخه یکسان به نظر نمی‌رسند.
 */
const FA = ['۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹']

export const toPersianDigits = (s: string | number) =>
  String(s).replace(/\d/g, (d) => FA[Number(d)])

/** قیمت ریالی سرور را به تومانِ خوانا تبدیل می‌کند */
export const toToman = (rial: number) =>
  toPersianDigits(Math.round(rial / 10).toLocaleString('en-US'))
