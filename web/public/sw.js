/**
 * سرویس‌ورکر — فقط پوسته اپ را کش می‌کند، نه داده.
 *
 * ⚠️ قلمرو این فایل عمداً محدود است. سرویس‌ورکر بدترین نوع باگ ممکن را
 * می‌سازد: کاربری که تا ابد نسخه خراب قدیمی را می‌بیند و رفعِ باگ هم
 * کمکش نمی‌کند، چون اصلاً درخواست تازه‌ای به سرور نمی‌رود. سه قاعده
 * جلوی این را می‌گیرد:
 *
 *   ۱. index.html هرگز کش نمی‌شود — همیشه شبکه، فقط اگر شبکه نبود از کش.
 *      این فایل به نام‌های hash‌دارِ آخرین build اشاره می‌کند؛ اگر خودش
 *      کهنه بماند، به فایل‌هایی اشاره می‌کند که دیگر روی سرور نیستند.
 *   ۲. فایل‌های داخل /assets/ نام‌شان hash محتواست (کار Vite) — پس کش
 *      همیشگی برایشان بی‌خطر است: build بعدی نام دیگری می‌سازد.
 *   ۳. /v1/ و /images/ اصلاً از این‌جا رد نمی‌شوند. داده و تصاویر درس
 *      باید همیشه زنده باشند؛ منطق آفلاین خودِ اپ (src/store.ts) از قبل
 *      این را حل کرده — سرویس‌ورکر نباید دوباره و بدتر حلش کند.
 */

const SHELL_CACHE = 'speakup-shell-v1'

self.addEventListener('install', (e) => {
  self.skipWaiting()
})

self.addEventListener('activate', (e) => {
  e.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(keys.filter((k) => k !== SHELL_CACHE).map((k) => caches.delete(k))),
    ),
  )
  self.clients.claim()
})

self.addEventListener('fetch', (e) => {
  const url = new URL(e.request.url)
  if (e.request.method !== 'GET' || url.origin !== location.origin) return
  if (url.pathname.startsWith('/v1/') || url.pathname.startsWith('/images/')) return

  const isShellDoc = url.pathname === '/' || url.pathname === '/index.html'
  const isHashedAsset = url.pathname.startsWith('/assets/')

  if (isShellDoc) {
    // شبکه اول؛ فقط وقتی آفلاینیم از کش — تا نسخه‌ی کهنه هرگز روی نسخه‌ی
    // تازه‌ی موجود ترجیح داده نشود
    e.respondWith(
      fetch(e.request)
        .then((res) => {
          caches.open(SHELL_CACHE).then((c) => c.put(e.request, res.clone()))
          return res
        })
        .catch(() => caches.match(e.request)),
    )
    return
  }

  if (isHashedAsset) {
    e.respondWith(
      caches.match(e.request).then(
        (hit) =>
          hit ||
          fetch(e.request).then((res) => {
            caches.open(SHELL_CACHE).then((c) => c.put(e.request, res.clone()))
            return res
          }),
      ),
    )
  }
})
