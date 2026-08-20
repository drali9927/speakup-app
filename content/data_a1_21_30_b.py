# -*- coding: utf-8 -*-
"""مکالمه، گرامر و تمرین دروس ۲۱ تا ۳۰ سطح A1."""

P, R = "تولیدی", "تشخیصی"

# (گوینده, انگلیسی, فارسی)
DLG = {
 21: [("A","Excuse me, where is the station?","ببخشید، ایستگاه کجاست؟"),
      ("B","Go straight on and turn left.","مستقیم برو و بپیچ چپ."),
      ("A","Is it far from here?","از اینجا دور است؟"),
      ("B","No, it's very near. Five minutes.","نه، خیلی نزدیک است. پنج دقیقه."),
      ("A","Thank you very much!","خیلی ممنون!"),
      ("B","You're welcome. Cross the bridge first.","خواهش می‌کنم. اول از پل رد شو.")],
 22: [("A","What are you doing?","داری چه کار می‌کنی؟"),
      ("B","I'm working. And you?","دارم کار می‌کنم. تو چطور؟"),
      ("A","I'm watching a film.","دارم فیلم می‌بینم."),
      ("B","Is Sara sleeping?","سارا خوابیده؟"),
      ("A","No, she isn't. She's studying.","نه، دارد درس می‌خواند."),
      ("B","She's always busy!","همیشه مشغول است!")],
 23: [("A","How do you go to work?","چطور سر کار می‌روی؟"),
      ("B","I usually walk, but today I'm taking the bus.","معمولاً پیاده می‌روم، اما امروز اتوبوس سوار می‌شوم."),
      ("A","Why?","چرا؟"),
      ("B","Because it's raining now.","چون الان باران می‌بارد."),
      ("A","Do you always start at eight?","همیشه ساعت هشت شروع می‌کنی؟"),
      ("B","Yes, and I never finish before five.","بله، و هرگز قبل از پنج تمام نمی‌کنم.")],
 24: [("A","Where were you yesterday?","دیروز کجا بودی؟"),
      ("B","I was at home. I was very tired.","خانه بودم. خیلی خسته بودم."),
      ("A","Were you ill?","مریض بودی؟"),
      ("B","No, I wasn't. Just tired.","نه، فقط خسته."),
      ("A","We were at the park. It was beautiful.","ما پارک بودیم. زیبا بود."),
      ("B","Was the weather good?","هوا خوب بود؟")],
 25: [("A","What did you do at the weekend?","آخر هفته چه کار کردی؟"),
      ("B","We played football and watched a film.","فوتبال بازی کردیم و فیلم دیدیم."),
      ("A","Did you enjoy it?","لذت بردی؟"),
      ("B","Yes! And I called my friend.","بله! و به دوستم زنگ زدم."),
      ("A","I stayed at home and studied.","من خانه ماندم و درس خواندم."),
      ("B","You worked very hard.","خیلی سخت کار کردی.")],
 26: [("A","Where did you go last summer?","تابستان گذشته کجا رفتی؟"),
      ("B","I went to Tehran with my family.","با خانواده‌ام به تهران رفتم."),
      ("A","What did you see there?","آنجا چه دیدی؟"),
      ("B","We saw the city and ate good food.","شهر را دیدیم و غذای خوب خوردیم."),
      ("A","Did you take photos?","عکس گرفتی؟"),
      ("B","Yes, I took many photos.","بله، عکس‌های زیادی گرفتم.")],
 27: [("A","Did you like the holiday?","تعطیلات را دوست داشتی؟"),
      ("B","Yes, I did. It was perfect.","بله. عالی بود."),
      ("A","Where did you stay?","کجا ماندی؟"),
      ("B","In a small hotel near the beach.","در هتل کوچکی نزدیک ساحل."),
      ("A","Was it expensive?","گران بود؟"),
      ("B","No, it wasn't. It was cheap and clean.","نه. ارزان و تمیز بود.")],
 28: [("A","What are you going to do next week?","هفته آینده قرار است چه کار کنی؟"),
      ("B","I'm going to start an English course.","قرار است یک دوره انگلیسی شروع کنم."),
      ("A","That's great! Where?","عالی است! کجا؟"),
      ("B","At the university near my house.","در دانشگاه نزدیک خانه‌ام."),
      ("A","Are you going to work too?","قرار است کار هم بکنی؟"),
      ("B","Yes, and I'm going to save money.","بله، و قرار است پول پس‌انداز کنم.")],
 29: [("A","Which city is bigger, Tehran or Yazd?","کدام شهر بزرگ‌تر است، تهران یا یزد؟"),
      ("B","Tehran is much bigger.","تهران خیلی بزرگ‌تر است."),
      ("A","Is your brother older than you?","برادرت از تو مسن‌تر است؟"),
      ("B","Yes, he's two years older.","بله، دو سال مسن‌تر است."),
      ("A","This tea is better than that one.","این چای از آن یکی بهتر است."),
      ("B","I agree. And it's cheaper too.","موافقم. و ارزان‌تر هم هست.")],
 30: [("A","How was your week?","هفته‌ات چطور بود؟"),
      ("B","It was the best week of the year!","بهترین هفته سال بود!"),
      ("A","Why? What happened?","چرا؟ چه شد؟"),
      ("B","I finished level A1.","سطح A1 را تمام کردم."),
      ("A","Well done! I'm proud of you.","آفرین! به تو افتخار می‌کنم."),
      ("B","Thank you. Now I'm ready for A2.","ممنون. حالا برای A2 آماده‌ام.")],
}

