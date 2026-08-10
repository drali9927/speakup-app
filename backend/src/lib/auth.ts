import { createHash, randomInt } from 'node:crypto'
import jwt from 'jsonwebtoken'
import type { Db } from '../db/index.js'
import { nowSec } from '../db/index.js'

export const CODE_LENGTH = 5
export const CODE_TTL_SECONDS = 120
export const RESEND_COOLDOWN_SECONDS = 80
export const MAX_VERIFY_ATTEMPTS = 5

/** شماره ایرانی: ۱۱ رقم و شروع با ۰۹ */
export function normalizePhone(raw: string): string | null {
  const d = raw.replace(/\D/g, '')
  if (d.length === 11 && d.startsWith('09')) return d
  // شکل بین‌المللی 98… را هم می‌پذیریم و به شکل محلی برمی‌گردانیم
  if (d.length === 12 && d.startsWith('98')) return `0${d.slice(2)}`
  return null
}

/**
 * کد ورود هرگز به‌صورت خام ذخیره نمی‌شود.
 * اگر دیتابیس لو برود، کدهای فعال نباید قابل استفاده باشند.
 */
export const hashCode = (phone: string, code: string) =>
  createHash('sha256').update(`${phone}:${code}`).digest('hex')

export const generateCode = () =>
  String(randomInt(0, 10 ** CODE_LENGTH)).padStart(CODE_LENGTH, '0')

export type RequestCodeResult =
  | { ok: true; code: string; expiresIn: number; codeId: number }
  | { ok: false; error: 'invalid_phone' | 'too_soon' | 'daily_limit'; retryAfter?: number }

/**
 * حذف کدی که ساخته شد ولی پیامکش نرفت.
 *
 * بدون این، قطعی سرویس پیامک کاربر را جریمه می‌کند: هم ۸۰ ثانیه منتظر
 * می‌ماند و هم یکی از سهمیه روزانه‌اش سوخته، بابت پیامکی که هرگز نرسید.
 */
export function discardCode(db: Db, codeId: number): void {
  db.prepare(`DELETE FROM otp_codes WHERE id = ?`).run(codeId)
}

export function requestCode(db: Db, rawPhone: string, dailyLimit = 10): RequestCodeResult {
  const phone = normalizePhone(rawPhone)
  if (!phone) return { ok: false, error: 'invalid_phone' }

  const ts = nowSec()
  const last = db
    .prepare(`SELECT created_at FROM otp_codes WHERE phone = ? ORDER BY created_at DESC LIMIT 1`)
    .get(phone) as { created_at: number } | undefined

  // جلوگیری از ارسال پیاپی — هم هزینه پیامک، هم سوءاستفاده
  if (last && ts - last.created_at < RESEND_COOLDOWN_SECONDS) {
    return { ok: false, error: 'too_soon', retryAfter: RESEND_COOLDOWN_SECONDS - (ts - last.created_at) }
  }

  // فاصله ۸۰ ثانیه به‌تنهایی جلوی هزینه را نمی‌گیرد: با همان فاصله هم
  // می‌شود در یک شبانه‌روز صدها پیامک روی یک شماره فرستاد. هر پیامک پول
  // است و هر پیامک ناخواسته یک شکایت.
  const { sent } = db
    .prepare(`SELECT COUNT(*) AS sent FROM otp_codes WHERE phone = ? AND created_at > ?`)
    .get(phone, ts - 86_400) as { sent: number }
  if (sent >= dailyLimit) {
    return { ok: false, error: 'daily_limit', retryAfter: 86_400 }
  }

  const code = generateCode()
  const info = db
    .prepare(`INSERT INTO otp_codes (phone, code_hash, expires_at, created_at) VALUES (?, ?, ?, ?)`)
    .run(phone, hashCode(phone, code), ts + CODE_TTL_SECONDS, ts)

  return { ok: true, code, expiresIn: CODE_TTL_SECONDS, codeId: Number(info.lastInsertRowid) }
}

export type VerifyResult =
  | { ok: true; userId: number; phone: string; referralCode: string; isNew: boolean }
  | { ok: false; error: 'invalid_phone' | 'no_code' | 'expired' | 'wrong_code' | 'too_many_attempts' }

