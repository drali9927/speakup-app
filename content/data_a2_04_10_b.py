# -*- coding: utf-8 -*-
"""مکالمه، گرامر و تمرین دروس ۴ تا ۱۰ سطح A2."""

P, R = "تولیدی", "تشخیصی"

DLG = {
 4: [("A","Why didn't you answer my call?","چرا جواب تلفنم را ندادی؟"),
     ("B","Sorry, I was cooking when you called.","ببخشید، وقتی زنگ زدی داشتم آشپزی می‌کردم."),
     ("A","I called three times!","سه بار زنگ زدم!"),
     ("B","The water was running, so I didn't hear it.","آب باز بود، برای همین نشنیدم."),
     ("A","And then the electricity went off.","و بعد برق رفت."),
     ("B","Yes! Luckily nothing was broken.","بله! خوشبختانه چیزی نشکست.")],
 5: [("A","So, what happened next?","خب، بعدش چه شد؟"),
     ("B","While we were waiting, it started to rain.","در حالی که منتظر بودیم، باران شروع شد."),
     ("A","Oh no. Did you have an umbrella?","وای نه. چتر داشتی؟"),
     ("B","No. Then suddenly a taxi stopped.","نه. بعد ناگهان یک تاکسی ایستاد."),
     ("A","That was lucky!","چه شانسی!"),
     ("B","In the end, we arrived on time.","در پایان، به‌موقع رسیدیم.")],
 6: [("A","This bag is really heavy.","این کیف واقعاً سنگین است."),
     ("B","I'll carry it for you.","برایت می‌برمش."),
     ("A","Thank you! And I need the address.","ممنون! و نشانی را لازم دارم."),
     ("B","No problem, I'll send it now.","مشکلی نیست، همین حالا می‌فرستم."),
     ("A","Will you be there tomorrow?","فردا آنجا خواهی بود؟"),
     ("B","Of course. I promise.","البته. قول می‌دهم.")],
 7: [("A","Are you free on Friday?","جمعه آزادی؟"),
     ("B","I'm meeting Sara at six.","ساعت شش با سارا قرار دارم."),
     ("A","What about Saturday?","شنبه چطور؟"),
     ("B","Saturday works. What time?","شنبه خوب است. چه ساعتی؟"),
     ("A","I'm booking a table for seven.","برای ساعت هفت میز رزرو می‌کنم."),
     ("B","Perfect. Please confirm it tomorrow.","عالی. لطفاً فردا تأییدش کن.")],
 8: [("A","Should I take an umbrella?","چتر بردارم؟"),
     ("B","It might rain later.","ممکن است بعداً باران ببارد."),
     ("A","The forecast says fog too.","پیش‌بینی مه هم می‌گوید."),
     ("B","Then there might be a delay.","پس ممکن است تأخیر باشد."),
     ("A","I'll take it just in case.","محض احتیاط برمی‌دارم."),
     ("B","Good idea. Nothing is certain.","فکر خوبی است. هیچ‌چیز قطعی نیست.")],
 9: [("A","If it rains, what will we do?","اگر باران ببارد، چه کار می‌کنیم؟"),
     ("B","If it rains, we'll stay at home.","اگر باران ببارد، خانه می‌مانیم."),
     ("A","And if it's sunny?","و اگر آفتابی باشد؟"),
     ("B","Then we'll go out early.","آن وقت زود بیرون می‌رویم."),
     ("A","If we walk, we'll save money.","اگر پیاده برویم، پول صرفه‌جویی می‌کنیم."),
     ("B","True. But we need a plan B.","درست است. اما به برنامه جایگزین نیاز داریم.")],
 10:[("A","Let's plan the trip together.","بیا سفر را با هم برنامه‌ریزی کنیم."),
     ("B","I suggest Friday morning.","جمعه صبح را پیشنهاد می‌کنم."),
     ("A","I prefer the afternoon.","من بعدازظهر را ترجیح می‌دهم."),
     ("B","Fine. We'll share the cost.","باشد. هزینه را تقسیم می‌کنیم."),
     ("A","Who's responsible for food?","چه کسی مسئول غذاست؟"),
     ("B","I am. Everything is done!","من. همه‌چیز انجام شد!")],
}

