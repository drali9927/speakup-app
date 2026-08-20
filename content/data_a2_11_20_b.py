# -*- coding: utf-8 -*-
"""مکالمه، گرامر و تمرین دروس ۱۱ تا ۲۰ سطح A2."""

P, R = "تولیدی", "تشخیصی"

DLG = {
 11:[("A","This bag is more expensive than that one.","این کیف از آن یکی گران‌تر است."),
     ("B","Yes, but the quality is better.","بله، اما کیفیتش بهتر است."),
     ("A","Is the cheaper one bad?","ارزان‌تره بد است؟"),
     ("B","Not bad, just less comfortable.","بد نیست، فقط کمتر راحت است."),
     ("A","Which brand is more popular?","کدام برند محبوب‌تر است؟"),
     ("B","This one. And today there's a discount.","این یکی. و امروز تخفیف هست.")],
 12:[("A","How does your brother drive?","برادرت چطور رانندگی می‌کند؟"),
     ("B","He drives very carefully.","خیلی با دقت رانندگی می‌کند."),
     ("A","Good. My cousin drives badly.","خوب است. پسرعموی من بد رانندگی می‌کند."),
     ("B","Does he go fast?","تند می‌رود؟"),
     ("A","Too fast. He never pays attention.","خیلی تند. هیچ‌وقت توجه نمی‌کند."),
     ("B","Tell him to slow down, politely.","مؤدبانه بهش بگو آهسته‌تر برود.")],
 13:[("A","Is your flat as big as mine?","آپارتمانت به‌اندازه مال من بزرگ است؟"),
     ("B","Almost. Yours is a little wider.","تقریباً. مال تو کمی پهن‌تر است."),
     ("A","And the rent?","و اجاره؟"),
     ("B","Mine isn't as expensive as yours.","مال من به‌گرانی مال تو نیست."),
     ("A","So there's a real difference.","پس تفاوت واقعی هست."),
     ("B","Yes, but both are good enough.","بله، اما هر دو به‌اندازه کافی خوب‌اند.")],
 14:[("A","How long have you lived here?","چه مدت است اینجا زندگی می‌کنی؟"),
     ("B","I've lived here for six years.","شش سال است اینجا زندگی می‌کنم."),
     ("A","And your job?","و کارت؟"),
     ("B","I've worked at the same company since 2020.","از ۲۰۲۰ در همان شرکت کار می‌کنم."),
     ("A","The city has changed a lot.","شهر خیلی تغییر کرده."),
     ("B","It has. But I still like it.","بله. اما هنوز دوستش دارم.")],
 15:[("A","Have you ever been abroad?","تا حالا خارج از کشور رفته‌ای؟"),
     ("B","Yes, I've visited three countries.","بله، سه کشور را دیده‌ام."),
     ("A","Have you ever climbed a mountain?","تا حالا از کوهی بالا رفته‌ای؟"),
     ("B","No, I've never done that.","نه، هرگز این کار را نکرده‌ام."),
     ("A","I've done it once. It was an adventure.","من یک بار کرده‌ام. ماجراجویی بود."),
     ("B","Then I should try it too.","پس من هم باید امتحانش کنم.")],
 16:[("A","Have you finished the report?","گزارش را تمام کرده‌ای؟"),
     ("B","I've already sent it.","قبلاً فرستادمش."),
     ("A","And the tickets?","و بلیت‌ها؟"),
     ("B","I haven't booked them yet.","هنوز رزروشان نکرده‌ام."),
     ("A","We're already late.","همین حالا دیرمان شده."),
     ("B","Don't worry. I've just called them.","نگران نباش. همین الان بهشان زنگ زدم.")],
 17:[("A","When did you move here?","کِی به اینجا نقل مکان کردی؟"),
     ("B","I moved two years ago.","دو سال پیش نقل مکان کردم."),
     ("A","And have you seen Ali lately?","و این اواخر علی را دیده‌ای؟"),
     ("B","I saw him last night, actually.","راستش دیشب دیدمش."),
     ("A","I haven't seen him this week.","من این هفته ندیدمش."),
     ("B","A new cafe has recently opened. Let's go.","کافه‌ای تازه افتتاح شده. بیا برویم.")],
 18:[("A","Where was this made?","این کجا ساخته شده؟"),
     ("B","It was made in a local factory.","در کارخانه‌ای محلی ساخته شد."),
     ("A","Is it hand-made?","دست‌ساز است؟"),
     ("B","Yes, and the material is traditional.","بله، و جنسش سنتی است."),
     ("A","This city is famous for carpets, right?","این شهر به فرش مشهور است، درست است؟"),
     ("B","It is. They've been produced here for centuries.","بله. قرن‌هاست اینجا تولید می‌شوند.")],
 19:[("A","Who's the man who helped you?","مردی که کمکت کرد کیست؟"),
     ("B","He's the neighbour who lives upstairs.","همسایه‌ای است که طبقه بالا زندگی می‌کند."),
     ("A","Is he the one who speaks English?","همانی است که انگلیسی صحبت می‌کند؟"),
     ("B","No, that's a colleague of mine.","نه، او یکی از همکاران من است."),
     ("A","The cafe where we met was his.","کافه‌ای که همدیگر را دیدیم مال او بود."),
     ("B","Really? He's a person who is very kind.","واقعاً؟ آدم خیلی مهربانی است.")],
 20:[("A","Tell me what happened.","بگو چه شد."),
     ("B","We were walking home. Meanwhile, it got dark.","داشتیم پیاده به خانه می‌رفتیم. در همان حال، هوا تاریک شد."),
     ("A","Sorry to interrupt — where exactly?","ببخشید حرفت را قطع می‌کنم — دقیقاً کجا؟"),
     ("B","Near the old bridge. Although it was late, we continued.","نزدیک پل قدیمی. اگرچه دیر بود، ادامه دادیم."),
     ("A","And the ending?","و پایانش؟"),
     ("B","At last we found the road. It was unforgettable.","بالاخره جاده را پیدا کردیم. فراموش‌نشدنی بود.")],
}

