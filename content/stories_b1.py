# -*- coding: utf-8 -*-
"""
داستانک‌های سطح B1 — شش داستان، روی دروس ۵، ۱۰، ۱۵، ۲۰، ۲۵ و ۳۰.

قاعده‌ای که همه‌شان را می‌سازد: **هر داستان فقط از واژه‌ها و گرامر
دروسِ پیش از خودش استفاده می‌کند**. اگر واژه تازه بیاید، داستان از
«ورودی قابل‌فهم» به «تمرین حدس زدن» تبدیل می‌شود و دقیقاً همان چیزی
را از دست می‌دهد که ارزشش بود.

جای هر داستان هم تصادفی نیست: بعد از هر پنج درس، جایی که کاربر
گرامر تازه‌ای را تمام کرده و باید ببیند در متن پیوسته چطور کار می‌کند.
  درس ۵  ← حال کامل استمراری و مجهول (دروس ۱ تا ۴)
  درس ۱۰ ← گذشته کامل و روایت دو لایه
  درس ۱۵ ← شرطی دوم، wish، مدال‌های گذشته
  درس ۲۰ ← نقل قول و افعال گزارشی
  درس ۲۵ ← مجهول غیرشخصی و have something done
  درس ۳۰ ← جمع‌بندی: هر چیزی که سطح ساخته

ساختار هر ورودی:
    (عنوان, [(بند انگلیسی, ترجمه فارسی), ...],
     [(پرسش, ترجمه پرسش, پاسخ درست, [گزینه‌ها], راهنما), ...])

ترجمه فارسیِ بند عمداً هست اما در اپ **پس از** خواندن متن انگلیسی
نشان داده می‌شود: کاربر باید اول تلاش کند، بعد بررسی کند.
"""

STORIES = {}

# ---------------------------------------------------------------- درس ۵
STORIES[5] = (
    "The Missing Report",
    [
        ("Sara has been working at the company for three years. "
         "She is in charge of the monthly report, and she has never been late with it.",
         "سارا سه سال است در آن شرکت کار می‌کند. مسئول گزارش ماهانه است و هرگز در تحویلش تأخیر نداشته."),

        ("On Monday morning her manager asked for the report. "
         "Sara opened her laptop and could not find it. The file had been deleted.",
         "دوشنبه صبح مدیرش گزارش را خواست. سارا لپ‌تاپش را باز کرد و پیدایش نکرد. فایل حذف شده بود."),

        ("She didn't panic. She had been saving a copy on the office system every Friday. "
         "The procedure had been set up two years earlier, and it had never been used until that day.",
         "وحشت نکرد. هر جمعه نسخه‌ای روی سامانه دفتر ذخیره می‌کرد. آن روال دو سال پیش راه‌اندازی شده بود و تا آن روز هرگز استفاده نشده بود."),

        ("The copy was there. Only one day of work had been lost. "
         "Sara handed the report in before lunch.",
         "نسخه آنجا بود. فقط یک روز کار از دست رفته بود. سارا گزارش را پیش از ناهار تحویل داد."),

        ("Her manager said nothing about the deleted file. "
         "He only said that the report was, as always, on time.",
         "مدیرش درباره فایل حذف‌شده چیزی نگفت. فقط گفت گزارش، مثل همیشه، به‌موقع است."),
    ],
    [
        ("How long has Sara been working at the company?",
         "سارا چه مدت است در آن شرکت کار می‌کند؟",
         "For three years",
         ["For three years", "For one year", "Since Monday", "For two months"],
         "بند اول را دوباره بخوان."),

        ("What had happened to the report file?",
         "چه بلایی سر فایل گزارش آمده بود؟",
         "It had been deleted",
         ["It had been deleted", "It had been sent", "It had been printed", "It had been approved"],
         "بند دوم — جمله مجهول با گذشته کامل."),

        ("Why didn't Sara panic?",
         "چرا سارا وحشت نکرد؟",
         "She had been saving a copy every week",
         ["She had been saving a copy every week",
          "She had finished the report early",
          "Her manager didn't need it",
          "She had another laptop"],
         "بند سوم — چه کاری را مرتب انجام می‌داد؟"),

        ("How much work was lost?",
         "چقدر کار از دست رفت؟",
         "One day",
         ["One day", "One week", "Nothing at all", "Two years"],
         "بند چهارم."),

        ("What did the manager say about the deleted file?",
         "مدیر درباره فایل حذف‌شده چه گفت؟",
         "Nothing",
         ["Nothing", "He was angry", "He asked for an apology", "He changed the procedure"],
         "بند آخر."),
    ],
)