GR = {
 4: [("گذشته استمراری",
      "برای کاری که در گذشته **در جریان بود**.\n"
      "ساختار: was / were + فعل با ing\n"
      "I was cooking. · They were waiting.\n"
      "was با I، he، she، it — were با you، we، they.\n"
      "تفاوتش با گذشته ساده: گذشته ساده کاری است که تمام شد؛ گذشته\n"
      "استمراری کاری است که کش داشت و چیزی وسطش اتفاق افتاد.",
      "I was cooking dinner.\nShe was waiting for the bus.\nThey were watching a film.\nIt was raining hard.\nWe were talking about you.",
      "کدام درست است؟", "I was cooking.",
      "I was cooking. | I were cooking. | I was cook.",
      "I were cooking. ← با I فعل was می‌آید نه were.\n"
      "I was cook. ← بعد از was فعل باید ing بگیرد."),
     ("when و while",
      "این دو، کار کوتاه را به کار کشدار وصل می‌کنند:\n"
      "**while** پیش از کار کشدار (استمراری) می‌آید:\n"
      "While I was cooking, she called.\n"
      "**when** پیش از کار کوتاه (گذشته ساده) می‌آید:\n"
      "I was cooking when she called.\n"
      "هر دو جمله یک معنی دارند؛ فقط ترتیب عوض شده.",
      "While I was cooking, she called.\nI was cooking when she called.\nWhile they were waiting, it started to rain.\nHe was sleeping when the phone rang.",
      "کدام درست است؟", "I was reading when he arrived.",
      "I was reading when he arrived. | I read when he was arriving.",
      "I read when he was arriving. ← کار کشدار (خواندن) باید استمراری باشد و کار کوتاه (رسیدن) ساده.")],

 5: [("روایت یک ماجرا",
      "برای تعریف کردن، دو زمان با هم کار می‌کنند:\n"
      "**گذشته استمراری** پس‌زمینه را می‌سازد: It was raining…\n"
      "**گذشته ساده** اتفاق‌ها را پشت‌سرهم می‌آورد: …then a taxi stopped.\n"
      "بدون این ترکیب، روایت فهرست خشک اتفاق‌ها می‌شود.",
      "It was raining and we were waiting.\nThen a taxi stopped.\nWe got in and arrived on time.\nWhile she was talking, I realised the time.",
      "کدام برای پس‌زمینه درست است؟", "It was raining.",
      "It was raining. | It rained.",
      "It rained. ← گذشته ساده اتفاق تمام‌شده را می‌گوید، نه پس‌زمینه کشدار."),
     ("کلمات ترتیب روایت",
      "این‌ها ماجرا را مرتب می‌کنند:\n"
      "at the beginning · then · after that · suddenly · finally · in the end\n"
      "معمولاً اول جمله می‌آیند و بعدشان ویرگول:\n"
      "Suddenly, the door opened.\n"
      "«finally» یعنی «سرانجام»، نه «بالاخره از سر ناچاری».",
      "At the beginning, I was nervous.\nThen we went out.\nAfter that, we had dinner.\nSuddenly, it started to rain.\nFinally, the bus arrived.\nIn the end, everything was fine.",
      "کدام کلمه پایان ماجرا را نشان می‌دهد؟", "in the end",
      "in the end | at the beginning | suddenly",
      "at the beginning ← آغاز ماجرا را نشان می‌دهد.\n"
      "suddenly ← اتفاق ناگهانی وسط ماجرا را نشان می‌دهد.")],

 6: [("will برای تصمیم لحظه‌ای",
      "وقتی **همان لحظه** تصمیم می‌گیریم، will می‌آید:\n"
      "This bag is heavy. — I'll carry it.\n"
      "شکل کوتاه: I'll · she'll · we'll\n"
      "بعد از will فعل ساده می‌آید، بدون to و بدون s:\n"
      "نادرست: She will helps — درست: She will help",
      "I'll help you.\nI'll open the window.\nShe'll bring some tea.\nWe'll check the time.\nThey'll send the address.",
      "کدام درست است؟", "She will help you.",
      "She will help you. | She will helps you. | She will to help you.",
      "She will helps you. ← بعد از will فعل s نمی‌گیرد.\n"
      "She will to help you. ← بعد از will حرف to نمی‌آید."),
     ("تفاوت will و be going to",
      "**will** = تصمیم همین لحظه، قول، پیش‌بینی\n"
      "I'll help you. (همین حالا تصمیم گرفتم)\n"
      "**be going to** = برنامه‌ای که از قبل ریخته شده\n"
      "I'm going to study tonight. (قبلاً تصمیمش را گرفته بودم)\n"
      "منفی will: won't — I won't be late.",
      "I'll answer it. (تلفن زنگ می‌زند)\nI'm going to call her tonight. (برنامه داشتم)\nI won't forget.\nIt'll probably rain.",
      # سناریوی پرسش با مثال‌های بالای همان کارت یکی نیست؛ پیش‌تر هم
      # جمله و هم موقعیت فارسی‌اش عیناً در مثال‌ها بود و پاسخ لو می‌رفت.
      "کسی در می‌زند. کدام درست است؟", "I'll open the door.",
      "I'll open the door. | I'm going to open the door.",
      "I'm going to open the door. ← این برای برنامه از پیش تعیین‌شده است، نه تصمیم همین لحظه.")],

 7: [("حال استمراری برای قرار آینده",
      "وقتی قرار **قطعی** است و زمان و مکانش معلوم، حال استمراری به‌کار\n"
      "می‌رود — حتی اگر درباره آینده باشد:\n"
      "I'm meeting Sara at six. · We're having dinner on Friday.\n"
      "معمولاً یک قید زمان آینده کنارش می‌آید تا با «همین حالا» اشتباه نشود.\n"
      "تفاوتش با be going to: این برای قرارِ گذاشته‌شده است، آن برای قصد.",
      "I'm meeting Sara at six.\nWe're having dinner on Friday.\nShe's flying to Tehran tomorrow.\nThey're coming at the weekend.",
      "کدام برای قرار قطعی درست‌تر است؟", "I'm meeting her at six.",
      "I'm meeting her at six. | I meet her at six.",
      "I meet her at six. ← حال ساده برای کار همیشگی است، نه قرار مشخص آینده."),
     ("پرسیدن درباره برنامه",
      "رایج‌ترین ساختارها:\n"
      "What are you doing on Friday?\n"
      "Are you free at the weekend?\n"
      "What time works for you?\n"
      "برای پیشنهاد دادن: Let's … / How about …?\n"
      "Let's meet at six. · How about Saturday?",
      "What are you doing on Friday?\nAre you free at the weekend?\nWhat time works for you?\nLet's meet at six.\nHow about Saturday?",
      "کدام برای پیشنهاد دادن است؟", "Let's meet at six.",
      "Let's meet at six. | We met at six.",
      "We met at six. ← این جمله گذشته است و پیشنهاد نیست.")],

 8: [("may و might",
      "برای چیزی که **ممکن است** اتفاق بیفتد، ولی مطمئن نیستیم:\n"
      "It might rain. · She may be late.\n"
      "might کمی نامطمئن‌تر از may است، اما در گفتار روزمره تفاوتشان کم است.\n"
      "بعدشان فعل ساده می‌آید، بدون to:\n"
      "نادرست: It might to rain — درست: It might rain\n"
      "منفی: might not / may not",
      "It might rain tomorrow.\nShe may be at home.\nThey might not come.\nWe may need an umbrella.\nHe might know the answer.",
      "کدام درست است؟", "It might rain.",
      "It might rain. | It might to rain. | It mights rain.",
      "It might to rain. ← بعد از might حرف to نمی‌آید.\n"
      "It mights rain. ← فعل کمکی s نمی‌گیرد."),
     ("درجه اطمینان",
      "از مطمئن به نامطمئن:\n"
      "will (مطمئن) → probably will → may / might → probably won't → won't\n"
      "It will rain. (مطمئنم)\n"
      "It might rain. (شاید)\n"
      "It won't rain. (مطمئنم که نه)\n"
      "قید probably پیش از فعل اصلی و بعد از will می‌آید: It'll probably rain.",
      "It will rain tomorrow.\nIt'll probably rain.\nIt might rain.\nIt probably won't rain.\nIt won't rain.",
      "کدام کمترین اطمینان را نشان می‌دهد؟", "It might rain.",
      "It might rain. | It will rain.",
      "It will rain. ← این بیشترین اطمینان را نشان می‌دهد.")],

 9: [("شرطی نوع اول",
      "برای شرطی که **واقعاً ممکن است** پیش بیاید:\n"
      "if + حال ساده ، will + فعل ساده\n"
      "If it rains, we'll stay at home.\n"
      "⚠️ مهم‌ترین اشتباه: بعد از if فعل **آینده نمی‌آید**.\n"
      "نادرست: If it will rain — درست: If it rains\n"
      "دو نیمه جمله جابه‌جا هم می‌شوند؛ اگر if اول بیاید، ویرگول لازم است.",
      "If it rains, we'll stay at home.\nWe'll stay at home if it rains.\nIf we walk, we'll save money.\nIf you're late, we'll miss the bus.",
      "کدام درست است؟", "If it rains, we'll stay.",
      "If it rains, we'll stay. | If it will rain, we'll stay.",
      "If it will rain, we'll stay. ← بعد از if فعل حال ساده می‌آید، نه will."),
     ("unless و otherwise",
      "**unless** یعنی «اگر … نه». جمله بعدش مثبت می‌ماند:\n"
      "We'll go unless it rains. = We'll go if it doesn't rain.\n"
      "نادرست: unless it doesn't rain\n"
      "**otherwise** یعنی «وگرنه» و نتیجه ناخوشایند را می‌آورد:\n"
      "Hurry, otherwise we'll be late.",
      "We'll go unless it rains.\nUnless you hurry, you'll miss it.\nHurry, otherwise we'll be late.\nTake a map, otherwise you'll get lost.",
      "کدام درست است؟", "We'll go unless it rains.",
      "We'll go unless it rains. | We'll go unless it doesn't rain.",
      "We'll go unless it doesn't rain. ← unless خودش معنی منفی دارد و جمله دوباره منفی نمی‌شود.")],

 10:[("مرور آینده",
      "سه راه حرف زدن درباره آینده، و کاربرد هرکدام:\n"
      "**will** — تصمیم لحظه‌ای، قول، پیش‌بینی: I'll help you.\n"
      "**be going to** — برنامه از پیش ریخته: I'm going to study.\n"
      "**حال استمراری** — قرار قطعی با زمان مشخص: I'm meeting her at six.\n"
      "**may / might** — احتمال: It might rain.\n"
      "هر چهار مورد درست‌اند؛ انتخاب به اینکه چقدر قطعی است بستگی دارد.",
      "I'll help you. (تصمیم لحظه‌ای)\nI'm going to save money. (برنامه)\nWe're meeting at six. (قرار)\nIt might rain. (احتمال)",
      "کدام برای قرار قطعی ساعت شش درست است؟", "We're meeting at six.",
      "We're meeting at six. | We'll meet at six maybe.",
      "We'll meet at six maybe. ← این نامطمئن است، در حالی که قرار قطعی گذاشته شده."),
     ("پیشنهاد دادن و موافقت",
      "پیشنهاد: I suggest Friday. · How about Saturday? · Let's share the cost.\n"
      "موافقت: I agree. · That's a great idea. · Fine.\n"
      "مخالفت مؤدبانه: I disagree. · I prefer the morning.\n"
      "بعد از suggest و prefer، اسم یا فعل با ing می‌آید:\n"
      "I prefer walking. (نه I prefer to walking)",
      "I suggest Friday.\nHow about Saturday?\nI agree with you.\nI prefer the morning.\nLet's share the cost.",
      "کدام درست است؟", "I prefer walking.",
      "I prefer walking. | I prefer to walking.",
      "I prefer to walking. ← بعد از to فعل ساده می‌آید، نه ing.")],
}

