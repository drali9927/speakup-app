import assert from 'node:assert/strict'
import { test, describe } from 'node:test'
import { openDb, nowSec, type Db } from '../src/db/index.js'
import { record, funnel, retention } from '../src/lib/events.js'

const db = (): Db => openDb(':memory:')
const DAY = 86_400

const ev = (name: string, at = nowSec()) => ({ name, at })

describe('ثبت رویداد', () => {
  test('رویداد بدون کاربر هم ثبت می‌شود', () => {
    // قیف از نصب شروع می‌شود و آن‌جا هنوز حسابی وجود ندارد
    const d = db()
    assert.equal(record(d, 'inst-1', null, [ev('app_open')]), 1)
    const row = d.prepare(`SELECT user_id FROM events`).get() as { user_id: number | null }
    assert.equal(row.user_id, null)
  })

  test('props به شکل JSON ذخیره می‌شود', () => {
    const d = db()
    record(d, 'inst-1', null, [{ name: 'lesson_done', at: nowSec(), props: { lesson: 'a1_l01' } }])
    const row = d.prepare(`SELECT props FROM events`).get() as { props: string }
    assert.equal(JSON.parse(row.props).lesson, 'a1_l01')
  })

  test('props خیلی بزرگ دور ریخته می‌شود، ولی رویداد می‌ماند', () => {
    const d = db()
    record(d, 'inst-1', null, [{ name: 'x', at: nowSec(), props: { big: 'a'.repeat(5000) } }])
    const row = d.prepare(`SELECT name, props FROM events`).get() as
      { name: string; props: string | null }
    assert.equal(row.name, 'x')
    assert.equal(row.props, null, 'رویداد نباید به خاطر props بزرگ گم شود')
  })

  test('ساعت غلط دستگاه با ساعت سرور جایگزین می‌شود', () => {
    // گوشی‌هایی با ساعت ۱۹۷۰ یا ۲۰۹۹ کم نیستند؛ اگر همان ذخیره شود،
    // نمودار قیف برای همیشه خراب می‌ماند.
    const d = db()
    record(d, 'inst-1', null, [{ name: 'app_open', at: 0 }])
    record(d, 'inst-2', null, [{ name: 'app_open', at: nowSec() + 400 * DAY }])
    const rows = d.prepare(`SELECT at FROM events`).all() as { at: number }[]
    const t = nowSec()
    for (const r of rows) assert.ok(Math.abs(r.at - t) < 60, `at=${r.at} باید نزدیک حالا باشد`)
  })

  test('نام خالی ثبت نمی‌شود', () => {
    const d = db()
    assert.equal(record(d, 'inst-1', null, [{ name: '', at: nowSec() }]), 0)
  })
})

describe('قیف', () => {
  test('بر حسب دستگاه یکتا شمرده می‌شود، نه تعداد رویداد', () => {
    // اگر رویداد بشماریم، یک کاربر پرمصرف کل نرخ عبور را بی‌معنا می‌کند
    const d = db()
    record(d, 'inst-1', null, [ev('app_open'), ev('app_open'), ev('app_open')])
    record(d, 'inst-2', null, [ev('app_open')])
    const f = funnel(d, 30)
    assert.equal(f[0]!.installs, 2)
  })

  test('نرخ عبور نسبت به گام قبل حساب می‌شود', () => {
    const d = db()
    for (const i of [1, 2, 3, 4]) record(d, `i${i}`, null, [ev('app_open')])
    for (const i of [1, 2]) record(d, `i${i}`, null, [ev('onboarding_done')])
    for (const i of [1]) record(d, `i${i}`, null, [ev('auth_done')])
    const f = funnel(d, 30)
    assert.equal(f[1]!.installs, 2)
    assert.equal(f[1]!.pctOfPrev, 50, 'از ۴ نفر، ۲ نفر رد شدند')
    assert.equal(f[2]!.pctOfFirst, 25, 'نسبت به گام اول')
  })

  test('قیف خالی صفر می‌دهد و نمی‌شکند', () => {
    const f = funnel(db(), 30)
    assert.equal(f[0]!.installs, 0)
    assert.equal(f[0]!.pctOfPrev, 0)
  })

  test('رویداد قدیمی‌تر از بازه شمرده نمی‌شود', () => {
    const d = db()
    record(d, 'old', null, [ev('app_open', nowSec() - 60 * DAY)])
    record(d, 'new', null, [ev('app_open')])
    assert.equal(funnel(d, 30)[0]!.installs, 1)
  })
})

describe('نگهداشت', () => {
  test('بازگشت روز دوم شمرده می‌شود', () => {
    const d = db()
    const t0 = nowSec() - 10 * DAY
    record(d, 'back', null, [ev('app_open', t0), ev('app_open', t0 + DAY + 3600)])
    record(d, 'gone', null, [ev('app_open', t0)])
    const r = retention(d, 30)
    assert.equal(r.cohort, 2)
    assert.equal(r.d1, 1)
  })

  test('بازگشت روز هفتم شمرده می‌شود', () => {
    const d = db()
    const t0 = nowSec() - 20 * DAY
    record(d, 'back7', null, [ev('app_open', t0), ev('app_open', t0 + 7 * DAY)])
    assert.equal(retention(d, 30).d7, 1)
  })

  test('بازگشت در همان روز، بازگشت روز دوم حساب نمی‌شود', () => {
    const d = db()
    const t0 = nowSec() - 5 * DAY
    record(d, 'same', null, [ev('app_open', t0), ev('app_open', t0 + 3600)])
    assert.equal(retention(d, 30).d1, 0)
  })
})
