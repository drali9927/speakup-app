# -*- coding: utf-8 -*-
"""مکالمه، گرامر و تمرین دروس ۲۱ تا ۳۰ سطح B1."""

P, R = "تولیدی", "تشخیصی"

DLG = {
 21:[("A","Have you seen the news?","خبر را دیدی؟"),
     ("B","It is said that prices will rise.","گفته می‌شود قیمت‌ها بالا می‌رود."),
     ("A","Has it been confirmed officially?","رسماً تأیید شده؟"),
     ("B","No. Nothing has been confirmed.","نه. چیزی تأیید نشده."),
     ("A","Then take it with a pinch of salt.","پس با احتیاط بپذیرش."),
     ("B","Agreed. We should verify it first.","موافقم. اول باید راستی‌آزمایی کنیم.")],
 22:[("A","Did you fix the car yourself?","خودت خودرو را درست کردی؟"),
     ("B","No, I had it repaired.","نه، دادم تعمیرش کنند."),
     ("A","Was it expensive?","گران بود؟"),
     ("B","They sent a quote first. It's under warranty.","اول قیمت پیشنهادی فرستادند. تحت گارانتی است."),
     ("A","We should get the flat decorated too.","آپارتمان را هم باید بدهیم رنگ کنند."),
     ("B","I'll sort it out this week.","این هفته حلش می‌کنم.")],
 23:[("A","What are you doing today?","امروز چه کار می‌کنی؟"),
     ("B","I have a few errands. Lots of paperwork.","چند کار بیرون دارم. کاغذبازی زیاد."),
     ("A","Did you fill in the form?","فرم را پر کردی؟"),
     ("B","Yes. I'll hand it in this afternoon.","بله. بعدازظهر تحویلش می‌دهم."),
     ("A","Don't forget proof of address.","مدرک نشانی را فراموش نکن."),
     ("B","I have a copy. Let's get it over with.","رونوشت دارم. بیا تمامش کنیم.")],
 24:[("A","Who's the man who lives upstairs?","مردی که طبقه بالا زندگی می‌کند کیست؟"),
     ("B","That's the landlord, whose car broke down yesterday.","صاحبخانه است، همانی که دیروز خودروش خراب شد."),
     ("A","Is the flat furnished?","آپارتمان مبله است؟"),
     ("B","Yes, and the rooms are spacious.","بله، و اتاق‌ها جادارند."),
     ("A","Do you get on with the neighbours?","با همسایه‌ها کنار می‌آیی؟"),
     ("B","Mostly. But I can't put up with the noise.","بیشتر اوقات. اما سروصدا را تحمل نمی‌کنم.")],
 25:[("A","Did you grow up here?","اینجا بزرگ شدی؟"),
     ("B","Yes. I used to walk to school every day.","بله. هر روز پیاده مدرسه می‌رفتم."),
     ("A","Was it different then?","آن موقع فرق داشت؟"),
     ("B","Every summer we would visit my grandmother.","هر تابستان می‌رفتیم دیدن مادربزرگم."),
     ("A","There used to be a shop on this corner.","قبلاً سر این نبش مغازه‌ای بود."),
     ("B","The street has changed beyond recognition.","خیابان تا حد ناشناختنی عوض شده.")],
 26:[("A","Did you go despite the rain?","با وجود باران رفتی؟"),
     ("B","We did. Although it was hard, we finished.","بله. اگرچه سخت بود تمام کردیم."),
     ("A","Was it worth it?","ارزشش را داشت؟"),
     ("B","The cost was high. However, the result was good.","هزینه بالا بود. اما نتیجه خوب بود."),
     ("A","I'd have stayed at home.","من خانه می‌ماندم."),
     ("B","Even so, I'd do it again.","با این وجود دوباره انجامش می‌دادم.")],
 27:[("A","Which one should we choose?","کدام را انتخاب کنیم؟"),
     ("B","This one is by far the best.","این به‌مراتب بهترین است."),
     ("A","But it costs far more.","اما خیلی بیشتر هزینه دارد."),
     ("B","The benefits outweigh the costs.","فایده‌ها بر هزینه‌ها می‌چربد."),
     ("A","The second one is nearly as good.","دومی تقریباً به‌اندازه خوب است."),
     ("B","True. The sooner we decide, the better.","درست است. هرچه زودتر تصمیم بگیریم بهتر.")],
 28:[("A","What do you think of the plan?","نظرت درباره برنامه چیست؟"),
     ("B","In my opinion, it's too early.","به نظر من زود است."),
     ("A","I see your point, but we have time.","منظورت را می‌فهمم، اما وقت داریم."),
     ("B","Personally, I doubt it will work.","شخصاً بعید می‌دانم جواب دهد."),
     ("A","Fair enough. That's a convincing argument.","قبول است. استدلال قانع‌کننده‌ای است."),
     ("B","Let's find common ground.","بیا نقطه مشترکی پیدا کنیم.")],
 29:[("A","So what's your conclusion?","پس نتیجه‌گیری‌ات چیست؟"),
     ("B","On the one hand, it's cheap.","از یک سو ارزان است."),
     ("A","And on the other hand?","و از سوی دیگر؟"),
     ("B","Firstly, it's slow. Secondly, it's risky.","نخست، کند است. دوم اینکه پرخطر است."),
     ("A","Is there a counter-argument?","استدلال متقابلی هست؟"),
     ("B","Overall, I'd say it's balanced. All in all, it works.","روی هم رفته می‌گویم متوازن است. در مجموع جواب می‌دهد.")],
 30:[("A","You've finished the intermediate level.","سطح متوسط را تمام کرده‌ای."),
     ("B","My fluency has really improved.","روانی‌ام واقعاً بهتر شده."),
     ("A","Can you follow a discussion now?","حالا می‌توانی بحث را دنبال کنی؟"),
     ("B","Yes, and I take part in meetings.","بله، و در جلسات شرکت می‌کنم."),
     ("A","What's next?","بعدش چه؟"),
     ("B","The advanced level. I'll keep practising.","سطح پیشرفته. تمرین را ادامه می‌دهم.")],
}

