import assert from 'node:assert/strict'
import { test, describe } from 'node:test'
import { loadConfig } from '../src/config.js'

/**
 * محافظ‌های پیکربندی تولید.
 *
 * این تست‌ها یک اشتباه مشخص را می‌گیرند: راه‌اندازی تولید با راز پیش‌فرض
 * توسعه، یا با کد ورودی که در پاسخ برگردانده می‌شود. هر دو یعنی
 * هر کسی می‌تواند به هر حسابی وارد شود.
 */
describe('loadConfig in production', () => {
  // پیامک واقعی شرط راه‌اندازی تولید است، پس تست‌های دیگر باید تنظیمش کنند
  const base = {
    NODE_ENV: 'production',
    SMS_PROVIDER: 'kavenegar',
    KAVENEGAR_API_KEY: 'key',
    KAVENEGAR_TEMPLATE: 'speakup-otp',
  } as NodeJS.ProcessEnv

  test('refuses to start without a secret', () => {
    assert.throws(() => loadConfig({ ...base }), /JWT_SECRET/)
  })

  test('refuses a short secret', () => {
    assert.throws(() => loadConfig({ ...base, JWT_SECRET: 'short' }), /JWT_SECRET/)
  })

  test('refuses to expose the OTP', () => {
    assert.throws(
      () => loadConfig({ ...base, JWT_SECRET: 'x'.repeat(40), EXPOSE_OTP: '1' }),
      /EXPOSE_OTP/,
    )
  })

  test('accepts a proper secret and never exposes the code', () => {
    const c = loadConfig({ ...base, JWT_SECRET: 'x'.repeat(40) })
    assert.equal(c.env, 'production')
    assert.equal(c.exposeOtpInResponse, false)
    assert.equal(c.sms.provider, 'kavenegar')
  })

  // بدون این محافظ‌ها سرور بالا می‌آید، کد می‌سازد، و هیچ کاربری پیامکی
  // دریافت نمی‌کند — خرابی‌ای که فقط از شکایت کاربر فهمیده می‌شود.
  test('refuses to start without a real SMS provider', () => {
    const { SMS_PROVIDER: _drop, ...noProvider } = base as Record<string, string>
    assert.throws(
      () => loadConfig({ ...noProvider, JWT_SECRET: 'x'.repeat(40) } as NodeJS.ProcessEnv),
      /SMS_PROVIDER/,
    )
    assert.throws(
      () => loadConfig({ ...base, SMS_PROVIDER: 'console', JWT_SECRET: 'x'.repeat(40) }),
      /SMS_PROVIDER/,
    )
  })

  test('refuses to start without SMS credentials', () => {
    assert.throws(
      () => loadConfig({ ...base, KAVENEGAR_API_KEY: '', JWT_SECRET: 'x'.repeat(40) }),
      /KAVENEGAR/,
    )
    assert.throws(
      () => loadConfig({ ...base, KAVENEGAR_TEMPLATE: '', JWT_SECRET: 'x'.repeat(40) }),
      /KAVENEGAR/,
    )
  })
})

describe('loadConfig in development', () => {
  test('works with no environment at all', () => {
    const c = loadConfig({} as NodeJS.ProcessEnv)
    assert.equal(c.env, 'development')
    assert.equal(c.port, 8080)
  })

  test('exposes the code so login can be tested without SMS', () => {
    assert.equal(loadConfig({} as NodeJS.ProcessEnv).exposeOtpInResponse, true)
  })

  test('the code can still be hidden explicitly', () => {
    assert.equal(loadConfig({ EXPOSE_OTP: '0' } as NodeJS.ProcessEnv).exposeOtpInResponse, false)
  })
})
