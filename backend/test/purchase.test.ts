import assert from 'node:assert/strict'
import { test, describe } from 'node:test'
import { openDb, nowSec } from '../src/db/index.js'
import {
  PLANS,
  activeSubscription,
  bazaarVerifier,
  rejectingVerifier,
  redeemPurchase,
  trustingVerifier,
} from '../src/lib/purchase.js'

const PLAN = PLANS[1]           // اشتراک ماهانه
const SKU = PLAN.sku

function db() {
  const d = openDb(':memory:')
  d.prepare(
    `INSERT INTO users (phone, referral_code, created_at, last_seen_at) VALUES (?, ?, ?, ?)`,
  ).run('09123456789', 'AAA111', nowSec(), nowSec())
  d.prepare(
    `INSERT INTO users (phone, referral_code, created_at, last_seen_at) VALUES (?, ?, ?, ?)`,
  ).run('09123456780', 'BBB222', nowSec(), nowSec())
  return d
}

function fakeFetch(response: unknown, status = 200) {
  const calls: string[] = []
  const impl = (async (url: string) => {
    calls.push(String(url))
    return { ok: status < 400, status, json: async () => response } as Response
  }) as unknown as typeof fetch
  return { impl, calls }
}

describe('bazaarVerifier', () => {
  test('accepts a paid receipt', async () => {
    const { impl, calls } = fakeFetch({ purchaseState: 0, purchaseTime: 1_700_000_000_000 })
    const v = bazaarVerifier({
      packageName: 'ir.speakup.app',
      accessToken: async () => 'TOKEN',
      fetchImpl: impl,
    })

    const r = await v.check(SKU, 'tok-1')
    assert.equal(r.valid, true)
    if (r.valid) assert.equal(r.purchasedAt, 1_700_000_000)
    assert.match(calls[0], new RegExp(`ir\\.speakup\\.app/inapp/${SKU}/purchases/tok-1`))
  })

  /**
   * مهم‌ترین تست این فایل: رسید باطل‌شده نباید اشتراک بدهد. اگر روزی این
   * شرط شل شود، هر کسی با یک رسید کنسل‌شده اشتراک می‌گیرد.
   */
  test('rejects a receipt that is not in the paid state', async () => {
    const { impl } = fakeFetch({ purchaseState: 1 })
    const v = bazaarVerifier({
      packageName: 'p',
      accessToken: async () => 'T',
      fetchImpl: impl,
    })
    assert.equal((await v.check(SKU, 'tok')).valid, false)
  })

  test('rejects an unknown receipt', async () => {
    const { impl } = fakeFetch({}, 404)
    const v = bazaarVerifier({ packageName: 'p', accessToken: async () => 'T', fetchImpl: impl })
    const r = await v.check(SKU, 'tok')
    assert.equal(r.valid, false)
    if (!r.valid) assert.equal(r.reason, 'receipt_not_found')
  })

  test('rejects rather than accepts when the provider is unreachable', async () => {
    // شکست شبکه نباید به معنی «خرید معتبر است» تعبیر شود
    const impl = (async () => { throw new Error('ENOTFOUND') }) as unknown as typeof fetch
    const v = bazaarVerifier({ packageName: 'p', accessToken: async () => 'T', fetchImpl: impl })
    assert.equal((await v.check(SKU, 'tok')).valid, false)
  })

  test('gives up rather than hanging', async () => {
    const impl = ((_u: string, o: { signal: AbortSignal }) =>
      new Promise((_res, rej) => {
        o.signal.addEventListener('abort', () => {
          const e = new Error('aborted')
          e.name = 'AbortError'
          rej(e)
        })
      })) as unknown as typeof fetch
    const v = bazaarVerifier({
      packageName: 'p',
      accessToken: async () => 'T',
      fetchImpl: impl,
      timeoutMs: 20,
    })
    const r = await v.check(SKU, 'tok')
    assert.equal(r.valid, false)
    if (!r.valid) assert.equal(r.reason, 'bazaar_timeout')
  })
})

describe('redeemPurchase', () => {
  test('activates a subscription for a valid receipt', async () => {
    const d = db()
    const r = await redeemPurchase(d, trustingVerifier, 1, SKU, 'tok-1')
    assert.equal(r.ok, true)
    if (!r.ok) return
    assert.equal(r.plan, PLAN.code)
    assert.equal(r.alreadyRedeemed, false)
    assert.ok(r.expiresAt > nowSec())

    assert.deepEqual(activeSubscription(d, 1), { plan: PLAN.code, expiresAt: r.expiresAt })
  })

  test('never grants a subscription on an invalid receipt', async () => {
    const d = db()
    const r = await redeemPurchase(d, rejectingVerifier, 1, SKU, 'tok-1')
    assert.equal(r.ok, false)
    if (!r.ok) assert.equal(r.error, 'invalid_receipt')
    assert.equal(activeSubscription(d, 1), null)
  })

  test('rejects a product that is not in the price list', async () => {
    const d = db()
    const r = await redeemPurchase(d, trustingVerifier, 1, 'speakup_free_forever', 'tok')
    assert.equal(r.ok, false)
    if (!r.ok) assert.equal(r.error, 'unknown_sku')
  })

  /**
   * اگر اپ پاسخ را نگیرد و دوباره بفرستد، نباید دو برابر اشتراک بگیرد.
   */
  test('is idempotent — the same token does not grant twice', async () => {
    const d = db()
    const first = await redeemPurchase(d, trustingVerifier, 1, SKU, 'tok-1')
    const second = await redeemPurchase(d, trustingVerifier, 1, SKU, 'tok-1')

    assert.equal(first.ok, true)
    assert.equal(second.ok, true)
    if (!first.ok || !second.ok) return
    assert.equal(second.alreadyRedeemed, true)
    assert.equal(second.expiresAt, first.expiresAt)

    const { c } = d.prepare('SELECT COUNT(*) AS c FROM subscriptions').get() as { c: number }
    assert.equal(c, 1)
  })

  test('one receipt cannot be reused by a second account', async () => {
    const d = db()
    await redeemPurchase(d, trustingVerifier, 1, SKU, 'tok-1')
    const other = await redeemPurchase(d, trustingVerifier, 2, SKU, 'tok-1')

    assert.equal(other.ok, false)
    if (!other.ok) assert.equal(other.error, 'token_belongs_to_other_user')
    assert.equal(activeSubscription(d, 2), null)
  })

  /**
   * کاربری که زودتر تمدید می‌کند نباید بخشی از اشتراکش را از دست بدهد.
   */
  test('renewal extends from the current expiry, not from today', async () => {
    const d = db()
    const first = await redeemPurchase(d, trustingVerifier, 1, SKU, 'tok-1')
    const second = await redeemPurchase(d, trustingVerifier, 1, SKU, 'tok-2')

    assert.equal(first.ok, true)
    assert.equal(second.ok, true)
    if (!first.ok || !second.ok) return

    assert.equal(second.expiresAt, first.expiresAt + PLAN.days * 86_400)
  })
})

describe('activeSubscription', () => {
  test('is null with no purchase', () => {
    assert.equal(activeSubscription(db(), 1), null)
  })

  test('an expired subscription does not count as active', () => {
    const d = db()
    const past = nowSec() - 86_400
    d.prepare(
      `INSERT INTO subscriptions
         (user_id, plan_code, status, started_at, expires_at, price_rial, gateway, created_at)
       VALUES (1, 'monthly', 'active', ?, ?, 0, 'bazaar', ?)`,
    ).run(past - 100, past, past - 100)

    assert.equal(activeSubscription(d, 1), null)
  })
})
