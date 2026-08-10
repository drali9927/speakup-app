/**
 * ارسال پیامک کد ورود.
 *
 * پشت یک واسط ساده نگه داشته شده چون سرویس پیامک در ایران چیزی است که
 * عوض می‌شود: قطعی، تغییر تعرفه، مسدود شدن خط. تعویض ارائه‌دهنده باید
 * یک فایل باشد، نه دست بردن در مسیر احراز هویت.
 *
 * کلید API فقط از متغیر محیطی می‌آید. رقیب کلیدهایش را داخل APK گذاشته
 * بود (سند ۰۵ بخش ۵.۴)؛ کلید پیامک از آن هم حساس‌تر است چون مستقیم پول
 * خرج می‌کند.
 */

export type SmsResult = { ok: true } | { ok: false; error: string }

export interface SmsSender {
  readonly name: string
  send(phone: string, code: string): Promise<SmsResult>
}

/**
 * توسعه: کد را در ترمینال چاپ می‌کند.
 * `loadConfig` اجازه نمی‌دهد این در تولید فعال شود.
 */
export const consoleSender: SmsSender = {
  name: 'console',
  async send(phone, code) {
    console.log(`[sms:console] ${phone} → ${code}`)
    return { ok: true }
  },
}

/** تولید بدون تنظیم ارائه‌دهنده نباید بی‌صدا رد شود */
export const nullSender: SmsSender = {
  name: 'null',
  async send() {
    return { ok: false, error: 'sms_not_configured' }
  },
}

export type KavenegarOptions = {
  apiKey: string
  /** نام الگوی تأییدشده در پنل کاوه‌نگار */
  template: string
  /** برای تست تزریق می‌شود */
  fetchImpl?: typeof fetch
  timeoutMs?: number
}

/**
 * کاوه‌نگار، اندپوینت verify/lookup.
 *
 * چرا lookup و نه ارسال معمولی: در ایران متن‌های حاوی کد یک‌بارمصرف روی
 * خطوط خدماتی عادی فیلتر می‌شوند. lookup الگوی از پیش تأییدشده را
 * می‌فرستد و همان مسیری است که برای OTP در دسترس است.
 *
 * نکته مهم برای تیم: متن الگو باید به هش ۱۱ کاراکتری اپ ختم شود، وگرنه
 * SMS Retriever اندروید کد را خودکار پر نمی‌کند و کاربر باید دستی تایپ کند.
 */
export function kavenegarSender(opts: KavenegarOptions): SmsSender {
  const doFetch = opts.fetchImpl ?? fetch
  const timeoutMs = opts.timeoutMs ?? 8000

  return {
    name: 'kavenegar',
    async send(phone, code) {
      // کلید داخل مسیر URL است، پس هرگز نباید URL کامل لاگ شود
      const url =
        `https://api.kavenegar.com/v1/${encodeURIComponent(opts.apiKey)}/verify/lookup.json` +
        `?receptor=${encodeURIComponent(phone)}` +
        `&token=${encodeURIComponent(code)}` +
        `&template=${encodeURIComponent(opts.template)}`

      const controller = new AbortController()
      const timer = setTimeout(() => controller.abort(), timeoutMs)
      try {
        const res = await doFetch(url, { method: 'GET', signal: controller.signal })
        const body = (await res.json().catch(() => null)) as
          | { return?: { status?: number; message?: string } }
          | null

        // کاوه‌نگار حتی روی خطا هم گاهی HTTP 200 می‌دهد؛ ملاک، status داخل بدنه است
        const status = body?.return?.status
        if (res.ok && status === 200) return { ok: true }
        return { ok: false, error: `kavenegar_${status ?? res.status}` }
      } catch (e) {
        const aborted = (e as { name?: string })?.name === 'AbortError'
        return { ok: false, error: aborted ? 'sms_timeout' : 'sms_unreachable' }
      } finally {
        clearTimeout(timer)
      }
    },
  }
}

export type SmsConfig = {
  provider: 'console' | 'kavenegar' | 'none'
  kavenegarApiKey: string
  kavenegarTemplate: string
}

export function createSmsSender(cfg: SmsConfig): SmsSender {
  switch (cfg.provider) {
    case 'console':
      return consoleSender
    case 'kavenegar':
      return kavenegarSender({ apiKey: cfg.kavenegarApiKey, template: cfg.kavenegarTemplate })
    default:
      return nullSender
  }
}
