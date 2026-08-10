#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
موتور تولید تصویر — ابزار جانبی و موقت.

تصاویر کارت‌های واژه را از روی اکسل محتوا با API تولید تصویر می‌سازد.
یک‌بار اجرا می‌شود، خروجی داخل assets اپ می‌نشیند، و بعد از آن دیگر لازم نیست.

این بخشی از اپ نیست — هیچ کلید API در اپ قرار نمی‌گیرد.

    # ۱) اول همیشه بدون هزینه ببینید چه چیزی ساخته می‌شود
    python3 imagegen.py --dry-run

    # ۲) تست واقعی با ۳ تصویر
    export OPENAI_API_KEY=sk-...
    python3 imagegen.py --limit 3

    # ۳) بعد از تأیید کیفیت، یک درس کامل
    python3 imagegen.py --lessons 1

    # ۴) و در نهایت همه
    python3 imagegen.py

اجرای دوباره، تصاویری که قبلاً ساخته شده‌اند را رد می‌کند (--force برای بازسازی).
"""

import argparse, base64, csv, json, os, re, sys, time, urllib.error, urllib.request
from concurrent.futures import ThreadPoolExecutor, as_completed

from openpyxl import load_workbook

BASE = os.path.dirname(os.path.abspath(__file__))
# سطح از متغیر محیطی یا خط فرمان — با افزودن A2، نام فایل ثابت
# دیگر جواب نمی‌داد.
LEVEL = os.environ.get("SPEAKUP_LEVEL", "A1")
XLSX = os.path.join(BASE, f"{LEVEL}-محتوا.xlsx")
# ⚠️ خروجی داخل APK نمی‌رود. فقط درس اول همراه اپ است و بقیه از سرور
# می‌آیند؛ اگر اینجا به assets اشاره کند، با هر تولید تازه حجم APK چند
# برابر می‌شود و کسی هم متوجه نمی‌شود تا زمان انتشار.
OUTDIR = os.path.abspath(os.path.join(BASE, "_images_all"))
# تصاویری که به سرور رفته‌اند — مرجع «قبلاً ساخته شده»
PUBLISHED = os.path.abspath(os.path.join(BASE, "..", "backend", "content", "images"))
LOGCSV = os.path.join(BASE, "imagegen-log.csv")
PREVIEW = os.path.join(BASE, "imagegen-preview.html")

API_BASE = os.environ.get("OPENAI_BASE_URL", "https://api.openai.com/v1")

# قیمت تقریبی هر تصویر ۱۰۲۴×۱۰۲۴ بر حسب دلار.
# مرجع: قیمت‌های اعلامی ۲۰۲۶ — پیش از اجرای انبوه، قیمت روز را تأیید کنید.
PRICES = {
    ("gpt-image-1.5", "low"):    0.009,
    ("gpt-image-1.5", "medium"): 0.040,
    ("gpt-image-1.5", "high"):   0.170,
    ("gpt-image-1-mini", "low"): 0.005,
    ("gpt-image-1-mini", "medium"): 0.020,
}

# ---------------------------------------------------------------- سبک تصویر
#
# یکدستی مهم‌ترین معیار است. API حافظه بین درخواست‌ها ندارد، پس تنها راه
# یکدست ماندن ۲۰۰ تصویر، تکرار دقیقاً همین توصیف سبک در هر درخواست است.
# این متن را بدون دلیل تغییر ندهید؛ اگر تغییر دادید، همه تصاویر را دوباره بسازید.

#
# مخاطب محصول ۲۵ تا ۳۵ ساله است. هر سبکی که بوی کتاب کودک بدهد رد است:
# نه چهره‌های گرد کارتونی، نه چشم‌های درشت، نه رنگ‌های اشباع بچگانه.

_NO_TEXT = ("ABSOLUTELY NO text, letters, words, numbers, captions, signs, "
            "logos or writing anywhere in the image.")
_ADULT = ("Subjects are adults aged 25-35 with realistic adult body proportions "
          "and mature facial features. "
          "No children, no teenagers, no cartoon-baby proportions, no oversized eyes.")

# پوشش محجوب — الزام بازار ایران و شرط بازبینی کافه‌بازار.
# بدون این قید، مدل برای صحنه‌های خانگی و خواب لباس نامناسب تولید می‌کند.
# این متن اختیاری نیست؛ حذفش یعنی تصاویر غیرقابل انتشار.
#
# بیان مثبت است، نه فهرست نفی. نسخه اول با نفی‌های صریح درباره پوشش بدن
# نوشته شده بود و فیلتر ایمنی، صحنه‌های دارای کودک را رد می‌کرد.
# توصیف آنچه باید باشد، هم از فیلتر عبور می‌کند هم مدل بهتر اجرا می‌کند.
_MODEST = (
    "Everyone in every scene wears modest, fully covering clothing: long sleeves "
    "down to the wrist, high closed necklines, loose comfortable fit, "
    "full-length trousers or long skirts. Home and evening scenes show the same "
    "modest long-sleeved clothing; resting scenes show a person covered by a blanket."
)

STYLES = {
    # سبک پیشنهادی: تصویرسازی تحریریه مدرن — همان زبان بصری اپ‌های بزرگسال
    "editorial": (
        "Modern editorial illustration for a professional language-learning app. "
        "Semi-flat vector shapes with subtle grain texture and soft depth. "
        "Warm sophisticated palette: terracotta, sage green, sand beige, dusty blue, "
        "cream. Inviting everyday atmosphere with soft natural light — not corporate "
        "or sterile. Confident uncluttered composition, plain light background. " + _ADULT + " " + _MODEST + " " + _NO_TEXT
    ),
    # جایگزین گرم‌تر: نقاشی دیجیتال نیمه‌واقع‌گرا
    "muted": (
        "Soft semi-realistic digital illustration for an adult language-learning app. "
        "Painterly rendering with gentle lighting and restrained detail. "
        "Desaturated natural palette: dusty blue, olive, warm grey, muted rust. "
        "Calm realistic contemporary settings, shallow depth of field feel, "
        "plain uncluttered background. " + _ADULT + " " + _MODEST + " " + _NO_TEXT
    ),
    # جایگزین مینیمال: برای وقتی که سادگی مهم‌تر از گرمی است
    "minimal": (
        "Clean minimal flat illustration for a professional language-learning app. "
        "Bold simple geometric shapes, no outlines, generous negative space. "
        "Restrained palette of slate blue, sand, muted teal and charcoal on off-white. "
        + _ADULT + " " + _MODEST + " " + _NO_TEXT
    ),
    # سبک قبلی — کودکانه، فقط برای مقایسه نگه داشته شده
    "kids": (
        "Children's educational illustration. Soft digital watercolor with clean "
        "line art, warm friendly palette of coral, teal, mustard and soft blue. "
        "Rounded shapes, gentle shading. Plain very light neutral background. "
        + _NO_TEXT
    ),
}
DEFAULT_STYLE = "editorial"

SCENE_RE = re.compile(r"Scene:\s*(.+)$", re.S)


# واژه‌هایی مثل boy و child ذاتاً کودک را تصویر می‌کنند.
# فیلتر ایمنی OpenAI هر توصیف تفصیلی پوشش و بدن را در کنار کودک رد می‌کند —
# و این محدودیت درستی است. برای این صحنه‌ها قید کوتاه و خنثی به‌کار می‌رود.
_CHILD_WORDS = re.compile(r'\b(child|children|boy|girl|baby|toddler|kid|kids|school-age)\b', re.I)
_MODEST_LIGHT = "Everyone wears simple modest everyday clothing."


def build_prompt(scene: str, style: str) -> str:
    base = STYLES[style]
    if _CHILD_WORDS.search(scene):
        base = base.replace(_MODEST, _MODEST_LIGHT).replace(_ADULT, "")
    return f"{base}\n\nSubject: {scene.strip().rstrip('.')}."


def read_rows(lessons=None):
    wb = load_workbook(XLSX, data_only=True)
    ws = wb["واژگان"]
    head = [c.value for c in ws[1]]
    out = []
    for r in ws.iter_rows(min_row=2, values_only=True):
        if r[0] is None:
            continue
        d = dict(zip(head, r))
        if lessons and d["درس"] not in lessons:
            continue
        raw = d["پرامپت تصویر (کپی کنید)"] or ""
        m = SCENE_RE.search(raw)
        out.append({
            "id": d["شناسه"],
            "lesson": d["درس"],
            "word": d["واژه"],
            "meaning": d["معنی فارسی"],
            "file": d["نام فایل تصویر"],
            "scene": (m.group(1) if m else raw).strip(),
        })
    return out


def generate(prompt, model, quality, size, api_key, timeout=180):
    body = json.dumps({
        "model": model,
        "prompt": prompt,
        "n": 1,
        "size": size,
        "quality": quality,
        "output_format": "png",
    }).encode()

    req = urllib.request.Request(
        f"{API_BASE}/images/generations",
        data=body,
        headers={"Authorization": f"Bearer {api_key}", "Content-Type": "application/json"},
    )
    with urllib.request.urlopen(req, timeout=timeout) as r:
        payload = json.load(r)
    return base64.b64decode(payload["data"][0]["b64_json"]), payload.get("usage", {})


def generate_with_retry(prompt, model, quality, size, api_key, attempts=4):
    delay = 4
    for i in range(attempts):
        try:
            return generate(prompt, model, quality, size, api_key)
        except urllib.error.HTTPError as e:
            detail = e.read().decode("utf-8", "replace")[:400]
            # 429 و 5xx قابل تلاش مجدد‌اند؛ بقیه نه
            if e.code in (429, 500, 502, 503, 504) and i < attempts - 1:
                time.sleep(delay)
                delay *= 2
                continue
            raise RuntimeError(f"HTTP {e.code}: {detail}") from None
        except urllib.error.URLError as e:
            if i < attempts - 1:
                time.sleep(delay)
                delay *= 2
                continue
            raise RuntimeError(f"اتصال برقرار نشد: {e.reason}") from None


def write_preview(rows):
    """صفحه HTML برای بازبینی سریع همه تصاویر ساخته‌شده در یک نگاه."""
    cards = []
    for r in rows:
        p = os.path.join(OUTDIR, r["file"])
        if not os.path.exists(p):
            continue
        rel = os.path.relpath(p, BASE)
        cards.append(
            f'<figure><img src="{rel}" loading="lazy">'
            f'<figcaption><b>{r["word"]}</b><br>{r["meaning"]}'
            f'<br><small>درس {r["lesson"]}</small></figcaption></figure>'
        )
    html = f"""<!doctype html><meta charset="utf-8"><title>بازبینی تصاویر</title>
