import assert from 'node:assert/strict'
import { test, describe } from 'node:test'
import { openDb, type Db } from '../src/db/index.js'
import { bulkCreateUsers, usersCsv, users as adminUsers } from '../src/lib/admin.js'

function db(): Db {
  return openDb(':memory:')
}

const phones = (d: Db): string[] =>
  (d.prepare(`SELECT phone FROM users ORDER BY phone`).all() as { phone: string }[]).map((r) => r.phone)

describe('ساخت گروهی کاربر', () => {
  test('شماره‌های ساده ساخته می‌شوند', () => {
    const d = db()
    const r = bulkCreateUsers(d, '09121234567\n09127654321')
    assert.equal(r.created, 2)
    assert.equal(r.errors, 0)
    assert.deepEqual(phones(d), ['09121234567', '09127654321'])
  })

  test('کاربر ساخته‌شده آخرین بازدید ندارد', () => {
    // اگر مهر زمان بخورد، همان لحظه در آمار «فعال امروز» می‌نشیند و
    // عدد فعال‌ها را به اندازه کل فایل باد می‌کند.
    const d = db()
    bulkCreateUsers(d, '09121234567')
    const row = d.prepare(`SELECT last_seen_at FROM users WHERE phone = ?`).get('09121234567') as
      { last_seen_at: number | null }
    assert.equal(row.last_seen_at, null)
  })

  test('اجرای دوباره همان فایل چیزی خراب نمی‌کند', () => {
    const d = db()
    bulkCreateUsers(d, '09121234567')
    const again = bulkCreateUsers(d, '09121234567')
    assert.equal(again.created, 0)
    assert.equal(again.existed, 1)
    assert.equal(again.errors, 0, 'تکراری نباید خطا باشد')
    assert.equal(phones(d).length, 1)
  })

  test('رقم فارسی پذیرفته می‌شود', () => {
    const d = db()
    const r = bulkCreateUsers(d, '۰۹۱۲۱۲۳۴۵۶۷')
    assert.equal(r.created, 1)
    assert.deepEqual(phones(d), ['09121234567'])
  })

  test('شکل بین‌المللی به شکل محلی برمی‌گردد', () => {
    const d = db()
    bulkCreateUsers(d, '989121234567')
    assert.deepEqual(phones(d), ['09121234567'])
  })

  test('خط خراب بقیه را نمی‌خواباند', () => {
    const d = db()
    const r = bulkCreateUsers(d, '09121234567\nسلام\n09127654321')
    assert.equal(r.created, 2)
    assert.equal(r.errors, 1)
    assert.equal(r.rows.find((x) => x.status === 'error')?.line, 2)
  })

  test('سطر عنوان اکسل رد می‌شود', () => {
    const d = db()
    const r = bulkCreateUsers(d, 'phone,plan\n09121234567,yearly')
    assert.equal(r.created, 1)
    assert.equal(r.errors, 0)
  })

  test('اشتراک هم‌زمان ثبت می‌شود', () => {
    const d = db()
    const r = bulkCreateUsers(d, '09121234567,yearly')
    assert.equal(r.created, 1)
    assert.equal(r.granted, 1)
    const sub = d.prepare(`SELECT plan_code, gateway FROM subscriptions`).get() as
      { plan_code: string; gateway: string }
    assert.equal(sub.plan_code, 'yearly')
    assert.equal(sub.gateway, 'manual')
  })

  test('تعداد روز دلخواه اعمال می‌شود', () => {
    const d = db()
    const r = bulkCreateUsers(d, '09121234567,manual,45')
    assert.equal(r.granted, 1)
    assert.match(r.rows[0]!.detail ?? '', /45/)
  })

  test('سلول‌های به‌سبک اکسل هم خوانده می‌شوند', () => {
    // اکسل هنگام خروجی، شماره را به شکل ="0912..." نگه می‌دارد
    const d = db()
    const r = bulkCreateUsers(d, '="09121234567"')
    assert.equal(r.created, 1)
    assert.deepEqual(phones(d), ['09121234567'])
  })

  test('خط خالی نادیده گرفته می‌شود', () => {
    const d = db()
    const r = bulkCreateUsers(d, '09121234567\n\n\n09127654321\n')
    assert.equal(r.created, 2)
    assert.equal(r.errors, 0)
  })

  test('هر کاربر کد معرف یکتا می‌گیرد', () => {
    const d = db()
    bulkCreateUsers(d, '09121234567\n09127654321\n09121112233')
    const n = (d.prepare(`SELECT COUNT(DISTINCT referral_code) n FROM users`).get() as { n: number }).n
    assert.equal(n, 3)
  })

  test('کاربر ساخته‌شده در فهرست پنل دیده می‌شود', () => {
    const d = db()
    bulkCreateUsers(d, '09121234567')
    assert.equal(adminUsers(d, '', 20, 0).total, 1)
  })
})

describe('خروجی CSV', () => {
  test('با BOM شروع می‌شود تا اکسل فارسی را درست بخواند', () => {
    const d = db()
    bulkCreateUsers(d, '09121234567')
    assert.ok(usersCsv(d).startsWith('﻿'))
  })

  test('شماره به شکل متن می‌ماند تا صفر اولش نپرد', () => {
    const d = db()
    bulkCreateUsers(d, '09121234567')
    assert.ok(usersCsv(d).includes('="09121234567"'))
  })

  test('یک ردیف عنوان و یک ردیف به ازای هر کاربر', () => {
    const d = db()
    bulkCreateUsers(d, '09121234567\n09127654321')
    const lines = usersCsv(d).trim().split('\r\n')
    assert.equal(lines.length, 3)
  })

  test('کاربر نمایشی در خروجی نمی‌آید', () => {
    const d = db()
    bulkCreateUsers(d, '09121234567')
    d.prepare(`INSERT INTO users (phone, referral_code, created_at, is_demo) VALUES (?, ?, ?, 1)`)
      .run('09129999999', 'DEMO01', 0)
    const csv = usersCsv(d)
    assert.ok(!csv.includes('09129999999'), 'کاربر نمایشی نباید در خروجی باشد')
  })
})

describe('کاربر نمایشی', () => {
  test('ورود گروهی به حساب نمایشی دست نمی‌زند', () => {
    // اگر در سکوت اشتراک روی حساب قلابی بنشیند، کاربر واقعی اشتراکش را
    // نمی‌گیرد و هیچ‌کس هم نمی‌فهمد چرا.
    const d = db()
    d.prepare(`INSERT INTO users (phone, referral_code, created_at, is_demo) VALUES (?, ?, ?, 1)`)
      .run('09001234567', 'DEMO02', 0)
    const r = bulkCreateUsers(d, '09001234567,yearly')
    assert.equal(r.errors, 1)
    assert.equal(r.granted, 0)
    assert.equal(r.created, 0)
    assert.equal(d.prepare(`SELECT COUNT(*) n FROM subscriptions`).get<{ n: number }>()?.n ?? 0, 0)
  })
})