EX = {
 4: [(R,"چندگزینه‌ای","I ___ cooking when she called.","was","","was | were | am","","با I فعل was می‌آید."),
     (R,"تطبیق","suddenly","ناگهان","","suddenly | luckily | careful","suddenly","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","They ___ waiting for the bus. (گذشته)","were","","","wait for","با they فعل were می‌آید."),
     (P,"متن آزاد","درست کنید: I was cook dinner.","I was cooking dinner.","","","break","بعد از was فعل ing می‌گیرد."),
     (P,"متن آزاد","با while بنویسید: I was cooking. She called.","While I was cooking, she called.","","","while","while پیش از کار کشدار می‌آید."),
     (P,"مرتب‌سازی","raining / it / hard / was","It was raining hard.","","","rain","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","وقتی زنگ زد داشتم آشپزی می‌کردم.","I was cooking when she called.","I was cooking when he called.","","while","کار کشدار استمراری و کار کوتاه ساده است."),
     (P,"گفتار","Be careful on the stairs.","Be careful on the stairs.","","","careful","جمله را واضح تلفظ کن.")],

 5: [(R,"چندگزینه‌ای","___ , the bus arrived.","Finally","","Finally | Suddenly | Then","finally","«سرانجام» پایان ماجرا را نشان می‌دهد."),
     (R,"تطبیق","nervous","مضطرب","","nervous | excited | strange","nervous","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","We ___ to walk. (decide)","decided","","","decide","گذشته ساده با ed ساخته می‌شود."),
     (P,"متن آزاد","پس‌زمینه بسازید: rain (گذشته استمراری)","It was raining.","","","rain","پس‌زمینه با گذشته استمراری می‌آید."),
     (P,"متن آزاد","کامل کنید: ___ the end, everything was fine.","In","","","end","«در پایان» یعنی in the end."),
     (P,"مرتب‌سازی","everything / she / explained","She explained everything.","","","explain","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","متوجه شدم دیرم شده.","I realised I was late.","","","realise","گذشته realise با ed ساخته می‌شود."),
     (P,"گفتار","In the end, everything was fine.","In the end, everything was fine.","","","end","جمله را واضح تلفظ کن.")],

 6: [(R,"چندگزینه‌ای","This is heavy. — I ___ carry it.","will","","will | am going to | was","carry","تصمیم همین لحظه با will می‌آید."),
     (R,"تطبیق","favour","لطف","","favour | help | promise","favour","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","I ___ send you the address.","will","'ll","","send","برای قول دادن will می‌آید."),
     (P,"متن آزاد","درست کنید: She will helps you.","She will help you.","","","help","بعد از will فعل s نمی‌گیرد."),
     (P,"متن آزاد","منفی کنید: I will forget.","I won't forget.","I will not forget.","","sure","منفی will همان won't است."),
     (P,"مرتب‌سازی","tea / bring / some / I'll","I'll bring some tea.","","","bring","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","بعداً بهت زنگ می‌زنم.","I'll call you later.","I will call you later.","","later","تصمیم لحظه‌ای با will بیان می‌شود."),
     (P,"گفتار","Of course I'll help you.","Of course I'll help you.","","","of course","جمله را واضح تلفظ کن.")],

 7: [(R,"چندگزینه‌ای","I ___ Sara at six tomorrow.","'m meeting","","'m meeting | meet | met","meeting","قرار قطعی با حال استمراری بیان می‌شود."),
     (R,"تطبیق","guest","مهمان","","guest | party | plan","guest","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","We ___ having dinner on Friday.","are","'re","","dinner","با we فعل are می‌آید."),
     (P,"متن آزاد","سوال بسازید: I'm free on Friday.","Are you free on Friday?","","","free","فعل to be به ابتدای جمله می‌رود."),
     (P,"متن آزاد","پیشنهاد بدهید با Let's: meet at six","Let's meet at six.","","","time","بعد از Let's فعل ساده می‌آید."),
     (P,"مرتب‌سازی","table / booking / a / I'm","I'm booking a table.","","","book","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","آخر هفته آزادی؟","Are you free at the weekend?","","","weekend","با فعل to be شروع کن."),
     (P,"گفتار","Please confirm the time tomorrow.","Please confirm the time tomorrow.","","","confirm","جمله را واضح تلفظ کن.")],

 8: [(R,"چندگزینه‌ای","It ___ rain tomorrow.","might","","might | might to | mights","might","بعد از might فعل ساده می‌آید."),
     (R,"تطبیق","forecast","پیش‌بینی هوا","","forecast | storm | delay","forecast","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","She ___ be at home. (احتمال)","may","might","","possible","برای احتمال may یا might می‌آید."),
     (P,"متن آزاد","درست کنید: It might to snow.","It might snow.","","","weather","بعد از might حرف to نمی‌آید."),
     (P,"متن آزاد","منفی کنید: They might come.","They might not come.","","","expect","منفی might همان might not است."),
     (P,"مرتب‌سازی","case / an / take / just / umbrella / in","Take an umbrella just in case.","","","just in case","جمله امری با فعل شروع می‌شود."),
     (P,"ترجمه به انگلیسی","ممکن است تأخیر باشد.","There might be a delay.","","","delay","برای «هست» از there is استفاده کن."),
     (P,"گفتار","There's a chance of rain today.","There's a chance of rain today.","","","chance","جمله را واضح تلفظ کن.")],

 9: [(R,"چندگزینه‌ای","If it ___ , we'll stay at home.","rains","","rains | will rain | rained","if","بعد از if فعل حال ساده می‌آید."),
     (R,"تطبیق","decision","تصمیم","","decision | choice | reason","decision","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","If we walk, we ___ save money.","will","'ll","","save","نیمه دوم شرطی نوع اول با will می‌آید."),
     (P,"متن آزاد","درست کنید: If it will rain, we'll stay.","If it rains, we'll stay.","","","stay","بعد از if فعل حال ساده می‌آید."),
     (P,"متن آزاد","با unless بنویسید: We'll go if it doesn't rain.","We'll go unless it rains.","","","unless","unless خودش معنی منفی دارد."),
     (P,"مرتب‌سازی","late / hurry / otherwise / we'll / be","Hurry, otherwise we'll be late.","","","otherwise","جمله امری اول می‌آید."),
     (P,"ترجمه به انگلیسی","اگر دیر کنیم، از دستش می‌دهیم.","If we're late, we'll miss it.","","","miss","شرطی نوع اول: if + حال ساده، will + فعل ساده."),
     (P,"گفتار","If it rains, we'll stay at home.","If it rains, we'll stay at home.","","","if","جمله را واضح تلفظ کن.")],

 10:[(R,"چندگزینه‌ای","I ___ the morning.","prefer","","prefer | prefers | preferring","prefer","با I فعل بدون s می‌آید."),
     (R,"تطبیق","budget","بودجه","","budget | option | schedule","budget","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","I ___ Friday morning. (پیشنهاد می‌کنم)","suggest","","","suggest","«پیشنهاد کردن» یعنی suggest."),
     (P,"متن آزاد","درست کنید: I prefer to walking.","I prefer walking.","I prefer to walk.","","prefer","بعد از to فعل ساده می‌آید، نه ing."),
     (P,"متن آزاد","با Let's بنویسید: share the cost","Let's share the cost.","","","share","بعد از Let's فعل ساده می‌آید."),
     (P,"مرتب‌سازی","idea / great / a / that's","That's a great idea.","","","idea","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","چه کسی مسئول غذاست؟","Who is responsible for food?","Who's responsible for food?","","responsible","با کلمه پرسشی شروع کن."),
     (P,"گفتار","Let's plan the trip together.","Let's plan the trip together.","","","together","جمله را واضح تلفظ کن.")],
}
