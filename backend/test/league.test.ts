import assert from 'node:assert/strict'
import { test, describe } from 'node:test'
import { openDb, type Db } from '../src/db/index.js'
import {
  COHORT_SIZE, PROMOTE_COUNT, RELEGATE_COUNT, TIERS,
  addXp, ensureMember, standings, weekStart,
} from '../src/lib/league.js'

const MON1 = new Date('2026-08-03T10:00:00Z') // دوشنبه
const MON2 = new Date('2026-08-10T10:00:00Z') // دوشنبه بعد
const MON3 = new Date('2026-08-17T10:00:00Z')

function db(): Db {
  return openDb(':memory:')
}

function addUser(d: Db, n: number): number {
  const info = d
    .prepare(`INSERT INTO users (phone, referral_code, created_at) VALUES (?, ?, ?)`)
    .run(`0912000${String(n).padStart(4, '0')}`, `R${String(n).padStart(5, '0')}`, 0)
  return Number(info.lastInsertRowid)
}

/** یک گروه پر، با امتیازهای نزولی تا رتبه‌ها قطعی باشند */
function fillCohort(d: Db, at: Date): number[] {
  const ids: number[] = []
  for (let i = 0; i < COHORT_SIZE; i++) {
    const id = addUser(d, i + 1)
    ids.push(id)
    addXp(d, id, (COHORT_SIZE - i) * 10, at)
  }
  return ids
}

describe('دوره لیگ', () => {
  test('هفته از دوشنبه شروع می‌شود', () => {
    assert.equal(weekStart(new Date('2026-08-05T23:00:00Z')), '2026-08-03')
    assert.equal(weekStart(new Date('2026-08-03T00:00:00Z')), '2026-08-03')
    assert.equal(weekStart(new Date('2026-08-09T23:59:00Z')), '2026-08-03')
  })

  test('پایان دوره دقیقاً هفت روز بعد از شروع است', () => {
    const d = db()
    const me = addUser(d, 1)
    const s = standings(d, me, MON1)
    assert.equal(s.endsAt - Math.floor(Date.parse('2026-08-03T00:00:00Z') / 1000), 7 * 86_400)
  })
})

describe('صعود و سقوط', () => {
  test('نفر اول بالا می‌رود', () => {
    const d = db()
    const ids = fillCohort(d, MON1)
    assert.equal(standings(d, ids[0], MON1).rows[0].isMe, true)

    ensureMember(d, ids[0], MON2)
    assert.equal(standings(d, ids[0], MON2).tier, 1)
    assert.equal(standings(d, ids[0], MON2).tierName, TIERS[1])
  })

  test('نفر آخر پایین می‌آید، اما از برنز پایین‌تر نه', () => {
    const d = db()
    const ids = fillCohort(d, MON1)
    const last = ids[COHORT_SIZE - 1]
    ensureMember(d, last, MON2)
    // از قبل در پایین‌ترین رده بود، پس همان‌جا می‌ماند
    assert.equal(standings(d, last, MON2).tier, 0)
  })

  test('کسی که وسط جدول است، رده‌اش عوض نمی‌شود', () => {
    const d = db()
    const ids = fillCohort(d, MON1)
    const middle = ids[Math.floor(COHORT_SIZE / 2)]
    ensureMember(d, middle, MON2)
    assert.equal(standings(d, middle, MON2).tier, 0)
  })

  test('صعود پشت سر هم، دو هفته دو رده', () => {
    const d = db()
    const ids = fillCohort(d, MON1)
    const top = ids[0]

    ensureMember(d, top, MON2)
    assert.equal(standings(d, top, MON2).tier, 1)

    // هفته دوم هم اول می‌شود
    addXp(d, top, 500, MON2)
    ensureMember(d, top, MON3)
    assert.equal(standings(d, top, MON3).tier, 2)
  })

  test('بدون امتیاز صعود نمی‌کند حتی اگر رتبه‌اش بالا باشد', () => {
    const d = db()
    // تنها عضو گروه، با صفر امتیاز: رتبه ۱ است ولی کاری نکرده
    const lonely = addUser(d, 1)
    ensureMember(d, lonely, MON1)
    ensureMember(d, lonely, MON2)
    assert.equal(standings(d, lonely, MON2).tier, 0)
  })

  test('از الماس بالاتر نمی‌رود', () => {
    const d = db()
    const me = addUser(d, 1)
    ensureMember(d, me, MON1)
    d.prepare(`UPDATE league_members SET tier = ?, xp = 100`).run(TIERS.length - 1)
    ensureMember(d, me, MON2)
    assert.equal(standings(d, me, MON2).tier, TIERS.length - 1)
  })
})

describe('جدول', () => {
  test('منطقه صعود و سقوط گزارش می‌شود', () => {
    const d = db()
    const ids = fillCohort(d, MON1)
    const s = standings(d, ids[0], MON1)
    assert.equal(s.promoteCount, PROMOTE_COUNT)
    assert.equal(s.relegateCount, RELEGATE_COUNT)
    assert.equal(s.rows.length, COHORT_SIZE)
    assert.equal(s.rows[0].rank, 1)
  })

  test('شماره تلفن کامل به بقیه نشان داده نمی‌شود', () => {
    const d = db()
    const ids = fillCohort(d, MON1)
    for (const r of standings(d, ids[0], MON1).rows) {
      assert.equal(r.name.includes('0912'), false)
    }
  })
})
