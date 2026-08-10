# -*- coding: utf-8 -*-
"""
داستانک‌های سطح A2 — شش داستان روی دروس ۵، ۱۰، ۱۵، ۲۰، ۲۵ و ۳۰.

در این سطح داستان باید **کوتاه و کاملاً روشن** باشد. جمله‌های بلند و
موقعیت مبهم (کاری که در B2 می‌کنیم) اینجا فقط کاربر را می‌ترساند و
رهایش می‌کند. پس: جمله‌های کوتاه، ترتیب زمانی ساده، و پرسش‌هایی که
پاسخشان مستقیم در متن هست.

قاعده ثابت: هر داستان فقط از گرامر و واژه دروسِ پیش از خودش.
  درس ۵  ← گذشته ساده و استمراری، روایت ساده
  درس ۱۰ ← آینده: will، going to، قرار
  درس ۱۵ ← مقایسه، قید حالت، as … as
  درس ۲۰ ← حال کامل: ever، never، already، yet
  درس ۲۵ ← مجهول ساده، توصیه و الزام
  درس ۳۰ ← جمع‌بندی سطح

ساختار: (عنوان, [(بند, ترجمه)], [(پرسش, ترجمه, پاسخ, [گزینه‌ها], راهنما)])
"""

STORIES = {}

STORIES[5] = (
    "The Power Cut",
    [
        ("Last Tuesday I was cooking dinner. The radio was playing and it was raining outside.",
         "سه‌شنبه گذشته داشتم شام درست می‌کردم. رادیو روشن بود و بیرون باران می‌بارید."),

        ("Suddenly the electricity went off. The kitchen was dark. I looked for a candle, "
         "but I couldn't find one.",
         "ناگهان برق رفت. آشپزخانه تاریک شد. دنبال شمع گشتم، اما پیدا نکردم."),

        ("Then somebody knocked on the door. It was my neighbour. "
         "She had two candles and she was smiling.",
         "بعد کسی در زد. همسایه‌ام بود. دو شمع داشت و لبخند می‌زد."),

        ("We finished the dinner together. Luckily nothing was broken. "
         "The electricity came back at ten o'clock.",
         "شام را با هم تمام کردیم. خوشبختانه چیزی نشکست. برق ساعت ده برگشت."),
    ],
    [
        ("What was the narrator doing when the electricity went off?",
         "وقتی برق رفت راوی چه می‌کرد؟",
         "Cooking dinner",
         ["Cooking dinner", "Watching a film", "Sleeping", "Reading a book"],
         "بند اول — گذشته استمراری."),

        ("Why couldn't the narrator find a candle?",
         "چرا راوی شمع پیدا نکرد؟",
         "The kitchen was dark",
         ["The kitchen was dark", "There were no candles",
          "The neighbour took them", "It was raining"],
         "بند دوم."),

        ("Who came to the door?",
         "چه کسی پشت در بود؟",
         "The neighbour",
         ["The neighbour", "A friend", "The manager", "Nobody"],
         "بند سوم."),

        ("When did the electricity come back?",
         "برق کِی برگشت؟",
         "At ten o'clock",
         ["At ten o'clock", "The next morning", "After dinner", "It didn't come back"],
         "بند آخر."),
    ],
)

STORIES[10] = (
    "The Weekend Plan",
    [
        ("Ali is meeting Sara on Friday at six. They are going to visit a new museum.",
         "علی جمعه ساعت شش با سارا قرار دارد. قرار است موزه تازه‌ای را ببینند."),

        ("The forecast says it might rain. Ali will take an umbrella just in case.",
         "پیش‌بینی می‌گوید شاید باران ببارد. علی محض احتیاط چتر برمی‌دارد."),

        ("If it rains, they will stay inside and have lunch there. "
         "If it's sunny, they will walk in the park after the museum.",
         "اگر باران ببارد، داخل می‌مانند و آنجا ناهار می‌خورند. اگر آفتابی باشد، بعد از موزه در پارک قدم می‌زنند."),

        ("Ali is booking a table now. He promises he won't be late this time.",
         "علی همین حالا میز رزرو می‌کند. قول می‌دهد این بار دیر نکند."),
    ],
    [
        ("When are Ali and Sara meeting?",
         "علی و سارا کِی قرار دارند؟",
         "On Friday at six",
         ["On Friday at six", "On Saturday at six", "On Friday at ten", "Next week"],
         "بند اول."),

        ("Why will Ali take an umbrella?",
         "چرا علی چتر برمی‌دارد؟",
         "Because it might rain",
         ["Because it might rain", "Because it is raining now",
          "Because Sara asked", "Because it is cold"],
         "بند دوم."),

        ("What will they do if it rains?",
         "اگر باران ببارد چه می‌کنند؟",
         "Stay inside and have lunch",
         ["Stay inside and have lunch", "Walk in the park",
          "Go home", "Visit another museum"],
         "بند سوم — شرطی نوع اول."),

        ("What does Ali promise?",
         "علی چه قولی می‌دهد؟",
         "That he won't be late",
         ["That he won't be late", "That he will pay",
          "That he will bring a friend", "That he will drive"],
         "بند آخر."),
    ],
)