# --------------------------------------------------------------- درس ۱۰
STORIES[10] = (
    "By the Time We Arrived",
    [
        ("We had planned the trip for weeks. The tickets had been booked in advance, "
         "and I had arranged everything with the hotel.",
         "هفته‌ها برای سفر برنامه ریخته بودیم. بلیت‌ها از پیش رزرو شده بود و من همه‌چیز را با هتل هماهنگ کرده بودم."),

        ("On Friday morning we were driving to the station when the traffic stopped. "
         "An accident had closed the main road an hour earlier.",
         "جمعه صبح داشتیم به سمت ایستگاه می‌رفتیم که ترافیک ایستاد. یک ساعت پیش‌تر تصادفی جاده اصلی را بسته بود."),

        ("By the time we reached the station, the train had left. "
         "I realised I had forgotten to check the departure time that morning.",
         "تا وقتی به ایستگاه رسیدیم، قطار رفته بود. فهمیدم آن صبح فراموش کرده بودم ساعت حرکت را چک کنم."),

        ("To be honest, I was ready to go home. But my friend had already found another train, "
         "two hours later, and it wasn't full.",
         "راستش را بخواهی آماده بودم برگردم خانه. اما دوستم پیش‌تر قطار دیگری پیدا کرده بود، دو ساعت بعد، و پر نبود."),

        ("We arrived late, and the hotel had given our room to someone else. "
         "Looking back, it was the best trip we had taken.",
         "دیر رسیدیم و هتل اتاقمان را به کس دیگری داده بود. به گذشته که نگاه می‌کنم، بهترین سفری بود که رفته بودیم."),
    ],
    [
        ("What had they done before the trip?",
         "پیش از سفر چه کرده بودند؟",
         "They had booked the tickets in advance",
         ["They had booked the tickets in advance",
          "They had cancelled the hotel",
          "They had missed a train before",
          "They had driven to another city"],
         "بند اول."),

        ("Why did the traffic stop?",
         "چرا ترافیک ایستاد؟",
         "An accident had closed the road",
         ["An accident had closed the road",
          "The train was late",
          "It was raining",
          "The road was being repaired"],
         "بند دوم."),

        ("What had the narrator forgotten?",
         "راوی چه چیزی را فراموش کرده بود؟",
         "To check the departure time",
         ["To check the departure time",
          "To book the hotel",
          "To buy the tickets",
          "To call his friend"],
         "بند سوم — گذشته کامل."),

        ("Who found the second train?",
         "قطار دوم را چه کسی پیدا کرد؟",
         "His friend",
         ["His friend", "The narrator", "The hotel", "Nobody"],
         "بند چهارم."),

        ("How does the narrator feel about the trip now?",
         "راوی حالا درباره سفر چه حسی دارد؟",
         "It was the best trip",
         ["It was the best trip",
          "He regrets going",
          "He would never travel again",
          "He blames his friend"],
         "بند آخر — «Looking back…»"),
    ],
)

# --------------------------------------------------------------- درس ۱۵
STORIES[15] = (
    "The Job He Didn't Take",
    [
        ("Two years ago Reza was offered a job abroad. The salary was better, "
         "and it was a rare opportunity.",
         "دو سال پیش به رضا شغلی در خارج پیشنهاد شد. حقوقش بهتر بود و فرصت کمیابی بود."),

        ("He thought about it for a month. If he had more time, he would have said yes. "
         "But his mother was ill, and he turned the offer down.",
         "یک ماه به آن فکر کرد. اگر وقت بیشتری داشت، بله می‌گفت. اما مادرش بیمار بود و پیشنهاد را رد کرد."),

        ("For a while he was frustrated. He kept thinking: I wish I knew what would have happened. "
         "He should have asked them to wait.",
         "مدتی کلافه بود. مدام فکر می‌کرد: کاش می‌دانستم چه می‌شد. باید از آن‌ها می‌خواست صبر کنند."),

        ("His colleague, who took the job instead, came back after eight months. "
         "He said the work had been much harder than he had expected.",
         "همکارش که به‌جای او شغل را گرفت، بعد از هشت ماه برگشت. گفت کار خیلی سخت‌تر از آنچه انتظار داشت بوده."),

        ("Reza doesn't think about it much now. If he were in that situation again, "
         "he would make the same decision. He has no regrets.",
         "رضا حالا زیاد به آن فکر نمی‌کند. اگر دوباره در آن موقعیت بود، همان تصمیم را می‌گرفت. پشیمان نیست."),
    ],
    [
        ("Why did Reza turn down the job?",
         "چرا رضا شغل را رد کرد؟",
         "His mother was ill",
         ["His mother was ill",
          "The salary was low",
          "He didn't like the country",
          "He had another offer"],
         "بند دوم."),

        ("What did Reza wish?",
         "رضا آرزوی چه چیزی داشت؟",
         "That he knew what would have happened",
         ["That he knew what would have happened",
          "That he had more money",
          "That his colleague had stayed",
          "That he had never been offered the job"],
         "بند سوم — ساختار wish."),

        ("What happened to his colleague?",
         "برای همکارش چه اتفاقی افتاد؟",
         "He came back after eight months",
         ["He came back after eight months",
          "He stayed abroad",
          "He was given a better job",
          "He refused the offer too"],
         "بند چهارم."),

        ("How would Reza decide today?",
         "رضا امروز چطور تصمیم می‌گرفت؟",
         "The same way",
         ["The same way", "He would accept it", "He would ask for more money", "He doesn't know"],
         "بند آخر — شرطی دوم."),

        ("How does Reza feel now?",
         "رضا حالا چه حسی دارد؟",
         "He has no regrets",
         ["He has no regrets", "He is still frustrated", "He blames his mother", "He feels guilty"],
         "بند آخر."),
    ],
)

