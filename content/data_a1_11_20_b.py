# -*- coding: utf-8 -*-
"""
مکالمه، گرامر و تمرین دروس ۱۱ تا ۲۰ سطح A1.

جدا از واژگان (`data_a1_11_20.py`) نگه داشته شده چون هر دو بلندند.

قاعده تمرین‌ها: حداقل ۶۰٪ تولیدی. `export_to_app.py` این را می‌سنجد و
اگر کمتر باشد با کد خطا خارج می‌شود — سند ۰۷، تمایز ۱.
"""

P, R = "تولیدی", "تشخیصی"

# (گوینده, انگلیسی, فارسی)
DLG = {
 11: [("A","What do you do in your free time?","در اوقات فراغتت چه می‌کنی؟"),
      ("B","I like reading. And you?","مطالعه دوست دارم. تو چطور؟"),
      ("A","I love music. I play the guitar.","من عاشق موسیقی‌ام. گیتار می‌نوازم."),
      ("B","Really? That's interesting!","واقعاً؟ جالب است!"),
      ("A","Do you like sport?","ورزش دوست داری؟"),
      ("B","Yes, but I hate running.","بله، اما از دویدن متنفرم.")],
 12: [("A","Is there any bread?","نان هست؟"),
      ("B","Yes, there is some bread on the table.","بله، کمی نان روی میز هست."),
      ("A","And milk? I want some milk.","و شیر؟ کمی شیر می‌خواهم."),
      ("B","No, there isn't any milk.","نه، هیچ شیری نیست."),
      ("A","Are there any apples?","سیب هست؟"),
      ("B","Yes, there are three apples.","بله، سه تا سیب هست.")],
 13: [("A","Good evening. A table for two, please.","عصر بخیر. یک میز دو نفره، لطفاً."),
      ("B","Of course. Here is the menu.","حتماً. این هم منو."),
      ("A","Thank you. I'd like the soup, please.","ممنون. سوپ می‌خواهم، لطفاً."),
      ("B","And to drink?","و برای نوشیدن؟"),
      ("A","Can I have some water?","می‌شود کمی آب بدهید؟"),
      ("B","Certainly. Your order is ready soon.","حتماً. سفارشتان به‌زودی آماده است.")],
 14: [("A","Is your flat big?","آپارتمانت بزرگ است؟"),
      ("B","There are three rooms and a kitchen.","سه اتاق و یک آشپزخانه دارد."),
      ("A","Is there a balcony?","بالکن هم دارد؟"),
      ("B","Yes, there is a small balcony.","بله، یک بالکن کوچک دارد."),
      ("A","And a garden?","و باغچه؟"),
      ("B","No, there isn't a garden.","نه، باغچه ندارد.")],
 15: [("A","Where is my bag?","کیفم کجاست؟"),
      ("B","It's under the table.","زیر میز است."),
      ("A","And the keys?","و کلیدها؟"),
      ("B","They are in your bag.","داخل کیفت هستند."),
      ("A","Where is the lamp?","چراغ کجاست؟"),
      ("B","It's next to the bed.","کنار تخت است.")],
 16: [("A","I have got a new coat.","یک پالتوی نو دارم."),
      ("B","What colour is it?","چه رنگی است؟"),
      ("A","It's red. Do you like it?","قرمز است. دوستش داری؟"),
      ("B","Yes! I have got a green one.","بله! من یک سبز دارم."),
      ("A","Have you got a scarf too?","شال گردن هم داری؟"),
      ("B","No, I haven't.","نه، ندارم.")],
 17: [("A","Excuse me, how much is this shirt?","ببخشید، این پیراهن چند است؟"),
      ("B","It's cheap. Only two hundred.","ارزان است. فقط دویست."),
      ("A","And how much are these shoes?","و این کفش‌ها چند هستند؟"),
      ("B","Those are expensive, I'm afraid.","متأسفانه آن‌ها گران هستند."),
      ("A","Can I try the shirt on?","می‌توانم پیراهن را پرو کنم؟"),
      ("B","Of course. Here you are.","حتماً. بفرمایید.")],
 18: [("A","How is the weather today?","امروز هوا چطور است؟"),
      ("B","It's cold and cloudy.","سرد و ابری است."),
      ("A","Is it raining?","باران می‌بارد؟"),
      ("B","Not now, but take an umbrella.","الان نه، اما چتر بردار."),
      ("A","What about tomorrow?","فردا چطور؟"),
      ("B","It's sunny and warm tomorrow.","فردا آفتابی و مطبوع است.")],
 19: [("A","Can you swim?","می‌توانی شنا کنی؟"),
      ("B","Yes, I can swim well.","بله، خوب شنا می‌کنم."),
      ("A","Can you drive?","رانندگی بلدی؟"),
      ("B","No, I can't drive.","نه، رانندگی بلد نیستم."),
      ("A","Can you help me tomorrow?","فردا می‌توانی کمکم کنی؟"),
      ("B","Yes, of course I can.","بله، البته که می‌توانم.")],
 20: [("A","How are you today?","امروز حالت چطور است؟"),
      ("B","Not good. I have a headache.","خوب نیستم. سردرد دارم."),
      ("A","Have you got a fever?","تب هم داری؟"),
      ("B","A little. I feel very tired.","کمی. خیلی خسته‌ام."),
      ("A","You should rest at home.","باید در خانه استراحت کنی."),
      ("B","Yes, and I should take some medicine.","بله، و باید دارو بخورم.")],
}