STORIES[15] = (
    "Two Shops",
    [
        ("There are two shops near my house. The first one is bigger and more modern.",
         "نزدیک خانه‌ام دو مغازه هست. اولی بزرگ‌تر و امروزی‌تر است."),

        ("The second shop is smaller, but the fruit there is fresher. "
         "It isn't as expensive as the first one.",
         "مغازه دوم کوچک‌تر است، اما میوه‌اش تازه‌تر است. به‌گرانی اولی نیست."),

        ("The owner of the small shop works quickly and speaks politely. "
         "He always remembers what I buy.",
         "صاحب مغازه کوچک سریع کار می‌کند و مؤدبانه حرف می‌زند. همیشه یادش هست چه می‌خرم."),

        ("Both shops are good, but I go to the second one. "
         "For me the service is more important than the size.",
         "هر دو مغازه خوب‌اند، اما من به دومی می‌روم. برای من خدمات از اندازه مهم‌تر است."),
    ],
    [
        ("Which shop is bigger?",
         "کدام مغازه بزرگ‌تر است؟",
         "The first one",
         ["The first one", "The second one", "They are the same", "Neither"],
         "بند اول."),

        ("What is better in the second shop?",
         "در مغازه دوم چه چیزی بهتر است؟",
         "The fruit is fresher",
         ["The fruit is fresher", "The shop is bigger",
          "The prices are higher", "It opens earlier"],
         "بند دوم."),

        ("How does the owner speak?",
         "صاحب مغازه چطور حرف می‌زند؟",
         "Politely",
         ["Politely", "Loudly", "Slowly", "Badly"],
         "بند سوم — قید حالت."),

        ("Why does the narrator choose the second shop?",
         "چرا راوی دومی را انتخاب می‌کند؟",
         "The service is more important to him",
         ["The service is more important to him", "It is bigger",
          "It is nearer", "It is more modern"],
         "بند آخر."),
    ],
)

STORIES[20] = (
    "Have You Ever…?",
    [
        ("My friend Nima has travelled a lot. He has visited eight countries.",
         "دوستم نیما خیلی سفر کرده. هشت کشور را دیده است."),

        ("I have never been abroad. I have always wanted to go, "
         "but I haven't saved enough money yet.",
         "من هرگز خارج از کشور نرفته‌ام. همیشه دلم می‌خواسته بروم، اما هنوز پول کافی جمع نکرده‌ام."),

        ("Last week Nima said something that surprised me. "
         "He said he has already seen the sea many times, but he has never seen snow.",
         "هفته گذشته نیما چیزی گفت که غافلگیرم کرد. گفت بارها دریا را دیده، اما هرگز برف ندیده است."),

        ("So this winter we have a plan. He is coming to my city, "
         "and I am going to show him snow for the first time.",
         "پس این زمستان برنامه‌ای داریم. او به شهر من می‌آید و من برای اولین بار برف را نشانش می‌دهم."),
    ],
    [
        ("How many countries has Nima visited?",
         "نیما چند کشور را دیده است؟",
         "Eight",
         ["Eight", "Two", "Many, but he doesn't know", "None"],
         "بند اول."),

        ("Why hasn't the narrator been abroad?",
         "چرا راوی خارج نرفته است؟",
         "He hasn't saved enough money",
         ["He hasn't saved enough money", "He doesn't want to go",
          "He has no passport", "He is afraid of flying"],
         "بند دوم."),

        ("What has Nima never seen?",
         "نیما هرگز چه چیزی را ندیده است؟",
         "Snow",
         ["Snow", "The sea", "A mountain", "A desert"],
         "بند سوم."),

        ("What is the plan for winter?",
         "برنامه زمستان چیست؟",
         "Nima will come and see snow",
         ["Nima will come and see snow", "They will travel abroad",
          "They will go to the sea", "Nima will move to the city"],
         "بند آخر."),
    ],
)

