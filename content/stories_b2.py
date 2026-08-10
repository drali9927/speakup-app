# -*- coding: utf-8 -*-
"""
داستانک‌های سطح B2 — شش داستان روی دروس ۵، ۱۰، ۱۵، ۲۰، ۲۵ و ۳۰.

همان قاعده B1: هر داستان فقط از گرامر و واژه دروسِ پیش از خودش
استفاده می‌کند. تفاوت B2 در جنس متن است، نه در طولش — اینجا داستان
باید **موقعیت مبهم** بسازد، نه صرفاً زنجیره اتفاق. یعنی چیزی که
پاسخش در متن هست اما مستقیم گفته نشده، تا خواندن واقعاً به فهمیدن
نیاز داشته باشد و نه به پیدا کردن جمله.

  درس ۵  ← شرطی سوم، سناریو و احتمال
  درس ۱۰ ← مصدر و اسم‌مصدر، توضیح دشواری
  درس ۱۵ ← وارونگی و جمله شکافته، تأکید
  درس ۲۰ ← زبان احتیاطی، تخمین، ارائه
  درس ۲۵ ← نامه رسمی، لحن، هم‌آیی
  درس ۳۰ ← جمع‌بندی: اقناع و پاسخ به اعتراض

ساختار: (عنوان, [(بند, ترجمه)], [(پرسش, ترجمه, پاسخ, [گزینه‌ها], راهنما)])
"""

STORIES = {}

STORIES[5] = (
    "The Second Interview",
    [
        ("Maryam had prepared for the interview for a week. She knew the figures, "
         "and she had rehearsed every answer she could think of.",
         "مریم یک هفته برای مصاحبه آماده شده بود. ارقام را می‌دانست و هر پاسخی را که به ذهنش رسیده بود تمرین کرده بود."),

        ("The first question was one she hadn't expected: what would you change about our product? "
         "If she had said nothing, she would have seemed uninterested. "
         "If she had said too much, she would have seemed rude.",
         "اولین پرسش چیزی بود که انتظارش را نداشت: چه چیزی را در محصول ما عوض می‌کردی؟ اگر چیزی نمی‌گفت، بی‌علاقه به نظر می‌رسید. اگر زیادی می‌گفت، گستاخ."),

        ("She named one thing, and explained why. The room was quiet for a moment. "
         "Then the manager wrote something down and moved on.",
         "یک چیز را نام برد و دلیلش را گفت. اتاق لحظه‌ای ساکت شد. بعد مدیر چیزی یادداشت کرد و رفت سراغ بعدی."),

        ("She got the job. Months later her manager told her that everyone had answered that question, "
         "but only two had given a reason. On reflection, she had been lucky: "
         "she had said what she actually thought.",
         "شغل را گرفت. ماه‌ها بعد مدیرش گفت همه به آن پرسش جواب داده بودند، اما فقط دو نفر دلیل آورده بودند. پس از تأمل، خوش‌شانس بوده: چیزی را گفته بود که واقعاً فکر می‌کرد."),
    ],
    [
        ("What would have happened if she had said nothing?",
         "اگر چیزی نمی‌گفت چه می‌شد؟",
         "She would have seemed uninterested",
         ["She would have seemed uninterested", "She would have got the job anyway",
          "She would have seemed rude", "The manager would have asked again"],
         "بند دوم — شرطی نوع سوم."),

        ("What did she actually do?",
         "در واقع چه کرد؟",
         "She named one thing and gave a reason",
         ["She named one thing and gave a reason", "She said nothing",
          "She asked for time", "She listed several problems"],
         "بند سوم."),

        ("What made her answer unusual?",
         "چه چیزی پاسخش را متفاوت کرد؟",
         "She gave a reason",
         ["She gave a reason", "She was the first to answer",
          "She criticised the product", "She had prepared it"],
         "بند آخر — «only two had given a reason»."),

        ("How does she explain her success now?",
         "حالا موفقیتش را چطور توضیح می‌دهد؟",
         "She said what she really thought",
         ["She said what she really thought", "She had prepared better than others",
          "The manager already knew her", "She was the only candidate"],
         "بند آخر."),
    ],
)

