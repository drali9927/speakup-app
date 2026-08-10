import assert from 'node:assert/strict'
import { test, describe } from 'node:test'
import { openDb, type Db } from '../src/db/index.js'
import { productiveAccuracy, pull, push } from '../src/lib/sync.js'

function setup(): { db: Db; userId: number } {
  const db = openDb(':memory:')
  const info = db
    .prepare(`INSERT INTO users (phone, referral_code, created_at) VALUES (?, ?, ?)`)
    .run('09123456789', 'ABC234', 0)
  return { db, userId: Number(info.lastInsertRowid) }
}

const progressOf = (db: Db, userId: number, activityId: string) =>
  db.prepare(`SELECT status, last_item, updated_at FROM user_progress WHERE user_id = ? AND activity_id = ?`)
    .get(userId, activityId) as { status: string; last_item: number; updated_at: number } | undefined

describe('progress conflicts', () => {
  test('a new row is stored', () => {
    const { db, userId } = setup()
    const s = push(db, userId, {
      progress: [{ activityId: 'A1', status: 'IN_PROGRESS', lastItem: 3, updatedAt: 100 }],
    })
    assert.equal(s.progressApplied, 1)
    assert.equal(progressOf(db, userId, 'A1')?.status, 'IN_PROGRESS')
  })

  test('progress moves forward', () => {
    const { db, userId } = setup()
    push(db, userId, { progress: [{ activityId: 'A1', status: 'IN_PROGRESS', updatedAt: 100 }] })
    push(db, userId, { progress: [{ activityId: 'A1', status: 'COMPLETED', updatedAt: 200 }] })
    assert.equal(progressOf(db, userId, 'A1')?.status, 'COMPLETED')
  })

  test('progress never goes backwards, even from a newer device', () => {
    const { db, userId } = setup()
    push(db, userId, { progress: [{ activityId: 'A1', status: 'COMPLETED', updatedAt: 100 }] })

    // گوشی دوم همان درس را ناتمام دارد و timestamp جدیدتری هم دارد
    const s = push(db, userId, {
      progress: [{ activityId: 'A1', status: 'IN_PROGRESS', updatedAt: 999 }],
    })
    assert.equal(s.progressRejected, 1)
    assert.equal(progressOf(db, userId, 'A1')?.status, 'COMPLETED', 'the completion survives')
  })

  test('at the same status the newer timestamp wins', () => {
    const { db, userId } = setup()
    push(db, userId, { progress: [{ activityId: 'A1', status: 'IN_PROGRESS', lastItem: 2, updatedAt: 100 }] })
    push(db, userId, { progress: [{ activityId: 'A1', status: 'IN_PROGRESS', lastItem: 5, updatedAt: 200 }] })
    assert.equal(progressOf(db, userId, 'A1')?.last_item, 5)
  })

  test('an older duplicate is rejected', () => {
    const { db, userId } = setup()
    push(db, userId, { progress: [{ activityId: 'A1', status: 'IN_PROGRESS', updatedAt: 500 }] })
    const s = push(db, userId, { progress: [{ activityId: 'A1', status: 'IN_PROGRESS', updatedAt: 100 }] })
    assert.equal(s.progressRejected, 1)
    assert.equal(progressOf(db, userId, 'A1')?.updated_at, 500)
  })

  test('lastItem never regresses', () => {
    const { db, userId } = setup()
    push(db, userId, { progress: [{ activityId: 'A1', status: 'IN_PROGRESS', lastItem: 9, updatedAt: 100 }] })
    push(db, userId, { progress: [{ activityId: 'A1', status: 'IN_PROGRESS', lastItem: 1, updatedAt: 200 }] })
    assert.equal(progressOf(db, userId, 'A1')?.last_item, 9)
  })
})