# (عنوان, توضیح, مثال‌ها)
GR = {
 21: [("جملات امری",
       "برای دستور دادن، راهنمایی کردن یا خواهش، فعل ساده اول جمله می‌آید و\n"
       "فاعل حذف می‌شود.\n"
       "Turn left. · Go straight on. · Cross the road.\n"
       "برای منفی، Don't پیش از فعل می‌آید: Don't turn right.\n"
       "افزودن please جمله را مؤدبانه می‌کند: Turn left, please.",
       "Turn left, please.\nGo straight on.\nCross the bridge.\nDon't turn right here.\nTake the bus number five."),
      ("پرسیدن آدرس",
       "رایج‌ترین ساختارها برای پرسیدن مسیر:\n"
       "Where is the station? · How can I get to the airport?\n"
       "Is it far from here? · Is it near?\n"
       "در جواب معمولاً جمله امری می‌آید، چون کوتاه و روشن است.\n"
       "شروع کردن با Excuse me در فرهنگ انگلیسی تقریباً همیشه لازم است.",
       "Excuse me, where is the station?\nIs it far from here?\nHow can I get to the airport?\nGo straight on and turn left.")],

 22: [("حال استمراری",
       "برای کاری که همین حالا در جریان است.\n"
       "ساختار: فعل to be + فعل اصلی با ing\n"
       "I am working. · She is reading. · They are waiting.\n"
       "نکته‌ای که فارسی‌زبان‌ها جا می‌اندازند: فعل to be حتماً باید بیاید.\n"
       "نادرست: She reading now — درست: She is reading now",
       "I am working now.\nShe is reading a book.\nThey are waiting outside.\nHe is talking on the phone.\nWe are watching a film."),
      ("منفی و سوالی",
       "منفی: not بعد از فعل to be می‌آید.\n"
       "I am not working. · She isn't sleeping. · They aren't waiting.\n"
       "سوالی: فعل to be به ابتدای جمله می‌رود.\n"
       "Are you working? — Yes, I am. / No, I'm not.\n"
       "Is she sleeping? — No, she isn't.\n"
       "در جواب کوتاه، فعل اصلی تکرار نمی‌شود.",
       "I'm not working today.\nShe isn't sleeping.\nAre you studying? — Yes, I am.\nIs he eating? — No, he isn't.\nWhat are you doing?")],

 23: [("حال ساده در برابر حال استمراری",
       "حال ساده برای کارهای همیشگی و تکراری:\n"
       "I usually walk to work. · She always drinks tea.\n"
       "حال استمراری برای همین حالا:\n"
       "Today I'm taking the bus. · She is drinking coffee now.\n"
       "یک نشانه ساده: هر وقت usually، always، every day دیدید حال ساده،\n"
       "و هر وقت now، today، at the moment دیدید حال استمراری.",
       "I usually walk, but today I'm taking the bus.\nShe always drinks tea, but now she's drinking coffee.\nHe works every day.\nHe is working now."),
      ("قیدهای تکرار",
       "این قیدها می‌گویند کاری چند وقت یک بار انجام می‌شود:\n"
       "always (۱۰۰٪) · usually · often · sometimes · never (۰٪)\n"
       "جایشان **پیش از فعل اصلی** است، اما **بعد از** فعل to be:\n"
       "I always walk. (پیش از فعل اصلی)\n"
       "She is always busy. (بعد از to be)\n"
       "با never جمله دیگر منفی نمی‌شود: I never drink coffee (نه don't).",
       "I always walk to work.\nShe usually finishes at five.\nWe often eat together.\nHe sometimes works late.\nI never drink coffee.\nShe is always busy.")],

 24: [("was و were",
       "گذشته فعل to be دو شکل دارد:\n"
       "was با I، he، she، it\n"
       "were با you، we، they\n"
       "I was at home. · They were happy.\n"
       "این ساده‌ترین راه حرف زدن درباره گذشته است و پیش از یادگیری\n"
       "گذشته ساده افعال دیگر می‌آید.",
       "I was at home yesterday.\nHe was at school.\nShe was very tired.\nWe were at the park.\nThey were happy."),
      ("منفی و سوالی",
       "منفی: wasn't و weren't\n"
       "I wasn't ill. · They weren't ready.\n"
       "سوالی: was یا were به ابتدای جمله می‌رود.\n"
       "Were you free yesterday? — Yes, I was. / No, I wasn't.\n"
       "Was the weather good? — No, it wasn't.\n"
       "برای پرسیدن جا: Where were you yesterday?",
       "I wasn't at home.\nThey weren't ready.\nWere you free? — Yes, I was.\nWas she happy? — No, she wasn't.\nWhere were you yesterday?")],

 25: [("گذشته ساده: افعال باقاعده",
       "برای کاری که در گذشته تمام شده. به فعل ed اضافه می‌شود و این شکل\n"
       "برای **همه** فاعل‌ها یکی است — حتی با he و she.\n"
       "I played. · She played. · They played.\n"
       "قاعده املا:\n"
       "بیشتر فعل‌ها: ed → work → worked\n"
       "فعل مختوم به e: فقط d → like → liked\n"
       "فعل مختوم به بی‌صدا + y: y به i تبدیل می‌شود → study → studied",
       "work → worked\nplay → played\nlike → liked\nstudy → studied\nstop → stopped\nvisit → visited"),
      ("کلمات زمان گذشته",
       "این کلمه‌ها نشان می‌دهند جمله درباره گذشته است:\n"
       "yesterday · last night · last week · two days ago\n"
       "جایشان معمولاً آخر جمله است:\n"
       "I worked yesterday. · We played football last week.\n"
       "ago همیشه **بعد از** مدت زمان می‌آید: two days ago (نه ago two days)",
       "I worked yesterday.\nWe played football last week.\nShe called me last night.\nThey arrived two days ago.")],

 26: [("گذشته ساده: افعال بی‌قاعده",
       "بعضی فعل‌های پرکاربرد ed نمی‌گیرند و شکل گذشته‌شان کاملاً فرق دارد.\n"
       "این‌ها را باید حفظ کرد — راه دیگری ندارد، اما تعدادشان محدود است.\n"
       "go → went · see → saw · eat → ate · take → took\n"
       "make → made · buy → bought · come → came · get → got\n"
       "خبر خوب: این شکل هم برای همه فاعل‌ها یکی است.",
       "go → went\nsee → saw\neat → ate\ndrink → drank\ntake → took\nmake → made\nbuy → bought\ncome → came\nwrite → wrote\nsleep → slept"),
      ("منفی گذشته",
       "برای منفی کردن گذشته، didn't می‌آید و فعل به **شکل ساده** برمی‌گردد.\n"
       "I went → I didn't go (نه I didn't went)\n"
       "She saw → She didn't see\n"
       "این مهم‌ترین اشتباه در این درس است: بعد از didn't هرگز شکل گذشته نمی‌آید،\n"
       "چون خودِ didn't گذشته بودن را نشان داده است.",
       "I didn't go to Tehran.\nShe didn't see the sea.\nWe didn't eat breakfast.\nHe didn't take the bus.\nThey didn't come.")],

 27: [("سوالی گذشته با did",
       "برای سوال در گذشته، did اول جمله می‌آید و فعل **ساده** می‌شود.\n"
       "Did you like the holiday? (نه Did you liked)\n"
       "Did she go to the beach?\n"
       "Where did you stay? · What did you do?\n"
       "همان قاعده منفی: چون did گذشته بودن را نشان داده، فعل ساده می‌ماند.",
       "Did you like it?\nDid she go to the beach?\nWhat did you do?\nWhere did you stay?\nDid they enjoy the trip?"),
      ("جواب کوتاه",
       "در انگلیسی جواب فقط Yes یا No کافی نیست و کمی خشک شنیده می‌شود.\n"
       "جواب کوتاه با did ساخته می‌شود:\n"
       "Did you like it? — Yes, I did. / No, I didn't.\n"
       "Did she stay there? — Yes, she did.\n"
       "با فعل to be در گذشته، جواب با was/were می‌آید نه did:\n"
       "Was it expensive? — No, it wasn't.",
       "Did you enjoy it? — Yes, I did.\nDid he travel? — No, he didn't.\nWas the hotel clean? — Yes, it was.\nWere they tired? — No, they weren't.")],

 28: [("be going to",
       "برای برنامه‌ای که از قبل تصمیمش گرفته شده.\n"
       "ساختار: فعل to be + going to + فعل ساده\n"
       "I am going to study. · She is going to travel.\n"
       "شکل کوتاه خیلی رایج است: I'm going to · She's going to\n"
       "دقت کنید بعد از going to فعل **ساده** می‌آید:\n"
       "نادرست: I'm going to studying — درست: I'm going to study",
       "I'm going to study tomorrow.\nShe's going to find a job.\nWe're going to travel next week.\nThey're going to buy a house.\nHe's going to start a course."),
      ("منفی و سوالی",
       "منفی: not بعد از فعل to be.\n"
       "I'm not going to work tomorrow.\n"
       "She isn't going to travel.\n"
       "سوالی: فعل to be به ابتدای جمله می‌رود.\n"
       "Are you going to study? — Yes, I am. / No, I'm not.\n"
       "What are you going to do? — I'm going to rest.",
       "I'm not going to work tomorrow.\nShe isn't going to move.\nAre you going to travel? — Yes, I am.\nWhat are you going to do?")],

 29: [("صفات تفضیلی",
       "برای مقایسه دو چیز، به صفت er اضافه می‌شود و بعدش than می‌آید.\n"
       "Tehran is bigger than Yazd.\n"
       "قاعده املا:\n"
       "صفت کوتاه: er → tall → taller\n"
       "صفت مختوم به e: فقط r → nice → nicer\n"
       "صفت کوتاه با یک بی‌صدای آخر: حرف دو بار می‌آید → big → bigger\n"
       "صفت مختوم به y: y به i تبدیل می‌شود → easy → easier",
       "tall → taller\nbig → bigger\nnice → nicer\neasy → easier\ncheap → cheaper\nold → older"),
      ("صفات بلند و بی‌قاعده",
       "برای صفت‌های بلند (دو بخش و بیشتر) به‌جای er، کلمه more می‌آید:\n"
       "expensive → more expensive\ndifficult → more difficult\n"
       "چند صفت هم کاملاً بی‌قاعده‌اند و باید حفظ شوند:\n"
       "good → better · bad → worse\n"
       "نادرست: more better — درست: better",
       "expensive → more expensive\ndifficult → more difficult\ninteresting → more interesting\ngood → better\nbad → worse\nThis tea is better than that one.")],

 30: [("صفات عالی",
       "برای گفتن «ترین» از میان همه، به صفت est اضافه می‌شود و **the**\n"
       "پیش از آن می‌آید.\n"
       "It is the biggest city. · She is the youngest.\n"
       "برای صفت‌های بلند، the most می‌آید:\n"
       "the most expensive · the most difficult\n"
       "بی‌قاعده‌ها: good → the best · bad → the worst\n"
       "فراموش نکردن the مهم است؛ بدون آن جمله ناقص شنیده می‌شود.",
       "It was the best day.\nThat was the worst film.\nIt is the biggest city.\nThis is the smallest room.\nIt's the most expensive hotel."),
      ("مرور سطح A1",
       "تا اینجا این زمان‌ها را یاد گرفتید:\n"
       "حال ساده — کارهای همیشگی: I work every day.\n"
       "حال استمراری — همین حالا: I am working now.\n"
       "گذشته ساده — تمام‌شده: I worked yesterday.\n"
       "آینده با be going to — برنامه: I'm going to work tomorrow.\n"
       "و این ساختارها: there is/are، have got، can، should، مقایسه.\n"
       "با این‌ها می‌شود درباره خود، خانواده، کار، گذشته و برنامه‌ها حرف زد.",
       "I work every day.\nI am working now.\nI worked yesterday.\nI'm going to work tomorrow.\nThere are two rooms.\nI have got a car.\nI can swim.\nYou should rest.")],
}

