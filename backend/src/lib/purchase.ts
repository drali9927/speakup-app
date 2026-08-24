/**
 * اعتبارسنجی رسید خرید کافه‌بازار.
 *
 * ⚠️ این کار **باید** سمت سرور انجام شود. اگر اپ خودش تصمیم بگیرد که خرید
 * معتبر است، با یک APK دستکاری‌شده اشتراک رایگان می‌شود — و این دقیقاً
 * همان کاری است که روی اپ‌های ایرانی زیاد انجام می‌شود.
 *
 * جریان کامل:
 *   ۱. اپ خرید را با Poolakey انجام می‌دهد و `purchaseToken` می‌گیرد
 *   ۲. توکن را به `POST /v1/purchase/verify` می‌فرستد
 *   ۳. سرور از API کافه‌بازار می‌پرسد که این توکن واقعاً پرداخت شده یا نه
 *   ۴. فقط در این صورت اشتراک فعال می‌شود
 *
 * هیچ‌جای این مسیر به حرف اپ اعتماد نمی‌شود — نه به قیمت، نه به تاریخ
 * انقضا، نه به اینکه اصلاً خریدی رخ داده.
 */

import type { Db } from '../db/index.js'
import { nowSec } from '../db/index.js'

/**
 * طرح‌های اشتراک.
 *
 * تنها منبع حقیقت قیمت — اپ فهرست را از `/v1/plans` می‌گیرد تا تغییر
 * قیمت نیازمند انتشار نسخه تازه نباشد. قیمت‌ها از سند ۰۴ می‌آیند و عمداً
 * از رقیب پایین‌تر بسته شده‌اند، به‌ویژه طرح هفتگی که رقیب ندارد و سد
 * ورود را پایین می‌آورد.
 *
 * مدت بر حسب **روز** است و نه ماه، چون طرح هفتگی در ماه نمی‌گنجد.
 */
export type Plan = {
  code: string
  /** شناسه محصول در پنل کافه‌بازار */
  sku: string
  title: string
  days: number
  priceRial: number
  badge?: string
  note?: string
}

/**
 * طرح‌های اشتراک.
 *
 * ⚠️ `sku` باید **دقیقاً** همان شناسه‌ای باشد که در پنل کافه‌بازار ساخته
 * شده. اگر یکی نباشند، خرید در همان گام اول شکست می‌خورد: بازار می‌گوید
 * چنین محصولی ندارم. و اگر به‌هر شکل رسیدی برسد، `planBySku` آن را پیدا
 * نمی‌کند و سرور ردش می‌کند.
 *
 * فهرست زیر با محصولات واقعی پنل هم‌تراز شده (۳۰d / ۹۰d / ۳۶۵d).
 * فهرست قبلی چهار طرح فرضی داشت که هیچ‌کدام در پنل ساخته نشده بودند.
 */
export const PLANS: readonly Plan[] = [
  { code: 'monthly', sku: '30d', title: 'اشتراک ۳۰ روزه', days: 30, priceRial: 3_990_000, note: 'برای شروع' },
  { code: 'quarterly', sku: '90d', title: 'اشتراک ۹۰ روزه', days: 90, priceRial: 6_490_000, badge: 'پرطرفدارترین' },
  { code: 'yearly', sku: '365d', title: 'اشتراک یک‌ساله', days: 365, priceRial: 9_990_000, badge: 'به‌صرفه‌ترین' },
] as const

export const planBySku = (sku: string): Plan | undefined => PLANS.find((p) => p.sku === sku)

// ---------------------------------------------------------------- ارائه‌دهنده

export type ReceiptState =
  /** پرداخت‌شده و باطل‌نشده */
  | { valid: true; purchasedAt: number }
  | { valid: false; reason: string }

export interface ReceiptVerifier {
  readonly name: string
  check(sku: string, purchaseToken: string): Promise<ReceiptState>
}

/**
 * در توسعه: هر رسیدی را می‌پذیرد تا بدون حساب کافه‌بازار بشود کل مسیر را
 * تست کرد. `loadConfig` اجازه نمی‌دهد این در تولید فعال شود.
 */
export const trustingVerifier: ReceiptVerifier = {
  name: 'trusting',
  async check() {
    return { valid: true, purchasedAt: nowSec() }
  },
}

/** تولید بدون تنظیم ارائه‌دهنده نباید بی‌صدا هر رسیدی را بپذیرد */
export const rejectingVerifier: ReceiptVerifier = {
  name: 'rejecting',
  async check() {
    return { valid: false, reason: 'verifier_not_configured' }
  },
}

export type BazaarOptions = {
  packageName: string
  /** توکن دسترسی از OAuth کافه‌بازار — از متغیر محیطی، نه از کد */
  accessToken: () => Promise<string>
  fetchImpl?: typeof fetch
  timeoutMs?: number
}

/**
 * API توسعه‌دهندگان کافه‌بازار.
 *
 * پاسخ `purchaseState === 0` یعنی پرداخت‌شده. `consumptionState` را عمداً
 * نادیده می‌گیریم: اشتراک ما مصرف‌شدنی نیست و اگر روزی محصولی مصرف‌شدنی
 * اضافه شد، باید تصمیم جداگانه‌ای برایش گرفته شود.
 */