# (عنوان, توضیح, مثال‌ها)
GR = {
 11: [("like / love / hate + ing",
       "برای گفتن اینکه از کاری خوشمان می‌آید یا نه، بعد از like، love و hate\n"
       "فعل با ing می‌آید.\n"
       "like = دوست داشتن · love = عاشق بودن · hate = متنفر بودن\n"
       "برخلاف فارسی که می‌گوییم «شنا کردن را دوست دارم»، در انگلیسی «to» نمی‌آید:\n"
       "نادرست: I like to swimming — درست: I like swimming",
       "I like swimming.\nShe loves reading.\nHe hates cooking.\nWe like listening to music.\nThey love dancing."),
      ("قاعده ساخت ing",
       "به بیشتر فعل‌ها فقط ing اضافه می‌شود: read → reading\n"
       "اگر فعل به e ختم شود، e حذف می‌شود: dance → dancing\n"
       "اگر فعل کوتاه به یک حرف بی‌صدا ختم شود، آن حرف دو بار می‌آید:\n"
       "swim → swimming · run → running",
       "read → reading\nplay → playing\ndance → dancing\nwrite → writing\nswim → swimming\nrun → running")],

 12: [("اسم شمارا و ناشمارا",
       "اسم شمارا را می‌شود شمرد و جمع بست: one apple, two apples\n"
       "اسم ناشمارا را نمی‌شود شمرد و همیشه مفرد می‌ماند:\n"
       "rice، bread، milk، water، sugar، meat\n"
       "نکته‌ای که فارسی‌زبان‌ها را گیر می‌اندازد: در فارسی می‌گوییم «دو تا نان»،\n"
       "اما در انگلیسی نمی‌شود گفت two breads. باید گفت two pieces of bread.",
       "one apple → two apples\none book → three books\nrice (بدون جمع)\nmilk (بدون جمع)\nsome water\ntwo pieces of bread"),
      ("some و any",
       "some در جمله مثبت می‌آید: I want some rice.\n"
       "any در جمله منفی و سوالی می‌آید:\n"
       "There isn't any milk. · Is there any bread?\n"
       "استثنا: وقتی چیزی تعارف یا درخواست می‌کنیم، در سوال هم some می‌آید:\n"
       "Would you like some tea?",
       "I want some bread.\nThere is some water.\nThere isn't any milk.\nIs there any cheese?\nWould you like some tea?")],

 13: [("would like",
       "would like یعنی «می‌خواهم» اما مؤدبانه‌تر از want است. در رستوران و\n"
       "مغازه همیشه از این استفاده می‌شود.\n"
       "شکل کوتاهش I'd like است.\n"
       "بعد از would like اسم می‌آید یا فعل با to:\n"
       "I'd like a coffee. · I'd like to order.",
       "I'd like a coffee.\nShe'd like the soup.\nWe'd like a table for two.\nI'd like to order, please.\nWould you like a dessert?"),
      ("Can I have …?",
       "برای درخواست کردن، این ساختار ساده و مؤدبانه است:\n"
       "Can I have + اسم + please?\n"
       "برای مؤدبانه‌تر شدن می‌شود could گفت: Could I have the bill, please?\n"
       "افزودن please تقریباً همیشه لازم است؛ بدون آن جمله بی‌ادبانه شنیده می‌شود.",
       "Can I have some water, please?\nCan I have the menu?\nCould I have the bill, please?\nCan we have two teas?")],

 14: [("there is / there are",
       "برای گفتن اینکه چیزی «هست» یا «وجود دارد» به‌کار می‌رود.\n"
       "there is برای مفرد و اسم ناشمارا: There is a kitchen.\n"
       "there are برای جمع: There are three rooms.\n"
       "شکل کوتاه: there's (فقط برای مفرد؛ there're تقریباً نوشته نمی‌شود).\n"
       "دقت کنید: it is یعنی «آن است»، اما there is یعنی «وجود دارد».",
       "There is a kitchen.\nThere is some water.\nThere are two windows.\nThere's a small garden.\nThere are four rooms."),
      ("منفی و سوالی",
       "منفی: There isn't a garden. · There aren't any chairs.\n"
       "سوالی: جای there و is/are عوض می‌شود.\n"
       "Is there a balcony? — Yes, there is. / No, there isn't.\n"
       "Are there any windows? — Yes, there are. / No, there aren't.\n"
       "در جواب کوتاه، there تکرار می‌شود نه it.",
       "There isn't a garage.\nThere aren't any stairs.\nIs there a bathroom? — Yes, there is.\nAre there two bedrooms? — No, there aren't.")],

 15: [("حروف اضافه مکان",
       "این حروف جای چیزها را نشان می‌دهند:\n"
       "in (داخل) · on (روی) · under (زیر) · next to (کنار)\n"
       "behind (پشت) · in front of (جلوی) · between (بین دو چیز)\n"
       "تفاوتی که زیاد اشتباه می‌شود: in یعنی داخلِ چیزی، on یعنی روی سطح.\n"
       "in the bag (داخل کیف) — on the table (روی میز)",
       "The key is in the bag.\nThe book is on the table.\nThe cat is under the chair.\nThe lamp is next to the bed.\nThe chair is behind the door.\nThe table is between two chairs."),
      ("پرسش با where",
       "برای پرسیدن جای چیزی از where استفاده می‌شود و بعد از آن فعل to be می‌آید:\n"
       "Where is my bag? — It's under the table.\n"
       "Where are the keys? — They are in your bag.\n"
       "برای مفرد is و برای جمع are.",
       "Where is the lamp? — It's on the desk.\nWhere are my shoes? — They are behind the door.\nWhere is the cat? — It's under the sofa.")],

 16: [("have got",
       "have got یعنی «داشتن». در انگلیسی بریتانیایی خیلی رایج است.\n"
       "با I، you، we، they → have got\n"
       "با he، she، it → has got\n"
       "شکل کوتاه: I've got · She's got\n"
       "معنی‌اش با have تفاوتی ندارد: I have a car = I have got a car",
       "I have got a red coat.\nShe has got a blue dress.\nWe have got two bags.\nHe's got a white shirt.\nThey have got new shoes."),
      ("منفی، سوالی و ترتیب صفت",
       "منفی: I haven't got a hat. · She hasn't got a scarf.\n"
       "سوالی: Have you got a coat? — Yes, I have. / No, I haven't.\n"
       "درباره رنگ: صفت همیشه **پیش از** اسم می‌آید، برخلاف فارسی.\n"
       "فارسی: پالتوی قرمز — انگلیسی: a red coat (نه a coat red)",
       "I haven't got a hat.\nHas she got a green scarf? — Yes, she has.\na red coat\na white shirt\nblack shoes")],

 17: [("How much / How many",
       "How much برای قیمت و برای اسم ناشمارا: How much is it?\n"
       "How many برای اسم شمارای جمع: How many apples?\n"
       "برای قیمت، فعل با مفرد یا جمع بودن کالا عوض می‌شود:\n"
       "How much is this shirt? (مفرد)\n"
       "How much are these shoes? (جمع)",
       "How much is this?\nHow much are these shoes?\nHow many bags do you want?\nHow much water is there?"),
      ("this / these / that / those",
       "برای نزدیک: this (مفرد) · these (جمع)\n"
       "برای دور: that (مفرد) · those (جمع)\n"
       "این چهارتا هم پیش از اسم می‌آیند و هم به‌تنهایی:\n"
       "this shirt / This is expensive.\n"
       "اشتباه رایج: these بعد از خودش اسم جمع می‌خواهد، نه مفرد.\n"
       "نادرست: these shirt — درست: these shirts",
       "This shirt is cheap.\nThese shoes are new.\nThat bag is expensive.\nThose coats are nice.\nHow much are these?")],

 18: [("it برای آب‌وهوا",
       "برای گفتن وضعیت هوا، جمله همیشه با it شروع می‌شود — حتی وقتی در فارسی\n"
       "فاعلی وجود ندارد.\n"
       "فارسی: «سرد است» — انگلیسی: It is cold. (نه فقط Is cold)\n"
       "ساختار: It is + صفت\n"
       "شکل کوتاه: It's cold today.",
       "It is cold today.\nIt's very hot in summer.\nIt's sunny.\nIt's cloudy and wet.\nIt's twenty degrees."),
      ("پرسش درباره هوا",
       "دو راه رایج برای پرسیدن:\n"
       "How is the weather? — It's rainy.\n"
       "What's the weather like? — It's cold.\n"
       "برای بارش، فعل با ing هم به‌کار می‌رود:\n"
       "It is raining. · It is snowing.\n"
       "تفاوت: It's rainy یعنی هوا بارانی است؛ It's raining یعنی همین حالا می‌بارد.",
       "How is the weather? — It's sunny.\nWhat's the weather like? — It's cold.\nIt is raining now.\nIt is snowing in winter.")],

 19: [("can / can't",
       "can یعنی «توانستن» و برای گفتن مهارت‌ها به‌کار می‌رود.\n"
       "شکل can برای همه فاعل‌ها یکی است — حتی با he و she هیچ s نمی‌گیرد.\n"
       "نادرست: She cans swim — درست: She can swim\n"
       "بعد از can فعل ساده می‌آید، بدون to:\n"
       "نادرست: I can to swim — درست: I can swim",
       "I can swim.\nShe can drive.\nHe can cook.\nWe can speak English.\nThey can dance."),
      ("منفی و سوالی با can",
       "منفی: cannot که تقریباً همیشه can't نوشته می‌شود.\n"
       "I can't drive. · She can't sing.\n"
       "سوالی: can به ابتدای جمله می‌رود.\n"
       "Can you swim? — Yes, I can. / No, I can't.\n"
       "can برای درخواست هم به‌کار می‌رود: Can you help me?",
       "I can't drive.\nHe can't cook.\nCan you swim? — Yes, I can.\nCan she speak English? — No, she can't.\nCan you help me, please?")],

 20: [("گفتن از بیماری",
       "دو ساختار رایج، هر دو درست:\n"
       "I have a headache. · I've got a headache.\n"
       "برای درد عضو بدن، این هم به‌کار می‌رود:\n"
       "My head hurts. · My back hurts.\n"
       "برای حال عمومی از feel استفاده می‌شود:\n"
       "I feel ill. · I feel tired. (نه I am feel)",
       "I have a headache.\nShe's got a cold.\nHe has a fever.\nMy back hurts.\nI feel very tired."),
      ("should برای توصیه",
       "should یعنی «بهتر است» یا «باید» — اما نرم‌تر از must.\n"
       "بعد از should فعل ساده می‌آید، بدون to:\n"
       "You should rest. (نه You should to rest)\n"
       "با he و she هیچ s نمی‌گیرد: She should rest.\n"
       "منفی: shouldn't — You shouldn't go to work today.",
       "You should rest.\nHe should see a doctor.\nShe should take this medicine.\nYou shouldn't work today.\nWe should sleep early.")],
}