# (دسته, نوع, صورت سوال, پاسخ, جایگزین‌ها, گزینه‌ها, واژه هدف, راهنما)
EX = {
 21: [(R,"چندگزینه‌ای","___ left at the corner.","Turn","","Turn | Turns | Turning","turn","جمله امری با فعل ساده شروع می‌شود."),
      (R,"تطبیق","station","ایستگاه","","station | street | road","station","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","___ straight on.","Go","","","straight","برای «برو» فعل go می‌آید."),
      (P,"متن آزاد","منفی کنید: Turn right here.","Don't turn right here.","Do not turn right here.","","turn","برای منفی Don't اول جمله می‌آید."),
      (P,"متن آزاد","مؤدبانه کنید: Cross the road.","Cross the road, please.","","","cross","با please مؤدبانه می‌شود."),
      (P,"مرتب‌سازی","the / cross / bridge","Cross the bridge.","","","bridge","جمله امری با فعل شروع می‌شود."),
      (P,"ترجمه به انگلیسی","ببخشید، ایستگاه کجاست؟","Excuse me, where is the station?","Excuse me, where's the station?","","station","با Excuse me شروع کن."),
      (P,"گفتار","Go straight on and turn left.","Go straight on and turn left.","","","left","جمله را واضح تلفظ کن.")],

 22: [(R,"چندگزینه‌ای","She ___ reading now.","is","","is | are | am","read","با she فعل is می‌آید."),
      (R,"تطبیق","busy","مشغول","","busy | quiet | ready","busy","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","They ___ waiting outside.","are","","","wait","با they فعل are می‌آید."),
      (P,"متن آزاد","درست کنید: She reading now.","She is reading now.","She's reading now.","","read","فعل to be جا افتاده است."),
      (P,"متن آزاد","سوالی کنید: You are working.","Are you working?","","","work","فعل to be به ابتدای جمله می‌رود."),
      (P,"مرتب‌سازی","film / watching / we / a / are","We are watching a film.","We're watching a film.","","watch","با فاعل شروع کن."),
      (P,"ترجمه به انگلیسی","دارد با تلفن حرف می‌زند.","He is talking on the phone.","She is talking on the phone.","","talk","حال استمراری: to be + ing"),
      (P,"گفتار","What are you doing now?","What are you doing now?","","","now","جمله را واضح تلفظ کن.")],

 23: [(R,"چندگزینه‌ای","I ___ walk to work, but today I'm taking the bus.","usually","","usually | now | tomorrow","usually","برای کار همیشگی قید تکرار می‌آید."),
      (R,"تطبیق","never","هرگز","","never | always | often","never","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","She ___ drinks tea. (همیشه)","always","","","always","«همیشه» یعنی always."),
      (P,"متن آزاد","درست کنید: I don't never drink coffee.","I never drink coffee.","","","never","با never جمله دیگر منفی نمی‌شود."),
      (P,"متن آزاد","قید را در جای درست بگذارید: She is busy. (always)","She is always busy.","","","busy","قید بعد از فعل to be می‌آید."),
      (P,"مرتب‌سازی","late / sometimes / works / he","He sometimes works late.","","","late","قید پیش از فعل اصلی می‌آید."),
      (P,"ترجمه به انگلیسی","اغلب با هم غذا می‌خوریم.","We often eat together.","","","often","قید پیش از فعل اصلی می‌آید."),
      (P,"گفتار","I usually walk to work.","I usually walk to work.","","","usually","جمله را واضح تلفظ کن.")],

 24: [(R,"چندگزینه‌ای","They ___ very happy.","were","","was | were | are","were","با they فعل were می‌آید."),
      (R,"تطبیق","angry","عصبانی","","angry | happy | sad","angry","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","I ___ at home yesterday.","was","","","home","با I فعل was می‌آید."),
      (P,"متن آزاد","منفی کنید: She was ill.","She wasn't ill.","She was not ill.","","ill","was به wasn't تبدیل می‌شود."),
      (P,"متن آزاد","سوالی کنید: You were free.","Were you free?","","","free","were به ابتدای جمله می‌رود."),
      (P,"مرتب‌سازی","park / we / at / were / the","We were at the park.","","","park","با فاعل شروع کن."),
      (P,"ترجمه به انگلیسی","دیروز کجا بودی؟","Where were you yesterday?","","","yesterday","برای پرسیدن جا where می‌آید."),
      (P,"گفتار","I was very tired last night.","I was very tired last night.","","","tired","جمله را واضح تلفظ کن.")],

 25: [(R,"چندگزینه‌ای","We ___ football last week.","played","","play | played | playing","played","برای گذشته ed اضافه می‌شود."),
      (R,"تطبیق","visited","دیدن رفت","","visited | called | helped","visited","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","She ___ English yesterday. (study)","studied","","","studied","y به i تبدیل و ed اضافه می‌شود."),
      (P,"متن آزاد","گذشته بنویسید: like","liked","","","liked","فعل به e ختم می‌شود، فقط d می‌گیرد."),
      (P,"متن آزاد","گذشته بنویسید: stop","stopped","","","stopped","حرف آخر دو بار می‌آید."),
      (P,"مرتب‌سازی","film / watched / a / I / yesterday","I watched a film yesterday.","","","watched","با فاعل شروع کن."),
      (P,"ترجمه به انگلیسی","دیروز تمام روز کار کرد.","She worked all day yesterday.","He worked all day yesterday.","","worked","به فعل ed اضافه کن."),
      (P,"گفتار","We played football last week.","We played football last week.","","","football","جمله را واضح تلفظ کن.")],

 26: [(R,"چندگزینه‌ای","I ___ to Tehran last summer.","went","","goed | went | going","went","گذشته go همان went است."),
      (R,"تطبیق","bought","خرید","","bought | took | made","bought","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","We ___ the sea. (see)","saw","","","saw","گذشته see همان saw است."),
      (P,"متن آزاد","گذشته بنویسید: eat","ate","","","ate","این فعل بی‌قاعده است."),
      (P,"متن آزاد","منفی کنید: I went to Tehran.","I didn't go to Tehran.","I did not go to Tehran.","","went","بعد از didn't فعل ساده می‌آید."),
      (P,"مرتب‌سازی","bus / took / the / she","She took the bus.","","","took","با فاعل شروع کن."),
      (P,"ترجمه به انگلیسی","عکس‌های زیادی گرفتم.","I took many photos.","","","took","گذشته take همان took است."),
      (P,"گفتار","We went to the mountain last week.","We went to the mountain last week.","","","mountain","جمله را واضح تلفظ کن.")],

 27: [(R,"چندگزینه‌ای","___ you like the holiday?","Did","","Do | Did | Was","holiday","برای سوال گذشته did می‌آید."),
      (R,"تطبیق","crowded","شلوغ","","crowded | quiet | beautiful","crowded","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","Where did you ___ ? (stay)","stay","","","stay","بعد از did فعل ساده می‌آید."),
      (P,"متن آزاد","درست کنید: Did you liked it?","Did you like it?","","","like","بعد از did فعل ساده می‌ماند."),
      (P,"متن آزاد","جواب کوتاه مثبت بدهید: Did she travel?","Yes, she did.","","","travel","جواب کوتاه با did ساخته می‌شود."),
      (P,"مرتب‌سازی","did / do / what / you","What did you do?","","","did","کلمه پرسشی اول می‌آید."),
      (P,"ترجمه به انگلیسی","کجا ماندی؟","Where did you stay?","","","stay","با Where did شروع کن."),
      (P,"گفتار","Did you enjoy the trip?","Did you enjoy the trip?","","","enjoy","جمله را واضح تلفظ کن.")],

 28: [(R,"چندگزینه‌ای","I'm going ___ study tomorrow.","to","","to | for | at","study","ساختار going to است."),
      (R,"تطبیق","future","آینده","","future | plan | course","future","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","She ___ going to find a job.","is","","","job","با she فعل is می‌آید."),
      (P,"متن آزاد","درست کنید: I'm going to studying.","I'm going to study.","","","study","بعد از going to فعل ساده می‌آید."),
      (P,"متن آزاد","سوالی کنید: You are going to travel.","Are you going to travel?","","","travel","فعل to be به ابتدای جمله می‌رود."),
      (P,"مرتب‌سازی","move / we / going / are / to","We are going to move.","We're going to move.","","move","با فاعل شروع کن."),
      (P,"ترجمه به انگلیسی","قرار است پول پس‌انداز کنم.","I am going to save money.","I'm going to save money.","","save","از be going to استفاده کن."),
      (P,"گفتار","I'm going to start a course.","I'm going to start a course.","","","course","جمله را واضح تلفظ کن.")],

 29: [(R,"چندگزینه‌ای","Tehran is ___ than Yazd.","bigger","","big | bigger | biggest","bigger","برای مقایسه دو چیز er می‌آید."),
      (R,"تطبیق","different","متفاوت","","different | same | better","different","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","He is ___ than me. (tall)","taller","","","taller","به صفت کوتاه er اضافه می‌شود."),
      (P,"متن آزاد","تفضیلی بنویسید: easy","easier","","","easier","y به i تبدیل می‌شود."),
      (P,"متن آزاد","تفضیلی بنویسید: expensive","more expensive","","","expensive","صفت بلند است، پس more می‌گیرد."),
      (P,"مرتب‌سازی","better / this / than / is / tea / that / one","This tea is better than that one.","","","better","با فاعل شروع کن."),
      (P,"ترجمه به انگلیسی","برادرم از من مسن‌تر است.","My brother is older than me.","My brother is older than I am.","","older","با than مقایسه کن."),
      (P,"گفتار","Tehran is bigger than Yazd.","Tehran is bigger than Yazd.","","","bigger","جمله را واضح تلفظ کن.")],

 30: [(R,"چندگزینه‌ای","It was ___ day of the year.","the best","","best | the best | the better","best","صفت عالی با the می‌آید."),
      (R,"تطبیق","progress","پیشرفت","","progress | practice | level","progress","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","It is ___ city in Iran. (big)","the biggest","","","biggest","صفت عالی با the و est می‌آید."),
      (P,"متن آزاد","عالی بنویسید: bad","the worst","worst","","worst","این صفت بی‌قاعده است."),
      (P,"متن آزاد","عالی بنویسید: expensive","the most expensive","most expensive","","expensive","صفت بلند با the most می‌آید."),
      (P,"مرتب‌سازی","level / next / continue / the / to","Continue to the next level.","","","continue","جمله امری با فعل شروع می‌شود."),
      (P,"ترجمه به انگلیسی","برای A2 آماده‌ای.","You are ready for A2.","You're ready for A2.","","ready","با فعل to be شروع کن."),
      (P,"گفتار","I am proud of my progress.","I am proud of my progress.","","","proud","جمله را واضح تلفظ کن.")],
}