STORIES[25] = (
    "At the Clinic",
    [
        ("On Sunday I had a headache and a fever. My mother said I should see a doctor.",
         "یکشنبه سردرد و تب داشتم. مادرم گفت باید بروم دکتر."),

        ("The clinic opens at eight. An appointment card was given to me at the desk, "
         "and I waited for twenty minutes.",
         "درمانگاه هشت باز می‌شود. کارت نوبت به من داده شد و بیست دقیقه منتظر ماندم."),

        ("The doctor asked about my symptoms. She said it wasn't a serious problem, "
         "but I had to rest for two days.",
         "دکتر درباره علائمم پرسید. گفت مشکل جدی‌ای نیست، اما باید دو روز استراحت کنم."),

        ("A prescription was written for me. The medicine was bought at the pharmacy next door. "
         "By Tuesday I felt much better.",
         "برایم نسخه نوشته شد. دارو از داروخانه بغلی خریده شد. تا سه‌شنبه خیلی بهتر بودم."),
    ],
    [
        ("What did the mother say?",
         "مادر چه گفت؟",
         "That he should see a doctor",
         ["That he should see a doctor", "That he must stay in bed",
          "That he mustn't go out", "That it was serious"],
         "بند اول — توصیه با should."),

        ("What was given at the desk?",
         "پشت میز چه چیزی داده شد؟",
         "An appointment card",
         ["An appointment card", "A prescription", "Medicine", "A form"],
         "بند دوم — جمله مجهول."),

        ("What did the doctor say about the problem?",
         "دکتر درباره مشکل چه گفت؟",
         "It wasn't serious",
         ["It wasn't serious", "It was very serious",
          "He needed a test", "He should go to hospital"],
         "بند سوم."),

        ("When did he feel better?",
         "کِی بهتر شد؟",
         "By Tuesday",
         ["By Tuesday", "On Sunday", "After a week", "The same day"],
         "بند آخر."),
    ],
)

STORIES[30] = (
    "One Year Later",
    [
        ("A year ago I couldn't say more than a few words in English. "
         "I used to think it was too late to start.",
         "یک سال پیش بیش از چند کلمه انگلیسی نمی‌توانستم بگویم. فکر می‌کردم برای شروع دیر است."),

        ("I studied a little every day. Some days I was tired and I did only ten minutes. "
         "But I never stopped completely.",
         "هر روز کمی درس خواندم. بعضی روزها خسته بودم و فقط ده دقیقه کار کردم. اما هرگز کاملاً متوقف نشدم."),

        ("Last month something happened. A tourist asked me for directions, "
         "and I answered him without thinking about the words.",
         "ماه گذشته اتفاقی افتاد. یک گردشگر نشانی پرسید و من بدون فکر کردن به واژه‌ها جوابش دادم."),

        ("I'm not fluent yet. But I can hold a conversation now, "
         "and that is something I couldn't do a year ago.",
         "هنوز روان نیستم. اما حالا می‌توانم گفت‌وگو را ادامه بدهم، و این کاری است که یک سال پیش نمی‌توانستم."),
    ],
    [
        ("What did the narrator think a year ago?",
         "راوی یک سال پیش چه فکر می‌کرد؟",
         "That it was too late to start",
         ["That it was too late to start", "That English was easy",
          "That he would never try", "That he needed a teacher"],
         "بند اول — used to."),

        ("What did he do on tired days?",
         "روزهای خسته چه می‌کرد؟",
         "Only ten minutes",
         ["Only ten minutes", "Nothing at all", "Two hours", "He rested completely"],
         "بند دوم."),

        ("What happened last month?",
         "ماه گذشته چه شد؟",
         "He gave directions to a tourist",
         ["He gave directions to a tourist", "He passed an exam",
          "He travelled abroad", "He met a teacher"],
         "بند سوم."),

        ("What can he do now?",
         "حالا چه کاری می‌تواند بکند؟",
         "Hold a conversation",
         ["Hold a conversation", "Speak like a native",
          "Write a book", "Teach English"],
         "بند آخر."),
    ],
)