# (دسته, نوع, صورت سوال, پاسخ, جایگزین‌ها, گزینه‌ها, واژه هدف, راهنما)
EX = {
 11: [(R,"چندگزینه‌ای","I like ___ .","swimming","","swim | swimming | to swimming","swimming","بعد از like فعل با ing می‌آید."),
      (R,"تطبیق","boring","کسل‌کننده","","boring | interesting | favourite","boring","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","She loves ___ books. (read)","reading","","","read","به فعل ing اضافه کن."),
      (P,"متن آزاد","با ing بنویسید: dance","dancing","","","dance","فعل به e ختم می‌شود، پس e حذف می‌شود."),
      (P,"متن آزاد","با ing بنویسید: run","running","","","run","فعل کوتاه است، حرف آخر دو بار می‌آید."),
      (P,"مرتب‌سازی","music / I / love / listening / to","I love listening to music.","","","music","با فاعل شروع کن."),
      (P,"ترجمه به انگلیسی","او از آشپزی متنفر است.","He hates cooking.","She hates cooking.","","cooking","بعد از hate فعل با ing می‌آید."),
      (P,"گفتار","This film is very interesting.","This film is very interesting.","","","interesting","جمله را واضح تلفظ کن.")],

 12: [(R,"چندگزینه‌ای","There isn't ___ milk.","any","","some | any | a","any","در جمله منفی any می‌آید."),
      (R,"تطبیق","bread","نان","","bread | rice | meat","bread","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","I want ___ rice, please.","some","","","rice","در جمله مثبت some می‌آید."),
      (P,"متن آزاد","منفی کنید: There is some cheese.","There isn't any cheese.","There is not any cheese.","","cheese","some در منفی به any تبدیل می‌شود."),
      (P,"متن آزاد","سوالی کنید: There are some apples.","Are there any apples?","","","apple","جای there و are عوض می‌شود."),
      (P,"مرتب‌سازی","water / some / can / have / I","Can I have some water?","","","water","سوال با فعل شروع می‌شود."),
      (P,"ترجمه به انگلیسی","هیچ شیری نیست.","There isn't any milk.","There is no milk.","","milk","برای منفی از any استفاده کن."),
      (P,"گفتار","I'm very hungry and thirsty.","I'm very hungry and thirsty.","","","hungry","جمله را واضح تلفظ کن.")],

 13: [(R,"چندگزینه‌ای","___ like the soup, please.","I'd","","I'd | I'm | I've","soup","شکل کوتاه would like همان I'd like است."),
      (R,"تطبیق","bill","صورتحساب","","bill | menu | order","bill","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","Can I ___ the menu, please?","have","","","menu","برای درخواست از Can I have استفاده کن."),
      (P,"متن آزاد","مؤدبانه بنویسید: I want a coffee.","I'd like a coffee.","I would like a coffee.","","coffee","want را با would like جایگزین کن."),
      (P,"متن آزاد","درخواست کنید: صورتحساب","Can I have the bill, please?","Could I have the bill, please?","","bill","با Can I have شروع کن."),
      (P,"مرتب‌سازی","two / a / for / table / please","A table for two, please.","","","table","با اسم شروع می‌شود."),
      (P,"ترجمه به انگلیسی","این سوپ خوشمزه است.","This soup is delicious.","","","delicious","صفت بعد از فعل to be می‌آید."),
      (P,"گفتار","Can I have some water, please?","Can I have some water, please?","","","water","جمله را واضح تلفظ کن.")],

 14: [(R,"چندگزینه‌ای","___ three rooms in my flat.","There are","","There is | There are | It is","room","برای جمع there are می‌آید."),
      (R,"تطبیق","kitchen","آشپزخانه","","kitchen | bedroom | garden","kitchen","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","___ a small garden.","There is","There's","","garden","برای مفرد there is می‌آید."),
      (P,"متن آزاد","منفی کنید: There is a garage.","There isn't a garage.","There is not a garage.","","garage","is به isn't تبدیل می‌شود."),
      (P,"متن آزاد","سوالی کنید: There is a balcony.","Is there a balcony?","","","balcony","جای there و is عوض می‌شود."),
      (P,"مرتب‌سازی","two / are / windows / there","There are two windows.","","","window","با there شروع کن."),
      (P,"ترجمه به انگلیسی","یک آشپزخانه بزرگ هست.","There is a big kitchen.","There's a big kitchen.","","kitchen","برای «هست» از there is استفاده کن."),
      (P,"گفتار","There are three rooms and a kitchen.","There are three rooms and a kitchen.","","","room","جمله را واضح تلفظ کن.")],

 15: [(R,"چندگزینه‌ای","The cat is ___ the table.","under","","in | on | under","under","«زیر» یعنی under."),
      (R,"تطبیق","shelf","قفسه","","shelf | desk | mirror","shelf","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","The key is ___ the bag.","in","","","key","«داخل» یعنی in."),
      (P,"متن آزاد","کامل کنید: The lamp is ___ the bed. (کنار)","next to","","","lamp","«کنار» یعنی next to."),
      (P,"متن آزاد","سوال بسازید: It's under the sofa.","Where is it?","Where's it?","","sofa","برای پرسیدن جا از where استفاده کن."),
      (P,"مرتب‌سازی","the / on / book / is / table / the","The book is on the table.","","","book","با فاعل شروع کن."),
      (P,"ترجمه به انگلیسی","صندلی پشت در است.","The chair is behind the door.","","","chair","«پشت» یعنی behind."),
      (P,"گفتار","The picture is on the wall.","The picture is on the wall.","","","picture","جمله را واضح تلفظ کن.")],

 16: [(R,"چندگزینه‌ای","She ___ got a blue dress.","has","","have | has | is","dress","با she فعل has می‌آید."),
      (R,"تطبیق","scarf","شال گردن","","scarf | hat | coat","scarf","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","I ___ got a red coat.","have","","","coat","با I فعل have می‌آید."),
      (P,"متن آزاد","منفی کنید: I have got a hat.","I haven't got a hat.","I have not got a hat.","","hat","have به haven't تبدیل می‌شود."),
      (P,"متن آزاد","درست بنویسید: a coat red","a red coat","","","coat","صفت پیش از اسم می‌آید."),
      (P,"مرتب‌سازی","white / has / a / he / shirt / got","He has got a white shirt.","He's got a white shirt.","","shirt","با فاعل شروع کن."),
      (P,"ترجمه به انگلیسی","کفش‌های من مشکی است.","My shoes are black.","","","shoes","shoes جمع است، پس are می‌آید."),
      (P,"گفتار","I have got a green scarf.","I have got a green scarf.","","","scarf","جمله را واضح تلفظ کن.")],

 17: [(R,"چندگزینه‌ای","How much ___ these shoes?","are","","is | are | do","shoes","shoes جمع است، پس are می‌آید."),
      (R,"تطبیق","expensive","گران","","expensive | cheap | open","expensive","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","___ much is this shirt?","How","","","shirt","برای پرسیدن قیمت How much می‌آید."),
      (P,"متن آزاد","درست کنید: these shirt","these shirts","","","shirt","بعد از these اسم جمع می‌آید."),
      (P,"متن آزاد","جمع کنید: How much is this bag?","How much are these bags?","","","bag","this به these و is به are تبدیل می‌شود."),
      (P,"مرتب‌سازی","try / can / on / I / it","Can I try it on?","","","try on","سوال با فعل شروع می‌شود."),
      (P,"ترجمه به انگلیسی","این کفش‌ها ارزان هستند.","These shoes are cheap.","","","cheap","برای نزدیک و جمع these می‌آید."),
      (P,"گفتار","How much is this, please?","How much is this, please?","","","price","جمله را واضح تلفظ کن.")],

 18: [(R,"چندگزینه‌ای","___ cold today.","It's","","It's | Is | There's","cold","جمله آب‌وهوا با it شروع می‌شود."),
      (R,"تطبیق","winter","زمستان","","winter | summer | spring","winter","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","It ___ very hot in summer.","is","","","summer","بعد از it فعل is می‌آید."),
      (P,"متن آزاد","درست کنید: Is cold today.","It is cold today.","It's cold today.","","cold","جمله بدون فاعل it نمی‌شود."),
      (P,"متن آزاد","سوال بسازید: It's sunny.","How is the weather?","What's the weather like?","","weather","برای پرسش از هوا این دو ساختار به‌کار می‌رود."),
      (P,"مرتب‌سازی","umbrella / an / take","Take an umbrella.","","","umbrella","جمله امری با فعل شروع می‌شود."),
      (P,"ترجمه به انگلیسی","امروز ابری است.","It is cloudy today.","It's cloudy today.","","cloudy","با It is شروع کن."),
      (P,"گفتار","It's sunny and warm today.","It's sunny and warm today.","","","sunny","جمله را واضح تلفظ کن.")],

 19: [(R,"چندگزینه‌ای","She ___ swim very well.","can","","can | cans | can to","swim","can با هیچ فاعلی s نمی‌گیرد."),
      (R,"تطبیق","drive","رانندگی کردن","","drive | ride | run","drive","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","I ___ drive a car. (نمی‌توانم)","can't","cannot","","drive","منفی can همان can't است."),
      (P,"متن آزاد","درست کنید: He can to cook.","He can cook.","","","cook","بعد از can فعل ساده بدون to می‌آید."),
      (P,"متن آزاد","سوالی کنید: You can swim.","Can you swim?","","","swim","can به ابتدای جمله می‌رود."),
      (P,"مرتب‌سازی","me / you / help / can","Can you help me?","","","help","سوال با can شروع می‌شود."),
      (P,"ترجمه به انگلیسی","او نمی‌تواند آواز بخواند.","She can't sing.","He can't sing.","","sing","برای منفی can't به‌کار می‌رود."),
      (P,"گفتار","I can speak a little English.","I can speak a little English.","","","speak","جمله را واضح تلفظ کن.")],

 20: [(R,"چندگزینه‌ای","You ___ rest at home.","should","","should | should to | shoulds","should","بعد از should فعل ساده می‌آید."),
      (R,"تطبیق","fever","تب","","fever | headache | cold","fever","واژه را به معنی درست وصل کن."),
      (P,"جای خالی","I have a ___ . (سردرد)","headache","","","headache","«سردرد» یعنی headache."),
      (P,"متن آزاد","درست کنید: You should to rest.","You should rest.","","","should","بعد از should حرف to نمی‌آید."),
      (P,"متن آزاد","با have got بنویسید: She has a cold.","She has got a cold.","She's got a cold.","","cold","has را به has got تبدیل کن."),
      (P,"مرتب‌سازی","doctor / should / a / he / see","He should see a doctor.","","","doctor","با فاعل شروع کن."),
      (P,"ترجمه به انگلیسی","خیلی خسته‌ام.","I am very tired.","I'm very tired.","","tired","با فعل to be شروع کن."),
      (P,"گفتار","I have a headache and a fever.","I have a headache and a fever.","","","headache","جمله را واضح تلفظ کن.")],
}