STORIES[10] = (
    "Too Technical to Explain",
    [
        ("My father asked me what I do at work. I started to explain, and after two minutes "
         "I could see that he had stopped following.",
         "پدرم پرسید سر کار چه می‌کنم. شروع کردم به توضیح دادن و بعد از دو دقیقه دیدم دیگر دنبال نمی‌کند."),

        ("The problem wasn't him. It's no use blaming the listener when the explanation is bad. "
         "I had used five technical words in one sentence.",
         "مشکل از او نبود. وقتی توضیح بد است، سرزنش کردن شنونده فایده‌ای ندارد. در یک جمله پنج واژه فنی به کار برده بودم."),

        ("So I tried again, in order to make it simpler. I compared the system to a post office: "
         "letters come in, they get sorted, they go out. He understood it immediately.",
         "پس دوباره تلاش کردم تا ساده‌ترش کنم. سامانه را به یک اداره پست تشبیه کردم: نامه‌ها می‌آیند، دسته‌بندی می‌شوند، بیرون می‌روند. فوراً فهمید."),

        ("Then he asked a question that I couldn't answer. It was a good question. "
         "Explaining something in simple words is the fastest way to find out what you don't understand yourself.",
         "بعد پرسشی پرسید که نتوانستم جوابش را بدهم. پرسش خوبی بود. توضیح دادن چیزی به زبان ساده سریع‌ترین راه فهمیدن این است که خودت چه چیزی را نمی‌دانی."),
    ],
    [
        ("Why did his father stop following?",
         "چرا پدرش دیگر دنبال نکرد؟",
         "The explanation used too many technical words",
         ["The explanation used too many technical words", "He wasn't interested",
          "The story was too long", "He didn't hear well"],
         "بند دوم."),

        ("What did the narrator compare the system to?",
         "راوی سامانه را به چه چیزی تشبیه کرد؟",
         "A post office",
         ["A post office", "A library", "A factory", "A school"],
         "بند سوم."),

        ("Why did he use the comparison?",
         "چرا از تشبیه استفاده کرد؟",
         "In order to make it simpler",
         ["In order to make it simpler", "Because his father worked there",
          "To make the story longer", "Because he had forgotten the details"],
         "بند سوم — مصدر هدف."),

        ("What did the narrator learn?",
         "راوی چه چیزی یاد گرفت؟",
         "Explaining simply shows what you don't understand",
         ["Explaining simply shows what you don't understand",
          "His father knew more than him",
          "Technical words are always wrong",
          "He should not talk about work"],
         "بند آخر."),
    ],
)

STORIES[15] = (
    "It Was the Quiet One",
    [
        ("Never have I been so wrong about a colleague. When Nima joined, he said almost nothing "
         "in meetings, and I assumed he had nothing to add.",
         "هرگز درباره همکاری این‌قدر اشتباه نکرده بودم. وقتی نیما آمد، در جلسات تقریباً چیزی نمی‌گفت و من فکر کردم چیزی برای گفتن ندارد."),

        ("What I hadn't noticed was that he was writing. After every meeting he sent a short summary "
         "to the team — three lines, no extra words.",
         "چیزی که ندیده بودم این بود که می‌نوشت. بعد از هر جلسه خلاصه کوتاهی برای تیم می‌فرستاد — سه خط، بدون واژه اضافه."),

        ("Rarely does anyone read long notes, but everyone read his. "
         "It was those summaries that stopped three arguments before they started.",
         "به‌ندرت کسی یادداشت بلند می‌خواند، اما همه مال او را می‌خواندند. همان خلاصه‌ها بود که جلوی سه بحث را پیش از شروعشان گرفت."),

        ("What matters in a meeting isn't how much you say. "
         "Not only did Nima say less than everyone, he was also the one who was listened to most.",
         "آنچه در جلسه مهم است این نیست که چقدر حرف می‌زنی. نیما نه‌تنها از همه کمتر حرف می‌زد، بلکه همان کسی بود که بیش از همه به حرفش گوش می‌دادند."),
    ],
    [
        ("What did the narrator assume at first?",
         "راوی اول چه فکری کرد؟",
         "That Nima had nothing to add",
         ["That Nima had nothing to add", "That Nima was new to the job",
          "That Nima wrote too much", "That Nima disliked meetings"],
         "بند اول."),

        ("What did Nima do after every meeting?",
         "نیما بعد از هر جلسه چه می‌کرد؟",
         "He sent a short summary",
         ["He sent a short summary", "He wrote a long report",
          "He called each person", "He asked for another meeting"],
         "بند دوم."),

        ("What effect did the summaries have?",
         "خلاصه‌ها چه اثری داشتند؟",
         "They stopped arguments before they started",
         ["They stopped arguments before they started",
          "They made meetings longer",
          "They were never read",
          "They replaced the meetings"],
         "بند سوم — جمله شکافته با It was…"),

        ("What is the point of the last paragraph?",
         "نکته بند آخر چیست؟",
         "Saying less can carry more weight",
         ["Saying less can carry more weight",
          "Meetings should be shorter",
          "Writing is better than speaking",
          "Quiet people are always right"],
         "بند آخر."),
    ],
)