GR = {
 11:[("صفت تفضیلی",
      "برای مقایسه دو چیز:\n"
      "**صفت کوتاه** (یک‌بخشی) + er → cheap → cheaper\n"
      "**صفت بلند** (دو بخش یا بیشتر) با more → expensive → more expensive\n"
      "⚠️ هرگز هر دو با هم نمی‌آیند: more cheaper نادرست است.\n"
      "بعد از صفت تفضیلی، than می‌آید: cheaper **than** this one.\n"
      "املا: big → bigger · easy → easier · nice → nicer",
      "This bag is cheaper than that one.\nThis one is more expensive.\nThe road is wider here.\nMy flat is bigger than yours.",
      "کدام درست است؟", "It's more expensive than mine.",
      "It's more expensive than mine. | It's more expensiver than mine. | It's expensiver than mine.",
      "It's more expensiver than mine. ← more و er با هم نمی‌آیند.\n"
      "It's expensiver than mine. ← صفت بلند er نمی‌گیرد و با more می‌آید."),
     ("بی‌قاعده‌ها و صفت عالی",
      "سه صفت بی‌قاعده که باید حفظ شوند:\n"
      "good → **better** → the best\n"
      "bad → **worse** → the worst\n"
      "far → further → the furthest\n"
      "**صفت عالی** یعنی «ترین» و همیشه the می‌گیرد:\n"
      "the cheapest · the most popular · the best",
      "The quality is better.\nThe weather is worse today.\nThis is the best price.\nIt's the most popular model.",
      "کدام درست است؟", "The weather is worse today.",
      "The weather is worse today. | The weather is badder today.",
      "The weather is badder today. ← bad بی‌قاعده است و تفضیلی‌اش worse می‌شود.")],

 12:[("ساختن قید از صفت",
      "قید می‌گوید کار **چگونه** انجام شد:\n"
      "صفت + ly → careful → carefully · quick → quickly\n"
      "املا: easy → easily (y به ily) · terrible → terribly\n"
      "⚠️ استثناها: good → **well** · fast → fast · hard → hard\n"
      "صفت به اسم می‌چسبد (a careful driver) و قید به فعل (drives carefully).",
      "He drives carefully.\nShe answered quickly.\nShe sings very well.\nHe works hard.\nPlease write clearly.",
      "کدام درست است؟", "She sings well.",
      "She sings well. | She sings good.",
      "She sings good. ← قید good نیست؛ صورت قیدی‌اش well است."),
     ("جای قید در جمله",
      "**قید حالت** معمولاً بعد از فعل یا بعد از مفعول می‌آید:\n"
      "He speaks slowly. · He opened the door quietly.\n"
      "نادرست: He speaks slowly English.\n"
      "درست: He speaks English slowly.\n"
      "**قید بسامد** (always، often، never) پیش از فعل اصلی و بعد از to be:\n"
      "He never pays attention. · He is never late.",
      "He opened the door quietly.\nShe speaks English slowly.\nHe never pays attention.\nThe car stopped suddenly.",
      "کدام درست است؟", "He speaks English slowly.",
      "He speaks English slowly. | He speaks slowly English.",
      "He speaks slowly English. ← قید حالت بین فعل و مفعولش نمی‌آید.")],

 13:[("as … as",
      "برای گفتن دو چیز **برابرند**:\n"
      "as + صفت ساده + as → It's as big as mine.\n"
      "⚠️ صفت وسط، تفضیلی نمی‌شود: as bigger as نادرست است.\n"
      "منفی یعنی کمتر: It **isn't as** expensive **as** yours.\n"
      "= Yours is more expensive.",
      "It's as big as mine.\nMine isn't as expensive as yours.\nHe's as tall as his brother.\nThis isn't as heavy as that.",
      "کدام درست است؟", "It's as big as mine.",
      "It's as big as mine. | It's as bigger as mine.",
      "It's as bigger as mine. ← بین دو as صفت ساده می‌آید نه تفضیلی."),
     ("both، neither و اندازه‌ها",
      "**both** = هر دو (فعل جمع): Both options are good.\n"
      "**neither** = هیچ‌کدام (فعل مفرد): Neither one is cheap.\n"
      "برای برابری و نسبت:\n"
      "the same as → It's the same as mine.\n"
      "different from → It's different from mine.\n"
      "twice as … as → It's twice as long as that.",
      "Both options are good.\nNeither one is cheap.\nIt's the same as mine.\nIt's twice as long.",
      "کدام درست است؟", "It's the same as mine.",
      "It's the same as mine. | It's the same with mine.",
      "It's the same with mine. ← بعد از the same حرف اضافه as می‌آید.")],

 14:[("حال کامل — for و since",
      "برای کاری که در گذشته شروع شده و **تا حالا ادامه دارد**:\n"
      "have / has + قسمت سوم فعل\n"
      "I **have lived** here for six years.\n"
      "**for** + مدت زمان: for six years, for a month, for an hour\n"
      "**since** + نقطه شروع: since 2020, since May, since I was a child\n"
      "⚠️ فارسی این را حال می‌گوید («شش سال است اینجا زندگی می‌کنم») و\n"
      "همین باعث می‌شود فارسی‌زبان‌ها اشتباهاً حال ساده به کار ببرند.",
      "I've lived here for six years.\nShe's worked here since 2020.\nWe've waited for an hour.\nI've known her since 2015.",
      "کدام درست است؟", "I've lived here for six years.",
      "I've lived here for six years. | I live here for six years. | I've lived here since six years.",
      "I live here for six years. ← برای مدتی که تا حالا ادامه دارد حال کامل می‌آید.\n"
      "I've lived here since six years. ← since نقطه شروع می‌خواهد نه مدت؛ اینجا for درست است."),
     ("How long و ساخت منفی",
      "پرسش: **How long have you** lived here?\n"
      "پاسخ کوتاه: For six years. · Since 2020.\n"
      "منفی: I **haven't** seen him for a week.\n"
      "کوتاه‌نویسی: I have → I've · she has → she's · have not → haven't\n"
      "⚠️ she's هم مخفف she is است و هم she has؛ از فعل بعدش تشخیص بده:\n"
      "She's tired (is) · She's worked here (has).",
      "How long have you lived here?\nFor six years.\nI haven't seen him for a week.\nShe's worked here since May.",
      "کدام درست است؟", "How long have you worked here?",
      "How long have you worked here? | How long you have worked here?",
      "How long you have worked here? ← در پرسش، فعل کمکی have پیش از فاعل می‌آید.")],

 15:[("حال کامل تجربی — ever و never",
      "برای پرسیدن از **تجربه در کل زندگی**، بدون اهمیت زمان:\n"
      "**Have you ever** been to Iran?\n"
      "پاسخ: Yes, I have. / No, I've **never** been.\n"
      "⚠️ never خودش منفی است: I haven't never نادرست است.\n"
      "برای شمردن: once, twice, three times\n"
      "I've been there **twice**.",
      "Have you ever been abroad?\nI've never tried it.\nI've visited many cities.\nWe've met twice.",
      "کدام درست است؟", "I've never been there.",
      "I've never been there. | I haven't never been there.",
      "I haven't never been there. ← never خودش منفی است و دوباره منفی نمی‌شود."),
     ("been یا gone",
      "دو معنی کاملاً متفاوت با یک فعل:\n"
      "**has been to** = رفته و **برگشته**: She's been to Paris.\n"
      "**has gone to** = رفته و **هنوز آنجاست**: She's gone to Paris.\n"
      "برای پرسش از تجربه همیشه been می‌آید:\n"
      "Have you ever **been** to Iran?",
      "Have you ever been to Iran?\nShe's been to Paris twice.\nHe's gone to the shop. (هنوز آنجاست)\nI've been there once.",
      "کدام برای تجربه درست است؟", "Have you ever been to Iran?",
      "Have you ever been to Iran? | Have you ever gone to Iran?",
      "Have you ever gone to Iran? ← gone یعنی هنوز آنجاست و برای پرسش از تجربه به کار نمی‌رود.")],

 16:[("already، yet و just",
      "سه قید که با حال کامل کار می‌کنند:\n"
      "**already** = زودتر از انتظار، در جمله مثبت، وسط جمله:\n"
      "I've **already** finished.\n"
      "**yet** = هنوز، در منفی و پرسش، **آخر** جمله:\n"
      "I haven't started **yet**. · Have you finished **yet**?\n"
      "**just** = همین چند لحظه پیش، وسط جمله:\n"
      "She's **just** arrived.",
      "I've already sent it.\nI haven't booked them yet.\nHave you finished yet?\nShe's just arrived.",
      "کدام درست است؟", "I haven't finished yet.",
      "I haven't finished yet. | I haven't finished already.",
      "I haven't finished already. ← already در جمله مثبت می‌آید؛ در منفی yet درست است."),
     ("جای درست این قیدها",
      "قاعده ساده جایگاه:\n"
      "already و just **بین have و فعل** می‌نشینند:\n"
      "I have **already** paid. · He has **just** left.\n"
      "yet **در انتهای** جمله می‌آید:\n"
      "I haven't paid **yet**.\n"
      "نادرست: I have yet not paid.",
      "I have already paid.\nHe has just left.\nWe haven't decided yet.\nHas she called yet?",
      "کدام درست است؟", "He has just left.",
      "He has just left. | He has left just.",
      "He has left just. ← just بین فعل کمکی و فعل اصلی می‌آید.")],

 17:[("گذشته ساده یا حال کامل؟",
      "این تفاوت، پرتکرارترین اشتباه A2 است:\n"
      "**گذشته ساده** وقتی زمانِ تمام‌شده گفته می‌شود:\n"
      "I moved here two years ago. · I saw him last night.\n"
      "**حال کامل** وقتی زمان گفته نمی‌شود یا هنوز تمام نشده:\n"
      "I've moved twice. · I've seen him this week.\n"
      "⚠️ ago همیشه با گذشته ساده می‌آید: I've moved two years ago نادرست است.",
      "I moved here two years ago.\nI've moved twice.\nI saw him last night.\nI haven't seen him this week.",
      "کدام درست است؟", "I saw him yesterday.",
      "I saw him yesterday. | I've seen him yesterday.",
      "I've seen him yesterday. ← وقتی زمان تمام‌شده (yesterday) گفته می‌شود گذشته ساده می‌آید."),
     ("نشانه‌های زمانی هرکدام",
      "این فهرست را به‌عنوان نشانه به خاطر بسپار:\n"
      "**گذشته ساده**: yesterday · last night · in 2019 · two years ago · when I was young\n"
      "**حال کامل**: today · this week · this year · recently · lately · ever · never · so far\n"
      "دلیلش ساده است: بازه‌های دسته دوم هنوز تمام نشده‌اند.",
      "I finished it yesterday.\nI've finished it today.\nWe met in 2019.\nWe've met twice this year.",
      "کدام با «this week» درست است؟", "I've seen him this week.",
      "I've seen him this week. | I saw him this week ago.",
      "I saw him this week ago. ← ago با بازه‌ای که هنوز ادامه دارد نمی‌آید.")],

 18:[("مجهول ساده",
      "وقتی **کننده کار مهم نیست** یا معلوم نیست:\n"
      "be + قسمت سوم فعل\n"
      "حال: This room **is used** for meetings.\n"
      "گذشته: The bridge **was built** in 1990.\n"
      "جمع: The tickets **were sold** quickly.\n"
      "⚠️ فعل be حذف نمی‌شود: The bridge built in 1990 معنی دیگری دارد.",
      "It was made in Iran.\nThe bridge was built in 1990.\nThis room is used for meetings.\nThe tickets were sold quickly.",
      "کدام درست است؟", "It was made in Iran.",
      "It was made in Iran. | It was make in Iran. | It made in Iran.",
      "It was make in Iran. ← بعد از was قسمت سوم فعل می‌آید.\n"
      "It made in Iran. ← فعل be در مجهول حذف نمی‌شود."),
     ("by و کاربرد مجهول",
      "اگر کننده کار مهم باشد با **by** می‌آید:\n"
      "It was designed **by** a young architect.\n"
      "اگر مهم نباشد، اصلاً نمی‌آید — و همین دلیل اصلی استفاده از مجهول است.\n"
      "مجهول در توضیح فرایند، تولید و تاریخچه زیاد به کار می‌رود:\n"
      "Rice **is grown** here. · This **is produced** locally.",
      "It was designed by a young architect.\nRice is grown here.\nThis is produced locally.\nThe car was repaired yesterday.",
      "کدام درست است؟", "It was written by a poet.",
      "It was written by a poet. | It was written from a poet.",
      "It was written from a poet. ← کننده کار در مجهول با by می‌آید نه from.")],

 19:[("who، which و that",
      "این کلمه‌ها دو جمله را یکی می‌کنند و توضیح می‌دهند **کدام** یکی:\n"
      "**who** برای افراد: the man **who** helped me\n"
      "**which** برای اشیا: the book **which** I bought\n"
      "**that** برای هر دو، و در گفتار رایج‌تر است.\n"
      "⚠️ فاعل تکرار نمی‌شود:\n"
      "نادرست: the man who he helped me",
      "The man who helped me was kind.\nThe book which I bought is interesting.\nThe house that we saw is old.\nA tool which is very useful.",
      "کدام درست است؟", "The man who helped me.",
      "The man who helped me. | The man who he helped me.",
      "The man who he helped me. ← who خودش فاعل است و he تکرار می‌شود."),
     ("where و حذف ضمیر",
      "**where** برای مکان: the cafe **where** we met\n"
      "**whose** برای مالکیت: the man **whose** car is red\n"
      "نکته کاربردی: اگر بعد از who/which/that **فاعل تازه‌ای** بیاید،\n"
      "می‌شود آن را حذف کرد:\n"
      "the book (which) **I** bought ✅ حذف می‌شود\n"
      "the man who helped me ❌ حذف نمی‌شود (فاعلی بعدش نیست)",
      "The cafe where we met was quiet.\nThe book I bought is interesting.\nThe man whose car is red.\nThe street where I grew up.",
      "کدام برای مکان درست است؟", "The cafe where we met.",
      "The cafe where we met. | The cafe which we met.",
      "The cafe which we met. ← برای مکان where می‌آید.")],

 20:[("وصل کردن جمله‌ها",
      "برای اینکه روایت فهرست خشک نشود:\n"
      "**although** = اگرچه، پیش از جمله کامل: Although it was late, we continued.\n"
      "**however** = اما، اول جمله تازه با ویرگول: However, we arrived late.\n"
      "**because** + جمله · **because of** + اسم:\n"
      "because it rained · because of the rain\n"
      "**so** = بنابراین: It rained, so we stayed.",
      "Although it was late, we continued.\nHowever, we arrived late.\nWe stayed because of the rain.\nIt rained, so we stayed.",
      "کدام درست است؟", "because of the rain",
      "because of the rain | because the rain",
      "because the rain ← پیش از اسم، because of می‌آید."),
     ("ترتیب روایت",
      "چهار قید که ماجرا را مرتب می‌کنند:\n"
      "**first** → شروع · **then / after that** → ادامه\n"
      "**meanwhile** → همزمان در جای دیگر\n"
      "**at last / finally** → پایان\n"
      "و برای تصحیح تصور شنونده: **in fact** (در واقع).\n"
      "این‌ها اول جمله می‌آیند و بعدشان ویرگول می‌گذاریم.",
      "First, we walked to the station.\nMeanwhile, it got dark.\nAt last, we found the road.\nIn fact, it was easy.",
      "کدام پایان ماجرا را نشان می‌دهد؟", "At last, we found the road.",
      "At last, we found the road. | Meanwhile, we found the road.",
      "Meanwhile, we found the road. ← meanwhile یعنی «در همان حال» و پایان را نشان نمی‌دهد.")],
}

