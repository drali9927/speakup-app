import { createHash } from 'node:crypto'
import { existsSync, readFileSync, readdirSync, statSync } from 'node:fs'
import { join } from 'node:path'

/**
 * تحویل محتوا.
 *
 * تا امروز کل محتوا داخل APK بود. برای ۱۰ درس قابل قبول است، اما با
 * ۳۰۰ درس نه حجم APK قابل قبول می‌ماند و نه هر اصلاح تایپی ارزش
 * انتشار نسخه تازه دارد.
 *
 * نسخه هر بسته از هش خود فایل می‌آید، نه از یک شماره دستی — یعنی
 * فراموش کردن افزایش شماره نسخه ممکن نیست.
 */

export type BundleInfo = {
  level: string
  version: string
  bytes: number
  updatedAt: number
}

export class ContentStore {
  constructor(private readonly dir: string) {}

  private pathFor(level: string): string | null {
    // نام سطح مستقیم در مسیر فایل می‌نشیند؛ باید محدود شود
    if (!/^[A-Za-z0-9_-]{1,16}$/.test(level)) return null
    const p = join(this.dir, `${level.toLowerCase()}.json`)
    return existsSync(p) ? p : null
  }

  /** فهرست بسته‌های موجود با نسخه — کلاینت با این تصمیم می‌گیرد چه دانلود کند */
  manifest(): BundleInfo[] {
    if (!existsSync(this.dir)) return []
    return readdirSync(this.dir)
      .filter((f) => f.endsWith('.json'))
      .map((f) => {
        const p = join(this.dir, f)
        const raw = readFileSync(p)
        return {
          level: f.replace(/\.json$/, '').toUpperCase(),
          version: createHash('sha256').update(raw).digest('hex').slice(0, 16),
          bytes: raw.byteLength,
          updatedAt: Math.floor(statSync(p).mtimeMs / 1000),
        }
      })
      .sort((a, b) => a.level.localeCompare(b.level))
  }

  /** بسته یک سطح — یا null اگر وجود نداشت */
  bundle(level: string): { body: string; version: string } | null {
    const p = this.pathFor(level)
    if (!p) return null
    const raw = readFileSync(p)
    return {
      body: raw.toString('utf8'),
      version: createHash('sha256').update(raw).digest('hex').slice(0, 16),
    }
  }
}