STORIES[20] = (
    "Roughly Two Hundred",
    [
        ("Before the event, I was asked how many people would come. "
         "I said roughly two hundred, give or take.",
         "پیش از برنامه، از من پرسیدند چند نفر می‌آیند. گفتم تقریباً دویست نفر، کم‌وبیش."),

        ("The data suggested that number. Attendance at similar events tends to fall "
         "between one hundred and fifty and two hundred and fifty.",
         "داده‌ها همان عدد را نشان می‌داد. حضور در برنامه‌های مشابه معمولاً بین صد و پنجاه تا دویست و پنجاه است."),

        ("On the day, three hundred and ten people arrived. We had chairs for two hundred. "
         "Admittedly, I had not considered that the weather would be good.",
         "روز برنامه سیصد و ده نفر آمدند. برای دویست نفر صندلی داشتیم. باید اعتراف کرد که در نظر نگرفته بودم هوا خوب باشد."),

        ("In my report I wrote that the estimate had been based on past data "
         "and that one variable had been ignored. Generally speaking, that is a better sentence "
         "than saying you were wrong — because it says what to change next time.",
         "در گزارشم نوشتم که تخمین بر پایه داده‌های گذشته بوده و یک متغیر نادیده گرفته شده. به‌طور کلی این جمله بهتر از گفتن «اشتباه کردم» است — چون می‌گوید دفعه بعد چه چیزی باید عوض شود."),
    ],
    [
        ("What was the original estimate?",
         "تخمین اولیه چه بود؟",
         "Roughly two hundred",
         ["Roughly two hundred", "Exactly two hundred",
          "Three hundred and ten", "One hundred and fifty"],
         "بند اول."),

        ("Where did the estimate come from?",
         "تخمین از کجا آمده بود؟",
         "From data on similar events",
         ["From data on similar events", "From a guess",
          "From the number of chairs", "From the weather forecast"],
         "بند دوم."),

        ("Which variable had been ignored?",
         "کدام متغیر نادیده گرفته شده بود؟",
         "The weather",
         ["The weather", "The cost", "The location", "The time of day"],
         "بند سوم."),

        ("Why does the narrator prefer his sentence in the report?",
         "چرا راوی جمله گزارشش را ترجیح می‌دهد؟",
         "It says what to change next time",
         ["It says what to change next time",
          "It hides the mistake",
          "It blames the data",
          "It is shorter"],
         "بند آخر."),
    ],
)

