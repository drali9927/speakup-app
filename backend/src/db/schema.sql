-- شمای پایگاه داده SpeakUp
--
-- عمداً با SQL ساده و مشترک نوشته شده تا مهاجرت به PostgreSQL
-- فقط تغییر نوع کلید و تابع زمان باشد، نه بازنویسی.
-- تفاوت‌ها در `src/db/index.ts` بالای فایل مستند شده‌اند.

PRAGMA foreign_keys = ON;

-- ---------------------------------------------------------------- کاربر

CREATE TABLE IF NOT EXISTS users (
  id             INTEGER PRIMARY KEY AUTOINCREMENT,
  phone          TEXT    NOT NULL UNIQUE,
  display_name   TEXT,
  referral_code  TEXT    NOT NULL UNIQUE,
  referred_by    INTEGER REFERENCES users(id),
  current_level  TEXT    NOT NULL DEFAULT 'A1',
  created_at     INTEGER NOT NULL,
  last_seen_at   INTEGER
);

CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);

-- کدهای ورود.
-- کد به‌صورت هش ذخیره می‌شود: اگر دیتابیس لو برود، کدهای فعال قابل استفاده نباشند.
CREATE TABLE IF NOT EXISTS otp_codes (
  id          INTEGER PRIMARY KEY AUTOINCREMENT,
  phone       TEXT    NOT NULL,
  code_hash   TEXT    NOT NULL,
  expires_at  INTEGER NOT NULL,
  attempts    INTEGER NOT NULL DEFAULT 0,
  consumed_at INTEGER,
  created_at  INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_otp_phone ON otp_codes(phone, created_at);

-- ---------------------------------------------------------------- اشتراک

CREATE TABLE IF NOT EXISTS subscriptions (
  id             INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id        INTEGER NOT NULL REFERENCES users(id),
  plan_code      TEXT    NOT NULL,
  status         TEXT    NOT NULL,          -- active | expired | refunded | pending
  started_at     INTEGER NOT NULL,
  expires_at     INTEGER,                   -- NULL برای مادام‌العمر
  price_rial     INTEGER NOT NULL,
  gateway        TEXT    NOT NULL,          -- bazaar | myket | direct
  purchase_token TEXT,
  created_at     INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_sub_user ON subscriptions(user_id, status);
-- یک توکن خرید فقط یک بار قابل استفاده است — جلوگیری از بازپخش رسید
CREATE UNIQUE INDEX IF NOT EXISTS idx_sub_token ON subscriptions(purchase_token)
  WHERE purchase_token IS NOT NULL;

-- ---------------------------------------------------------------- همگام‌سازی

CREATE TABLE IF NOT EXISTS user_progress (
  user_id      INTEGER NOT NULL REFERENCES users(id),
  activity_id  TEXT    NOT NULL,
  status       TEXT    NOT NULL,
  score        REAL,
  last_item    INTEGER NOT NULL DEFAULT 0,
  completed_at INTEGER,
  updated_at   INTEGER NOT NULL,
  PRIMARY KEY (user_id, activity_id)
);

CREATE TABLE IF NOT EXISTS leitner_cards (
  user_id        INTEGER NOT NULL REFERENCES users(id),
  entry_id       TEXT    NOT NULL,
  word           TEXT    NOT NULL,
  box            INTEGER NOT NULL DEFAULT 1,
  due_at         INTEGER NOT NULL,
  correct_streak INTEGER NOT NULL DEFAULT 0,
  total_reviews  INTEGER NOT NULL DEFAULT 0,
  source         TEXT    NOT NULL,
  updated_at     INTEGER NOT NULL,
  PRIMARY KEY (user_id, entry_id)
);

CREATE INDEX IF NOT EXISTS idx_leitner_due ON leitner_cards(user_id, due_at);

-- زنجیره فقط اینجا معتبر است.
-- کلاینت هرگز طول زنجیره را نمی‌فرستد؛ فقط «امروز فعالیت کردم» را اعلام
-- می‌کند و محاسبه با زمان سرور انجام می‌شود. بدون این، تغییر ساعت گوشی
-- زنجیره را بی‌معنا می‌کند (سند ۰۳ / F-10).
CREATE TABLE IF NOT EXISTS streaks (
  user_id            INTEGER PRIMARY KEY REFERENCES users(id),
  current_length     INTEGER NOT NULL DEFAULT 0,
  longest_length     INTEGER NOT NULL DEFAULT 0,
  last_active_date   TEXT,                  -- yyyy-mm-dd به وقت تهران
  freeze_count       INTEGER NOT NULL DEFAULT 0,
  freezes_used_total INTEGER NOT NULL DEFAULT 0,
  updated_at         INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS streak_days (
  user_id INTEGER NOT NULL REFERENCES users(id),
  date    TEXT    NOT NULL,
  status  TEXT    NOT NULL,                 -- ACTIVE | FROZEN | MISSED
  PRIMARY KEY (user_id, date)
);

-- گزارش پاسخ‌ها — پایه متریک «نرخ تولید صحیح در مرور تأخیری» (سند ۰۷ بخش ۷.۷)
CREATE TABLE IF NOT EXISTS answer_log (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id       INTEGER NOT NULL REFERENCES users(id),
  item_id       TEXT    NOT NULL,
  activity_id   TEXT    NOT NULL,
  is_productive INTEGER NOT NULL,
  user_answer   TEXT    NOT NULL,
  is_correct    INTEGER NOT NULL,
  target_word   TEXT,
  answered_at   INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_answer_user ON answer_log(user_id, answered_at);

-- ---------------------------------------------------------------- لیگ
--
-- رتبه‌بندی هفتگی. هر دوشنبه دوره تازه‌ای شروع می‌شود و کاربران در
-- گروه‌های سی‌نفره رقابت می‌کنند.
--
-- امتیاز اینجا دوباره ذخیره می‌شود و از xp_events دستگاه جمع نمی‌شود:
-- رتبه‌بندی باید سمت سرور معتبر باشد، وگرنه هر کسی می‌تواند با دستکاری
-- دیتابیس محلی، صدر جدول را بخرد.
CREATE TABLE IF NOT EXISTS league_weeks (
  id          INTEGER PRIMARY KEY AUTOINCREMENT,
  starts_on   TEXT    NOT NULL UNIQUE,     -- تاریخ دوشنبه شروع، YYYY-MM-DD
  created_at  INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS league_members (
  week_id   INTEGER NOT NULL REFERENCES league_weeks(id) ON DELETE CASCADE,
  user_id   INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  cohort    INTEGER NOT NULL,             -- شماره گروه سی‌نفره
  tier      INTEGER NOT NULL DEFAULT 0,   -- رده: ۰ برنز … ۹ الماس
  xp        INTEGER NOT NULL DEFAULT 0,
  PRIMARY KEY (week_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_league_rank
  ON league_members(week_id, cohort, xp DESC);

-- ---------------------------------------------------------------- رویدادها
--
-- قیف محصول. بدون این، بعد از انتشار نمی‌دانیم کاربر کجا رها می‌کند و
-- ممکن است ماه‌ها روی قیمت کار کنیم در حالی که نیمی از کاربران در درس
-- اول رفته‌اند.
--
-- خودمیزبان و نه Firebase: کاربر ایرانی سرویس‌های گوگل را همیشه در
-- دسترس ندارد و Firebase Analytics بدون Google Play Services کار
-- نمی‌کند — یعنی همان کاربرانی که باید بشماریم، شمرده نمی‌شوند. ضمناً
-- داده کاربر از دست ما بیرون نمی‌رود.
--
-- install_id: شناسه دستگاه، پیش از ورود ساخته می‌شود. قیف از نصب شروع
-- می‌شود و در آن لحظه هنوز حساب کاربری وجود ندارد. user_id بعد از ورود
-- پر می‌شود و همان install_id به حساب وصل می‌ماند.
CREATE TABLE IF NOT EXISTS events (
  id          INTEGER PRIMARY KEY AUTOINCREMENT,
  install_id  TEXT    NOT NULL,
  user_id     INTEGER REFERENCES users(id),
  name        TEXT    NOT NULL,
  props       TEXT,                        -- JSON، اختیاری
  at          INTEGER NOT NULL,            -- ثانیه یونیکس، ساعت دستگاه
  received_at INTEGER NOT NULL             -- ساعت سرور، برای وقتی ساعت دستگاه غلط است
);
CREATE INDEX IF NOT EXISTS idx_events_name_at ON events(name, at);
CREATE INDEX IF NOT EXISTS idx_events_install ON events(install_id);
CREATE INDEX IF NOT EXISTS idx_events_user ON events(user_id);