# --------------------------------------------------------------- درس ۲۰
STORIES[20] = (
    "What He Actually Said",
    [
        ("At the meeting on Tuesday, the manager suggested delaying the launch. "
         "He said the data wasn't conclusive yet.",
         "در جلسه سه‌شنبه، مدیر پیشنهاد داد راه‌اندازی را عقب بیندازیم. گفت داده‌ها هنوز قطعی نیست."),

        ("Two people heard it differently. One told the team that the project had been cancelled. "
         "The other said that nothing had been decided.",
         "دو نفر دو جور شنیدند. یکی به تیم گفت پروژه لغو شده. دیگری گفت هیچ تصمیمی گرفته نشده."),

        ("By Thursday the rumour had spread. Someone asked me whether we were all losing our jobs. "
         "I said I didn't know, which was true.",
         "تا پنج‌شنبه شایعه پخش شده بود. یکی از من پرسید آیا همه‌مان کارمان را از دست می‌دهیم. گفتم نمی‌دانم، که راست بود."),

        ("The manager called a second meeting. He admitted that he hadn't been clear, "
         "and he apologised for the confusion.",
         "مدیر جلسه دومی گذاشت. پذیرفت که روشن حرف نزده بوده و بابت سردرگمی عذرخواهی کرد."),

        ("Then he explained it again, in three short sentences. "
         "Nobody misunderstood him the second time.",
         "بعد دوباره توضیحش داد، در سه جمله کوتاه. بار دوم کسی بد برداشت نکرد."),
    ],
    [
        ("What did the manager suggest?",
         "مدیر چه پیشنهادی داد؟",
         "Delaying the launch",
         ["Delaying the launch", "Cancelling the project", "Hiring more people", "Changing the data"],
         "بند اول — بعد از suggest فعل ing می‌آید."),

        ("What did the first person tell the team?",
         "نفر اول به تیم چه گفت؟",
         "That the project had been cancelled",
         ["That the project had been cancelled",
          "That nothing had been decided",
          "That the launch was ready",
          "That the manager was leaving"],
         "بند دوم."),

        ("What did someone ask the narrator?",
         "یکی از راوی چه پرسید؟",
         "Whether they were losing their jobs",
         ["Whether they were losing their jobs",
          "When the launch would happen",
          "Who had started the rumour",
          "Why the manager was angry"],
         "بند سوم — نقل قول پرسش."),

        ("What did the manager admit?",
         "مدیر چه چیزی را پذیرفت؟",
         "That he hadn't been clear",
         ["That he hadn't been clear",
          "That the project was cancelled",
          "That the data was wrong",
          "That he had lied"],
         "بند چهارم."),

        ("What happened the second time?",
         "بار دوم چه شد؟",
         "Nobody misunderstood him",
         ["Nobody misunderstood him",
          "The rumour spread again",
          "The meeting was cancelled",
          "He said nothing new"],
         "بند آخر."),
    ],
)

