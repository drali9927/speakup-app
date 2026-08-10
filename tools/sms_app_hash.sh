#!/usr/bin/env bash
# هش ۱۱ کاراکتری SMS Retriever
#
# اندروید کد پیامک را فقط وقتی خودکار پر می‌کند که متن پیامک به این رشته
# ختم شود. رشته از «نام بسته + گواهی امضا» ساخته می‌شود، پس برای دیباگ و
# انتشار متفاوت است — و تا کلید انتشار ساخته نشود، هش نهایی به دست نمی‌آید.
#
# این هش باید داخل الگوی ثبت‌شده در پنل کاوه‌نگار باشد، نه در کد اپ.
# اگر اشتباه باشد هیچ خطایی دیده نمی‌شود؛ فقط کاربر باید کد را دستی تایپ کند.
#
# استفاده:
#   tools/sms_app_hash.sh                                  # کلید دیباگ
#   tools/sms_app_hash.sh release.jks upload ir.speakup.app
set -euo pipefail

KEYSTORE="${1:-$HOME/.android/debug.keystore}"
ALIAS="${2:-androiddebugkey}"
PACKAGE="${3:-ir.speakup.app}"

if [ ! -f "$KEYSTORE" ]; then
  echo "کلید پیدا نشد: $KEYSTORE" >&2
  exit 1
fi

# رمز کلید دیباگ همیشه android است؛ برای کلید انتشار پرسیده می‌شود
if [ "$KEYSTORE" = "$HOME/.android/debug.keystore" ]; then
  STOREPASS="android"
else
  read -r -s -p "رمز keystore: " STOREPASS
  echo
fi

# اندروید خودِ بایت‌های DER گواهی را به‌صورت hex در ساخت هش استفاده می‌کند
CERT_HEX=$(
  keytool -exportcert -keystore "$KEYSTORE" -alias "$ALIAS" -storepass "$STOREPASS" 2>/dev/null |
    xxd -p | tr -d '\n'
)

if [ -z "$CERT_HEX" ]; then
  echo "استخراج گواهی شکست خورد — نام مستعار یا رمز را بررسی کن" >&2
  exit 1
fi

# sha256("package hex") → ۹ بایت نخست → base64 → ۱۱ کاراکتر نخست
HASH=$(
  printf '%s %s' "$PACKAGE" "$CERT_HEX" |
    openssl dgst -sha256 -binary |
    head -c 9 |
    openssl base64 |
    tr -d '\n' |
    cut -c1-11
)

echo "بسته:  $PACKAGE"
echo "هش:    $HASH"
echo
echo "نمونه متن الگو (خط آخر باید دقیقاً همین باشد):"
echo "کد ورود اسپیک‌آپ: %token"
echo "$HASH"
