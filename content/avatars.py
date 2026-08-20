#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
شخصیت‌های پروفایل.

    export OPENAI_API_KEY=sk-...
    python3 content/avatars.py

کاربر یکی از این‌ها را برای پروفایلش برمی‌دارد. چرا شخصیتِ آماده و نه
آپلود عکس: آپلود یعنی ذخیره‌سازی، تعدیل محتوا، و حریم خصوصی — سه دردسر
که هیچ‌کدام به یادگیری زبان ربط ندارند. مجموعه‌ای از شخصیت‌های خوش‌ساخت،
همان حس شخصی‌سازی را بدون هیچ‌کدام از این‌ها می‌دهد.

هشت شخصیت، همه در یک سبک تا کنار هم یک مجموعه دیده شوند و نه هشت تصویر
بی‌ربط. مخاطب ما بزرگسال ایرانی است، پس شخصیت‌ها بزرگسال‌اند و نه
کارتونِ کودکانه.
"""
import base64
import json
import os
import sys
import urllib.error
import urllib.request

API_BASE = "https://api.openai.com/v1"

# سبک مشترک — همان چیزی که هشت تصویر را یک مجموعه می‌کند
STYLE = (
    "Friendly flat vector avatar portrait for a language-learning app, head and "
    "shoulders only, centered in frame, simple bold shapes, smooth gradients, "
    "no outlines, warm and approachable. Clean solid pastel circular background. "
    "Adult in their late twenties or thirties, never a child, natural proportions, "
    "no oversized eyes, no text, no letters, no logos. "
    "Subject: "
)

AVATARS = {
    "avatar_01": "a smiling woman with dark hair in a bun, wearing a green sweater",
    "avatar_02": "a smiling man with short dark hair and a trimmed beard, wearing a rust shirt",
    "avatar_03": "a woman wearing a soft teal headscarf, calm confident smile",
    "avatar_04": "a man with glasses and wavy hair, wearing a mustard shirt",
    "avatar_05": "a woman with shoulder-length curly hair, wearing a blue denim jacket",
    "avatar_06": "a man with a shaved head and a warm smile, wearing a grey hoodie",
    "avatar_07": "a woman with a burgundy headscarf and small earrings, gentle smile",
    "avatar_08": "a man with light brown hair and a friendly grin, wearing a navy jumper",
}


def generate(prompt, api_key):
    body = json.dumps({
        "model": "gpt-image-1.5",
        "prompt": prompt,
        "n": 1,
        "size": "1024x1024",
        "quality": "high",
        "output_format": "png",
    }).encode()
    req = urllib.request.Request(
        f"{API_BASE}/images/generations",
        data=body,
        headers={"Authorization": f"Bearer {api_key}", "Content-Type": "application/json"},
    )
    with urllib.request.urlopen(req, timeout=300) as r:
        return base64.b64decode(json.load(r)["data"][0]["b64_json"])


def main():
    api_key = os.environ.get("OPENAI_API_KEY")
    if not api_key:
        sys.exit("متغیر محیطی OPENAI_API_KEY تنظیم نشده است.")

    out_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "_avatars")
    os.makedirs(out_dir, exist_ok=True)

    for name, subject in AVATARS.items():
        path = os.path.join(out_dir, f"{name}.png")
        if os.path.exists(path):
            print(f"از قبل هست: {name}")
            continue
        try:
            data = generate(STYLE + subject, api_key)
        except urllib.error.HTTPError as e:
            sys.exit(f"{name}: HTTP {e.code} — {e.read().decode('utf-8', 'replace')[:300]}")
        with open(path, "wb") as f:
            f.write(data)
        print(f"ساخته شد: {name}  ({len(data) / 1024:.0f} کیلوبایت)")


if __name__ == "__main__":
    main()
