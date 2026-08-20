#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
تصویرهای ابزارک صفحه خانه.

    export OPENAI_API_KEY=sk-...
    python3 content/widget_art.py

ابزارک تا امروز از اموجی استفاده می‌کرد: 🔥 وقتی امروز تمرین شده و 🕯 وقتی
نه. ایده درست بود — حالتِ تصویر باید عوض شود نه فقط عدد — اما اموجی روی هر
گوشی شکل دیگری دارد و در ابزارکِ سفید، ریز و بی‌جان دیده می‌شود.

دو تصویر ساخته می‌شود که یک **جفت** باشند: همان شکل، یکی روشن و یکی خاموش.
تفاوتشان باید از فاصله چند متری خوانده شود، چون تنها کارِ این تصویر همین
است.

پس‌زمینه شفاف است تا روی کارت سفید ابزارک بنشیند، و شکل‌ها درشت و ساده‌اند
چون در ۹۶ پیکسل دیده می‌شوند نه ۱۰۲۴.
"""
import base64
import json
import os
import sys
import urllib.error
import urllib.request

API_BASE = "https://api.openai.com/v1"

STYLE = (
    "Flat vector icon illustration, bold simple shapes, thick soft rounded forms, "
    "smooth gradients, no outlines, no text, no letters, no numbers, "
    "centered single object, generous empty margin around it, "
    "fully transparent background, crisp and readable when scaled down to 96 pixels. "
)

ART = {
    "widget_flame_on": STYLE + (
        "A single warm campfire flame, lively and burning bright. "
        "Rich amber and orange gradient with a pale yellow core, a soft warm glow. "
        "The shape leans slightly and feels alive and energetic."
    ),
    # سخت‌گیری روی «فقط شعله»: تلاش اول برای نسخه خاموش، هیزم هم کشید و
    # جفت به هم خورد — شکل موقع تعویض حالت می‌پرید.
    "widget_flame_off": STYLE + (
        "ONLY a flame shape and nothing else. Absolutely no logs, no firewood, "
        "no sticks, no ground, no base, no sparks, no smoke — just the flame itself, "
        "floating alone. The flame is unlit and dormant: cool neutral grey and pale "
        "slate tones, completely desaturated, no warm colour at all, no glow. "
        "Exactly the same silhouette, proportion and size as a burning flame, so it "
        "reads as the same object simply switched off."
    ),
}


def generate(prompt, api_key, model="gpt-image-1.5", size="1024x1024", quality="high"):
    body = json.dumps({
        "model": model,
        "prompt": prompt,
        "n": 1,
        "size": size,
        "quality": quality,
        "output_format": "png",
        # شفافیت لازم است: ابزارک زمینه سفید دارد و مربع رنگی رویش زشت است
        "background": "transparent",
    }).encode()
    req = urllib.request.Request(
        f"{API_BASE}/images/generations",
        data=body,
        headers={"Authorization": f"Bearer {api_key}", "Content-Type": "application/json"},
    )
    with urllib.request.urlopen(req, timeout=300) as r:
        payload = json.load(r)
    return base64.b64decode(payload["data"][0]["b64_json"])


def main():
    api_key = os.environ.get("OPENAI_API_KEY")
    if not api_key:
        sys.exit("متغیر محیطی OPENAI_API_KEY تنظیم نشده است.")

    out_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "_widget")
    os.makedirs(out_dir, exist_ok=True)

    for name, prompt in ART.items():
        path = os.path.join(out_dir, f"{name}.png")
        if os.path.exists(path):
            print(f"از قبل هست: {name}")
            continue
        try:
            data = generate(prompt, api_key)
        except urllib.error.HTTPError as e:
            sys.exit(f"{name}: HTTP {e.code} — {e.read().decode('utf-8', 'replace')[:300]}")
        with open(path, "wb") as f:
            f.write(data)
        print(f"ساخته شد: {path}  ({len(data) / 1024:.0f} کیلوبایت)")


if __name__ == "__main__":
    main()