describe('leitner conflicts', () => {
  const card = (over: Partial<Parameters<typeof push>[2] extends never ? never : any> = {}) => ({
    entryId: 'd-hello', word: 'hello', box: 1, dueAt: 1000,
    source: 'AUTO_WRONG', updatedAt: 100, ...over,
  })

  test('a new card is stored', () => {
    const { db, userId } = setup()
    const s = push(db, userId, { leitner: [card()] })
    assert.equal(s.leitnerApplied, 1)
  })

  test('the newer version wins', () => {
    const { db, userId } = setup()
    push(db, userId, { leitner: [card({ box: 1, updatedAt: 100 })] })
    push(db, userId, { leitner: [card({ box: 3, dueAt: 5000, updatedAt: 200 })] })

    const row = db.prepare(`SELECT box, due_at FROM leitner_cards WHERE user_id = ?`).get(userId) as
      { box: number; due_at: number }
    assert.equal(row.box, 3)
    assert.equal(row.due_at, 5000, 'box and due date move together')
  })

  test('an older version is rejected', () => {
    const { db, userId } = setup()
    push(db, userId, { leitner: [card({ box: 4, updatedAt: 500 })] })
    const s = push(db, userId, { leitner: [card({ box: 1, updatedAt: 100 })] })
    assert.equal(s.leitnerRejected, 1)

    const row = db.prepare(`SELECT box FROM leitner_cards WHERE user_id = ?`).get(userId) as { box: number }
    assert.equal(row.box, 4)
  })

  test('an identical timestamp does not overwrite', () => {
    const { db, userId } = setup()
    push(db, userId, { leitner: [card({ box: 2, updatedAt: 100 })] })
    const s = push(db, userId, { leitner: [card({ box: 5, updatedAt: 100 })] })
    assert.equal(s.leitnerRejected, 1)
  })
})

describe('answers', () => {
  test('are append-only and never conflict', () => {
    const { db, userId } = setup()
    const a = {
      itemId: 'i1', activityId: 'A1', isProductive: true,
      userAnswer: 'wrong', isCorrect: false, targetWord: 'meet', answeredAt: 10,
    }
    push(db, userId, { answers: [a] })
    push(db, userId, { answers: [a] })

    const n = db.prepare(`SELECT COUNT(*) AS n FROM answer_log WHERE user_id = ?`).get(userId) as { n: number }
    assert.equal(n.n, 2, 'both attempts are kept — the log is a history, not a state')
  })
})

describe('pull', () => {
  test('returns only rows changed after `since`', () => {
    const { db, userId } = setup()
    push(db, userId, {
      progress: [
        { activityId: 'old', status: 'COMPLETED', updatedAt: 100 },
        { activityId: 'new', status: 'COMPLETED', updatedAt: 300 },
      ],
    })
    const r = pull(db, userId, 200)
    assert.equal(r.progress.length, 1)
    assert.equal(r.progress[0]?.activityId, 'new')
  })

  test('does not leak another user rows', () => {
    const { db, userId } = setup()
    const other = Number(
      db.prepare(`INSERT INTO users (phone, referral_code, created_at) VALUES (?, ?, ?)`)
        .run('09120000002', 'ZZZ999', 0).lastInsertRowid,
    )
    push(db, other, { progress: [{ activityId: 'secret', status: 'COMPLETED', updatedAt: 500 }] })

    const r = pull(db, userId, 0)
    assert.equal(r.progress.length, 0)
  })
})

describe('productiveAccuracy', () => {
  test('ignores recognition exercises', () => {
    const { db, userId } = setup()
    push(db, userId, {
      answers: [
        // تشخیصی و درست — نباید شمرده شود
        { itemId: 'i1', activityId: 'A', isProductive: false, userAnswer: 'x', isCorrect: true, answeredAt: 10 },
        { itemId: 'i2', activityId: 'A', isProductive: true, userAnswer: 'x', isCorrect: true, answeredAt: 10 },
        { itemId: 'i3', activityId: 'A', isProductive: true, userAnswer: 'x', isCorrect: false, answeredAt: 10 },
      ],
    })
    assert.equal(productiveAccuracy(db, userId, 0), 0.5)
  })

  test('is null when there is nothing to measure', () => {
    const { db, userId } = setup()
    assert.equal(productiveAccuracy(db, userId, 0), null)
  })
})
