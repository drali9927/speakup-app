# -*- coding: utf-8 -*-
"""مکالمه، گرامر و تمرین دروس ۱ تا ۱۰ سطح B1."""

P, R = "تولیدی", "تشخیصی"

DLG = {
 1: [("A","How long have you been working here?","چه مدت است اینجا کار می‌کنی؟"),
     ("B","I've been working here for three years.","سه سال است اینجا کار می‌کنم."),
     ("A","Has the workload increased?","حجم کار زیاد شده؟"),
     ("B","Yes, I've been doing overtime all month.","بله، تمام ماه اضافه‌کاری کرده‌ام."),
     ("A","Are you in charge of the new project?","مسئول پروژه تازه‌ای؟"),
     ("B","I am. It's demanding, but rewarding.","بله. سخت است، اما رضایت‌بخش.")],
 2: [("A","Has the report been checked?","گزارش بررسی شده؟"),
     ("B","It's been checked twice.","دو بار بررسی شده."),
     ("A","And the delivery?","و تحویل؟"),
     ("B","It's been delayed by the supplier.","تأمین‌کننده به تأخیر انداخته."),
     ("A","Was a new procedure set up?","روال تازه‌ای راه‌اندازی شد؟"),
     ("B","Yes, and the work is still in progress.","بله، و کار هنوز در جریان است.")],
 3: [("A","Are you used to the new office yet?","به دفتر تازه عادت کرده‌ای؟"),
     ("B","Not completely. At first it felt strange.","نه کاملاً. اول غریب بود."),
     ("A","It takes time to get used to it.","عادت کردن به آن زمان می‌برد."),
     ("B","True. But I'm getting used to the routine.","درست است. اما دارم به روال عادت می‌کنم."),
     ("A","Your confidence has grown.","اعتماد به نفست بیشتر شده."),
     ("B","Gradually. It's no longer difficult.","به‌تدریج. دیگر سخت نیست.")],
 4: [("A","Why didn't you see her?","چرا ندیدیش؟"),
     ("B","By the time I arrived, she had already gone.","تا وقتی رسیدم، رفته بود."),
     ("A","Did you know about the change?","از تغییر خبر داشتی؟"),
     ("B","No. I realised I had forgotten the message.","نه. فهمیدم پیام را فراموش کرده بودم."),
     ("A","That's a misunderstanding, not a mistake.","این سوءتفاهم است، نه اشتباه."),
     ("B","Fortunately, nothing was lost in the end.","خوشبختانه در پایان چیزی از دست نرفت.")],
 5: [("A","Can you describe what happened?","می‌توانی توصیف کنی چه شد؟"),
     ("B","We were walking home. Suddenly the lights went out.","داشتیم پیاده می‌رفتیم. ناگهان چراغ‌ها خاموش شد."),
     ("A","Was there a witness?","شاهدی بود؟"),
     ("B","One. But he told a different version.","یکی. اما روایت متفاوتی گفت."),
     ("A","Apparently nobody knew the cause.","ظاهراً کسی علتش را نمی‌دانست."),
     ("B","Looking back, it wasn't very dramatic.","به گذشته که نگاه می‌کنم خیلی پرهیجان نبود.")],
 6: [("A","We're behind schedule.","از برنامه عقبیم."),
     ("B","I know. We were stuck in a traffic jam.","می‌دانم. در راه‌بندان گیر کرده بودیم."),
     ("A","By the time you arrived, the meeting had ended.","تا وقتی رسیدی، جلسه تمام شده بود."),
     ("B","I've arranged another time.","وقت دیگری ترتیب داده‌ام."),
     ("A","Book the room in advance this time.","این بار اتاق را از پیش رزرو کن."),
     ("B","Agreed. It's urgent now.","موافقم. حالا فوری است.")],
 7: [("A","What did he say exactly?","دقیقاً چه گفت؟"),
     ("B","He claimed he had finished it.","ادعا کرد تمامش کرده بود."),
     ("A","And did he admit the delay?","و تأخیر را پذیرفت؟"),
     ("B","No, he denied everything.","نه، همه‌چیز را انکار کرد."),
     ("A","Is there any evidence?","شاهدی هست؟"),
     ("B","According to the report, no. It was misleading.","به گفته گزارش، نه. گمراه‌کننده بود.")],
 8: [("A","What would you do with a free year?","با یک سال آزاد چه می‌کردی؟"),
     ("B","If I had a free year, I'd travel.","اگر یک سال آزاد داشتم سفر می‌کردم."),
     ("A","Even if you couldn't afford it?","حتی اگر توانش را نداشتی؟"),
     ("B","If I were wealthy, I'd help others too.","اگر ثروتمند بودم به بقیه هم کمک می‌کردم."),
     ("A","Let's be realistic.","بیا واقع‌بین باشیم."),
     ("B","If I were in your place, I'd take the risk.","جای تو بودم ریسک می‌کردم.")],
 9: [("A","You look tired.","خسته به‌نظر می‌رسی."),
     ("B","I wish I had more time.","کاش وقت بیشتری داشتم."),
     ("A","Do you miss your old team?","دلتنگ تیم قدیمت هستی؟"),
     ("B","I do. The first month was lonely.","بله. ماه اول تنها بودم."),
     ("A","You should be proud of the work.","باید به کارت افتخار کنی."),
     ("B","I am. It's time to move on.","هستم. وقتش است از آن بگذرم.")],
 10:[("A","We have three options.","سه گزینه داریم."),
     ("B","Let's weigh up the advantages.","بیا مزیت‌ها را سبک و سنگین کنیم."),
     ("A","The first is risky but fast.","اولی پرخطر است اما سریع."),
     ("B","In theory it works. In practice, it's harder.","در تئوری جواب می‌دهد. در عمل سخت‌تر است."),
     ("A","So we need a compromise.","پس به سازش نیاز داریم."),
     ("B","Either way, we'll manage.","در هر صورت از پسش برمی‌آییم.")],
}

