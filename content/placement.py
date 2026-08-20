# -*- coding: utf-8 -*-
"""
آزمون تعیین سطح.

چرا لازم است: تا امروز هر کاربری از A1 درس ۱ شروع می‌کرد، با
«He is a manager.» کسی که کمی انگلیسی بلد است همان‌جا اپ را می‌بندد — و
دقیقاً همان کسی است که بیشترین احتمال خرید را دارد، چون انگیزه‌اش
جدی‌تر است.

**قاعده طراحی:** سه پرسش برای هر سطح، به ترتیب دشواری. اگر کاربر دو
پرسش پشت‌سرهم را غلط بزند، آزمون همان‌جا تمام می‌شود — به سقفش رسیده و
ادامه دادن فقط او را خسته و دلسرد می‌کند.

سطح پیشنهادی: بالاترین سطحی که در آن دست‌کم ۲ از ۳ درست بوده. اگر حتی
در A1 هم به این حد نرسید، از A1 شروع می‌کند — که بد نیست، همان جایی
است که باید باشد.

هر پرسش:
    (سطح، متن جمله با ___ ، گزینه‌ها، اندیس پاسخ درست، توضیح فارسی)

گزینه‌ها عمداً **هم‌شکل** انتخاب شده‌اند: اگر یک گزینه از بقیه بلندتر یا
عجیب‌تر باشد، کاربر بدون دانستن گرامر هم حذفش می‌کند و آزمون به‌جای
سنجش، حدس‌زدن می‌شود.
"""

QUESTIONS = [
    # ---------------- A1 ----------------
    ("A1", "She ___ a doctor.",
     ["am", "is", "are", "be"], 1,
     "با he / she / it همیشه is می‌آید."),
    ("A1", "I ___ to school every day.",
     ["go", "goes", "going", "gone"], 0,
     "حال ساده با I به شکل ساده فعل می‌آید."),
    ("A1", "There ___ two books on the table.",
     ["is", "are", "was", "be"], 1,
     "برای جمع، are می‌آید."),

    # ---------------- A2 ----------------
    ("A2", "We ___ to Shiraz last summer.",
     ["go", "went", "gone", "going"], 1,
     "گذشته ساده go می‌شود went."),
    ("A2", "This book is ___ than that one.",
     ["cheap", "cheaper", "cheapest", "more cheap"], 1,
     "صفت کوتاه با er تفضیلی می‌شود."),
    ("A2", "I have ___ finished my homework.",
     ["yet", "already", "since", "ago"], 1,
     "already در جمله مثبت حال کامل می‌آید."),

    # ---------------- B1 ----------------
    ("B1", "She ___ here since 2019.",
     ["works", "worked", "has been working", "is working"], 2,
     "کاری که از گذشته تا حالا ادامه دارد، حال کامل استمراری می‌گیرد."),
    ("B1", "If I ___ more time, I would travel more.",
     ["have", "had", "will have", "am having"], 1,
     "شرطی نوع دوم: if + گذشته ساده، و would در جمله دوم."),
    ("B1", "He said he ___ the film before.",
     ["sees", "saw", "had seen", "has seen"], 2,
     "در نقل قول، حال کامل به گذشته کامل تبدیل می‌شود."),

    # ---------------- B2 ----------------
    ("B2", "You ___ told me earlier; I could have helped.",
     ["should", "should have", "must", "would"], 1,
     "should have + قسمت سوم، برای کاری که در گذشته انجام نشد."),
    ("B2", "The report ___ by the time we arrived.",
     ["was finished", "had been finished", "has finished", "finished"], 1,
     "مجهول در گذشته کامل: had been + قسمت سوم."),
    ("B2", "Not only ___ late, but he also forgot the documents.",
     ["he was", "was he", "he is", "is he"], 1,
     "بعد از Not only جای فعل و فاعل عوض می‌شود."),
]

LEVELS = ["A1", "A2", "B1", "B2"]
PER_LEVEL = 3
#: اگر این تعداد پرسش پشت‌سرهم غلط شد، آزمون تمام می‌شود
STOP_AFTER_WRONG = 2
#: برای عبور از هر سطح، دست‌کم این تعداد از سه پرسش باید درست باشد
PASS_THRESHOLD = 2


def validate() -> None:
    """محتوا باید پیش از خروجی گرفتن سالم باشد، وگرنه آزمون بی‌معنا می‌شود."""
    from collections import Counter

    per = Counter(q[0] for q in QUESTIONS)
    for lv in LEVELS:
        assert per[lv] == PER_LEVEL, f"{lv}: {per[lv]} پرسش، باید {PER_LEVEL} باشد"

    for i, (lv, text, opts, ans, why) in enumerate(QUESTIONS, 1):
        assert "___" in text, f"پرسش {i}: جای خالی ندارد"
        assert len(opts) == 4, f"پرسش {i}: باید ۴ گزینه داشته باشد"
        assert len(set(opts)) == 4, f"پرسش {i}: گزینه تکراری دارد"
        assert 0 <= ans < 4, f"پرسش {i}: اندیس پاسخ نامعتبر"
        assert why.strip(), f"پرسش {i}: توضیح فارسی ندارد"

        # طول گزینه‌ها نباید فاحش فرق کند، وگرنه پاسخ از روی شکل حدس زده می‌شود
        lens = [len(o) for o in opts]
        assert max(lens) - min(lens) <= 12, (
            f"پرسش {i}: اختلاف طول گزینه‌ها زیاد است {opts}"
        )


def as_dict() -> dict:
    validate()
    return {
        "version": 1,
        "perLevel": PER_LEVEL,
        "stopAfterWrong": STOP_AFTER_WRONG,
        "passThreshold": PASS_THRESHOLD,
        "questions": [
            {
                "id": f"p{i:02d}",
                "level": lv,
                "text": text,
                "options": opts,
                "answer": ans,
                "explanationFa": why,
            }
            for i, (lv, text, opts, ans, why) in enumerate(QUESTIONS, 1)
        ],
    }


if __name__ == "__main__":
    import json
    import os

    data = as_dict()
    out = os.path.join(
        os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
        "android", "app", "src", "main", "assets", "placement.json",
    )
    with open(out, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=1)
    print(f"✅ {len(data['questions'])} پرسش تعیین سطح → {out}")
