#!/usr/bin/env bash
# انتشار تصاویر بهینه‌شده روی سرور محتوا.
#
# جای این مرحله در خط لوله:
#   imagegen.py  →  optimize_images.py  →  publish_images.sh
#
# چرا اسکریپت جدا: تصاویر عمداً داخل APK نمی‌روند (فقط درس اول). این مرحله
# آخرین حلقه است و اگر جا بیفتد، اپ «تصویر بارگذاری نشد» نشان می‌دهد بدون
# اینکه هیچ خطایی جایی ثبت شود.
set -euo pipefail

BASE="$(cd "$(dirname "$0")" && pwd)"
SRC="$BASE/_images_all"
SERVER="$BASE/../backend/content/images"
BUNDLED="$BASE/../android/app/src/main/assets/images"

# فقط درس اول همراه APK می‌ماند، به‌علاوه تصویر صفحه معرفی
BUNDLE_GLOB="a1_l01_*.webp"
EXTRA_BUNDLED="a1_l09_w15_together.webp"

[ -d "$SRC" ] || { echo "پوشه تصاویر پیدا نشد: $SRC" >&2; exit 1; }

png_left=$(find "$SRC" -name '*.png' | wc -l | tr -d ' ')
if [ "$png_left" -gt 0 ]; then
  echo "⚠️  $png_left فایل PNG بهینه‌نشده مانده است." >&2
  echo "    اول این را بزنید: python3 content/optimize_images.py" >&2
  exit 1
fi

mkdir -p "$SERVER" "$BUNDLED"
cp "$SRC"/*.webp "$SERVER"/

# APK فقط همان چند تصویر را نگه می‌دارد.
#
# مرجع، پوشه سرور است و نه _images_all: با افزوده‌شدن سطوح تازه، اجرای
# imagegen فقط تصاویرِ غایب را می‌سازد، پس _images_all لزوماً تصاویر
# درس اول را ندارد. اگر از آنجا کپی می‌کردیم، این پوشه خالی می‌ماند و
# اپ در نخستین اجرای بدون اینترنت بی‌تصویر می‌شد — بی‌آنکه خطایی بدهد.
rm -f "$BUNDLED"/*.webp
cp "$SERVER"/$BUNDLE_GLOB "$BUNDLED"/
[ -f "$SERVER/$EXTRA_BUNDLED" ] && cp "$SERVER/$EXTRA_BUNDLED" "$BUNDLED"/

if [ "$(ls "$BUNDLED"/*.webp 2>/dev/null | wc -l | tr -d ' ')" -eq 0 ]; then
  echo "هیچ تصویری همراه APK نشد — الگوی $BUNDLE_GLOB چیزی پیدا نکرد." >&2
  exit 1
fi

server_n=$(ls "$SERVER"/*.webp 2>/dev/null | wc -l | tr -d ' ')
bundled_n=$(ls "$BUNDLED"/*.webp 2>/dev/null | wc -l | tr -d ' ')
bundled_mb=$(du -sm "$BUNDLED" | cut -f1)

echo "سرور   : $server_n تصویر"
echo "همراه APK: $bundled_n تصویر ($bundled_mb مگابایت)"

# اگر روزی همه تصاویر داخل assets برگردند، APK چند برابر می‌شود
if [ "$bundled_mb" -gt 3 ]; then
  echo "⚠️  حجم تصاویر داخل APK بیش از حد انتظار است — بررسی کنید." >&2
  exit 1
fi