GR = {
 21:[("مجهول غیرشخصی",
      "وقتی خبری را می‌گویی که منبعش معلوم نیست، دو ساختار در اختیار داری که هر دو یک معنی می‌دهند.\n"
      "در شکل اول جمله را با it شروع می‌کنی و می‌گویی **It is said that** prices will rise.\n"
      "در شکل دوم خودِ فاعل را جلو می‌آوری و می‌گویی **Prices are said to** rise، که فشرده‌تر است و در تیتر خبری بیشتر دیده می‌شود.\n"
      "افعالی که در این ساختار می‌نشینند say و believe و think و report و know هستند.\n"
      "یک نکته را فراموش نکن: در شکل دوم بعد از are said حتماً to می‌آید.",
      "It is said that prices will rise.\nPrices are said to rise.\nIt is believed to be old.\nHe is thought to be abroad.",
      "کدام درست است؟", "It is said that prices will rise.",
      "It is said that prices will rise. | It is said prices will rise that. | They say that is said prices rise.",
      "It is said prices will rise that. ← that باید پیش از جمله بیاید.\n"
      "They say that is said prices rise. ← دو ساختار با هم مخلوط شده است."),
     ("قید و لحن خبر",
      "چند قید هست که نشان می‌دهند گوینده تا چه اندازه پشت خبر ایستاده است.\n"
      "قید **reportedly** یعنی بنا بر گزارش‌ها، و فاصله‌ای محترمانه با خبر می‌گذارد.\n"
      "قید **allegedly** یعنی بنا به ادعا و می‌گوید که موضوع هنوز ثابت نشده، پس در خبر حقوقی زیاد به کار می‌رود.\n"
      "قید **apparently** هم یعنی ظاهراً و از همه غیررسمی‌تر است.\n"
      "جای این‌ها معمولاً بین فاعل و فعل است، مثل He **reportedly** left the country.",
      "He reportedly left the country.\nThe money was allegedly missing.\nApparently nobody knew.\nIt was widely believed.",
      "کدام درست است؟", "He reportedly left the country.",
      "He reportedly left the country. | He left reportedly the country.",
      "He left reportedly the country. ← این قید بین فاعل و فعل می‌آید.")],

 22:[("have something done",
      "وقتی کاری را دیگری برای تو انجام می‌دهد، از ساختار have به‌علاوه مفعول و قسمت سوم فعل استفاده می‌کنی.\n"
      "پس وقتی می‌گویی I **had the car repaired**، یعنی ماشین تعمیر شد ولی خودت تعمیرش نکردی.\n"
      "ترتیب اجزا در این ساختار مهم است و جابه‌جا کردنش معنی را عوض می‌کند، پس I had repaired the car چیز دیگری می‌گوید.\n"
      "در فارسی همین مفهوم را با «دادم تعمیرش کنند» می‌رسانیم، و چون آنجا فعل معلوم است، فارسی‌زبان‌ها اغلب در انگلیسی هم اشتباهاً جمله معلوم می‌سازند.",
      "I had the car repaired.\nWe had the flat decorated.\nShe had her hair cut.\nWe had a new system installed.",
      "کدام درست است؟", "I had the car repaired.",
      "I had the car repaired. | I had repaired the car. | I have repaired the car by someone.",
      "I had repaired the car. ← این یعنی خودت تعمیرش کرده بودی.\n"
      "I have repaired the car by someone. ← چنین ساختاری در انگلیسی نیست."),
     ("get something done",
      "فعل **get** همان کار have را می‌کند اما لحنش گفتاری‌تر است.\n"
      "پس I **got my hair cut** دقیقاً همان معنی I had my hair cut را می‌دهد.\n"
      "در جمله امری و وقتی درباره برنامه‌ای حرف می‌زنی، get طبیعی‌تر می‌نشیند، مثل Let's **get it fixed** و I need to **get this checked**.\n"
      "برای ساختن زمان‌های دیگر هم کار سختی نداری، چون فقط have یا get صرف می‌شود: I'm **having** it repaired و I'll **get** it done.",
      "I got my hair cut.\nLet's get it fixed.\nI'm having it repaired.\nI'll get it done tomorrow.",
      "کدام درست است؟", "Let's get it fixed.",
      "Let's get it fixed. | Let's get fix it.",
      "Let's get fix it. ← بعد از مفعول، قسمت سوم فعل می‌آید.")],

 23:[("فعل‌های عبارتی کار اداری",
      "چند فعل عبارتی هست که در کار اداری روزمره مدام تکرار می‌شوند و معنی‌شان از اجزایشان درنمی‌آید، پس باید یکجا حفظشان کنی.\n"
      "برای پر کردن فرم **fill in** را داری، و برای تحویل دادن **hand in** و برای تحویل گرفتن **pick up**.\n"
      "برای رساندن چیزی **drop off** به کار می‌رود و برای حل و فصل کردن یک کار **sort out**.\n"
      "یک نکته ساختاری هم هست که زیاد اشتباه می‌شود: وقتی مفعول ضمیر باشد، وسط فعل می‌نشیند. پس می‌گویی fill **it** in و نه fill in it.",
      "Fill in the form.\nFill it in, please.\nHand it in by Friday.\nI'll pick it up tomorrow.",
      "کدام درست است؟", "Fill it in, please.",
      "Fill it in, please. | Fill in it, please.",
      "Fill in it, please. ← با ضمیر، مفعول بین فعل و حرف اضافه می‌آید."),
     ("apply for و register",
      "حرف اضافه این فعل‌ها ثابت است و عوض کردنش از پرتکرارترین اشتباه‌هاست.\n"
      "برای درخواست مجوز می‌گویی **apply for** a permit و نه apply to a permit.\n"
      "برای ثبت‌نام در دوره **register for** a course می‌آید، اما برای ثبت‌نام نزد پزشک **register with** a doctor.\n"
      "برای درخواست مهلت بیشتر هم **ask for** an extension را داری.\n"
      "در مدارک هم همین دقت لازم است: **proof of** address و a **copy of** the original.",
      "I applied for a permit.\nYou must register first.\nWe asked for an extension.\nBring proof of address.",
      "کدام درست است؟", "I applied for a permit.",
      "I applied for a permit. | I applied to a permit.",
      "I applied to a permit. ← حرف اضافه ثابت for است.")],

 24:[("عبارت وصفی تعریفی",
      "بند وصفی تعریفی همان چیزی است که مشخص می‌کند منظورت کدام یکی است، و اگر برش داری جمله ناقص می‌ماند.\n"
      "مثلاً در The man **who lives upstairs** is a landlord، بدون آن بند معلوم نیست از کدام مرد حرف می‌زنی.\n"
      "برای افراد **who** می‌آوری و برای اشیا **which** یا **that**.\n"
      "برای مالکیت هم **whose** را داری، مثل the man **whose** car broke down.\n"
      "نکته نگارشی‌اش این است که این نوع بند هرگز ویرگول نمی‌گیرد.",
      "The man who lives upstairs is the landlord.\nThe flat which we saw was small.\nThe man whose car broke down.\nThe street where I grew up.",
      "کدام درست است؟", "The man whose car broke down.",
      "The man whose car broke down. | The man who his car broke down.",
      "The man who his car broke down. ← برای مالکیت whose می‌آید."),
     ("عبارت وصفی توضیحی",
      "بند وصفی توضیحی فقط اطلاعات اضافه می‌دهد و اگر حذفش کنی جمله باز هم کامل است، برای همین با ویرگول جدا می‌شود.\n"
      "مثلاً در My landlord, **who lives upstairs**, is friendly، جمله بدون آن بند هم معنی خودش را دارد.\n"
      "تفاوت این دو نوع را از دو نشانه بشناس: نوع تعریفی ویرگول نمی‌گیرد و that را می‌پذیرد، اما نوع توضیحی ویرگول می‌گیرد و that را نمی‌پذیرد.\n"
      "یک کاربرد دیگر هم دارد که وقتی به کل جمله پیش از خودش اشاره می‌کنی به کار می‌آید، مثل …, **which is why** we stayed.",
      "My landlord, who lives upstairs, is friendly.\nThe flat, which was cheap, needed work.\nIt rained, which is why we stayed.",
      "کدام درست است؟", "My landlord, who lives upstairs, is friendly.",
      "My landlord, who lives upstairs, is friendly. | My landlord, that lives upstairs, is friendly.",
      "My landlord, that lives upstairs, is friendly. ← بند توضیحی that نمی‌پذیرد.")],

 25:[("used to و would",
      "هر دو ساختار used to و would از عادت گذشته می‌گویند، اما دامنه‌شان یکی نیست.\n"
      "ساختار **used to** هم برای کاری که انجام می‌دادی به کار می‌رود و هم برای حالتی که وجود داشت، پس هم I used to walk درست است و هم There used to be a shop.\n"
      "اما **would** فقط برای کار تکراری می‌آید و نه برای حالت، مثل Every summer we would visit.\n"
      "به همین دلیل نمی‌توانی بگویی There would be a shop here و منظورت عادت گذشته باشد، چون «بودن» حالت است و would نمی‌پذیرد.",
      "I used to walk to school.\nThere used to be a shop here.\nEvery summer we would visit.\nWe would play in the backyard.",
      "کدام درست است؟", "There used to be a shop here.",
      "There used to be a shop here. | There would be a shop here.",
      "There would be a shop here. ← would برای حالت به کار نمی‌رود."),
     ("منفی و پرسش used to",
      "منفی و پرسشِ used to با did ساخته می‌شود و در این حالت d از آخر use می‌افتد.\n"
      "پس می‌گویی I **didn't use to** like it و می‌پرسی **Did you use to** live here؟\n"
      "شکل‌های didn't used to و did you used to نادرست‌اند، هرچند در گفتار گاهی شنیده می‌شوند.\n"
      "یادت باشد که این ساختار با **be used to** فرق دارد؛ آن یکی یعنی عادت داشتن و بعدش فعل با ing می‌آید.",
      "I didn't use to like coffee.\nDid you use to live here?\nI used to walk. (عادت گذشته)\nI'm used to walking. (عادت فعلی)",
      "کدام درست است؟", "Did you use to live here?",
      "Did you use to live here? | Did you used to live here?",
      "Did you used to live here? ← با did، فعل use بدون d می‌آید.")],

 26:[("despite و although",
      "هر دو گروه تضاد را می‌رسانند، اما آنچه بعدشان می‌آید یکسان نیست و همین‌جا بیشترین اشتباه رخ می‌دهد.\n"
      "بعد از **although** و **even though** یک جمله کامل می‌نشیند، مثل Although it **was** hard, we finished.\n"
      "اما بعد از **despite** و **in spite of** فقط اسم یا فعل با ing می‌آید، مثل Despite **the rain**, we went و Despite **being** tired….\n"
      "پرتکرارترین خطا هم همین است که بگویی despite it was hard.\n"
      "اگر بعد از despite حتماً جمله کامل می‌خواهی، باید بگویی despite **the fact that** it was hard.",
      "Although it was hard, we finished.\nDespite the rain, we went.\nIn spite of the cost, we bought it.\nDespite being tired, he continued.",
      "کدام درست است؟", "Despite the rain, we went.",
      "Despite the rain, we went. | Despite it rained, we went. | Despite of the rain, we went.",
      "Despite it rained, we went. ← بعد از despite جمله کامل نمی‌آید.\n"
      "Despite of the rain, we went. ← despite حرف اضافه of نمی‌گیرد؛ in spite of درست است."),
     ("مقایسه و تضاد در متن",
      "برای گذاشتن دو طرف یک مقایسه کنار هم، چند ابزار داری که جایشان در جمله فرق می‌کند.\n"
      "عبارت‌های **whereas** و **while** وسط جمله و با ویرگول می‌آیند، مثل He likes cities, **whereas** I prefer villages.\n"
      "اما **however** و **nevertheless** اول جمله تازه می‌نشینند و بعدشان ویرگول می‌گذاری.\n"
      "برای نقطه مقابل **on the contrary** را داری و برای مقایسه با یک اسم **unlike**.\n"
      "و وقتی می‌خواهی از شباهت بگویی، **similarly** و **likewise** به کارت می‌آیند.",
      "He likes cities, whereas I prefer villages.\nHowever, the cost was high.\nOn the contrary, it helped.\nUnlike me, she enjoys it.",
      "کدام درست است؟", "Unlike me, she enjoys it.",
      "Unlike me, she enjoys it. | Unlike I do, she enjoys it.",
      "Unlike I do, she enjoys it. ← بعد از unlike اسم یا ضمیر مفعولی می‌آید.")],

 27:[("the … the …",
      "برای گفتن «هرچه … بیشتر، … بیشتر» از الگوی دو نیمه‌ای استفاده می‌کنی که هر دو نیمه‌اش با the شروع می‌شود.\n"
      "کوتاه‌ترین شکلش **The sooner**, the better است.\n"
      "شکل کاملش را در **The more** we practise, **the better** we get می‌بینی.\n"
      "ساختارش این است که در هر نیمه، the می‌آید و بعدش صفت تفضیلی.\n"
      "پس دو چیز را جا نینداز: هر دو نیمه the می‌خواهند و صفت هم باید حتماً تفضیلی باشد.",
      "The sooner, the better.\nThe more we practise, the better we get.\nThe longer we wait, the harder it gets.",
      "کدام درست است؟", "The sooner, the better.",
      "The sooner, the better. | Sooner is better the.",
      "Sooner is better the. ← ساختار درست the + تفضیلی، the + تفضیلی است."),
     ("تشدید و تخفیف مقایسه",
      "چند قید هست که شدت یک مقایسه را کم و زیاد می‌کنند.\n"
      "قیدهای **much** و **far** و **considerably** مقایسه را پررنگ می‌کنند، مثل It's **far** more expensive.\n"
      "قیدهای **slightly** و **a bit** آن را کم‌رنگ می‌کنند، مثل It's **slightly** cheaper.\n"
      "برای صفت عالی هم **by far** را داری، مثل It's **by far** the best.\n"
      "یک نکته را بسپار: very با صفت تفضیلی نمی‌آید، پس very cheaper نادرست است.",
      "It's far more expensive.\nIt's slightly cheaper.\nIt's by far the best.\nIt's considerably faster.",
      "کدام درست است؟", "It's far more expensive.",
      "It's far more expensive. | It's very more expensive.",
      "It's very more expensive. ← very با صفت تفضیلی نمی‌آید؛ much یا far می‌آید.")],

 28:[("بیان نظر",
      "برای گفتن نظرت طیفی از عبارت‌ها داری که از محتاطانه شروع می‌شود و به قاطع می‌رسد.\n"
      "نرم‌ترینش **I'd say** it's too early است.\n"
      "یک پله بالاتر **In my opinion**, it's fair می‌آید و بعد از آن **Personally**, I disagree.\n"
      "و قاطع‌ترینشان **As far as I'm concerned**, it's done است.\n"
      "یک هشدار هم داشته باش: عبارت درست in my opinion است و نه in my idea، چون آن یکی ترجمه لفظ‌به‌لفظ «به نظر من» است و در انگلیسی معنا ندارد.",
      "I'd say it's too early.\nIn my opinion, it's fair.\nPersonally, I disagree.\nAs far as I'm concerned, it's done.",
      "کدام درست است؟", "In my opinion, it's fair.",
      "In my opinion, it's fair. | In my idea, it's fair. | To my opinion, it's fair.",
      "In my idea, it's fair. ← چنین عبارتی در انگلیسی نیست.\n"
      "To my opinion, it's fair. ← حرف اضافه درست in است."),
     ("موافقت و مخالفت مؤدبانه",
      "موافقت و مخالفت هم درجه دارند و انتخاب درستشان لحن گفت‌وگو را می‌سازد.\n"
      "برای موافقت کامل **Absolutely** و **Exactly** و **Fair enough** را داری.\n"
      "برای موافقت جزئی می‌گویی **I partly agree** یا **I see your point, but…**.\n"
      "برای مخالفت نرم **I'm not sure about that** به کار می‌رود و برای مخالفت قاطع **I strongly disagree**.\n"
      "و اگر بحث به جایی نرسید، می‌توانی بدون درگیری تمامش کنی با **Let's agree to disagree**.",
      "Absolutely, I agree.\nI see your point, but…\nI'm not sure about that.\nLet's agree to disagree.",
      "کدام مخالفت نرم است؟", "I'm not sure about that.",
      "I'm not sure about that. | I strongly disagree.",
      "I strongly disagree. ← این مخالفت قاطع است، نه نرم.")],

 29:[("ساختن یک استدلال",
      "هر استدلال منظم چهار جای ثابت دارد که با عبارت‌های مشخصی پر می‌شوند.\n"
      "برای گذاشتن دو طرف کنار هم **On the one hand** … **on the other hand** … را داری.\n"
      "برای شماره‌گذاری دلیل‌ها **Firstly** … **Secondly** … **Finally** … می‌آید.\n"
      "برای افزودن دلیل تازه از **Moreover** و **Furthermore** استفاده می‌کنی.\n"
      "و برای رسیدن به نتیجه **Therefore** و **Thus** و **In conclusion** را داری.\n"
      "یک قاعده را رعایت کن: اگر on the one hand را آوردی، on the other hand هم باید بیاید وگرنه جمله نیمه‌کاره می‌ماند.",
      "On the one hand, it's cheap.\nOn the other hand, it's slow.\nMoreover, it saves time.\nTherefore, we chose the second.",
      "کدام درست است؟", "On the other hand, it's slow.",
      "On the other hand, it's slow. | In the other hand, it's slow.",
      "In the other hand, it's slow. ← حرف اضافه درست on است."),
     ("وزن دادن به دو طرف",
      "برای اینکه استدلالت یک‌طرفه به نظر نرسد، باید نشان دهی طرف دیگر را هم دیده‌ای.\n"
      "می‌گویی There's **evidence for** both sides تا معلوم شود هر دو طرف دلیل دارند.\n"
      "با There's a **counter-argument** به استدلال مقابل اشاره می‌کنی.\n"
      "با The benefits **outweigh** the costs می‌گویی کدام طرف سنگین‌تر است.\n"
      "و در پایان با **Overall** یا **All in all**, it's balanced جمع‌بندی می‌کنی.\n"
      "حواست به حرف اضافه باشد: بعد از evidence همیشه for می‌آید و نه of.",
      "There's evidence for both sides.\nThere's a counter-argument.\nThe benefits outweigh the costs.\nAll in all, it went well.",
      "کدام درست است؟", "There's evidence for both sides.",
      "There's evidence for both sides. | There's evidence of both sides.",
      "There's evidence of both sides. ← در این معنا حرف اضافه for درست است.")],

 30:[("جمع‌بندی B1 — ساختارها",
      "این سطح روی پنج ساختار بنا شده و اگر هر پنج را بشناسی، B1 را واقعاً تمام کرده‌ای.\n"
      "با **حال کامل استمراری** از کاری می‌گویی که ادامه داشته، مثل I've been working here.\n"
      "با **گذشته کامل** ترتیب دو رویداد گذشته را روشن می‌کنی، مثل She had already left.\n"
      "با **شرطی دوم و wish** از فرض و آرزو حرف می‌زنی، مثل If I had time… و I wish I knew.\n"
      "با **مدال به‌علاوه have** درباره گذشته حدس می‌زنی، مثل He must have forgotten.\n"
      "و با **نقل قول کامل** حرف دیگران را بازگو می‌کنی، مثل She asked me where I lived.",
      "I've been working here.\nShe had already left.\nIf I had time, I'd help.\nHe must have forgotten.\nShe asked me where I lived.",
      "کدام حدس درباره گذشته است؟", "He must have forgotten.",
      "He must have forgotten. | She had already left.",
      "She had already left. ← این گذشته کامل است و حدس نیست."),
     ("جمع‌بندی B1 — کارکردها",
      "کنار آن ساختارها، حالا چهار کار هم هست که می‌توانی به انگلیسی انجام دهی.\n"
      "می‌توانی نظرت را بگویی، با عبارت‌هایی مثل In my opinion… و I see your point, but….\n"
      "می‌توانی استدلال بسازی، با On the one hand… Therefore….\n"
      "می‌توانی کاری را به دیگری بسپاری، مثل I had the car repaired.\n"
      "و می‌توانی دقیق توصیف کنی، مثل The man whose car broke down.\n"
      "قدم بعدی B2 است که سه چیز تازه می‌آورد: زبان انتزاعی، فرض درباره گذشته، و لحن رسمی.",
      "In my opinion, it's fair.\nOn the one hand, it's cheap.\nI had the car repaired.\nThe man whose car broke down.",
      "کدام سفارش کار به دیگری است؟", "I had the car repaired.",
      "I had the car repaired. | I repaired the car.",
      "I repaired the car. ← این یعنی خودت تعمیرش کردی.")],
}

