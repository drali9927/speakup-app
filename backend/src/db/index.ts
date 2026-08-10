import Database from 'better-sqlite3'
import { readFileSync } from 'node:fs'
import { dirname, join } from 'node:path'
import { fileURLToPath } from 'node:url'

const here = dirname(fileURLToPath(import.meta.url))

/**
 * لایه پایگاه داده.
 *
 * فعلاً SQLite است، نه PostgreSQL که در سند ۰۵ آمده. دلیل عملی:
 * بدون Postgres محلی، کد بک‌اند تست‌نشده می‌ماند و کد تست‌نشده
 * همان چیزی است که در انتشار می‌شکند.
 *
 * برای مهاجرت به PostgreSQL فقط همین فایل و این چهار مورد عوض می‌شوند:
 *   ۱. `INTEGER PRIMARY KEY AUTOINCREMENT`  →  `SERIAL PRIMARY KEY`
 *   ۲. جای‌نگهدارها از `?` به `$1, $2, …`
 *   ۳. `db.prepare(...).run/get/all`  →  `pool.query(...)`
 *   ۴. بولین‌ها که اینجا ۰/۱ ذخیره می‌شوند  →  نوع `BOOLEAN`
 * بقیه کد چون فقط از توابع همین فایل استفاده می‌کند دست نمی‌خورد.
 */
export function openDb(path: string) {
  const db = new Database(path)
  db.pragma('journal_mode = WAL')
  db.pragma('foreign_keys = ON')
  db.exec(readFileSync(join(here, 'schema.sql'), 'utf8'))

  // مهاجرت‌های افزایشی.
  //
  // ALTER TABLE داخل schema.sql نمی‌آید چون آن فایل در هر بار بالا آمدن
  // سرور اجرا می‌شود و بار دوم با «ستون تکراری» می‌شکند.
  //
  // is_demo: کاربران نمایشی لیگ. با پرچم مشخص‌اند تا از آمار واقعی جدا
  // بمانند، و ساختنشان در production ممنوع است.
  const cols = db.prepare(`PRAGMA table_info(users)`).all() as Array<{ name: string }>
  if (!cols.some(c => c.name === 'is_demo')) {
    db.exec(`ALTER TABLE users ADD COLUMN is_demo INTEGER NOT NULL DEFAULT 0`)
  }
  return db
}

export type Db = ReturnType<typeof openDb>

/** ثانیه یونیکس — همه زمان‌ها در دیتابیس با همین واحد ذخیره می‌شوند */
export const nowSec = () => Math.floor(Date.now() / 1000)
