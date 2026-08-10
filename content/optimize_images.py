#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
بهینه‌سازی تصاویر برای بسته اپ.

خروجی خام API فایل PNG با ابعاد ۱۰۲۴ و حجم حدود ۱.۵ مگابایت است.
۲۰۰ تصویر یعنی ۳۰۰ مگابایت — حجمی که برای کاربر ایرانی و بازبینی
کافه‌بازار غیرقابل قبول است.

کارت واژه در اپ ۲۶۰dp ارتفاع دارد؛ روی چگال‌ترین صفحه‌ها حدود ۱۰۴۰ پیکسل.
اما تصویر داخل کارت با ContentScale.Fit نمایش داده می‌شود و ۷۲۰ پیکسل
عملاً از حد تشخیص چشم بالاتر است. WebP هم در همان کیفیت،
یک‌دهم حجم PNG است.

    python3 optimize_images.py            # بهینه‌سازی درجا
    python3 optimize_images.py --check    # فقط گزارش، بدون تغییر
"""

import argparse, os, sys
from PIL import Image

BASE = os.path.dirname(os.path.abspath(__file__))
# همان پوشه‌ای که imagegen در آن می‌نویسد — نه assets اپ
IMGDIR = os.path.abspath(os.path.join(BASE, "_images_all"))
ORIGINALS = os.path.join(BASE, "_originals")

SIZE = 720
QUALITY = 82


def human(n):
    return f"{n/1048576:.1f} MB" if n > 1048576 else f"{n/1024:.0f} KB"


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--size", type=int, default=SIZE)
    ap.add_argument("--quality", type=int, default=QUALITY)
    ap.add_argument("--check", action="store_true", help="فقط گزارش وضعیت فعلی")
    a = ap.parse_args()

    pngs = sorted(f for f in os.listdir(IMGDIR) if f.endswith(".png"))
    webps = sorted(f for f in os.listdir(IMGDIR) if f.endswith(".webp"))
    before = sum(os.path.getsize(os.path.join(IMGDIR, f)) for f in pngs + webps)

    print(f"  پوشه      : {IMGDIR}")
    print(f"  PNG       : {len(pngs)}")
    print(f"  WebP      : {len(webps)}")
    print(f"  حجم فعلی  : {human(before)}")

    if a.check or not pngs:
        if not pngs:
            print("\n  چیزی برای بهینه‌سازی نیست.")
        return

    # نسخه اصلی نگه داشته می‌شود؛ اگر بعداً ابعاد بزرگ‌تر لازم شد
    # نباید دوباره هزینه تولید داد.
    os.makedirs(ORIGINALS, exist_ok=True)

    done = 0
    for f in pngs:
        src = os.path.join(IMGDIR, f)
        dst = os.path.join(IMGDIR, f[:-4] + ".webp")
        im = Image.open(src).convert("RGB")
        if max(im.size) > a.size:
            im = im.resize((a.size, a.size), Image.LANCZOS)
        im.save(dst, "WEBP", quality=a.quality, method=6)
        os.replace(src, os.path.join(ORIGINALS, f))
        done += 1

    after = sum(
        os.path.getsize(os.path.join(IMGDIR, f))
        for f in os.listdir(IMGDIR) if f.endswith(".webp")
    )
    print()
    print(f"  تبدیل شد  : {done} تصویر → WebP {a.size}px کیفیت {a.quality}")
    print(f"  حجم جدید  : {human(after)}")
    print(f"  کاهش      : {(1 - after/before)*100:.0f}٪")
    print(f"  اصل‌ها    : {ORIGINALS}")


if __name__ == "__main__":
    main()