<style>
 body{{font-family:system-ui,sans-serif;background:#fafafc;margin:0;padding:24px;direction:rtl}}
 h1{{font-size:20px}}
 .g{{display:grid;grid-template-columns:repeat(auto-fill,minmax(200px,1fr));gap:16px}}
 figure{{margin:0;background:#fff;border-radius:12px;padding:10px;box-shadow:0 1px 3px #0001}}
 img{{width:100%;border-radius:8px;display:block;aspect-ratio:1;object-fit:cover}}
 figcaption{{text-align:center;font-size:13px;margin-top:8px;line-height:1.7}}
 small{{color:#888}}
</style>
<h1>بازبینی تصاویر — {len(cards)} تصویر</h1>
<div class="g">{''.join(cards)}</div>"""
    with open(PREVIEW, "w", encoding="utf-8") as f:
        f.write(html)
    return len(cards)


def log_rows(entries):
    new = not os.path.exists(LOGCSV)
    with open(LOGCSV, "a", newline="", encoding="utf-8") as f:
        w = csv.writer(f)
        if new:
            w.writerow(["time", "id", "word", "file", "model", "quality", "usd", "status"])
        w.writerows(entries)


def parse_lessons(spec):
    """«1,2» یا «11-20» یا ترکیبشان → مجموعه شماره درس.

    بازه لازم شد چون با رسیدن به ۲۰ درس، نوشتن تک‌تک شماره‌ها هم طولانی
    است و هم جای اشتباه دارد — و اشتباه اینجا یعنی پول خرج تصویر اضافه.
    """
    if not spec:
        return None
    out = set()
    for part in spec.split(","):
        part = part.strip()
        if "-" in part:
            lo, hi = part.split("-", 1)
            out.update(range(int(lo), int(hi) + 1))
        elif part:
            out.add(int(part))
    return out or None


def main():
    ap = argparse.ArgumentParser(description="موتور تولید تصویر کارت‌های واژه")
    ap.add_argument("--model", default="gpt-image-1.5",
                    help="مدل تولید تصویر (پیش‌فرض gpt-image-1.5)")
    ap.add_argument("--quality", default="low", choices=["low", "medium", "high"],
                    help="کیفیت — با low شروع کنید")
    ap.add_argument("--size", default="1024x1024")
    ap.add_argument("--style", default=DEFAULT_STYLE, choices=list(STYLES))
    ap.add_argument("--limit", type=int, help="فقط این تعداد تصویر بساز (برای تست)")
    ap.add_argument("--lessons", help="فقط این درس‌ها، مثلاً 1,2 یا 11-20")
    ap.add_argument("--level", default=LEVEL, help="کد سطح، مثلاً A1 یا A2")
    ap.add_argument("--force", action="store_true", help="تصاویر موجود را دوباره بساز")
    ap.add_argument("--dry-run", action="store_true", help="بدون تماس با API — فقط نمایش و برآورد هزینه")
    ap.add_argument("--workers", type=int, default=3, help="تعداد درخواست همزمان")
    ap.add_argument("--preview-only", action="store_true", help="فقط صفحه بازبینی را بساز")
    a = ap.parse_args()

    global XLSX
    XLSX = os.path.join(BASE, f"{a.level}-محتوا.xlsx")
    if not os.path.exists(XLSX):
        sys.exit(f"فایل اکسل پیدا نشد: {XLSX}")
    lessons = parse_lessons(a.lessons)
    rows = read_rows(lessons)

    os.makedirs(OUTDIR, exist_ok=True)

    if a.preview_only:
        print(f"صفحه بازبینی ساخته شد با {write_preview(rows)} تصویر:\n  {PREVIEW}")
        return

    # تصویری که قبلاً منتشر شده دوباره ساخته نمی‌شود.
    #
    # OUTDIR فقط PNGهای خام را دارد؛ نسخه منتشرشده WebP است و در پوشه
    # سرور می‌ماند. بدون این بررسی، هر اجرای تازه همه تصاویرِ از قبل
    # منتشرشده را دوباره می‌ساخت — هم پول دوباره، هم تصویر متفاوت برای
    # واژه‌ای که کاربر قبلاً دیده است.
    def already(r):
        stem = r["file"].rsplit(".", 1)[0]
        return (os.path.exists(os.path.join(OUTDIR, r["file"]))
                or os.path.exists(os.path.join(PUBLISHED, stem + ".webp")))

    todo = [r for r in rows if a.force or not already(r)]
    if a.limit:
        todo = todo[:a.limit]

    unit = PRICES.get((a.model, a.quality))
    est = f"${unit * len(todo):.2f}" if unit else "نامشخص"

    print(f"  مدل        : {a.model} · کیفیت {a.quality} · {a.size}")
    print(f"  سبک        : {a.style}")
    print(f"  کل واژه‌ها  : {len(rows)}")
    print(f"  موجود      : {len(rows) - len(todo) if not a.limit else len(rows) - len(todo)}")
    print(f"  ساخته می‌شود: {len(todo)}")
    print(f"  برآورد هزینه: {est}" + ("" if unit else "  (قیمت این ترکیب در جدول نیست)"))
    print()

    if not todo:
        print("چیزی برای ساختن نیست.")
        write_preview(rows)
        return

    if a.dry_run:
        print("--- نمونه پرامپت کامل ---\n")
        print(build_prompt(todo[0]["scene"], a.style))
        print(f"\n--- {min(len(todo), 10)} مورد اول ---")
        for r in todo[:10]:
            print(f"  {r['id']}  {r['word']:<16} → {r['file']}")
        print("\nاجرای واقعی: همین دستور را بدون --dry-run بزنید.")
        return

    api_key = os.environ.get("OPENAI_API_KEY")
    if not api_key:
        sys.exit("متغیر محیطی OPENAI_API_KEY تنظیم نشده است.")

    if len(todo) > 20:
        ans = input(f"{len(todo)} تصویر با برآورد {est} ساخته می‌شود. ادامه؟ [y/N] ")
        if ans.strip().lower() not in ("y", "yes"):
            print("لغو شد.")
            return

    done = failed = 0
    entries = []
    started = time.time()

    def work(r):
        prompt = build_prompt(r["scene"], a.style)
        data, usage = generate_with_retry(prompt, a.model, a.quality, a.size, api_key)
        with open(os.path.join(OUTDIR, r["file"]), "wb") as f:
            f.write(data)
        return r, usage

    with ThreadPoolExecutor(max_workers=a.workers) as ex:
        futures = {ex.submit(work, r): r for r in todo}
        for fut in as_completed(futures):
            r = futures[fut]
            stamp = time.strftime("%Y-%m-%d %H:%M:%S")
            try:
                fut.result()
                done += 1
                entries.append([stamp, r["id"], r["word"], r["file"], a.model, a.quality, unit or "", "ok"])
                print(f"  ✅ [{done + failed}/{len(todo)}] {r['word']:<16} → {r['file']}")
            except Exception as e:
                failed += 1
                entries.append([stamp, r["id"], r["word"], r["file"], a.model, a.quality, 0, f"error: {e}"])
                print(f"  ❌ [{done + failed}/{len(todo)}] {r['word']:<16} {e}")

    log_rows(entries)
    n = write_preview(rows)
    spent = f"${unit * done:.2f}" if unit else "نامشخص"

    print()
    print(f"  ساخته شد   : {done}")
    print(f"  ناموفق     : {failed}")
    print(f"  هزینه تقریبی: {spent}")
    print(f"  زمان       : {time.time() - started:.0f} ثانیه")
    print(f"  گزارش      : {LOGCSV}")
    print(f"  بازبینی    : {PREVIEW}  ({n} تصویر)")
    if failed:
        sys.exit(1)


if __name__ == "__main__":
    main()