export function verifyCode(db: Db, rawPhone: string, code: string): VerifyResult {
  const phone = normalizePhone(rawPhone)
  if (!phone) return { ok: false, error: 'invalid_phone' }

  const ts = nowSec()
  const row = db
    .prepare(
      `SELECT id, code_hash, expires_at, attempts FROM otp_codes
       WHERE phone = ? AND consumed_at IS NULL
       ORDER BY created_at DESC LIMIT 1`,
    )
    .get(phone) as { id: number; code_hash: string; expires_at: number; attempts: number } | undefined

  if (!row) return { ok: false, error: 'no_code' }
  if (row.attempts >= MAX_VERIFY_ATTEMPTS) return { ok: false, error: 'too_many_attempts' }
  if (row.expires_at < ts) return { ok: false, error: 'expired' }

  if (row.code_hash !== hashCode(phone, code.replace(/\D/g, ''))) {
    db.prepare(`UPDATE otp_codes SET attempts = attempts + 1 WHERE id = ?`).run(row.id)
    return { ok: false, error: 'wrong_code' }
  }

  // کد یک‌بارمصرف است
  db.prepare(`UPDATE otp_codes SET consumed_at = ? WHERE id = ?`).run(ts, row.id)

  const existing = db.prepare(`SELECT id, referral_code FROM users WHERE phone = ?`).get(phone) as
    | { id: number; referral_code: string }
    | undefined

  if (existing) {
    db.prepare(`UPDATE users SET last_seen_at = ? WHERE id = ?`).run(ts, existing.id)
    return { ok: true, userId: existing.id, phone, referralCode: existing.referral_code, isNew: false }
  }

  const referralCode = makeReferralCode(db)
  const info = db
    .prepare(`INSERT INTO users (phone, referral_code, created_at, last_seen_at) VALUES (?, ?, ?, ?)`)
    .run(phone, referralCode, ts, ts)

  return { ok: true, userId: Number(info.lastInsertRowid), phone, referralCode, isNew: true }
}

/**
 * کاربر را می‌سازد یا برمی‌گرداند — بدون بررسی کد.
 *
 * فقط مسیر حساب تست از این استفاده می‌کند؛ مسیر عادی همیشه از
 * verifyCode می‌گذرد که کد یک‌بارمصرف را مصرف و تلاش‌ها را محدود می‌کند.
 */
export function ensureUser(db: Db, phone: string): {
  userId: number; phone: string; referralCode: string; isNew: boolean
} {
  const ts = Math.floor(Date.now() / 1000)
  const existing = db.prepare(`SELECT id, referral_code FROM users WHERE phone = ?`).get(phone) as
    | { id: number; referral_code: string }
    | undefined
  if (existing) {
    db.prepare(`UPDATE users SET last_seen_at = ? WHERE id = ?`).run(ts, existing.id)
    return { userId: existing.id, phone, referralCode: existing.referral_code, isNew: false }
  }
  const referralCode = makeReferralCode(db)
  const info = db
    .prepare(`INSERT INTO users (phone, referral_code, created_at, last_seen_at) VALUES (?, ?, ?, ?)`)
    .run(phone, referralCode, ts, ts)
  return { userId: Number(info.lastInsertRowid), phone, referralCode, isNew: true }
}

/** کد معرف ۶ کاراکتری — بدون حروف مشابه (O/0، I/1) تا شفاهی هم قابل انتقال باشد */
function makeReferralCode(db: Db): string {
  const alphabet = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
  for (let attempt = 0; attempt < 20; attempt++) {
    let code = ''
    for (let i = 0; i < 6; i++) code += alphabet[randomInt(0, alphabet.length)]
    const taken = db.prepare(`SELECT 1 FROM users WHERE referral_code = ?`).get(code)
    if (!taken) return code
  }
  throw new Error('could not allocate a unique referral code')
}

// ---------------------------------------------------------------- توکن

export type TokenPayload = { sub: number; phone: string }

export function signToken(secret: string, payload: TokenPayload, expiresIn = '90d'): string {
  return jwt.sign(payload, secret, { expiresIn } as jwt.SignOptions)
}

export function verifyToken(secret: string, token: string): TokenPayload | null {
  try {
    const p = jwt.verify(token, secret) as jwt.JwtPayload
    if (typeof p.sub !== 'number' || typeof p.phone !== 'string') return null
    return { sub: p.sub, phone: p.phone }
  } catch {
    return null
  }
}
