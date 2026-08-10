import assert from 'node:assert/strict'
import { test, describe } from 'node:test'
import { openDb } from '../src/db/index.js'
import {
  MAX_VERIFY_ATTEMPTS,
  discardCode,
  normalizePhone,
  requestCode,
  signToken,
  verifyCode,
  verifyToken,
} from '../src/lib/auth.js'

const db = () => openDb(':memory:')

describe('normalizePhone', () => {
  test('accepts a local Iranian number', () => {
    assert.equal(normalizePhone('09123456789'), '09123456789')
  })
  test('strips separators', () => {
    assert.equal(normalizePhone('0912 345 6789'), '09123456789')
  })
  test('converts international form to local', () => {
    assert.equal(normalizePhone('989123456789'), '09123456789')
  })
  test('rejects wrong prefix or length', () => {
    assert.equal(normalizePhone('08123456789'), null)
    assert.equal(normalizePhone('0912345678'), null)
    assert.equal(normalizePhone(''), null)
  })
})

describe('requestCode', () => {
  test('issues a code of the expected length', () => {
    const d = db()
    const r = requestCode(d, '09123456789')
    assert.equal(r.ok, true)
    if (r.ok) assert.match(r.code, /^\d{5}$/)
  })

  test('rejects an invalid phone', () => {
    const r = requestCode(db(), '12345')
    assert.equal(r.ok, false)
    if (!r.ok) assert.equal(r.error, 'invalid_phone')
  })

  test('blocks a second request inside the cooldown', () => {
    const d = db()
    requestCode(d, '09123456789')
    const second = requestCode(d, '09123456789')
    assert.equal(second.ok, false)
    if (!second.ok) assert.equal(second.error, 'too_soon')
  })

  /**
   * فاصله ۸۰ ثانیه به‌تنهایی هزینه را مهار نمی‌کند: با همان فاصله هم
   * می‌شود شبانه‌روز صدها پیامک روی یک شماره فرستاد.
   */
  test('caps the number of codes per phone per day', () => {
    const d = db()
    const phone = '09123456789'
    // فاصله زمانی را می‌سازیم تا محدودیت ۸۰ ثانیه دخالت نکند
    const older = (n: number) =>
      d.prepare(
        `INSERT INTO otp_codes (phone, code_hash, expires_at, created_at)
         VALUES (?, 'x', 0, strftime('%s','now') - ?)`,
      ).run(phone, n)

    for (let i = 0; i < 3; i++) older(1000 * (i + 1))
    const blocked = requestCode(d, phone, 3)
    assert.equal(blocked.ok, false)
    if (!blocked.ok) assert.equal(blocked.error, 'daily_limit')

    // شماره دیگری نباید تحت تأثیر باشد
    assert.equal(requestCode(d, '09121110000', 3).ok, true)
  })

  test('codes older than a day do not count toward the cap', () => {
    const d = db()
    const phone = '09123456789'
    for (let i = 0; i < 5; i++) {
      d.prepare(
        `INSERT INTO otp_codes (phone, code_hash, expires_at, created_at)
         VALUES (?, 'x', 0, strftime('%s','now') - 90000)`,
      ).run(phone)
    }
    assert.equal(requestCode(d, phone, 3).ok, true)
  })

  test('a discarded code frees the cooldown and the daily quota', () => {
    // قطعی سرویس پیامک نباید کاربر را جریمه کند
    const d = db()
    const first = requestCode(d, '09123456789', 2)
    assert.equal(first.ok, true)
    if (!first.ok) return

    discardCode(d, first.codeId)

    const retry = requestCode(d, '09123456789', 2)
    assert.equal(retry.ok, true)
  })

  test('never stores the raw code', () => {
    const d = db()
    const r = requestCode(d, '09123456789')
    assert.equal(r.ok, true)
    if (!r.ok) return
    const row = d.prepare('SELECT code_hash FROM otp_codes LIMIT 1').get() as { code_hash: string }
    assert.notEqual(row.code_hash, r.code)
    assert.match(row.code_hash, /^[0-9a-f]{64}$/)
  })
})