export function bazaarVerifier(opts: BazaarOptions): ReceiptVerifier {
  const doFetch = opts.fetchImpl ?? fetch
  const timeoutMs = opts.timeoutMs ?? 8000

  return {
    name: 'bazaar',
    async check(sku, purchaseToken) {
      const controller = new AbortController()
      const timer = setTimeout(() => controller.abort(), timeoutMs)
      try {
        const token = await opts.accessToken()
        const url =
          `https://pardakht.cafebazaar.ir/devapi/v2/api/validate/` +
          `${encodeURIComponent(opts.packageName)}/inapp/` +
          `${encodeURIComponent(sku)}/purchases/${encodeURIComponent(purchaseToken)}/`

        const res = await doFetch(url, {
          headers: { Authorization: token },
          signal: controller.signal,
        })

        if (res.status === 404) return { valid: false, reason: 'receipt_not_found' }
        if (!res.ok) return { valid: false, reason: `bazaar_http_${res.status}` }

        const body = (await res.json().catch(() => null)) as
          | { purchaseState?: number; purchaseTime?: number }
          | null

        if (!body || body.purchaseState !== 0) {
          return { valid: false, reason: `purchase_state_${body?.purchaseState ?? 'unknown'}` }
        }

        // purchaseTime میلی‌ثانیه است؛ بقیه سیستم با ثانیه کار می‌کند
        const purchasedAt = body.purchaseTime ? Math.floor(body.purchaseTime / 1000) : nowSec()
        return { valid: true, purchasedAt }
      } catch (e) {
        const aborted = (e as { name?: string })?.name === 'AbortError'
        return { valid: false, reason: aborted ? 'bazaar_timeout' : 'bazaar_unreachable' }
      } finally {
        clearTimeout(timer)
      }
    },
  }
}

// ---------------------------------------------------------------- ثبت اشتراک

export type RedeemResult =
  | { ok: true; plan: string; expiresAt: number; alreadyRedeemed: boolean }
  | { ok: false; error: 'unknown_sku' | 'invalid_receipt' | 'token_belongs_to_other_user' }

/**
 * ثبت اشتراک پس از تأیید رسید.
 *
 * دو حالت لبه که هر دو در عمل پیش می‌آیند:
 *
 * **ارسال دوباره همان توکن.** اگر اپ پاسخ را نگیرد و دوباره بفرستد، نباید
 * دو ماه اشتراک بگیرد. توکن یکتاست (ایندکس `idx_sub_token`) و در این حالت
 * همان اشتراک قبلی برگردانده می‌شود.
 *
 * **تمدید پیش از انقضا.** مبدأ تمدید، انقضای فعلی است نه امروز؛ وگرنه
 * کاربری که زودتر تمدید می‌کند بخشی از اشتراکش را از دست می‌دهد.
 */
export async function redeemPurchase(
  db: Db,
  verifier: ReceiptVerifier,
  userId: number,
  sku: string,
  purchaseToken: string,
): Promise<RedeemResult> {
  const plan = planBySku(sku)
  if (!plan) return { ok: false, error: 'unknown_sku' }

  const existing = db
    .prepare(`SELECT user_id, plan_code, expires_at FROM subscriptions WHERE purchase_token = ?`)
    .get(purchaseToken) as { user_id: number; plan_code: string; expires_at: number } | undefined

  if (existing) {
    // یک رسید نباید برای دو حساب کار کند
    if (existing.user_id !== userId) return { ok: false, error: 'token_belongs_to_other_user' }
    return {
      ok: true,
      plan: existing.plan_code,
      expiresAt: existing.expires_at,
      alreadyRedeemed: true,
    }
  }

  const state = await verifier.check(sku, purchaseToken)
  if (!state.valid) return { ok: false, error: 'invalid_receipt' }

  const ts = nowSec()
  const currentExpiry = (
    db
      .prepare(
        `SELECT MAX(expires_at) AS e FROM subscriptions
         WHERE user_id = ? AND status = 'active' AND expires_at IS NOT NULL`,
      )
      .get(userId) as { e: number | null }
  ).e

  const startFrom = currentExpiry && currentExpiry > ts ? currentExpiry : ts
  const expiresAt = startFrom + plan.days * 86_400

  db.prepare(
    `INSERT INTO subscriptions
       (user_id, plan_code, status, started_at, expires_at, price_rial, gateway, purchase_token, created_at)
     VALUES (?, ?, 'active', ?, ?, ?, 'bazaar', ?, ?)`,
  ).run(userId, plan.code, startFrom, expiresAt, plan.priceRial, purchaseToken, ts)

  return { ok: true, plan: plan.code, expiresAt, alreadyRedeemed: false }
}

/** اشتراک فعال کاربر، یا null */
export function activeSubscription(
  db: Db,
  userId: number,
): { plan: string; expiresAt: number } | null {
  const row = db
    .prepare(
      `SELECT plan_code, expires_at FROM subscriptions
       WHERE user_id = ? AND status = 'active' AND (expires_at IS NULL OR expires_at > ?)
       ORDER BY expires_at DESC LIMIT 1`,
    )
    .get(userId, nowSec()) as { plan_code: string; expires_at: number } | undefined

  return row ? { plan: row.plan_code, expiresAt: row.expires_at } : null
}