# --------------------------------------------------------------- درس ۲۵
STORIES[25] = (
    "The Flat Upstairs",
    [
        ("The flat upstairs had been empty for a year. It is said that the owner lives abroad "
         "and never visits.",
         "آپارتمان طبقه بالا یک سال خالی بود. گفته می‌شود صاحبش خارج زندگی می‌کند و هرگز نمی‌آید."),

        ("Last spring we had the roof repaired, and the workers found a leak above that flat. "
         "The water had been running for months.",
         "بهار پیش سقف را دادیم تعمیر کنند و کارگرها بالای همان آپارتمان نشتی پیدا کردند. آب ماه‌ها جاری بوده."),

        ("The estate agent was called. He said the owner had been informed, "
         "but nothing was done for six weeks.",
         "به بنگاه املاک زنگ زدند. گفت صاحبخانه در جریان گذاشته شده، اما شش هفته هیچ کاری نشد."),

        ("Then, one morning, a plumber arrived. The work was carried out in two days "
         "and the flat was decorated the following week.",
         "بعد، یک روز صبح، لوله‌کشی آمد. کار در دو روز انجام شد و هفته بعد آپارتمان رنگ شد."),

        ("A young couple moved in last month. They get on well with everyone. "
         "Apparently the owner had sold the flat and nobody had told us.",
         "زوج جوانی ماه پیش اسباب‌کشی کردند. با همه خوب کنار می‌آیند. ظاهراً صاحبخانه آپارتمان را فروخته بوده و کسی به ما نگفته بود."),
    ],
    [
        ("How long had the flat been empty?",
         "آپارتمان چه مدت خالی بود؟",
         "For a year",
         ["For a year", "For six weeks", "For two days", "For a month"],
         "بند اول."),

        ("What did they have done to the roof?",
         "با سقف چه کردند؟",
         "They had it repaired",
         ["They had it repaired", "They repaired it themselves", "They had it painted", "They replaced it"],
         "بند دوم — ساختار have something done."),

        ("What did the estate agent say?",
         "بنگاه املاک چه گفت؟",
         "That the owner had been informed",
         ["That the owner had been informed",
          "That the owner was coming",
          "That nothing could be done",
          "That the flat had been sold"],
         "بند سوم."),

        ("How long did the repair take?",
         "تعمیر چقدر طول کشید؟",
         "Two days",
         ["Two days", "Six weeks", "A month", "A year"],
         "بند چهارم."),

        ("What had actually happened to the flat?",
         "در واقع چه بلایی سر آپارتمان آمده بود؟",
         "It had been sold",
         ["It had been sold", "It had been rented", "It had been closed", "It had been damaged"],
         "بند آخر."),
    ],
)

# --------------------------------------------------------------- درس ۳۰
STORIES[30] = (
    "The Presentation",
    [
        ("I had been preparing the presentation for two weeks. "
         "In my opinion it was the best work I had done that year.",
         "دو هفته بود که ارائه را آماده می‌کردم. به نظر من بهترین کاری بود که آن سال کرده بودم."),

        ("On the morning, the projector wasn't working. If I had checked it the day before, "
         "I would have found the problem. I had to present without slides.",
         "آن صبح، پروژکتور کار نمی‌کرد. اگر روز قبل بررسی‌اش کرده بودم، مشکل را پیدا می‌کردم. مجبور شدم بدون اسلاید ارائه بدهم."),

        ("I began by outlining the plan. On the one hand I had no visual aid; "
         "on the other hand, people had to listen instead of reading.",
         "با گفتن کلیات برنامه شروع کردم. از یک سو کمک بصری نداشتم؛ از سوی دیگر، مردم مجبور بودند به‌جای خواندن گوش بدهند."),

        ("A colleague objected to one point, and I conceded it. "
         "Admittedly, the cost was higher than I had said. Nonetheless, the benefits outweighed it.",
         "همکاری به یک نکته اعتراض کرد و من پذیرفتمش. باید اعتراف کرد هزینه بیشتر از آن بود که گفته بودم. با این حال، فایده‌ها بر آن می‌چربید."),

        ("The proposal was accepted. Afterwards my manager said it had been the clearest "
         "presentation of the year — and that I should never use slides again.",
         "پیشنهاد پذیرفته شد. بعدش مدیرم گفت روشن‌ترین ارائه سال بوده — و اینکه دیگر هرگز از اسلاید استفاده نکنم."),
    ],
    [
        ("How long had he been preparing?",
         "چه مدت آماده می‌کرد؟",
         "For two weeks",
         ["For two weeks", "For two days", "Since the morning", "For a year"],
         "بند اول — حال کامل استمراری در گذشته."),

        ("What would have happened if he had checked the projector?",
         "اگر پروژکتور را بررسی کرده بود چه می‌شد؟",
         "He would have found the problem",
         ["He would have found the problem",
          "The presentation would have been cancelled",
          "He would have used slides anyway",
          "Nothing would have changed"],
         "بند دوم — شرطی نوع سوم."),

        ("What was the advantage of having no slides?",
         "نداشتن اسلاید چه مزیتی داشت؟",
         "People had to listen",
         ["People had to listen",
          "The talk was shorter",
          "Nobody objected",
          "He finished early"],
         "بند سوم."),

        ("What did he do when a colleague objected?",
         "وقتی همکاری اعتراض کرد چه کرد؟",
         "He conceded the point",
         ["He conceded the point", "He refused to answer", "He changed the proposal", "He got angry"],
         "بند چهارم."),

        ("What did the manager say afterwards?",
         "مدیر بعدش چه گفت؟",
         "It had been the clearest presentation of the year",
         ["It had been the clearest presentation of the year",
          "It had been too long",
          "The proposal was rejected",
          "He should prepare better slides"],
         "بند آخر."),
    ],
)