GR = {
 1: [("حال کامل استمراری",
      "این زمان برای کاری است که در گذشته شروع شده، تا همین حالا ادامه دارد و آنچه برایت مهم است خودِ ادامه داشتن آن است.\n"
      "ساختش ساده است: have یا has می‌آوری، بعد **been**، و بعد فعل با ing. مثلاً وقتی می‌گویی I've **been working** here for three years، یعنی سه سال است که پیوسته مشغول کارم.\n"
      "تفاوتش با حال کامل ساده در همین تأکید است. جمله I've worked here می‌گوید سابقه این کار را دارم و نتیجه‌اش هست، اما I've been working here می‌گوید مدتی است بی‌وقفه مشغولم.\n"
      "پس اگر می‌خواهی روی مدت زمان انگشت بگذاری، شکل استمراری را انتخاب کن.",
      "I've been working here for three years.\n"
      "She's been dealing with it all week.\n"
      "They've been waiting since morning.\n"
      "It's been raining all day.",
      "کدام درست است؟", "I've been working here.",
      "I've been working here. | I've been work here. | I'm working here for three years.",
      "I've been work here. ← بعد از been فعل باید ing بگیرد.\n"
      "I'm working here for three years. ← برای مدتی که تا حالا ادامه دارد، حال استمراری کافی نیست."),
     ("افعالی که استمراری نمی‌شوند",
      "بعضی فعل‌ها حالت را نشان می‌دهند نه عملی که انجام می‌شود، و به همین دلیل ing نمی‌گیرند.\n"
      "مشهورترین‌هایشان know و believe و understand و like و want و need و belong هستند.\n"
      "پس نمی‌توانی بگویی I've been knowing him for years؛ شکل درست I've **known** him for years است.\n"
      "قاعده‌اش را ساده به خاطر بسپار: با این فعل‌ها همیشه حال کامل ساده می‌آید و هرگز شکل استمراری.",
      "I've known him for years.\n"
      "I've understood it from the start.\n"
      "She's wanted this for a long time.\n"
      "We've needed help since May.",
      "کدام درست است؟", "I've known him for years.",
      "I've known him for years. | I've been knowing him for years.",
      "I've been knowing him for years. ← know فعل حالت است و استمراری نمی‌شود.")],

 2: [("مجهول با زمان‌های کامل",
      "قاعده مجهول در هر زمانی یکی است و هرگز عوض نمی‌شود: **be + قسمت سوم فعل**.\n"
      "آنچه فرق می‌کند فقط زمانِ خودِ be است، نه چیز دیگر.\n"
      "در حال کامل می‌گویی It **has been** checked، در گذشته کامل It **had been** checked، برای آینده It **will be** checked، و بعد از یک فعل مدال It **must be** checked.\n"
      "همین که یاد بگیری زمان روی be می‌نشیند، ساختن مجهول در هر زمانی برایت آسان می‌شود.",
      "The report has been checked.\n"
      "The part had been replaced.\n"
      "The form will be completed tomorrow.\n"
      "Everything must be inspected.",
      "کدام درست است؟", "It has been checked.",
      "It has been checked. | It has checked. | It has been check.",
      "It has checked. ← فعل be در مجهول حذف نمی‌شود.\n"
      "It has been check. ← بعد از been قسمت سوم فعل می‌آید."),
     ("چرا مجهول در متن رسمی",
      "در گزارش و دستورالعمل معمولاً مهم نیست چه کسی کار را انجام داده، و برای همین مجهول طبیعی‌تر از معلوم است.\n"
      "جمله The system was updated last night روان‌تر از Someone updated the system last night به نظر می‌رسد، چون شنونده دنبال آن «کسی» نیست.\n"
      "اما اگر کننده کار واقعاً مهم باشد، با by می‌آوری‌اش: The delivery has been delayed **by the supplier**.\n"
      "یک هشدار هم بده به خودت. اگر همه‌جا مجهول بنویسی متن سنگین و خشک می‌شود، پس فقط جایی از آن استفاده کن که فاعل به‌راستی بی‌اهمیت است.",
      "The system was updated last night.\n"
      "The delivery has been delayed by the supplier.\n"
      "The test was carried out yesterday.\n"
      "The request was rejected.",
      "کدام درست است؟", "It was delayed by the supplier.",
      "It was delayed by the supplier. | It was delayed from the supplier.",
      "It was delayed from the supplier. ← کننده کار با by می‌آید نه from.")],

 3: [("be used to و get used to",
      "این دو عبارت را با used to اشتباه نگیر، چون آن یکی درباره عادت گذشته است و این‌ها درباره حالا.\n"
      "وقتی می‌گویی **be used to**، یعنی همین حالا عادت داری، مثل I'm used to the noise.\n"
      "وقتی می‌گویی **get used to**، یعنی داری کم‌کم عادت می‌کنی، مثل I'm getting used to it.\n"
      "نکته‌ای که بیشترین اشتباه را می‌سازد این است که بعد از هر دو، اسم یا فعل با ing می‌آید و نه فعل ساده. پس درست این است که بگویی I'm used to **working** late و نه to work.\n"
      "دلیلش هم روشن است: to در این عبارت‌ها حرف اضافه است، نه نشانه مصدر.",
      "I'm used to the noise.\n"
      "I'm getting used to the routine.\n"
      "She's used to working late.\n"
      "He isn't used to the weather yet.",
      "کدام درست است؟", "I'm used to working late.",
      "I'm used to working late. | I'm used to work late.",
      "I'm used to work late. ← بعد از used to فعل ing می‌گیرد."),
     ("used to برای عادت گذشته",
      "ساختار **used to + فعل ساده** برای کاری است که در گذشته عادتت بود و حالا دیگر نیست، مثل I **used to** walk to school.\n"
      "در منفی و پرسش، did کار زمان را می‌کند و خودِ use دیگر d نمی‌گیرد: I **didn't use to** like it و **Did you use to** live here؟\n"
      "اگر بین این دو ساختار گیر کردی، به آنچه بعدش می‌آید نگاه کن. used to **walk** یعنی عادت گذشته، اما used to **walking** یعنی عادت داشتن در حال.",
      "I used to walk to school.\n"
      "I didn't use to like coffee.\n"
      "Did you use to live here?\n"
      "I'm used to walking now.",
      "کدام عادت گذشته را نشان می‌دهد؟", "I used to walk to school.",
      "I used to walk to school. | I'm used to walking to school.",
      "I'm used to walking to school. ← این یعنی الان عادت دارم، نه اینکه در گذشته بود.")],

 4: [("گذشته کامل",
      "گذشته کامل برای کاری است که پیش از یک کار گذشته دیگر تمام شده بود، و ساختش had به‌علاوه قسمت سوم فعل است.\n"
      "مثلاً در جمله When I arrived, she **had** already **gone** دو نقطه در گذشته داری: رفتنِ او زودتر بوده و رسیدنِ تو دیرتر.\n"
      "حواست باشد که اگر ترتیب رویدادها از خود جمله روشن است، گذشته ساده هم کافی است و لازم نیست کار را پیچیده کنی.\n"
      "گذشته کامل درست وقتی به کار می‌آید که بخواهی نشان دهی کدام اتفاق زودتر رخ داده است.",
      "When I arrived, she had already gone.\n"
      "I realised I had forgotten it.\n"
      "We had previously agreed.\n"
      "They had left before the rain started.",
      "کدام درست است؟", "She had gone before I arrived.",
      "She had gone before I arrived. | She had went before I arrived.",
      "She had went before I arrived. ← بعد از had قسمت سوم فعل می‌آید، نه گذشته ساده."),
     ("گذشته کامل یا گذشته ساده",
      "این دو جمله شبیه هم‌اند اما دو چیز متفاوت می‌گویند.\n"
      "وقتی می‌گویی When I arrived, she **left**، یعنی اول تو رسیدی و بعد او رفت.\n"
      "اما وقتی می‌گویی When I arrived, she **had left**، یعنی او پیش‌تر رفته بود و تو او را ندیدی.\n"
      "چند کلمه هم هستند که معمولاً کنار گذشته کامل می‌نشینند و دیدنشان نشانه خوبی است: already و just و never و before و by the time و after.",
      "When I arrived, she left.\n"
      "When I arrived, she had left.\n"
      "By the time we came, it had closed.\n"
      "I had never seen it before.",
      "«زودتر رفته بود» کدام است؟", "When I arrived, she had left.",
      "When I arrived, she had left. | When I arrived, she left.",
      "When I arrived, she left. ← این یعنی بعد از رسیدن من رفت.")],

 5: [("سه زمان در یک روایت",
      "یک روایت خوب معمولاً سه لایه دارد و هر لایه زمان خودش را می‌خواهد.\n"
      "پس‌زمینه را با گذشته استمراری می‌سازی، مثل We were walking home.\n"
      "اتفاق‌های اصلی را با گذشته ساده می‌گویی، مثل suddenly the lights went out.\n"
      "و آنچه پیش‌تر از همه رخ داده بود را با گذشته کامل می‌آوری، مثل The power had failed earlier.\n"
      "اگر همه‌چیز را فقط با گذشته ساده بنویسی، روایتت به یک فهرست خشک تبدیل می‌شود و آن حسِ داستان را از دست می‌دهد.",
      "We were walking home when the lights went out.\n"
      "The power had failed earlier.\n"
      "Afterwards we went home.\n"
      "Meanwhile, we waited outside.",
      "کدام پس‌زمینه است؟", "We were walking home.",
      "We were walking home. | We walked home.",
      "We walked home. ← این یک اتفاق تمام‌شده است و پس‌زمینه نمی‌سازد."),
     ("عبارت‌های نرم‌کننده",
      "در روایت واقعی، فارسی‌زبان‌ها معمولاً همین عبارت‌های کوتاه را کم می‌آورند، در حالی که همین‌ها لحن را طبیعی می‌کنند.\n"
      "وقتی می‌خواهی رُک باشی می‌گویی **To be honest**, I was scared.\n"
      "وقتی مطمئن نیستی می‌گویی **As far as I know**, nobody saw it.\n"
      "برای گفتن نظر خودت **From my point of view**, it was fair را داری، و برای نگاه به گذشته **Looking back**, it was funny.\n"
      "همه این‌ها ابتدای جمله می‌آیند و بعدشان ویرگول می‌گذاری.",
      "To be honest, I was scared.\n"
      "As far as I know, it's fine.\n"
      "From my point of view, it was fair.\n"
      "Looking back, it was funny.",
      "کدام درست است؟", "To be honest, I was scared.",
      "To be honest, I was scared. | To honest, I was scared.",
      "To honest, I was scared. ← عبارت ثابت To be honest است.")],

 6: [("by the time",
      "ساختار **by the time** با گذشته ساده می‌آید و نیمه دوم جمله گذشته کامل می‌گیرد.\n"
      "مثلاً **By the time** we arrived, it **had closed** یعنی تا وقتی ما رسیدیم، آنجا پیش‌تر بسته شده بود.\n"
      "اشتباه رایج این است که بعد از by the time فعل آینده بیاورند، اما By the time we will arrive نادرست است.\n"
      "برای آینده باید بگویی By the time we **arrive**, it **will have closed**؛ یعنی نیمه اول حال ساده می‌ماند و آینده در نیمه دوم نشان داده می‌شود.",
      "By the time we arrived, it had closed.\n"
      "By the time you read this, I'll have left.\n"
      "By the time she called, we had finished.",
      "کدام درست است؟", "By the time we arrived, it had closed.",
      "By the time we arrived, it had closed. | By the time we will arrive, it closed.",
      "By the time we will arrive, it closed. ← بعد از by the time فعل آینده نمی‌آید."),
     ("as soon as و until",
      "عبارت **as soon as** یعنی به‌محض اینکه، و برای کاری است که بلافاصله بعد از کار دیگری می‌آید، مثل As soon as I heard, I called.\n"
      "عبارت **until** یعنی تا اینکه، و پایان یک وضعیت را نشان می‌دهد، مثل We waited until it stopped.\n"
      "نکته مشترکشان این است که وقتی جمله درباره آینده باشد، هر دو فعل حال ساده می‌گیرند و نه آینده.\n"
      "پس درست این است که بگویی I'll call you as soon as I **arrive** و نه as soon as I will arrive.",
      "As soon as I heard, I called.\n"
      "We waited until it stopped.\n"
      "I'll call as soon as I arrive.\n"
      "Stay here until I come back.",
      "کدام درست است؟", "I'll call as soon as I arrive.",
      "I'll call as soon as I arrive. | I'll call as soon as I will arrive.",
      "I'll call as soon as I will arrive. ← بعد از as soon as فعل حال ساده می‌آید.")],

 7: [("نقل قول با گذشته کامل",
      "وقتی حرفی را بازگو می‌کنی که خودش درباره گذشته بوده، فعل یک پله عقب‌تر می‌رود و به گذشته کامل می‌رسد.\n"
      "مثلاً «I finished it» در نقل قول می‌شود He said he **had finished** it، و «I have seen it» می‌شود She said she **had seen** it.\n"
      "نکته آرامش‌بخش این است که گذشته کامل دیگر جایی برای عقب رفتن ندارد و همان‌جا می‌ماند، پس لازم نیست دنبال زمانِ عقب‌ترش بگردی.",
      "He said he had finished it.\n"
      "She said she had seen it before.\n"
      "They said they had already left.\n"
      "He claimed he had paid.",
      "«I finished it» بازگو شود:", "He said he had finished it.",
      "He said he had finished it. | He said he has finished it.",
      "He said he has finished it. ← در نقل قولِ گذشته، فعل به گذشته کامل می‌رود."),
     ("افعال گزارشی و بار معنایی",
      "فعلی که برای گزارش کردن انتخاب می‌کنی، نظر تو را هم درباره آن حرف منتقل می‌کند و بی‌طرف نیست.\n"
      "فعل **said** خنثی است، اما **claimed** یعنی ادعا کرد و شاید حرفش درست نباشد.\n"
      "با **admitted** می‌گویی اعتراف کرد و با **denied** می‌گویی انکار کرد، در حالی که **insisted** اصرار کردن است و **confirmed** تأیید کردن.\n"
      "یک نکته ساختاری هم هست: بعد از deny فعل با ing می‌آید، پس می‌گویی He denied **taking** it.",
      "He claimed he had finished.\n"
      "She admitted she had forgotten.\n"
      "He denied taking it.\n"
      "She confirmed she had sent it.",
      "کدام درست است؟", "He denied taking it.",
      "He denied taking it. | He denied to take it.",
      "He denied to take it. ← بعد از deny فعل ing می‌آید.")],

 8: [("شرطی نوع دوم",
      "شرطی نوع دوم برای موقعیتی است که همین حالا فرضی و غیرواقعی است، و ساختش این است که بعد از if گذشته ساده بیاوری و در نیمه دیگر **would** به‌علاوه فعل ساده.\n"
      "مثلاً If I **had** more time, I **would** travel یعنی وقت ندارم، ولی دارم فرض می‌کنم که داشتم.\n"
      "حواست باشد این گذشته، گذشته واقعی نیست و فقط نشانه فرضی بودن جمله است.\n"
      "تفاوتش با شرطی اول را از همین‌جا بفهم: If I have time, I'll travel یعنی شاید وقت داشته باشم، اما If I had time, I'd travel یعنی ندارم.",
      "If I had more time, I would travel.\n"
      "If I lived abroad, I'd travel more.\n"
      "If we had a car, we'd go today.\n"
      "What would you do?",
      "کدام درست است؟", "If I had time, I would travel.",
      "If I had time, I would travel. | If I would have time, I would travel.",
      "If I would have time, I would travel. ← بعد از if فعل would نمی‌آید."),
     ("If I were",
      "در شرطی نوع دوم، حتی با I و he و she هم **were** می‌آید و نه was.\n"
      "پس می‌گویی If I **were** you, I'd wait و می‌گویی If she **were** here, she'd help.\n"
      "در گفتار روزمره was هم شنیده می‌شود، اما were درست‌تر است و در نوشتار رسمی همان را به کار ببر.\n"
      "یک عبارت آماده و پرکاربرد هم داری که همیشه با were می‌آید: **If I were in your place…**",
      "If I were you, I'd wait.\n"
      "If I were wealthy, I'd help others.\n"
      "If she were here, she'd know.\n"
      "If I were in your place, I'd accept.",
      "کدام درست است؟", "If I were you, I'd wait.",
      "If I were you, I'd wait. | If I am you, I'd wait.",
      "If I am you, I'd wait. ← در شرطی دوم فعل به گذشته می‌رود و were می‌آید.")],

 9: [("wish + گذشته",
      "برای آرزوی چیزی که همین حالا نیست، بعد از wish فعل گذشته می‌آوری.\n"
      "پس I wish I **knew** the answer یعنی نمی‌دانم، و I wish I **had** more time یعنی وقت ندارم.\n"
      "اگر افسوس گذشته را می‌خواهی بگویی، یک پله عقب‌تر می‌روی: I wish I **had taken** his advice یعنی نگرفتم و حالا پشیمانم.\n"
      "اشتباهی که باید از آن دوری کنی این است که بعد از wish فعل حال بیاوری؛ جمله I wish I know نادرست است.",
      "I wish I knew the answer.\n"
      "I wish I had more time.\n"
      "I wish I had taken his advice.\n"
      "I wish it were easier.",
      "کدام درست است؟", "I wish I knew the answer.",
      "I wish I knew the answer. | I wish I know the answer.",
      "I wish I know the answer. ← بعد از wish فعل به گذشته می‌رود."),
     ("if only و بیان احساس",
      "عبارت **if only** همان کار wish را می‌کند اما احساس را پررنگ‌تر نشان می‌دهد، مثل **If only** I had more time!\n"
      "برای گفتن حال خودت هم به چند ساختار ثابت نیاز داری. با I feel **disappointed / relieved / frustrated** حالت را می‌گویی.\n"
      "برای افتخار و سپاس می‌گویی I'm **proud of** the team و I'm **grateful for** the help.\n"
      "حرف اضافه در این‌ها ثابت است و عوض کردنش جمله را غلط می‌کند، پس همیشه proud **of** و grateful **for** بماند.",
      "If only I had more time!\n"
      "I feel disappointed.\n"
      "I'm proud of the team.\n"
      "I'm grateful for the help.",
      "کدام درست است؟", "I'm proud of the team.",
      "I'm proud of the team. | I'm proud for the team.",
      "I'm proud for the team. ← حرف اضافه ثابت proud of است.")],

 10:[("مرور شرطی دوم",
      "شرطی دوم چند شکل دارد که همه یک چیز می‌گویند.\n"
      "ساده‌ترینش **If** I had time, I'd help است.\n"
      "برای پرسیدن، **What would you do if** you had a free year? را داری.\n"
      "و وقتی می‌خواهی خودت را جای کسی بگذاری، **If I were in your place**, I'd wait به کار می‌آید.\n"
      "حتی می‌توانی نتیجه را بدون if بگویی، مثل **Otherwise**, we'd be late.\n"
      "زیر همه این شکل‌ها یک قاعده خوابیده است: نیمه if گذشته می‌گیرد و نیمه دیگر would.",
      "If I had time, I'd help.\n"
      "What would you do if you won?\n"
      "If I were in your place, I'd wait.\n"
      "Otherwise, we'd be late.",
      "کدام درست است؟", "What would you do if you won?",
      "What would you do if you won? | What would you do if you would win?",
      "What would you do if you would win? ← بعد از if فعل would نمی‌آید."),
     ("سنجیدن گزینه‌ها",
      "چند عبارت هست که بحث درباره یک تصمیم را حرفه‌ای می‌کند و بدون آن‌ها حرفت ساده به نظر می‌رسد.\n"
      "برای شمردن خوبی و بدی می‌گویی The main **advantage** is speed و The **disadvantage** is cost.\n"
      "وقتی جواب قطعی نداری می‌گویی It **depends on** the budget.\n"
      "برای جدا کردن نظر از واقعیت، **In theory** it works, but **in practice** it's harder را داری.\n"
      "و برای جمع‌بندی، Let's **weigh up** the options و **Either way**, we'll manage به کارت می‌آید.",
      "The main advantage is speed.\n"
      "It depends on the budget.\n"
      "In theory it works.\n"
      "Either way, we'll manage.",
      "کدام درست است؟", "It depends on the budget.",
      "It depends on the budget. | It depends to the budget.",
      "It depends to the budget. ← حرف اضافه ثابت depend on است.")],
}