STORIES[25] = (
    "The Tone of the Email",
    [
        ("A supplier had sent us the wrong parts twice. My first email said: "
         "This is the second time. Please fix it.",
         "یک تأمین‌کننده دو بار قطعه اشتباه فرستاده بود. ایمیل اولم این بود: این دومین بار است. لطفاً درستش کنید."),

        ("My manager read it before it went out. He said it wasn't wrong, "
         "but that it would come across as rude, and that we would need this supplier again.",
         "مدیرم پیش از فرستادن خواندش. گفت غلط نیست، اما بی‌ادبانه به نظر می‌رسد و ما دوباره به این تأمین‌کننده نیاز داریم."),

        ("We rewrote it together. I am writing with reference to order 4120. "
         "Unfortunately the parts we received do not match the order. "
         "I would be grateful if you could confirm the correct delivery date.",
         "با هم بازنویسی‌اش کردیم. «با اشاره به سفارش ۴۱۲۰ می‌نویسم. متأسفانه قطعاتی که دریافت کردیم با سفارش نمی‌خواند. سپاسگزار می‌شوم اگر تاریخ تحویل درست را تأیید کنید.»"),

        ("The reply came the same day, with an apology and a new date. "
         "Nothing in the second email was softer in meaning. Only the wording had been changed.",
         "پاسخ همان روز آمد، با عذرخواهی و تاریخ تازه. هیچ‌چیز در ایمیل دوم از نظر معنا نرم‌تر نبود. فقط نگارشش عوض شده بود."),
    ],
    [
        ("What was wrong with the first email?",
         "ایمیل اول چه ایرادی داشت؟",
         "Its tone, not its meaning",
         ["Its tone, not its meaning", "It had the wrong order number",
          "It was too long", "It asked for the wrong thing"],
         "بند دوم."),

        ("Why did the manager care about the tone?",
         "چرا مدیر به لحن اهمیت داد؟",
         "They would need the supplier again",
         ["They would need the supplier again", "The email was going to a customer",
          "The supplier had complained before", "It was company policy"],
         "بند دوم."),

        ("What did the second email ask for?",
         "ایمیل دوم چه چیزی خواست؟",
         "Confirmation of the correct delivery date",
         ["Confirmation of the correct delivery date", "A refund",
          "An apology", "A new supplier"],
         "بند سوم."),

        ("What is the point of the last paragraph?",
         "نکته بند آخر چیست؟",
         "The wording changed, not the message",
         ["The wording changed, not the message",
          "The second email asked for less",
          "The supplier was frightened",
          "Polite emails get slower replies"],
         "بند آخر."),
    ],
)

STORIES[30] = (
    "Building the Case",
    [
        ("We wanted to replace a system that everyone complained about but nobody wanted to pay for. "
         "Admittedly, the cost was high.",
         "می‌خواستیم سامانه‌ای را عوض کنیم که همه از آن شکایت داشتند اما کسی حاضر نبود پولش را بدهد. باید اعتراف کرد هزینه‌اش بالا بود."),

        ("Instead of starting with the price, we started with the hours. "
         "We had measured how long the old system took: eleven hours a week, across the team.",
         "به‌جای شروع از قیمت، از ساعت‌ها شروع کردیم. اندازه گرفته بودیم سامانه قدیمی چقدر وقت می‌گیرد: هفته‌ای یازده ساعت، در کل تیم."),

        ("Someone objected that the figure was not conclusive. That was a valid concern, "
         "and we conceded it: the measurement had covered only one month.",
         "یکی اعتراض کرد که رقم قطعی نیست. نگرانی بجایی بود و پذیرفتیمش: اندازه‌گیری فقط یک ماه را پوشش داده بود."),

        ("Nonetheless, even at half that figure the change paid for itself within a year. "
         "It was that sentence, and not the long report, that won everyone over.",
         "با این حال، حتی با نصف آن رقم هم تغییر ظرف یک سال هزینه خودش را درمی‌آورد. همان یک جمله بود، نه گزارش بلند، که همه را قانع کرد."),
    ],
    [
        ("Why was the change difficult to propose?",
         "چرا پیشنهاد این تغییر سخت بود؟",
         "The cost was high",
         ["The cost was high", "Nobody complained about the old system",
          "The team was too small", "There was no alternative"],
         "بند اول."),

        ("What did they start with instead of the price?",
         "به‌جای قیمت از چه چیزی شروع کردند؟",
         "The hours the old system took",
         ["The hours the old system took", "The list of complaints",
          "The supplier's offer", "The manager's opinion"],
         "بند دوم."),

        ("How did they answer the objection?",
         "به اعتراض چطور پاسخ دادند؟",
         "They accepted it and explained the limit",
         ["They accepted it and explained the limit",
          "They ignored it",
          "They repeated the figure",
          "They asked for another month"],
         "بند سوم."),

        ("What convinced everyone in the end?",
         "در پایان چه چیزی همه را قانع کرد؟",
         "One sentence about paying for itself",
         ["One sentence about paying for itself",
          "The long report",
          "The manager's decision",
          "The list of complaints"],
         "بند آخر — جمله شکافته با It was…"),
    ],
)
