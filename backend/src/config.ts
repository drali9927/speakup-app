/**
 * پیکربندی از متغیرهای محیطی.
 *
 * هیچ رازی در کد نیست. رقیب کلید Firebase و OneSignal را داخل APK
 * گذاشته بود و هر کسی می‌توانست استخراجشان کند (سند ۰۵ بخش ۵.۴) —
 * همان اشتباه را تکرار نمی‌کنیم.
 */
import type { SmsConfig } from './lib/sms.js'

export type Config = {
  port: number
  dbPath: string
  jwtSecret: string
  /** در توسعه، کد ورود در پاسخ برگردانده می‌شود تا بدون پیامک بتوان تست کرد */
  exposeOtpInResponse: boolean
  /**
   * شماره‌هایی که بدون پیامک وارد می‌شوند — فقط برای تست دستی.
   *
   * ⚠️ در production این فهرست باید خالی باشد و اگر نباشد سرور بالا
   * نمی‌آید. یک شماره در این فهرست یعنی هرکسی که آن شماره را بداند
   * می‌تواند بدون کد وارد آن حساب شود.
   */
  testPhones: string[]
  env: 'development' | 'production'
  contentDir: string
  imagesDir: string
  /** لاگ یک‌خطی هر درخواست؛ با REQUEST_LOG=0 خاموش می‌شود */
  requestLog: boolean
  sms: SmsConfig
  /** سقف پیامک روزانه برای هر شماره — سد هزینه و سوءاستفاده */
  smsDailyLimitPerPhone: number
  bazaar: {
    verifyReceipts: boolean
    packageName: string
    accessToken: string
  }
}

export function loadConfig(env = process.env): Config {
  const mode = env.NODE_ENV === 'production' ? 'production' : 'development'
  const secret = env.JWT_SECRET ?? ''
  // در توسعه پیش‌فرض console است تا بدون حساب پیامک بتوان کار کرد
  const smsProvider = (env.SMS_PROVIDER ?? (mode === 'production' ? 'none' : 'console')) as
    | 'console'
    | 'kavenegar'
    | 'none'

  const testPhones = (env.TEST_PHONES ?? '').split(',').map(x => x.trim()).filter(Boolean)

  if (mode === 'production') {
    // یک شماره در این فهرست یعنی ورود بدون کد. اگر با تنظیمات توسعه
    // به production منتقل شود، هرکسی که شماره را بداند وارد آن حساب
    // می‌شود — پس سرور اصلاً بالا نمی‌آید.
    if (testPhones.length > 0) {
      throw new Error('TEST_PHONES must be empty in production')
    }
    // در تولید، راز باید صریح تنظیم شود — نه پیش‌فرض، نه کوتاه
    if (secret.length < 32) {
      throw new Error('JWT_SECRET must be set to at least 32 characters in production')
    }
    if (env.EXPOSE_OTP === '1') {
      throw new Error('EXPOSE_OTP must never be enabled in production')
    }
    // بدون این بررسی، یک متغیر محیطی جاافتاده یعنی سروری که بالا می‌آید،
    // کد می‌سازد، و هیچ‌کس پیامکی دریافت نمی‌کند — خرابی‌ای که فقط از
    // شکایت کاربر فهمیده می‌شود. بهتر است همان لحظه بالا نیاید.
    if (smsProvider !== 'kavenegar') {
      throw new Error('SMS_PROVIDER must be a real provider in production')
    }
    if (!env.KAVENEGAR_API_KEY || !env.KAVENEGAR_TEMPLATE) {
      throw new Error('KAVENEGAR_API_KEY and KAVENEGAR_TEMPLATE are required in production')
    }
  }

  return {
    port: Number(env.PORT ?? 8080),
    dbPath: env.DB_PATH ?? 'speakup.db',
    jwtSecret: secret || 'dev-only-insecure-secret-do-not-use-in-production',
    exposeOtpInResponse: mode !== 'production' && env.EXPOSE_OTP !== '0',
    testPhones,
    env: mode,
    contentDir: env.CONTENT_DIR ?? 'content',
    imagesDir: env.IMAGES_DIR ?? 'content/images',
    requestLog: env.REQUEST_LOG !== '0',
    sms: {
      provider: smsProvider,
      kavenegarApiKey: env.KAVENEGAR_API_KEY ?? '',
      kavenegarTemplate: env.KAVENEGAR_TEMPLATE ?? '',
    },
    smsDailyLimitPerPhone: Number(env.SMS_DAILY_LIMIT ?? 10),
    bazaar: {
      verifyReceipts: Boolean(env.BAZAAR_ACCESS_TOKEN),
      packageName: env.BAZAAR_PACKAGE ?? 'ir.speakup.app',
      accessToken: env.BAZAAR_ACCESS_TOKEN ?? '',
    },
  }
}
