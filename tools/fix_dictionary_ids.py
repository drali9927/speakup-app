#!/usr/bin/env python3
"""
رفع تصادم شناسه‌های دیکشنری بین سطح‌ها.

مسئله
-----
شناسه هر مدخل در `content/export_to_app.py` فقط از املای واژه ساخته
می‌شود (`d-{word}`). این شناسه درون یک سطح یکتاست، ولی بین سطح‌ها نه —
و اپ هر چهار سطح را در **یک** جدول با کلید اصلی `id` می‌ریزد
(`OnConflictStrategy.REPLACE`). نتیجه: مدخل سطح بالاتر، مدخل سطح
پایین‌تر را نابود می‌کند.

نمونه‌ای که روی گوشی دیده شد: کاربر «book» را در A1 با معنی «کتاب» یاد
گرفت؛ پس از دانلود محتوای A2، همان شناسه `d-book` با «رزرو کردن»
بازنویسی شد و کارت لایتنرش معنی عوض کرد.

این اسکریپت دو دسته را جدا می‌کند
--------------------------------
**هم‌نگاره** (بخش کلام متفاوت) — دو واژهٔ واقعاً متفاوت‌اند و باید دو
مدخل بمانند. شناسهٔ مدخلِ سطحِ بالاتر با پسوند بخش کلام یکتا می‌شود.
شناسهٔ سطحِ پایین‌تر **دست نمی‌خورد**، تا کارت‌های لایتنرِ کاربران فعلی
یتیم نشوند.

**تقریباً یکسان** (بخش کلام یکی، فقط نگارش معنی فرق دارد) — تصمیم
ویرایشی است، نه مکانیکی. اسکریپت این‌ها را فقط **گزارش** می‌کند و
دست نمی‌زند.

استفاده
-------
    python3 tools/fix_dictionary_ids.py --check   backend/content   # فقط گزارش
    python3 tools/fix_dictionary_ids.py --write   backend/content   # اعمال
"""
import argparse, json, pathlib, sys
from collections import defaultdict

LEVELS = ["A1", "A2", "B1", "B2"]   # ترتیب = اولویت؛ سطح پایین‌تر شناسه‌اش را نگه می‌دارد


def load(folder: pathlib.Path):
    out = {}
    for lv in LEVELS:
        p = folder / f"{lv.lower()}.json"
        if not p.exists():
            sys.exit(f"پیدا نشد: {p}")
        out[lv] = json.loads(p.read_text(encoding="utf-8"))
    return out


def analyse(bundles):
    by_id = defaultdict(dict)
    for lv in LEVELS:
        for e in bundles[lv]["dictionary"]:
            by_id[e["id"]][lv] = e

    homographs, near_copies = [], []
    for eid, per_level in by_id.items():
        if len({e["translationFa"] for e in per_level.values()}) < 2:
            continue
        if len({e.get("pos") for e in per_level.values()}) > 1:
            homographs.append((eid, per_level))
        else:
            near_copies.append((eid, per_level))
    return homographs, near_copies


def new_id(eid, entry):
    """پسوند از بخش کلام؛ «modal verb» → «modal» تا شناسه ساده بماند."""
    pos = (entry.get("pos") or "x").split()[0].lower()
    return f"{eid}-{pos}"


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("folder", type=pathlib.Path)
    ap.add_argument("--write", action="store_true", help="فایل‌ها را واقعاً عوض کن")
    args = ap.parse_args()

    bundles = load(args.folder)
    homographs, near_copies = analyse(bundles)

    print(f"هم‌نگاره (شناسه عوض می‌شود): {len(homographs)}")
    changes = 0
    for eid, per_level in sorted(homographs):
        levels = [lv for lv in LEVELS if lv in per_level]
        keep, rest = levels[0], levels[1:]
        print(f"\n  {eid}")
        print(f"      {keep}: {per_level[keep]['translationFa']}   (بدون تغییر)")
        for lv in rest:
            e = per_level[lv]
            nid = new_id(eid, e)
            print(f"      {lv}: {e['translationFa']}   →  {nid}")
            if args.write:
                e["id"] = nid
                changes += 1

    print(f"\n\nتقریباً یکسان (دست نخورد — تصمیم ویرایشی): {len(near_copies)}")
    for eid, per_level in sorted(near_copies):
        vals = " | ".join(f"{lv}:{e['translationFa']}" for lv, e in per_level.items())
        print(f"  {eid:<26} {vals}")
    print(
        "\n  ↑ این‌ها یک واژه با نگارش‌های متفاوتِ معنی‌اند. تا وقتی یکی‌شان\n"
        "    انتخاب نشود، معنی‌ای که کاربر می‌بیند به ترتیب دانلود سطح‌ها\n"
        "    بستگی دارد. پیشنهاد: در برگهٔ واژگانِ سطح بالاتر، معنی را با\n"
        "    سطح پایین‌تر یکی کنید تا هر دو یک مدخل بمانند."
    )

    if args.write:
        for lv in LEVELS:
            p = args.folder / f"{lv.lower()}.json"
            p.write_text(
                json.dumps(bundles[lv], ensure_ascii=False, separators=(",", ":")),
                encoding="utf-8",
            )
        print(f"\n✓ {changes} شناسه عوض شد و {len(LEVELS)} فایل بازنویسی شد.")
        print("  یادآوری: نسخهٔ بستهٔ محتوا را بالا ببرید تا اپ‌ها دوباره دانلود کنند.")
    else:
        print("\n(حالت گزارش — چیزی نوشته نشد. برای اعمال: --write)")


if __name__ == "__main__":
    main()