describe('verifyCode', () => {
  test('accepts the correct code and creates the user', () => {
    const d = db()
    const req = requestCode(d, '09123456789')
    assert.equal(req.ok, true)
    if (!req.ok) return

    const v = verifyCode(d, '09123456789', req.code)
    assert.equal(v.ok, true)
    if (!v.ok) return
    assert.equal(v.isNew, true)
    assert.match(v.referralCode, /^[A-Z2-9]{6}$/)
  })

  test('returning user keeps the same id and referral code', () => {
    const d = db()
    const first = requestCode(d, '09123456789')
    if (!first.ok) throw new Error('setup')
    const a = verifyCode(d, '09123456789', first.code)
    if (!a.ok) throw new Error('setup')

    // شبیه‌سازی گذشت زمان تا محدودیت ارسال دوباره رد شود
    d.prepare('UPDATE otp_codes SET created_at = created_at - 999').run()
    const second = requestCode(d, '09123456789')
    if (!second.ok) throw new Error('setup')
    const b = verifyCode(d, '09123456789', second.code)
    if (!b.ok) throw new Error('setup')

    assert.equal(b.userId, a.userId)
    assert.equal(b.referralCode, a.referralCode)
    assert.equal(b.isNew, false)
  })

  test('rejects a wrong code', () => {
    const d = db()
    const r = requestCode(d, '09123456789')
    if (!r.ok) throw new Error('setup')
    const wrong = r.code === '00000' ? '11111' : '00000'
    const v = verifyCode(d, '09123456789', wrong)
    assert.equal(v.ok, false)
    if (!v.ok) assert.equal(v.error, 'wrong_code')
  })

  test('a code cannot be used twice', () => {
    const d = db()
    const r = requestCode(d, '09123456789')
    if (!r.ok) throw new Error('setup')
    assert.equal(verifyCode(d, '09123456789', r.code).ok, true)

    const again = verifyCode(d, '09123456789', r.code)
    assert.equal(again.ok, false)
    if (!again.ok) assert.equal(again.error, 'no_code')
  })

  test('locks out after too many wrong attempts', () => {
    const d = db()
    const r = requestCode(d, '09123456789')
    if (!r.ok) throw new Error('setup')
    const wrong = r.code === '00000' ? '11111' : '00000'

    for (let i = 0; i < MAX_VERIFY_ATTEMPTS; i++) verifyCode(d, '09123456789', wrong)
    const locked = verifyCode(d, '09123456789', wrong)
    assert.equal(locked.ok, false)
    if (!locked.ok) assert.equal(locked.error, 'too_many_attempts')
  })

  test('an expired code is refused', () => {
    const d = db()
    const r = requestCode(d, '09123456789')
    if (!r.ok) throw new Error('setup')
    d.prepare('UPDATE otp_codes SET expires_at = expires_at - 9999').run()

    const v = verifyCode(d, '09123456789', r.code)
    assert.equal(v.ok, false)
    if (!v.ok) assert.equal(v.error, 'expired')
  })

  test('a code issued for one phone does not work for another', () => {
    const d = db()
    const r = requestCode(d, '09123456789')
    if (!r.ok) throw new Error('setup')
    const v = verifyCode(d, '09120000000', r.code)
    assert.equal(v.ok, false)
  })
})

describe('token', () => {
  const secret = 'a'.repeat(40)

  test('round-trips the payload', () => {
    const t = signToken(secret, { sub: 7, phone: '09123456789' })
    assert.deepEqual(verifyToken(secret, t), { sub: 7, phone: '09123456789' })
  })

  test('rejects a token signed with another secret', () => {
    const t = signToken('b'.repeat(40), { sub: 7, phone: '09123456789' })
    assert.equal(verifyToken(secret, t), null)
  })

  test('rejects garbage', () => {
    assert.equal(verifyToken(secret, 'not-a-token'), null)
  })
})
