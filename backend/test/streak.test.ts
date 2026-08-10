import assert from 'node:assert/strict'
import { test, describe } from 'node:test'
import { openDb, type Db } from '../src/db/index.js'
import { MAX_FREEZES, checkIn, earnFreeze, getStreak } from '../src/lib/streak.js'
import { daysBetween, shiftDays, todayInAppTz } from '../src/lib/time.js'

function setup(): { db: Db; userId: number } {
  const db = openDb(':memory:')
  const info = db
    .prepare(`INSERT INTO users (phone, referral_code, created_at) VALUES (?, ?, ?)`)
    .run('09123456789', 'ABC234', 0)
  return { db, userId: Number(info.lastInsertRowid) }
}

/** عقب بردن آخرین فعالیت، برای شبیه‌سازی گذشت روز بدون دست‌کاری ساعت */
function backdate(db: Db, userId: number, days: number) {
  db.prepare(`UPDATE streaks SET last_active_date = ? WHERE user_id = ?`)
    .run(shiftDays(todayInAppTz(), -days), userId)
}

describe('time helpers', () => {
  test('daysBetween counts whole days', () => {
    assert.equal(daysBetween('2026-08-04', '2026-08-05'), 1)
    assert.equal(daysBetween('2026-08-04', '2026-08-04'), 0)
    assert.equal(daysBetween('2026-07-31', '2026-08-01'), 1)
  })

  test('shiftDays crosses month boundaries', () => {
    assert.equal(shiftDays('2026-08-01', -1), '2026-07-31')
    assert.equal(shiftDays('2026-02-28', 1), '2026-03-01')
  })

  test('today is an ISO date', () => {
    assert.match(todayInAppTz(), /^\d{4}-\d{2}-\d{2}$/)
  })
})

describe('checkIn', () => {
  test('first ever check-in starts the streak at one', () => {
    const { db, userId } = setup()
    const r = checkIn(db, userId)
    assert.equal(r.kind, 'extended')
    assert.equal(r.state.currentLength, 1)
  })

  test('a second check-in on the same day changes nothing', () => {
    const { db, userId } = setup()
    checkIn(db, userId)
    const again = checkIn(db, userId)
    assert.equal(again.kind, 'already_today')
    assert.equal(again.state.currentLength, 1)
  })

  test('consecutive days extend the streak', () => {
    const { db, userId } = setup()
    checkIn(db, userId)
    backdate(db, userId, 1)
    const r = checkIn(db, userId)
    assert.equal(r.kind, 'extended')
    assert.equal(r.state.currentLength, 2)
  })

  test('one missed day without a freeze breaks the streak', () => {
    const { db, userId } = setup()
    checkIn(db, userId)
    backdate(db, userId, 2)
    const r = checkIn(db, userId)
    assert.equal(r.kind, 'broken')
    assert.equal(r.state.currentLength, 1)
  })

  test('one missed day is covered by one freeze', () => {
    const { db, userId } = setup()
    checkIn(db, userId)
    earnFreeze(db, userId)
    backdate(db, userId, 2)

    const r = checkIn(db, userId)
    assert.equal(r.kind, 'frozen')
    assert.equal(r.state.currentLength, 2)
    assert.equal(r.state.freezeCount, 0, 'the freeze is consumed')
  })

  test('two missed days with only one freeze still breaks', () => {
    const { db, userId } = setup()
    checkIn(db, userId)
    earnFreeze(db, userId)
    backdate(db, userId, 3)

    const r = checkIn(db, userId)
    assert.equal(r.kind, 'broken')
    assert.equal(r.state.currentLength, 1)
  })

  test('three missed days are covered by three freezes', () => {
    const { db, userId } = setup()
    checkIn(db, userId)
    for (let i = 0; i < 3; i++) earnFreeze(db, userId)
    backdate(db, userId, 4)

    const r = checkIn(db, userId)
    assert.equal(r.kind, 'frozen')
    assert.equal(r.state.currentLength, 2)
    assert.equal(r.state.freezeCount, 0)
  })

  test('the longest streak is remembered after a break', () => {
    const { db, userId } = setup()
    checkIn(db, userId)
    backdate(db, userId, 1); checkIn(db, userId)
    backdate(db, userId, 1); checkIn(db, userId)
    assert.equal(getStreak(db, userId).currentLength, 3)

    backdate(db, userId, 5)
    const broken = checkIn(db, userId)
    assert.equal(broken.state.currentLength, 1)
    assert.equal(broken.state.longestLength, 3, 'the record survives')
  })

  test('a backwards clock does not break the streak', () => {
    const { db, userId } = setup()
    checkIn(db, userId)
    // آخرین فعالیت در آینده — یعنی ساعت سرور عقب رفته است
    db.prepare(`UPDATE streaks SET last_active_date = ? WHERE user_id = ?`)
      .run(shiftDays(todayInAppTz(), 2), userId)

    const r = checkIn(db, userId)
    assert.notEqual(r.kind, 'broken')
    assert.equal(r.state.currentLength, 2)
  })

  test('covered days are recorded as FROZEN', () => {
    const { db, userId } = setup()
    checkIn(db, userId)
    earnFreeze(db, userId)
    backdate(db, userId, 2)
    checkIn(db, userId)

    const frozen = db
      .prepare(`SELECT COUNT(*) AS n FROM streak_days WHERE user_id = ? AND status = 'FROZEN'`)
      .get(userId) as { n: number }
    assert.equal(frozen.n, 1)
  })
})

describe('earnFreeze', () => {
  test('stores up to the cap and refuses beyond it', () => {
    const { db, userId } = setup()
    for (let i = 0; i < MAX_FREEZES; i++) {
      assert.equal(earnFreeze(db, userId), true, `freeze ${i + 1} should be granted`)
    }
    assert.equal(earnFreeze(db, userId), false, 'the cap is enforced')
    assert.equal(getStreak(db, userId).freezeCount, MAX_FREEZES)
  })
})
