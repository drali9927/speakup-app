#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
اکسل محتوا  →  بسته JSON برای اپ.

هر بار محتوا را در اکسل به‌روز کردید، این را اجرا کنید.
خروجی مستقیم در assets اپ می‌نشیند و اپ در اولین اجرا آن را وارد دیتابیس می‌کند.
"""

import argparse, json, os, sys
from openpyxl import load_workbook

BASE = os.path.dirname(os.path.abspath(__file__))

# سطح از خط فرمان می‌آید. تا پیش از A2 همه‌جا «A1» ثابت نوشته شده بود؛
# با افزودن سطح دوم، آن ثابت‌ها به‌جای یک تغییر، ده تغییر پراکنده می‌شدند.
LEVEL = os.environ.get("SPEAKUP_LEVEL", "A1")


def paths(level):
    low = level.lower()
    return (
        os.path.join(BASE, f"{level}-محتوا.xlsx"),
        os.path.abspath(os.path.join(BASE, "..", "android", "app", "src", "main",
                                     "assets", f"content_{low}.json")),
    )

LEVELS = [
    # نام فارسی همان اصطلاح آشنای کلاس زبان است. کاربر ایرانی «مقدماتی ۲» را
    # از قبل می‌شناسد و ترتیبش را هم می‌فهمد؛ نام‌های توصیفی هیچ‌کدام را
    # نمی‌رساندند.
    {"code": "starter", "titleFa": "پیش‌مقدماتی", "titleEn": "Starter",           "sortOrder": 0},
    {"code": "A1",      "titleFa": "مقدماتی ۱",  "titleEn": "A1 · Elementary 1",  "sortOrder": 1},
    {"code": "A2",      "titleFa": "مقدماتی ۲",  "titleEn": "A2 · Elementary 2",  "sortOrder": 2},
    {"code": "B1",      "titleFa": "متوسط ۱",    "titleEn": "B1 · Intermediate 1","sortOrder": 3},
    {"code": "B2",      "titleFa": "متوسط ۲",    "titleEn": "B2 · Intermediate 2","sortOrder": 4},
    {"code": "C1",      "titleFa": "پیشرفته",    "titleEn": "C1 · Advanced",      "sortOrder": 5},
]

# رنگ شش‌ضلعی شماره درس — ریتم بصری لیست (سند ۰۲ بخش ۲.۳)
PALETTE = ["#F5A623", "#A8B400", "#00A99D", "#4A90D9", "#9B59B6",
           "#E8663D", "#3DBE8B", "#D94F70", "#5C6BC0", "#F2B705"]

# نوع فارسی در اکسل  →  ActivityType در کد کاتلین
TYPE_MAP = {
    "چندگزینه‌ای":      "MULTIPLE_CHOICE",
    "تطبیق":            "MATCHING",
    "جای خالی":         "FILL_BLANK",
    "متن آزاد":         "FREE_TEXT",
    "مرتب‌سازی":        "REORDER",
    "ترجمه به انگلیسی": "TRANSLATE_TO_EN",
    "گفتار":            "SPEAKING",
}
PRODUCTIVE = {"FILL_BLANK", "FREE_TEXT", "REORDER", "TRANSLATE_TO_EN", "SPEAKING", "LISTENING"}


def rows(ws):
    """ردیف‌های برگه را به dict تبدیل می‌کند (کلید = عنوان ستون)."""
    head = [c.value for c in ws[1]]
    for r in ws.iter_rows(min_row=2, values_only=True):
        if r[0] is None:
            continue
        yield dict(zip(head, r))


def main():
    global LEVEL
    ap = argparse.ArgumentParser()
    ap.add_argument("--level", default=LEVEL, help="کد سطح، مثلاً A1 یا A2")
    LEVEL = ap.parse_args().level
    XLSX, OUT = paths(LEVEL)

    if not os.path.exists(XLSX):
        sys.exit(f"فایل اکسل پیدا نشد: {XLSX}")
    wb = load_workbook(XLSX, data_only=True)

    lessons, sections, activities, items = [], [], [], []

    # --- گروه‌بندی داده‌های اکسل بر اساس درس
    vocab, dialog, grammar, exercise = {}, {}, {}, {}
    for r in rows(wb["واژگان"]):     vocab.setdefault(r["درس"], []).append(r)
    for r in rows(wb["مکالمه"]):     dialog.setdefault(r["درس"], []).append(r)
    for r in rows(wb["گرامر"]):      grammar.setdefault(r["درس"], []).append(r)
    for r in rows(wb["تمرین‌ها"]):   exercise.setdefault(r["درس"], []).append(r)

    # داستانک اختیاری است: سطوحی که هنوز ندارند بدون خطا رد می‌شوند.
    story = {}
    if "داستانک" in wb.sheetnames:
        for r in rows(wb["داستانک"]):
            story.setdefault(r["درس"], []).append(r)

    def sec(lesson_id, stype, order, minutes):
        sid = f"{lesson_id}-S{order}"
        sections.append({"id": sid, "lessonId": lesson_id, "type": stype,
                         "sortOrder": order, "estimatedMinutes": minutes})
        return sid

    def act(section_id, order, title, desc, atype):
        aid = f"{section_id}-A{order}"
        activities.append({"id": aid, "sectionId": section_id, "title": title,
                           "descriptionFa": desc, "activityType": atype, "sortOrder": order})
        return aid

    warnings = []

    # تخمین زمان از خود محتوا، نه دستی.
    #
    # عددهای قبلی از رقیب کپی شده بودند و با محتوای ما نمی‌خواندند: بخش
    # گرامر «۱۲´» نشان می‌داد در حالی که ۲.۴ دقیقه بود، و کاربر با دیدن
    # آن عدد اصلاً بازش نمی‌کرد.
    #
    # ضربه‌ها از رفتار واقعی صفحه‌ها شمرده شده، و برای کارت آموزشی
    # زمان خواندن متن هم اضافه می‌شود.
    TAPS = {
        "FLASHCARD": 4, "WORD_MATCH": 2, "DIALOGUE": 0.2, "TEACHING": 2,
        "MULTIPLE_CHOICE": 2, "MATCHING": 2, "FILL_BLANK": 6, "FREE_TEXT": 8, "STORY": 1,
        "TRANSLATE_TO_EN": 6, "REORDER": 5, "SPEAKING": 2, "LISTENING": 7,
    }
    SEC_PER_TAP = 2.2
    FA_WPM = 200
    # سرعت خواندن انگلیسی برای زبان‌آموز — حدود نصف سرعت زبان مادری
    EN_WPM = 90

    def estimate(section_id):
        secs = 0.0
        for a in activities:
            if a["sectionId"] != section_id:
                continue
            n = sum(1 for i in items if i["activityId"] == a["id"])
            secs += TAPS.get(a["activityType"], 3) * n * SEC_PER_TAP
            if a["activityType"] == "STORY":
                words = sum(
                    len((i.get("prompt") or "").split())
                    for i in items if i["activityId"] == a["id"]
                )
                secs += words / EN_WPM * 60
            if a["activityType"] == "TEACHING":
                words = sum(
                    len((i.get("promptFa") or "").split()) + len((i.get("correctAnswer") or "").split())
                    for i in items if i["activityId"] == a["id"]
                )
                secs += words / FA_WPM * 60
        return max(1, round(secs / 60))

    for lr in rows(wb["نقشه دروس"]):
        n = lr["درس"]
        if n not in vocab:                      # فقط درس‌هایی که محتوا دارند
            continue
        lesson_id = f"{LEVEL}-L{n:02d}"
        lessons.append({
            "id": lesson_id, "levelCode": LEVEL, "number": n,
            "titleEn": lr["عنوان انگلیسی"],
            "grammarTopicFa": lr["موضوع گرامر"],
            "themeFa": lr["ماموریت درس"],
            "isFree": lr["رایگان؟"] == "بله",
            "estimatedMinutes": 26,
            "colorHex": PALETTE[(n - 1) % len(PALETTE)],
        })

        # ۱ — واژگان، در دو نیمه
        #
        # ۲۰ واژه یکجا یعنی ۱۲۰ ضربه پشت‌سرهم؛ کاربر وسطش رها می‌کند و
        # هیچ حس «تمام کردن» نمی‌گیرد. دو نیمه ده‌تایی، دو بار حس اتمام
        # و دو بار امتیاز می‌دهد — و هر کدام با وعده «روزی ده دقیقه» می‌خواند.
        sid = sec(lesson_id, "VOCABULARY", 1, 0)
        half = (len(vocab[n]) + 1) // 2
        for part, chunk in enumerate([vocab[n][:half], vocab[n][half:]], 1):
            if not chunk:
                continue
            aid = act(sid, part * 2 - 1, f"Vocabulary {part}",
                      "لغات جدید درس را مرور کرده و معنای آن را بخاطر بسپارید.", "FLASHCARD")
            for i, v in enumerate(chunk, 1):
                items.append({
                    "id": f"{aid}-I{i:02d}", "activityId": aid, "sortOrder": i,
                    "prompt": v["واژه"], "promptFa": v["معنی فارسی"],
                    "hintFa": f'{v["بخش کلام"]} · {v["تلفظ IPA"]}',
                    "imageFile": v["نام فایل تصویر"], "ttsText": v["متن صوتی"],
                    "targetWord": v["واژه"],
                    # جمله مثال نمایشی است، نه پاسخ — در ستون‌های example ذخیره می‌شود
                    "exampleEn": v["جمله مثال"], "exampleFa": v["ترجمه جمله"],
                })

            # بازی جفت‌یابی بلافاصله بعد از همان ده واژه — بازیابی در حالی
            # که هنوز تازه‌اند، نه بیست واژه بعدتر.
            aid = act(sid, part * 2, f"Word Match {part}", "واژه‌ها را به معنی‌شان وصل کن.", "WORD_MATCH")
            for i, v in enumerate(chunk, 1):
                items.append({
                    "id": f"{aid}-I{i:02d}", "activityId": aid, "sortOrder": i,
                    "prompt": v["واژه"], "promptFa": v["معنی فارسی"],
                    "targetWord": v["واژه"], "ttsText": v["واژه"],
                })

        # ۲ — مکالمه
        if n in dialog:
            sid = sec(lesson_id, "CONVERSATION", 2, 0)
            # DIALOGUE و نه LISTENING: مکالمه صفحه گفتگو دارد، نه تمرین
            # دیکته. وقتی نوعش LISTENING بود، هر خط مکالمه به یک دیکته
            # چندجمله‌ای تبدیل می‌شد که نه شدنی بود نه هدف این بخش.
            aid = act(sid, 1, "Conversation 1", "مکالمه درس را گوش کنید و دنبال کنید.", "DIALOGUE")
            for i, d in enumerate(dialog[n], 1):
                items.append({
                    "id": f"{aid}-I{i:02d}", "activityId": aid, "sortOrder": i,
                    "prompt": d["جمله انگلیسی"], "promptFa": d["ترجمه فارسی"],
                    "ttsText": d["جمله انگلیسی"], "hintFa": d["گوینده"],
                    # جنسیت گوینده از خود محتوا می‌آید و نه از شماره خط.
                    # پیش‌تر اپ فرض می‌کرد نوبت‌ها یک‌درمیان‌اند؛ این فرض
                    # امروز درست است ولی با اولین مکالمه‌ای که دو خط پشت‌سرهم
                    # از یک نفر داشته باشد می‌شکند، و بی‌سروصدا هم می‌شکند.
                    "voice": {"زن": "F", "مرد": "M"}.get(str(d.get("صدای TTS") or "").strip()),
                })

        # ۳ — گرامر: کارت‌های آموزش + تمرین‌های غیرگفتاری
        # --- داستانک (فقط دروسی که برایشان نوشته شده)
        #
        # جایش عمداً بعد از واژگان و مکالمه است: داستان باید **بعد از**
        # دیدن واژه‌ها خوانده شود، وگرنه به‌جای ورودی قابل‌فهم، تمرین
        # حدس زدن می‌شود.
        if n in story:
            rows_n = story[n]
            paras = [r for r in rows_n if (r["نوع"] or "").strip() == "بند"]
            qs = [r for r in rows_n if (r["نوع"] or "").strip() == "پرسش"]
            ssid = sec(lesson_id, "STORY", 25, 0)

            aid = act(ssid, 1, rows_n[0]["عنوان"] or "Story",
                      "داستان را بخوان و گوش کن، بعد به پرسش‌ها جواب بده.", "STORY")
            for i, r in enumerate(paras, 1):
                items.append({
                    "id": f"{aid}-I{i:02d}", "activityId": aid, "sortOrder": i,
                    "prompt": r["متن انگلیسی"], "promptFa": r["ترجمه فارسی"],
                    "ttsText": r["متن انگلیسی"],
                })

            for qi, r in enumerate(qs, 1):
                qaid = act(ssid, 1 + qi, f"Question {qi}",
                           "بر اساس داستان جواب بده.", "MULTIPLE_CHOICE")
                items.append({
                    "id": f"{qaid}-I01", "activityId": qaid, "sortOrder": 1,
                    "prompt": r["متن انگلیسی"], "promptFa": r["ترجمه فارسی"],
                    "correctAnswer": r["پاسخ درست"],
                    "options": r["گزینه‌ها"],
                    "hintFa": r["راهنما"],
                })

        sid = sec(lesson_id, "GRAMMAR", 3, 0)
        order = 0
        for g in grammar.get(n, []):
            order += 1
            aid = act(sid, order, g["کارت"], "گرامر درس را با دقت آموزش ببینید.", "TEACHING")
            # پرسش بررسی و نکته هر گزینه غلط، همراه خودِ کارت می‌روند تا
            # صفحه آموزش بتواند بلافاصله بعد از توضیح بپرسد.
            tips = {}
            for line in (g["نکته هر گزینه غلط"] or "").split("\n"):
                if "←" in line:
                    opt, tip = line.split("←", 1)
                    tips[opt.strip()] = tip.strip()
            items.append({
                "id": f"{aid}-I01", "activityId": aid, "sortOrder": 1,
                "prompt": g["عنوان"], "promptFa": g["توضیح فارسی"],
                "correctAnswer": g["مثال‌ها"],
                "checkPrompt": g["پرسش بررسی"],
                "checkAnswer": g["پاسخ درست"],
                "checkOptions": g["گزینه‌ها"],
                # قالب «گزینه::نکته» و جداکننده || — چون خود نکته‌ها ویرگول
                # و خط تیره دارند و نباید با جداکننده اشتباه شوند
                "checkTips": " || ".join(f"{o}::{t}" for o, t in tips.items()),
            })

        speaking_rows = []
        for e in exercise.get(n, []):
            atype = TYPE_MAP.get(e["نوع"])
            if atype is None:
                warnings.append(f"درس {n}: نوع تمرین ناشناخته «{e['نوع']}»")
                continue
            if atype == "SPEAKING":
                speaking_rows.append(e)
                continue
            order += 1
            aid = act(sid, order, e["تمرین"],
                      "پس از یادگیری گرامر، تمرینات مربوط به آن را انجام دهید.", atype)
            items.append({
                "id": f"{aid}-I01", "activityId": aid, "sortOrder": 1,
                "prompt": e["صورت سوال"],
                "correctAnswer": e["پاسخ درست"],
                "alternatives": e["پاسخ‌های جایگزین"],
                "options": e["گزینه‌ها"],
                "hintFa": e["راهنما"],
                "targetWord": e["واژه هدف (لایتنر)"],
            })

        # ۴ — گفتار
        #
        # برگه تمرین‌ها فقط **یک** تمرین گفتار در هر درس دارد، و یک آیتم
        # برای بخشی که مجوز میکروفون می‌خواهد معامله بدی است: کاربر یک بار
        # «اجازه نمی‌دهم» می‌زند و در اندروید آن «نه» دائمی است — یعنی برای
        # یک آیتم، کل بخش گفتار را برای همیشه از دست می‌دهیم.
        #
        # دو جمله دیگر از مثال‌های همین درس اضافه می‌شود، مثل کاری که بخش
        # شنیدار می‌کند. محتوای تازه‌ای نوشته نمی‌شود و جای‌های انتخابی هم با
        # انتخاب‌های شنیدار (۲، ۸، ۱۴) یکی نیست تا همان جمله دو بار نیاید.
        if speaking_rows:
            sid = sec(lesson_id, "SPEAKING", 4, 0)
            aid = act(sid, 1, "Speaking 1", "در این بخش، گفتار انگلیسی خود را تقویت کنید.", "SPEAKING")
            order_s = 0
            for e in speaking_rows:
                order_s += 1
                items.append({
                    "id": f"{aid}-I{order_s:02d}", "activityId": aid, "sortOrder": order_s,
                    "prompt": e["صورت سوال"], "correctAnswer": e["پاسخ درست"],
                    "ttsText": e["صورت سوال"], "hintFa": e["راهنما"],
                    "targetWord": e["واژه هدف (لایتنر)"],
                })
            said = {(e["پاسخ درست"] or "").strip().lower() for e in speaking_rows}
            for i, v in enumerate(vocab[n]):
                if i not in (4, 11):
                    continue
                sent = (v["جمله مثال"] or "").strip()
                if not sent or sent.lower() in said:
                    continue
                said.add(sent.lower())
                order_s += 1
                items.append({
                    "id": f"{aid}-I{order_s:02d}", "activityId": aid, "sortOrder": order_s,
                    "prompt": sent, "correctAnswer": sent,
                    "ttsText": sent, "targetWord": v["واژه"],
                    "hintFa": "جمله را بلند و شمرده بگو.",
                })

        # ۵ — شنیدار: دیکته
        #
        # رقیب بخش شنیدار جدا دارد و ما نداشتیم. محتوایش تازه نوشته نمی‌شود:
        # از واژه‌ها و جمله‌های همین درس ساخته می‌شود، پس با هر تغییر محتوا
        # خودش به‌روز می‌ماند و جایی برای ناهمخوانی نمی‌ماند.
        #
        # ترتیب از آسان به سخت: اول واژه تنها، بعد جمله. دیکته جمله در A1
        # اگر از ابتدا بیاید دلسردکننده است، اما حذفش یعنی کاربر هرگز
        # گفتار پیوسته را تمرین نمی‌کند.
        vrows = vocab[n]
        if vrows:
            sid = sec(lesson_id, "LISTENING", 5, 0)
            aid = act(sid, 1, "Listening 1",
                      "آنچه می‌شنوی را بنویس. اول واژه، بعد جمله.", "LISTENING")
            # انتخاب معین بر اساس جای واژه در درس — با هر build یکی می‌ماند
            word_picks = [vrows[i] for i in (0, 5, 10, 15) if i < len(vrows)]
            sent_picks = [
                v for i, v in enumerate(vrows)
                if i in (2, 8, 14) and (v["جمله مثال"] or "").strip()
            ]
            order_l = 0
            for v in word_picks:
                order_l += 1
                items.append({
                    "id": f"{aid}-I{order_l:02d}", "activityId": aid, "sortOrder": order_l,
                    "prompt": v["واژه"], "correctAnswer": v["واژه"],
                    "ttsText": v["واژه"], "targetWord": v["واژه"],
                    "hintFa": "واژه‌ای که می‌شنوی را بنویس.",
                })
            for v in sent_picks:
                order_l += 1
                items.append({
                    "id": f"{aid}-I{order_l:02d}", "activityId": aid, "sortOrder": order_l,
                    "prompt": v["جمله مثال"], "correctAnswer": v["جمله مثال"],
                    "ttsText": v["جمله مثال"], "targetWord": v["واژه"],
                    "hintFa": "جمله‌ای که می‌شنوی را بنویس.",
                })

        # ۶ — مرور واژگان: از واژه‌های همین درس ساخته می‌شود
        #
        # به دو نیمه شکسته می‌شود و نه یک فعالیت بیست‌تایی. این بخش
        # سنگین‌ترین بخش درس است (نزدیک ۴۰٪ زمان) و درست در خسته‌ترین
        # لحظه می‌آید؛ بیست تمرین تولیدیِ پشت‌سرهم بدون هیچ نقطه توقفی،
        # همان‌جایی است که کاربر درس را نیمه رها می‌کند.
        #
        # با دو فعالیت، وسطش یک صفحه پایان می‌آید: امتیاز، «۱ از ۲ فعالیتِ
        # این بخش»، و دکمه صریحِ «فعلاً کافی است». محتوا همان است، اما
        # رها کردنش دیگر شکست حساب نمی‌شود.
        sid = sec(lesson_id, "VOCAB_REVIEW", 6, 0)
        words = vocab[n]
        half = (len(words) + 1) // 2
        for part, chunk in enumerate([words[:half], words[half:]], 1):
            if not chunk:
                continue
            aid = act(
                sid, part, f"Vocabulary Review {part}",
                "واژه‌های این درس را مرور کنید.", "TRANSLATE_TO_EN",
            )
            for i, v in enumerate(chunk, 1):
                items.append({
                    "id": f"{aid}-I{i:02d}", "activityId": aid, "sortOrder": i,
                    "prompt": v["معنی فارسی"], "correctAnswer": v["واژه"],
                    "targetWord": v["واژه"], "ttsText": v["واژه"],
                })

    # --- تخمین زمان: بعد از ساخته شدن همه فعالیت‌ها، یک بار محاسبه
    for sct in sections:
        sct["estimatedMinutes"] = estimate(sct["id"])
    by_lesson = {}
    for sct in sections:
        by_lesson[sct["lessonId"]] = by_lesson.get(sct["lessonId"], 0) + sct["estimatedMinutes"]
    for l in lessons:
        l["estimatedMinutes"] = by_lesson.get(l["id"], 0)

    # --- دیکشنری: از خود برگه واژگان ساخته می‌شود
    # واژه‌های دروس همان مدخل‌هایی هستند که لایتنر خودکار به آن‌ها ارجاع می‌دهد.
    dictionary, seen = [], set()
    for n in sorted(vocab):
        for v in vocab[n]:
            w = v["واژه"].strip().lower()
            if w in seen:
                continue
            seen.add(w)
            dictionary.append({
                "id": f"d-{w.replace(' ', '_').replace(chr(39), '')}",
                "word": w,
                "lemma": w,
                "pos": v["بخش کلام"],
                "ipaUk": v["تلفظ IPA"],
                "ipaUs": v["تلفظ IPA"],
                "definitionEn": None,
                "translationFa": v["معنی فارسی"],
                "exampleEn": v["جمله مثال"],
                "exampleFa": v["ترجمه جمله"],
                "frequencyRank": len(dictionary),
            })

    bundle = {"version": 1, "levelCode": LEVEL, "levels": LEVELS,
              "lessons": lessons, "sections": sections,
              "activities": activities, "items": items,
              "dictionary": dictionary}

    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with open(OUT, "w", encoding="utf-8") as f:
        json.dump(bundle, f, ensure_ascii=False, separators=(",", ":"))

    # همان بسته برای سرور — وگرنه دو نسخه از هم جدا می‌افتند و
    # کاربر بسته‌ای می‌گیرد که با APK هم‌خوان نیست.
    server_out = os.path.abspath(os.path.join(BASE, "..", "backend", "content", f"{LEVEL.lower()}.json"))
    if os.path.isdir(os.path.dirname(server_out)):
        raw = json.dumps(bundle, ensure_ascii=False, separators=(",", ":")).encode("utf-8")
        with open(server_out, "wb") as f:
            f.write(raw)

        # نسخه فشرده، همین‌جا و یک بار برای همیشه.
        #
        # فشرده کردن در لحظه درخواست، CPU سرور را در هر بار می‌سوزاند و
        # هزینه را بالا می‌برد. بسته محتوا فقط وقتی عوض می‌شود که ما
        # خروجی بگیریم، پس فشرده‌اش را همان موقع می‌سازیم و سرور فقط
        # فایل آماده را می‌فرستد.
        import gzip
        with gzip.open(server_out + ".gz", "wb", compresslevel=9) as f:
            f.write(raw)
        gz = os.path.getsize(server_out + ".gz")
        print(f"  نسخه سرور  : {server_out}")
        print(f"  فشرده      : {gz / 1024:.0f} کیلوبایت "
              f"({len(raw) / gz:.1f} برابر کوچک‌تر)")

    # --- بررسی سلامت خروجی
    prod = sum(1 for a in activities if a["activityType"] in PRODUCTIVE)

    # پرسش‌های داستانک از این نسبت بیرون‌اند — و این عمدی است.
    #
    # قاعده ۶۰٪ برای تمرین واژه و گرامر گذاشته شد تا اپ به آزمون
    # چندگزینه‌ای تبدیل نشود. اما درک مطلبِ متن ذاتاً تشخیصی است: پرسیدن
    # «در داستان چه شد؟» به‌شکل تولیدی یعنی از کاربر B1 انشا خواستن.
    # پس جدا شمرده می‌شود، نه اینکه نادیده گرفته شود — عددش پایین چاپ
    # می‌شود تا اگر روزی داستانک‌ها بیش از حد زیاد شدند، دیده شود.
    story_sections = {x["id"] for x in sections if x["type"] == "STORY"}
    story_acts = [a for a in activities if a["sectionId"] in story_sections]
    drill = [
        a for a in activities
        if a["activityType"] not in ("TEACHING", "FLASHCARD")
        and a["sectionId"] not in story_sections
    ]
    prod_drill = sum(1 for a in drill if a["activityType"] in PRODUCTIVE)
    missing_img = sum(1 for i in items if i.get("imageFile") and False)

    print(f"نوشته شد: {OUT}")
    print(f"  دروس       : {len(lessons)}")
    print(f"  بخش‌ها      : {len(sections)}")
    print(f"  فعالیت‌ها   : {len(activities)}")
    print(f"  آیتم‌ها     : {len(items)}")
    print(f"  دیکشنری    : {len(dictionary)}")

    # کفِ تبلیغ‌شده در صفحه ورود: «بیش از ۲۰۰۰ واژه».
    #
    # صفحه ورود پیش از وارد شدن محتوا به دیتابیس نشان داده می‌شود، پس
    # نمی‌تواند عدد را زنده بخواند و ناچار یک عدد ثابت دارد. پیش‌تر
    # «۲۴۰۰ واژه» نوشته بود در حالی که واقعیت ۲٬۰۸۸ بود — عددی که کسی
    # به‌روزش نکرده بود. این محافظ نمی‌گذارد آن ادعا دوباره دروغ شود.
    #
    # سنجش روی **مجموع همه سطوح** است و نه همین سطح، چون ادعای صفحه
    # ورود درباره کل محتواست. سطوحی که هنوز ساخته نشده‌اند نادیده
    # می‌مانند تا ساختِ تک‌سطحی بی‌خود نشکند.
    ADVERTISED_FLOOR = 2000
    all_words = set()
    for other in ("a1", "a2", "b1", "b2"):
        path = os.path.join(os.path.dirname(OUT), f"content_{other}.json")
        if not os.path.exists(path):
            continue
        with open(path, encoding="utf-8") as fh:
            all_words.update(
                (e.get("word") or "").lower() for e in json.load(fh).get("dictionary", [])
            )
    all_words.discard("")
    if len(all_words) >= 4 * 500 and len(all_words) < ADVERTISED_FLOOR:
        raise SystemExit(
            f"❌ مجموع واژه‌ها ({len(all_words)}) از کفِ تبلیغ‌شده در صفحه ورود "
            f"({ADVERTISED_FLOOR}) کمتر است. یا محتوا اضافه کن یا آن متن را عوض کن."
        )
    if all_words:
        print(f"  مجموع واژه همه سطوح: {len(all_words)}")

    print(f"  حجم        : {os.path.getsize(OUT)/1024:.0f} کیلوبایت")
    print()
    ratio = prod_drill / len(drill) if drill else 0
    print(f"  نسبت تولیدی در تمرین‌ها: {prod_drill}/{len(drill)} = {ratio:.0%}",
          "✅" if ratio >= 0.60 else "❌ زیر آستانه ۶۰٪")
    if story_acts:
        nq = sum(1 for a in story_acts if a["activityType"] == "MULTIPLE_CHOICE")
        print(f"  داستانک (جدا از نسبت بالا): {len(story_sections)} داستان · {nq} پرسش درک مطلب")

    # هر کارت گرامر باید بررسی درک داشته باشد. بدون این، بخش آموزش دوباره
    # همان خواندن منفعلی می‌شود که تمایزمان با رقیب بود.
    teaching = [a["id"] for a in activities if a["activityType"] == "TEACHING"]
    grammar_teaching = [a for a in teaching if "-S3-" in a]
    no_check = [
        i["id"] for i in items
        if i["activityId"] in grammar_teaching and not i.get("checkPrompt")
    ]
    print(f"  بررسی درک روی کارت‌های گرامر: "
          f"{len(grammar_teaching) - len(no_check)}/{len(grammar_teaching)}",
          "✅" if not no_check else f"❌ بدون بررسی: {no_check[:5]}")

    # نکته هدفمند روی هر گزینه غلط — اصلِ تمایز این بخش
    missing_tips = []
    for i in items:
        if not i.get("checkPrompt"):
            continue
        opts = [o.strip() for o in (i.get("checkOptions") or "").split("|") if o.strip()]
        tipped = {p.split("::", 1)[0].strip() for p in (i.get("checkTips") or "").split("||") if "::" in p}
        for o in opts:
            if o != i.get("checkAnswer") and o not in tipped:
                missing_tips.append(f"{i['id']}:{o}")
    if missing_tips:
        print(f"  ❌ گزینه‌های غلط بدون نکته: {missing_tips[:5]}")

    for w in warnings:
        print("  ⚠️ ", w)
    if ratio < 0.60 or no_check or missing_tips:
        sys.exit(1)


if __name__ == "__main__":
    main()