EX = {
 1: [(R,"چندگزینه‌ای","I ___ here for three years.","have been working","","have been working | am working | have been work","so far","حال کامل استمراری با have been + ing ساخته می‌شود."),
     (R,"تطبیق","responsibility","مسئولیت","","responsibility | workload | achievement","responsibility","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","She's been ___ with it all week. (deal)","dealing","","","deal with","بعد از been فعل ing می‌گیرد."),
     (P,"متن آزاد","درست کنید: I've been knowing him for years.","I've known him for years.","","","manage","know فعل حالت است و استمراری نمی‌شود."),
     (P,"متن آزاد","سوال بسازید: I've been working here for three years.","How long have you been working here?","","","deadline","با How long شروع کن."),
     (P,"مرتب‌سازی","overtime / been / doing / I've","I've been doing overtime.","","","overtime","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","تمام هفته به این رسیدگی کرده‌ام.","I've been dealing with this all week.","","","deal with","حال کامل استمراری بساز."),
     (P,"گفتار","It's demanding, but rewarding.","It's demanding, but rewarding.","","","rewarding","جمله را واضح تلفظ کن.")],

 2: [(R,"چندگزینه‌ای","The report ___ checked.","has been","","has been | has | has been being","procedure","مجهول کامل با has been + قسمت سوم ساخته می‌شود."),
     (R,"تطبیق","supplier","تأمین‌کننده","","supplier | delivery | approval","supplier","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","The part ___ been replaced.","has","","","replace","برای فاعل مفرد has می‌آید."),
     (P,"متن آزاد","درست کنید: It has been check twice.","It has been checked twice.","","","inspect","بعد از been قسمت سوم فعل می‌آید."),
     (P,"متن آزاد","مجهول کنید: They carried out the test yesterday.","The test was carried out yesterday.","","","carry out","مفعول به ابتدای جمله می‌رود."),
     (P,"مرتب‌سازی","approved / plan / was / the","The plan was approved.","","","approve","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","تحویل به تأخیر افتاده است.","The delivery has been delayed.","","","delivery","مجهول کامل بساز."),
     (P,"گفتار","The work is still in progress.","The work is still in progress.","","","in progress","جمله را واضح تلفظ کن.")],

 3: [(R,"چندگزینه‌ای","I'm used to ___ late.","working","","working | work | to work","be used to","بعد از used to فعل ing می‌گیرد."),
     (R,"تطبیق","attitude","نگرش","","attitude | patience | confidence","attitude","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","I'm getting ___ to the routine.","used","","","get used to","ساختار get used to است."),
     (P,"متن آزاد","درست کنید: I'm used to work late.","I'm used to working late.","","","be used to","بعد از used to فعل ing می‌گیرد."),
     (P,"متن آزاد","با used to بنویسید (عادت گذشته): I / walk / to school","I used to walk to school.","","","routine","used to + فعل ساده برای عادت گذشته."),
     (P,"مرتب‌سازی","difficult / longer / it's / no","It's no longer difficult.","","","no longer","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","به‌تدریج اوضاع بهتر شد.","Things gradually improved.","","","gradually","قید پیش از فعل می‌آید."),
     (P,"گفتار","At first it felt strange.","At first it felt strange.","","","at first","جمله را واضح تلفظ کن.")],

 4: [(R,"چندگزینه‌ای","When I arrived, she ___ already gone.","had","","had | has | was","by then","گذشته کامل با had ساخته می‌شود."),
     (R,"تطبیق","relief","آسودگی","","relief | apology | delay","relief","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","I realised I ___ forgotten it.","had","","","realise","برای کار زودتر، گذشته کامل می‌آید."),
     (P,"متن آزاد","درست کنید: She had went before I arrived.","She had gone before I arrived.","","","earlier","بعد از had قسمت سوم فعل می‌آید."),
     (P,"متن آزاد","دو جمله را یکی کنید: I arrived. She had left.","When I arrived, she had left.","","","previously","با When شروع کن."),
     (P,"مرتب‌سازی","late / by / was / then / too / it","By then it was too late.","","","by then","با By then شروع کن."),
     (P,"ترجمه به انگلیسی","خوشبختانه چیزی از دست نرفت.","Fortunately, nothing was lost.","","","fortunately","با قید شروع کن."),
     (P,"گفتار","It was just a misunderstanding.","It was just a misunderstanding.","","","misunderstanding","جمله را واضح تلفظ کن.")],

 5: [(R,"چندگزینه‌ای","We ___ home when the lights went out.","were walking","","were walking | walked | had walked","meanwhile","پس‌زمینه با گذشته استمراری می‌آید."),
     (R,"تطبیق","witness","شاهد","","witness | incident | detail","witness","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","___ , I was scared. (راستش را بخواهی)","To be honest","","","to be honest","عبارت ثابت To be honest است."),
     (P,"متن آزاد","درست کنید: To honest, I was scared.","To be honest, I was scared.","","","to be honest","عبارت ثابت be می‌خواهد."),
     (P,"متن آزاد","با Looking back بنویسید: it / be / funny","Looking back, it was funny.","","","looking back","بعد از عبارت ابتدایی ویرگول می‌آید."),
     (P,"مرتب‌سازی","different / a / told / version / he","He told a different version.","","","version","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","تا جایی که می‌دانم، مشکلی نیست.","As far as I know, it's fine.","","","as far as I know","با عبارت ثابت شروع کن."),
     (P,"گفتار","Apparently nobody knew.","Apparently nobody knew.","","","apparently","جمله را واضح تلفظ کن.")],

 6: [(R,"چندگزینه‌ای","By the time we ___ , it had closed.","arrived","","arrived | will arrive | had arrived","by the time","بعد از by the time گذشته ساده می‌آید."),
     (R,"تطبیق","schedule","برنامه زمانی","","schedule | appointment | delay","schedule","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","I'll call as soon as I ___ .","arrive","","","as soon as","بعد از as soon as فعل حال ساده می‌آید."),
     (P,"متن آزاد","درست کنید: I'll call as soon as I will arrive.","I'll call as soon as I arrive.","","","as soon as","بعد از as soon as فعل آینده نمی‌آید."),
     (P,"متن آزاد","با By the time بنویسید: we / arrive — the meeting / end","By the time we arrived, the meeting had ended.","","","by the time","نیمه دوم گذشته کامل می‌گیرد."),
     (P,"مرتب‌سازی","schedule / behind / we're","We're behind schedule.","","","behind schedule","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","از پیش رزروش کن.","Book it in advance.","","","in advance","جمله امری با فعل شروع می‌شود."),
     (P,"گفتار","The meeting was postponed.","The meeting was postponed.","","","postpone","جمله را واضح تلفظ کن.")],

 7: [(R,"چندگزینه‌ای","He said he ___ finished it.","had","","had | has | have","claim","در نقل قولِ گذشته، گذشته کامل می‌آید."),
     (R,"تطبیق","evidence","شواهد","","evidence | rumour | fact","evidence","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","He denied ___ it. (take)","taking","","","deny","بعد از deny فعل ing می‌آید."),
     (P,"متن آزاد","درست کنید: He said he has finished it.","He said he had finished it.","","","confirm","در نقل قولِ گذشته فعل عقب می‌رود."),
     (P,"متن آزاد","بازگو کنید: \"I have seen it.\" (she)","She said she had seen it.","","","mention","حال کامل به گذشته کامل می‌رود."),
     (P,"مرتب‌سازی","everything / denied / he","He denied everything.","","","deny","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","به گفته گزارش، درست نبود.","According to the report, it wasn't true.","","","according to","با عبارت ثابت شروع کن."),
     (P,"گفتار","She admitted she had forgotten.","She admitted she had forgotten.","","","admit","جمله را واضح تلفظ کن.")],

 8: [(R,"چندگزینه‌ای","If I ___ more time, I'd travel.","had","","had | have | would have","imagine","در شرطی دوم بعد از if گذشته ساده می‌آید."),
     (R,"تطبیق","opportunity","فرصت","","opportunity | risk | benefit","opportunity","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","If I ___ you, I'd wait.","were","was","","in your place","در شرطی دوم were می‌آید."),
     (P,"متن آزاد","درست کنید: If I would have time, I would travel.","If I had time, I would travel.","","","afford","بعد از if فعل would نمی‌آید."),
     (P,"متن آزاد","شرطی دوم بسازید: I / be wealthy — I / help others","If I were wealthy, I'd help others.","","","wealthy","نیمه دوم با would می‌آید."),
     (P,"مرتب‌سازی","do / would / what / you","What would you do?","","","decision","با کلمه پرسشی شروع کن."),
     (P,"ترجمه به انگلیسی","جای تو بودم ریسک می‌کردم.","If I were in your place, I'd take the risk.","","","risk","با If I were شروع کن."),
     (P,"گفتار","Let's be realistic.","Let's be realistic.","","","realistic","جمله را واضح تلفظ کن.")],

 9: [(R,"چندگزینه‌ای","I wish I ___ the answer.","knew","","knew | know | would know","wish","بعد از wish فعل به گذشته می‌رود."),
     (R,"تطبیق","grateful","سپاسگزار","","grateful | relieved | guilty","grateful","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","I'm proud ___ the team.","of","","","proud of","حرف اضافه ثابت proud of است."),
     (P,"متن آزاد","درست کنید: I wish I know the answer.","I wish I knew the answer.","","","wish","بعد از wish فعل به گذشته می‌رود."),
     (P,"متن آزاد","افسوس گذشته بنویسید: I / take / his advice","I wish I had taken his advice.","","","advice","برای گذشته، wish + had + قسمت سوم."),
     (P,"مرتب‌سازی","on / time / it's / move / to","It's time to move on.","","","move on","با فاعل It's شروع کن."),
     (P,"ترجمه به انگلیسی","کاش وقت بیشتری داشتم.","I wish I had more time.","If only I had more time.","","if only","با I wish شروع کن."),
     (P,"گفتار","I'm grateful for the help.","I'm grateful for the help.","","","grateful","جمله را واضح تلفظ کن.")],

 10:[(R,"چندگزینه‌ای","It ___ on the budget.","depends","","depends | depend | depends to","depend on","حرف اضافه ثابت depend on است."),
     (R,"تطبیق","compromise","سازش","","compromise | alternative | consequence","compromise","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","In ___ it works, but in practice it's harder.","theory","","","in theory","تقابل in theory و in practice است."),
     (P,"متن آزاد","درست کنید: It depends to the budget.","It depends on the budget.","","","depend on","حرف اضافه ثابت on است."),
     (P,"متن آزاد","با Either way بنویسید: we / manage","Either way, we'll manage.","","","either way","بعد از عبارت ابتدایی ویرگول می‌آید."),
     (P,"مرتب‌سازی","options / up / the / weigh / let's","Let's weigh up the options.","","","weigh up","با Let's شروع کن."),
     (P,"ترجمه به انگلیسی","مزیت اصلی سرعت است.","The main advantage is speed.","","","advantage","با فاعل شروع کن."),
     (P,"گفتار","It's a risky plan.","It's a risky plan.","","","risky","جمله را واضح تلفظ کن.")],
}
