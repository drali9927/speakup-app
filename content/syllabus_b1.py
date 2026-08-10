# -*- coding: utf-8 -*-
"""
سرفصل سطح B1 — سی درس.

منطق ترتیب، همان منطق A1 و A2 (سند در syllabus_a2.py):
گرامر پیش‌نیاز ترتیب را تعیین می‌کند، نه موضوع.

جهش اصلی B1 نسبت به A2 این است که کاربر از «گفتن آنچه هست» به
«گفتن آنچه ممکن بود، باید می‌شد، یا اگر می‌شد» می‌رسد:

  ۱ تا ۳    پل از A2 — مرور حال کامل و مجهول با بار واژگانی تازه
  ۴ تا ۷    گذشته کامل و روایت چندلایه
  ۸ تا ۱۱   شرطی دوم و آرزو — بزرگ‌ترین جهش ذهنی این سطح
  ۱۲ تا ۱۵  مدال‌های حدس و گله (must have، should have)
  ۱۶ تا ۱۹  نقل قول کامل و افعال گزارشی
  ۲۰ تا ۲۳  مجهول پیشرفته و ساختارهای رسمی
  ۲۴ تا ۲۷  عبارت‌های وصفی، used to و would برای عادت گذشته
  ۲۸ تا ۳۰  بیان نظر، استدلال و جمع‌بندی سطح

هر پنجمین درس سبک‌تر است تا کاربر جا بیفتد. سه درس اول عمداً روی
واژه‌های پرکاربردِ کار و زندگی می‌ماند، نه گرامر تازه.
"""

SYLLABUS = [
 (1,  "I've been working here.",      "حال کامل استمراری",              "کار و مسئولیت",        True),
 (2,  "It has been done.",            "مرور مجهول با زمان‌های کامل",     "فرایند و سازمان",      False),
 (3,  "I'm used to it now.",          "be used to و get used to",       "سازگاری با تغییر",     False),
 (4,  "She had already left.",        "گذشته کامل",                     "روایت دو لایه",        False),
 (5,  "A story worth telling.",       "مرور روایت گذشته",               "تعریف کردن ماجرا",     False),
 (6,  "By the time we arrived…",      "by the time و ترتیب رویدادها",   "تأخیر و برنامه",       False),
 (7,  "He said he had seen it.",      "نقل قول با گذشته کامل",          "بازگو کردن ماجرا",     False),
 (8,  "If I had more time…",          "شرطی نوع دوم",                   "آرزو و فرض",           False),
 (9,  "I wish I knew.",               "wish + گذشته",                   "افسوس و آرزو",         False),
 (10, "What would you do?",           "مرور شرطی دوم",                  "موقعیت فرضی",          False),
 (11, "Unless we act now.",           "unless، as long as، in case",    "شرط و احتیاط",         False),
 (12, "He must have forgotten.",      "مدال + have + قسمت سوم",         "حدس درباره گذشته",     False),
 (13, "You should have told me.",     "should have و گله",              "پشیمانی و انتقاد",     False),
 (14, "It might have been better.",   "درجه اطمینان در گذشته",          "تحلیل تصمیم",          False),
 (15, "Looking back.",                "مرور مدال‌های گذشته",             "مرور تجربه",           False),
 (16, "She asked me where I lived.",  "نقل قول پرسش",                   "مصاحبه و پرسش",        False),
 (17, "He suggested waiting.",        "افعال گزارشی + ing / to",        "گزارش جلسه",           False),
 (18, "They refused to accept it.",   "افعال گزارشی منفی",              "اختلاف و مذاکره",      False),
 (19, "In other words.",              "بازگویی و خلاصه کردن",           "توضیح دوباره",         False),
 (20, "The report was written by…",   "مجهول با فاعل مبهم",             "گزارش رسمی",           False),
 (21, "It is said that…",             "مجهول غیرشخصی",                  "خبر و شایعه",          False),
 (22, "I had it repaired.",           "have something done",            "خدمات و سفارش کار",    False),
 (23, "Getting things done.",         "مرور مجهول کاربردی",             "کار روزمره",           False),
 (24, "The man who lives next door.", "عبارت وصفی تعریفی و توضیحی",     "توصیف افراد و جاها",   False),
 (25, "I used to walk to school.",    "used to و would برای گذشته",     "خاطرات",               False),
 (26, "Despite the rain, we went.",   "despite، although، while",       "تضاد و مقایسه",        False),
 (27, "The sooner, the better.",      "ساختارهای مقایسه‌ای پیشرفته",     "انتخاب و اولویت",      False),
 (28, "In my opinion…",               "بیان نظر و موافقت و مخالفت",     "گفت‌وگوی جدی",         False),
 (29, "On the one hand…",             "استدلال و وزن‌کردن دو طرف",       "تصمیم‌گیری",           False),
 (30, "Ready for B2.",                "مرور کلی B1",                    "جمع‌بندی سطح",         False),
]