EX = {
 11:[(R,"چندگزینه‌ای","This bag is ___ than that one.","more expensive","","more expensive | expensiver | more expensiver","expensive","صفت بلند با more می‌آید."),
     (R,"تطبیق","expensive","گران","","expensive | discount | quality","expensive","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","This street is ___ than that one. (quiet)","quieter","","","quiet","صفت کوتاه er می‌گیرد."),
     (P,"متن آزاد","درست کنید: The weather is badder today.","The weather is worse today.","","","worse","bad بی‌قاعده است."),
     (P,"متن آزاد","مقایسه کنید: this bag / cheap / that bag","This bag is cheaper than that bag.","","","cheap","بعد از صفت تفضیلی than می‌آید."),
     (P,"مرتب‌سازی","better / the / is / quality","The quality is better.","","","quality","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","این مدل محبوب‌تر است.","This model is more popular.","","","popular","صفت بلند با more می‌آید."),
     (P,"گفتار","The quality is better, but it's more expensive.","The quality is better, but it's more expensive.","","","price","جمله را واضح تلفظ کن.")],

 12:[(R,"چندگزینه‌ای","He drives very ___ .","carefully","","carefully | careful | carefuly","carefully","قید با ly ساخته می‌شود."),
     (R,"تطبیق","carefully","با دقت","","carefully | quickly | slowly","carefully","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","She sings very ___ . (good)","well","","","well","صورت قیدی good همان well است."),
     (P,"متن آزاد","درست کنید: He speaks slowly English.","He speaks English slowly.","","","slowly","قید حالت بعد از مفعول می‌آید."),
     (P,"متن آزاد","قید بسازید: easy →","easily","","","easily","y به ily تبدیل می‌شود."),
     (P,"مرتب‌سازی","suddenly / the / stopped / car","The car stopped suddenly.","","","suddenly","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","لطفاً آهسته صحبت کن.","Please speak slowly.","","","slowly","جمله امری با فعل شروع می‌شود."),
     (P,"گفتار","He never pays attention.","He never pays attention.","","","attention","جمله را واضح تلفظ کن.")],

 13:[(R,"چندگزینه‌ای","It's as ___ as mine.","big","","big | bigger | biggest","same","بین دو as صفت ساده می‌آید."),
     (R,"تطبیق","similar","شبیه","","similar | different | equal","similar","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","Mine isn't ___ expensive as yours.","as","","","as","ساختار as … as است."),
     (P,"متن آزاد","درست کنید: It's the same with mine.","It's the same as mine.","","","same","بعد از the same حرف اضافه as می‌آید."),
     (P,"متن آزاد","با as … as بنویسید: big / my flat / your flat","My flat is as big as your flat.","","","wide","صفت ساده بین دو as می‌نشیند."),
     (P,"مرتب‌سازی","good / options / are / both","Both options are good.","","","both","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","هیچ‌کدام ارزان نیست.","Neither one is cheap.","","","neither","بعد از neither فعل مفرد می‌آید."),
     (P,"گفتار","This isn't as heavy as that one.","This isn't as heavy as that one.","","","heavy","جمله را واضح تلفظ کن.")],

 14:[(R,"چندگزینه‌ای","I ___ here for six years.","have lived","","have lived | live | am living","live","برای مدتی که تا حالا ادامه دارد حال کامل می‌آید."),
     (R,"تطبیق","improve","بهتر شدن","","improve | keep | grow","improve","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","She's worked here ___ 2020.","since","","","since","since نقطه شروع می‌خواهد."),
     (P,"متن آزاد","درست کنید: I've lived here since six years.","I've lived here for six years.","","","for","برای مدت زمان for می‌آید."),
     (P,"متن آزاد","سوال بسازید: I've lived here for six years.","How long have you lived here?","","","period","با How long شروع کن."),
     (P,"مرتب‌سازی","changed / have / things","Things have changed.","","","change","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","انگلیسی‌ام بهتر شده.","My English has improved.","","","improve","حال کامل با has + قسمت سوم ساخته می‌شود."),
     (P,"گفتار","I've known her since 2015.","I've known her since 2015.","","","since","جمله را واضح تلفظ کن.")],

 15:[(R,"چندگزینه‌ای","Have you ___ been abroad?","ever","","ever | never | already","ever","برای پرسش از تجربه ever می‌آید."),
     (R,"تطبیق","abroad","خارج از کشور","","abroad | adventure | culture","abroad","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","I've ___ tried it. (هرگز)","never","","","never","never خودش منفی است."),
     (P,"متن آزاد","درست کنید: I haven't never been there.","I've never been there.","I have never been there.","","never","never دوباره منفی نمی‌شود."),
     (P,"متن آزاد","سوال بسازید با ever: you / climb / a mountain","Have you ever climbed a mountain?","","","climb","با Have you ever شروع کن."),
     (P,"مرتب‌سازی","cities / visited / many / I've","I've visited many cities.","","","visit","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","تا حالا به ایران رفته‌ای؟","Have you ever been to Iran?","","","been","برای تجربه been می‌آید نه gone."),
     (P,"گفتار","I've never done that before.","I've never done that before.","","","experience","جمله را واضح تلفظ کن.")],

 16:[(R,"چندگزینه‌ای","I haven't finished ___ .","yet","","yet | already | just","yet","yet در منفی و آخر جمله می‌آید."),
     (R,"تطبیق","yet","هنوز","","yet | just | deadline","yet","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","She's ___ arrived. (همین الان)","just","","","just","«همین الان» یعنی just."),
     (P,"متن آزاد","درست کنید: He has left just.","He has just left.","","","just","just بین have و فعل می‌آید."),
     (P,"متن آزاد","منفی کنید با yet: I've booked the tickets.","I haven't booked the tickets yet.","","","book","yet به انتهای جمله منفی می‌رود."),
     (P,"مرتب‌سازی","already / it / sent / I've","I've already sent it.","","","send","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","قبلاً تمامش کرده‌ام.","I've already finished.","I have already finished.","","finish","already بین have و فعل می‌آید."),
     (P,"گفتار","Everything is ready.","Everything is ready.","","","ready","جمله را واضح تلفظ کن.")],

 17:[(R,"چندگزینه‌ای","I ___ him yesterday.","saw","","saw | have seen | had seen","before","با زمان تمام‌شده گذشته ساده می‌آید."),
     (R,"تطبیق","career","حرفه، شغل","","career | company | project","career","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","I moved here two years ___ .","ago","","","ago","«پیش» یعنی ago."),
     (P,"متن آزاد","درست کنید: I've seen him yesterday.","I saw him yesterday.","","","before","با yesterday گذشته ساده می‌آید."),
     (P,"متن آزاد","با this week بنویسید: I / not / see / him","I haven't seen him this week.","","","lately","بازه‌ای که تمام نشده حال کامل می‌گیرد."),
     (P,"مرتب‌سازی","recently / we've / moved","We've recently moved.","","","move","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","دو سال پیش نقل مکان کردم.","I moved two years ago.","","","ago","ago با گذشته ساده می‌آید."),
     (P,"گفتار","A new cafe has recently opened.","A new cafe has recently opened.","","","recently","جمله را واضح تلفظ کن.")],

 18:[(R,"چندگزینه‌ای","It ___ in Iran.","was made","","was made | was make | made","made","بعد از was قسمت سوم فعل می‌آید."),
     (R,"تطبیق","factory","کارخانه","","factory | material | machine","factory","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","The bridge ___ built in 1990.","was","","","built","برای فاعل مفرد در گذشته was می‌آید."),
     (P,"متن آزاد","درست کنید: It was written from a poet.","It was written by a poet.","","","written","کننده کار با by می‌آید."),
     (P,"متن آزاد","مجهول کنید: They sold the tickets quickly.","The tickets were sold quickly.","","","sold","مفعول به ابتدای جمله می‌رود."),
     (P,"مرتب‌سازی","here / is / rice / grown","Rice is grown here.","","","grown","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","این محلی تولید می‌شود.","This is produced locally.","","","produced","مجهول حال با is + قسمت سوم ساخته می‌شود."),
     (P,"گفتار","It's completely hand-made.","It's completely hand-made.","","","hand-made","جمله را واضح تلفظ کن.")],

 19:[(R,"چندگزینه‌ای","The man ___ helped me was kind.","who","","who | which | where","who","برای افراد who می‌آید."),
     (R,"تطبیق","colleague","همکار","","colleague | owner | customer","colleague","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","The book ___ I bought is interesting.","which","that","","which","برای اشیا which یا that می‌آید."),
     (P,"متن آزاد","درست کنید: The man who he helped me.","The man who helped me.","","","who","who خودش فاعل است."),
     (P,"متن آزاد","با where بنویسید: the cafe / we met","The cafe where we met.","","","where","برای مکان where می‌آید."),
     (P,"مرتب‌سازی","kind / who / very / a / is / person","A person who is very kind.","","","kind","با اسم شروع کن."),
     (P,"ترجمه به انگلیسی","همسایه‌ای که طبقه بالا زندگی می‌کند.","The neighbour who lives upstairs.","","","neighbour","برای افراد who می‌آید."),
     (P,"گفتار","A tool which is really useful.","A tool which is really useful.","","","tool","جمله را واضح تلفظ کن.")],

 20:[(R,"چندگزینه‌ای","We stayed ___ the rain.","because of","","because of | because | although","because of","پیش از اسم because of می‌آید."),
     (R,"تطبیق","scene","صحنه","","scene | character | silence","scene","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","___ it was late, we continued.","Although","","","although","«اگرچه» یعنی although."),
     (P,"متن آزاد","درست کنید: We stayed because the rain.","We stayed because of the rain.","","","because of","پیش از اسم because of می‌آید."),
     (P,"متن آزاد","پایان ماجرا را بنویسید با At last: we / find / the road","At last, we found the road.","","","at last","بعد از At last ویرگول می‌آید."),
     (P,"مرتب‌سازی","dark / it / meanwhile / got","Meanwhile, it got dark.","","","meanwhile","قید ترتیب اول جمله می‌آید."),
     (P,"ترجمه به انگلیسی","سفر فراموش‌نشدنی‌ای بود.","It was an unforgettable trip.","","","unforgettable","با فاعل It شروع کن."),
     (P,"گفتار","Sorry to interrupt, but I have a question.","Sorry to interrupt, but I have a question.","","","interrupt","جمله را واضح تلفظ کن.")],
}