EX = {
 21:[(R,"چندگزینه‌ای","It ___ that prices will rise.","is said","","is said | says | is saying","it is said","مجهول غیرشخصی با is said ساخته می‌شود."),
     (R,"تطبیق","allegation","ادعا، اتهام","","allegation | headline | media","allegation","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","He ___ left the country. (بنا بر گزارش‌ها)","reportedly","","","reportedly","قید بین فاعل و فعل می‌آید."),
     (P,"متن آزاد","درست کنید: He left reportedly the country.","He reportedly left the country.","","","reportedly","این قید پیش از فعل اصلی می‌آید."),
     (P,"متن آزاد","فشرده کنید: It is believed that it is old.","It is believed to be old.","","","it is believed","ساختار be + said/believed + to است."),
     (P,"مرتب‌سازی","fast / spread / news / the","The news spread fast.","","","spread","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","اول باید راستی‌آزمایی کنیم.","We should verify it first.","","","verify","با فاعل We شروع کن."),
     (P,"گفتار","Take it with a pinch of salt.","Take it with a pinch of salt.","","","take with a pinch of salt","جمله را واضح تلفظ کنید.")],

 22:[(R,"چندگزینه‌ای","I ___ the car repaired.","had","","had | had been | have repaired","have something done","ساختار have + مفعول + قسمت سوم است."),
     (R,"تطبیق","plumber","لوله‌کش","","plumber | electrician | technician","plumber","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","We had the flat ___ . (decorate)","decorated","","","decorate","بعد از مفعول قسمت سوم فعل می‌آید."),
     (P,"متن آزاد","درست کنید: I had repaired the car by a mechanic.","I had the car repaired by a mechanic.","","","have something done","مفعول پیش از قسمت سوم فعل می‌آید."),
     (P,"متن آزاد","با get بنویسید: let's / it / fix","Let's get it fixed.","","","get something done","بعد از مفعول قسمت سوم می‌آید."),
     (P,"مرتب‌سازی","order / of / lift / out / the / is","The lift is out of order.","","","out of order","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","هنوز تحت گارانتی است.","It's still under warranty.","","","under warranty","با فاعل It شروع کن."),
     (P,"گفتار","I'll sort it out today.","I'll sort it out today.","","","sort out","جمله را واضح تلفظ کنید.")],

 23:[(R,"چندگزینه‌ای","Fill ___ , please.","it in","","it in | in it | in","fill in","با ضمیر، مفعول وسط می‌آید."),
     (R,"تطبیق","paperwork","کاغذبازی","","paperwork | permit | proof","paperwork","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","I applied ___ a permit.","for","","","apply for","حرف اضافه ثابت for است."),
     (P,"متن آزاد","درست کنید: Fill in it, please.","Fill it in, please.","","","fill in","با ضمیر، مفعول بین فعل و حرف اضافه می‌آید."),
     (P,"متن آزاد","بنویسید: hand / it / by Friday","Hand it in by Friday.","","","hand in","جمله امری با فعل شروع می‌شود."),
     (P,"مرتب‌سازی","up / had / queue / we / to","We had to queue up.","","","queue up","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","باید کارتم را تمدید کنم.","I need to renew my card.","","","renew","با فاعل I شروع کن."),
     (P,"گفتار","Let's get it over with.","Let's get it over with.","","","get it over with","جمله را واضح تلفظ کنید.")],

 24:[(R,"چندگزینه‌ای","The man ___ car broke down.","whose","","whose | who | which","whose","برای مالکیت whose می‌آید."),
     (R,"تطبیق","tenant","مستأجر","","tenant | landlord | resident","tenant","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","My landlord, ___ lives upstairs, is friendly.","who","","","landlord","برای افراد who می‌آید."),
     (P,"متن آزاد","درست کنید: The man who his car broke down.","The man whose car broke down.","","","whose","برای مالکیت whose می‌آید."),
     (P,"متن آزاد","با which is why بنویسید: it rained / we stayed","It rained, which is why we stayed.","","","which is why","نیمه دوم با ویرگول می‌آید."),
     (P,"مرتب‌سازی","noise / put / can't / the / with / I / up","I can't put up with the noise.","","","put up with","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","با همسایه‌هایمان کنار می‌آییم.","We get on with our neighbours.","","","get on with","با فاعل We شروع کن."),
     (P,"گفتار","The rooms are spacious.","The rooms are spacious.","","","spacious","جمله را واضح تلفظ کنید.")],

 25:[(R,"چندگزینه‌ای","There ___ be a shop here.","used to","","used to | would | was used to","used to be","برای حالت گذشته used to می‌آید."),
     (R,"تطبیق","nostalgia","حس نوستالژی","","nostalgia | memory | tradition","nostalgia","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","Did you ___ to live here?","use","","","used to","با did فعل use بدون d می‌آید."),
     (P,"متن آزاد","درست کنید: Did you used to live here?","Did you use to live here?","","","used to","با did، d حذف می‌شود."),
     (P,"متن آزاد","با would بنویسید: every summer / we / visit","Every summer we would visit.","","","would","would برای کار تکراری گذشته می‌آید."),
     (P,"مرتب‌سازی","town / up / a / I / small / grew / in","I grew up in a small town.","","","grow up","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","خیابان تا حد ناشناختنی عوض شده.","The street has changed beyond recognition.","","","change beyond recognition","حال کامل بساز."),
     (P,"گفتار","I used to walk to school.","I used to walk to school.","","","used to","جمله را واضح تلفظ کنید.")],

 26:[(R,"چندگزینه‌ای","___ the rain, we went.","Despite","","Despite | Despite of | Although","despite","بعد از despite اسم می‌آید."),
     (R,"تطبیق","contrast","تضاد","","contrast | similarity | comparison","contrast","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","___ it was hard, we finished.","Although","","","although","پیش از جمله کامل although می‌آید."),
     (P,"متن آزاد","درست کنید: Despite it rained, we went.","Despite the rain, we went.","Although it rained, we went.","","despite","بعد از despite جمله کامل نمی‌آید."),
     (P,"متن آزاد","با whereas بنویسید: he likes cities / I prefer villages","He likes cities, whereas I prefer villages.","","","whereas","پیش از whereas ویرگول می‌آید."),
     (P,"مرتب‌سازی","so / again / it / even / I'd / do","Even so, I'd do it again.","","","even so","با Even so شروع کن."),
     (P,"ترجمه به انگلیسی","برخلاف من، او لذت می‌برد.","Unlike me, she enjoys it.","","","unlike","با Unlike شروع کن."),
     (P,"گفتار","However, the cost was high.","However, the cost was high.","","","however","جمله را واضح تلفظ کنید.")],

 27:[(R,"چندگزینه‌ای","It's ___ more expensive.","far","","far | very | so","far more","با صفت تفضیلی far می‌آید نه very."),
     (R,"تطبیق","trade-off","بده‌بستان","","trade-off | preference | criteria","trade-off","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","The sooner, ___ better.","the","","","the sooner the better","ساختار the … the … است."),
     (P,"متن آزاد","درست کنید: It's very more expensive.","It's far more expensive.","It's much more expensive.","","far more","very با تفضیلی نمی‌آید."),
     (P,"متن آزاد","با the … the … بنویسید: we practise more / we get better","The more we practise, the better we get.","","","the more the better","هر دو نیمه the می‌خواهند."),
     (P,"مرتب‌سازی","costs / outweigh / benefits / the / the","The benefits outweigh the costs.","","","outweigh","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","این به‌مراتب بهترین است.","This is by far the best.","","","by far","با فاعل This شروع کن."),
     (P,"گفتار","It's good value for money.","It's good value for money.","","","value for money","جمله را واضح تلفظ کنید.")],

 28:[(R,"چندگزینه‌ای","___ , it's fair.","In my opinion","","In my opinion | In my idea | To my opinion","in my opinion","عبارت ثابت in my opinion است."),
     (R,"تطبیق","viewpoint","دیدگاه","","viewpoint | argument | common ground","viewpoint","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","___ , I disagree. (شخصاً)","Personally","","","personally","قید ابتدای جمله می‌آید."),
     (P,"متن آزاد","درست کنید: In my idea, it's fair.","In my opinion, it's fair.","","","in my opinion","چنین عبارتی در انگلیسی نیست."),
     (P,"متن آزاد","مخالفت نرم بنویسید: that / work","I'm not sure about that.","","","I'm not sure about","با I'm not sure شروع کن."),
     (P,"مرتب‌سازی","ground / found / common / we","We found common ground.","","","common ground","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","منظورت را می‌فهمم، اما…","I see your point, but…","","","I see your point","با عبارت ثابت شروع کن."),
     (P,"گفتار","That's a convincing argument.","That's a convincing argument.","","","convincing","جمله را واضح تلفظ کنید.")],

 29:[(R,"چندگزینه‌ای","___ , it's slow.","On the other hand","","On the other hand | In the other hand | At the other hand","on the one hand","حرف اضافه درست on است."),
     (R,"تطبیق","counter-argument","استدلال متقابل","","counter-argument | evidence for | conclude","counter-argument","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","There's evidence ___ both sides.","for","","","evidence for","حرف اضافه ثابت for است."),
     (P,"متن آزاد","درست کنید: In the other hand, it's slow.","On the other hand, it's slow.","","","on the one hand","حرف اضافه درست on است."),
     (P,"متن آزاد","نتیجه بگیرید: we / choose the second","Therefore, we chose the second.","","","therefore","با قید نتیجه شروع کن."),
     (P,"مرتب‌سازی","all / went / in / all / well / it","All in all, it went well.","","","all in all","با All in all شروع کن."),
     (P,"ترجمه به انگلیسی","روی هم رفته ارزشش را دارد.","Overall, it's worth it.","","","overall","با قید شروع کن."),
     (P,"گفتار","In conclusion, we should wait.","In conclusion, we should wait.","","","in conclusion","جمله را واضح تلفظ کنید.")],

 30:[(R,"چندگزینه‌ای","I ___ working here for three years.","have been","","have been | had been | am","intermediate","حال کامل استمراری با have been ساخته می‌شود."),
     (R,"تطبیق","fluency","روانی","","fluency | accuracy | range","fluency","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","I need to brush ___ my grammar.","up","","","brush up","فعل عبارتی brush up است."),
     (P,"متن آزاد","درست کنید: I had the car repair.","I had the car repaired.","","","complex","بعد از مفعول قسمت سوم فعل می‌آید."),
     (P,"متن آزاد","با wish بنویسید: I / know / the answer","I wish I knew the answer.","","","capable","بعد از wish فعل به گذشته می‌رود."),
     (P,"مرتب‌سازی","practising / keep / day / every","Keep practising every day.","","","keep practising","جمله امری با فعل شروع می‌شود."),
     (P,"ترجمه به انگلیسی","حالا می‌توانم گفت‌وگو را ادامه دهم.","I can hold a conversation now.","","","hold a conversation","با فاعل I شروع کن."),
     (P,"گفتار","Well done — level complete.","Well done — level complete.","","","well done","جمله را واضح تلفظ کنید.")],
}
