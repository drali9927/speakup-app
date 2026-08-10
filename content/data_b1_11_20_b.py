# -*- coding: utf-8 -*-
"""مکالمه، گرامر و تمرین دروس ۱۱ تا ۲۰ سطح B1."""

P, R = "تولیدی", "تشخیصی"

DLG = {
 11:[("A","Shall we go tomorrow?","فردا برویم؟"),
     ("B","Yes, unless it rains.","بله، مگر اینکه باران ببارد."),
     ("A","Take a coat in case it's cold.","محض احتیاط کت بردار."),
     ("B","Good idea. And a backup plan?","فکر خوبی است. و برنامه پشتیبان؟"),
     ("A","In the worst case, we'll walk.","در بدترین حالت پیاده می‌رویم."),
     ("B","Fine, as long as we leave early.","باشد، تا وقتی زود راه بیفتیم.")],
 12:[("A","He hasn't replied.","جواب نداده."),
     ("B","He must have forgotten.","حتماً فراموش کرده."),
     ("A","He can't have forgotten. I reminded him.","محال است فراموش کرده باشد. یادآوری کردم."),
     ("B","Then he might have been busy.","پس شاید سرش شلوغ بوده."),
     ("A","That's one possibility.","این یک احتمال است."),
     ("B","No wonder — he was working late.","جای تعجب نیست — تا دیروقت کار می‌کرد.")],
 13:[("A","Why didn't you say anything?","چرا چیزی نگفتی؟"),
     ("B","I know. I should have told you.","می‌دانم. باید بهت می‌گفتم."),
     ("A","It wasn't only your fault.","فقط تقصیر تو نبود."),
     ("B","I shouldn't have waited so long.","نباید این‌قدر صبر می‌کردم."),
     ("A","Well, lesson learned.","خب، درسش را گرفتیم."),
     ("B","I'll make up for it.","جبرانش می‌کنم.")],
 14:[("A","Was it the right decision?","تصمیم درستی بود؟"),
     ("B","In retrospect, yes.","حالا که برمی‌گردم، بله."),
     ("A","We underestimated the work though.","اما کار را دست‌کم گرفتیم."),
     ("B","True. The timing could have been better.","درست است. زمان‌بندی می‌توانست بهتر باشد."),
     ("A","It might have failed completely.","می‌توانست کاملاً شکست بخورد."),
     ("B","All things considered, it went well.","با در نظر گرفتن همه‌چیز خوب پیش رفت.")],
 15:[("A","What did that year teach you?","آن سال چه چیزی یادت داد؟"),
     ("B","It was a turning point.","نقطه عطف بود."),
     ("A","Was it hard?","سخت بود؟"),
     ("B","Very tough. I almost gave up.","خیلی دشوار. نزدیک بود تسلیم شوم."),
     ("A","But you kept going.","اما ادامه دادی."),
     ("B","I did, and the effort paid off.","بله، و تلاش نتیجه داد.")],
 16:[("A","How did the interview go?","مصاحبه چطور بود؟"),
     ("B","They asked me where I had worked before.","پرسیدند قبلاً کجا کار کرده‌ام."),
     ("A","Were you nervous?","مضطرب بودی؟"),
     ("B","At first. But I felt well prepared.","اول. اما احساس آمادگی می‌کردم."),
     ("A","Did they ask about your salary expectation?","درباره انتظار حقوقی‌ات پرسیدند؟"),
     ("B","They did. They'll get back to me.","بله. جواب می‌دهند.")],
 17:[("A","What's on the agenda?","دستور جلسه چیست؟"),
     ("B","He suggested waiting until next month.","پیشنهاد داد تا ماه آینده صبر کنیم."),
     ("A","Did anyone bring up the cost?","کسی هزینه را مطرح کرد؟"),
     ("B","Yes. She pointed out the risk too.","بله. خطر را هم خاطرنشان کرد."),
     ("A","So what did we agree to?","پس با چه چیزی موافقت کردیم؟"),
     ("B","To look into it. Three action points.","با بررسی‌اش. سه اقدام لازم.")],
 18:[("A","Did they accept the offer?","پیشنهاد را پذیرفتند؟"),
     ("B","No, they refused to sign.","نه، حاضر نشدند امضا کنند."),
     ("A","On what grounds?","به چه دلیل؟"),
     ("B","They objected to two of the terms.","به دو شرط اعتراض داشتند."),
     ("A","Can we be flexible?","می‌توانیم انعطاف نشان دهیم؟"),
     ("B","Let's meet halfway and come to terms.","بیا به توافق میانه برسیم.")],
 19:[("A","I didn't follow that.","متوجه نشدم."),
     ("B","In other words, the plan has changed.","به عبارت دیگر، برنامه عوض شده."),
     ("A","Could you simplify it?","می‌شود ساده‌اش کنی؟"),
     ("B","To put it simply, we start later.","ساده بگویم، دیرتر شروع می‌کنیم."),
     ("A","Now I get the point.","حالا منظورت را گرفتم."),
     ("B","In short, nothing is lost.","خلاصه اینکه چیزی از دست نرفته.")],
 20:[("A","Has the report been published?","گزارش منتشر شده؟"),
     ("B","It was released this morning.","امروز صبح منتشر شد."),
     ("A","Who wrote it?","چه کسی نوشتش؟"),
     ("B","The author is anonymous.","نویسنده‌اش ناشناس است."),
     ("A","And the new manager?","و مدیر تازه؟"),
     ("B","She was appointed last week. We were told to wait.","هفته پیش منصوب شد. به ما گفته شد صبر کنیم.")],
}

GR = {
 11:[("unless، as long as، in case",
      "سه ابزار شرط داری که فارسی‌زبان‌ها بیش از همه جایشان را با هم عوض می‌کنند.\n"
      "عبارت **unless** یعنی اگر … نه، مثل We'll go **unless** it rains که یعنی می‌رویم مگر اینکه باران بیاید.\n"
      "عبارت **as long as** شرطی ادامه‌دار می‌سازد، مثل It's fine **as long as** you're careful که یعنی تا وقتی مواظب باشی مشکلی نیست.\n"
      "عبارت **in case** برای احتیاطِ پیش از وقوع است، مثل Take a coat **in case** it's cold.\n"
      "همین آخری را با if اشتباه نگیر، چون if بعد از وقوع می‌آید و in case پیش از آن. جمله Take an umbrella **if** it rains یعنی اگر بارید بردار، اما Take an umbrella **in case** it rains یعنی شاید ببارد، پس از همین حالا بردار.",
      "We'll go unless it rains.\nIt's fine as long as you're careful.\nTake a coat in case it's cold.\nI'll come even if it's late.",
      "کدام «محض احتیاط» را می‌رساند؟", "Take a coat in case it's cold.",
      "Take a coat in case it's cold. | Take a coat if it's cold.",
      "Take a coat if it's cold. ← این یعنی فقط در صورت سرد بودن، نه از پیش."),
     ("زمان بعد از این کلمه‌ها",
      "بعد از unless و as long as و in case و provided that، وقتی جمله درباره آینده است فعل حال ساده می‌گیرد و نه will.\n"
      "پس درست این است که بگویی We'll go unless it **rains** و نه unless it will rain، و به همین شکل I'll wait as long as you **need**.\n"
      "همین قاعده برای when و before و after و until و as soon as هم برقرار است، پس یک بار که یادش بگیری در همه‌شان به کارت می‌آید.\n"
      "عبارت even if هم از همین قانون پیروی می‌کند: I'll come even if it **is** late.",
      "We'll go unless it rains.\nI'll wait as long as you need.\nCall me before you leave.\nI'll stay until she arrives.",
      "کدام درست است؟", "We'll go unless it rains.",
      "We'll go unless it rains. | We'll go unless it will rain.",
      "We'll go unless it will rain. ← بعد از unless فعل آینده نمی‌آید.")],

 12:[("مدال + have + قسمت سوم",
      "وقتی می‌خواهی درباره گذشته حدس بزنی، مدال را با have و قسمت سوم فعل می‌آوری.\n"
      "با **must have** می‌گویی حتماً چنین بوده، مثل He must have forgotten.\n"
      "با **can't have** می‌گویی محال است چنین بوده باشد، مثل She can't have known.\n"
      "و با **might have** یا **could have** می‌گویی شاید چنین بوده، مثل They might have left.\n"
      "ساختار در هر سه یکی است و تنها چیزی که عوض می‌شود خودِ مدال است. پس نه must have forgot درست است و نه must had forgotten.",
      "He must have forgotten.\nShe can't have known.\nThey might have left early.\nIt could have been worse.",
      "کدام درست است؟", "He must have forgotten.",
      "He must have forgotten. | He must had forgotten. | He must have forgot.",
      "He must had forgotten. ← بعد از مدال have می‌آید نه had.\n"
      "He must have forgot. ← قسمت سوم forget همان forgotten است."),
     ("مطمئن یا نامطمئن",
      "این سه ساختار یک طیف می‌سازند که از مطمئن شروع می‌شود و به نامطمئن می‌رسد.\n"
      "در یک سر طیف **must have** است که یعنی تقریباً مطمئنم چنین بوده.\n"
      "وسط طیف **may have** و **might have** و **could have** می‌نشینند که یعنی شاید.\n"
      "و در سر دیگر **can't have** است که یعنی تقریباً مطمئنم چنین نبوده.\n"
      "نکته‌ای که باید بسپاری این است که منفیِ must have همان can't have است و نه mustn't have، چون mustn't ممنوعیت را می‌رساند و نه حدس را.",
      "He must have left. (مطمئنم)\nHe might have left. (شاید)\nHe can't have left. (مطمئنم که نه)",
      "منفیِ «حتماً رفته» کدام است؟", "He can't have left.",
      "He can't have left. | He mustn't have left.",
      "He mustn't have left. ← mustn't برای ممنوعیت است، نه برای حدس منفی.")],

 13:[("should have",
      "ساختار should have برای کاری است که در گذشته درست بود اما انجام نشد.\n"
      "وقتی می‌گویی You **should have told** me، یعنی نگفتی و کاش می‌گفتی.\n"
      "و وقتی می‌گویی I **shouldn't have waited**، یعنی صبر کردم در حالی که نباید می‌کردم.\n"
      "حواست به لحن این ساختار باشد، چون هم گله را می‌رساند و هم پشیمانی را و در گفت‌وگو می‌تواند تند به نظر برسد.\n"
      "اگر خواستی نرم‌ترش کنی، **It might have been better to** tell me را به کار ببر.",
      "You should have told me.\nI shouldn't have waited.\nWe should have booked earlier.\nHe shouldn't have said that.",
      "کدام درست است؟", "You should have told me.",
      "You should have told me. | You should told me. | You should have tell me.",
      "You should told me. ← have حذف نمی‌شود.\n"
      "You should have tell me. ← بعد از have قسمت سوم فعل می‌آید."),
     ("پذیرفتن و جبران",
      "برای پذیرفتن اشتباه و جبران کردنش، چند عبارت ثابت هست که همیشه به همین شکل می‌آیند.\n"
      "برای رد کردن تقصیر می‌گویی It wasn't my **fault**، و در پاسخ می‌شنوی That's not an **excuse**.\n"
      "برای پذیرفتن مسئولیت می‌گویی He took **responsibility for** it.\n"
      "و برای جبران، I'll **make up for** it و I don't want to **let you down** را داری.\n"
      "حرف اضافه در این‌ها ثابت است، پس همیشه responsibility **for** و make up **for** بماند.",
      "It wasn't my fault.\nHe took responsibility for it.\nI'll make up for it.\nWith hindsight, I was wrong.",
      "کدام درست است؟", "He took responsibility for it.",
      "He took responsibility for it. | He took responsibility of it.",
      "He took responsibility of it. ← حرف اضافه ثابت for است.")],

 14:[("could have و might have برای نتیجه دیگر",
      "وقتی می‌خواهی بگویی چیز دیگری هم ممکن بود رخ دهد، could have و might have به کارت می‌آیند.\n"
      "جمله It **could have been** worse یعنی ممکن بود بدتر شود ولی نشد، و The timing **might have been** better یعنی زمان‌بندی می‌توانست بهتر باشد.\n"
      "این‌ها را با should have اشتباه نگیر. آن یکی سرزنش می‌کند، اما این‌ها فقط امکانی را نشان می‌دهند که پیش نیامد و باری از ملامت ندارند.",
      "It could have been worse.\nThe timing might have been better.\nIt might have failed completely.\nWe could have waited.",
      "کدام سرزنش نیست؟", "It could have been worse.",
      "It could have been worse. | You should have waited.",
      "You should have waited. ← این سرزنش و گله است."),
     ("عبارت‌های سنجش",
      "برای تحلیل کردن یک تصمیم، چند عبارت هست که حرفت را سنجیده نشان می‌دهد.\n"
      "وقتی از حالا به گذشته نگاه می‌کنی می‌گویی **In retrospect**, it was fine.\n"
      "برای جمع‌بندی روی هم رفته **On balance**, it worked را داری، و برای در نظر گرفتن همه‌چیز **All things considered**, it went well.\n"
      "دو جمله آماده هم هست که زیاد به کار می‌آیند: **It was worth trying** و **We underestimated** the work.\n"
      "بیشتر این‌ها ابتدای جمله می‌نشینند و بعدشان ویرگول می‌گذاری.",
      "In retrospect, it was fine.\nOn balance, it worked.\nAll things considered, it went well.\nIt was worth trying.",
      "کدام درست است؟", "It was worth trying.",
      "It was worth trying. | It was worth to try.",
      "It was worth to try. ← بعد از worth فعل ing می‌آید.")],

 15:[("مرور مدال‌های گذشته",
      "سه ساختاری که در این چند درس یاد گرفتی شکل یکسانی دارند اما سه کار متفاوت می‌کنند.\n"
      "با **must have** حدس می‌زنی، مثل He must have forgotten.\n"
      "با **should have** گله و پشیمانی را می‌رسانی، مثل You should have told me.\n"
      "و با **could have** از امکانی می‌گویی که پیش نیامد، مثل It could have been worse.\n"
      "هر سه از الگوی مدال به‌علاوه have و قسمت سوم می‌آیند، پس تفاوتشان در ساختار نیست و فقط در معناست.",
      "He must have forgotten.\nYou should have told me.\nIt could have been worse.\nShe can't have known.",
      "کدام حدس است؟", "He must have forgotten.",
      "He must have forgotten. | You should have told me.",
      "You should have told me. ← این گله است، نه حدس."),
     ("گفتن مسیر رشد",
      "برای تعریف کردن مسیر رشد خودت، چند ساختار هست که روایت را طبیعی می‌کند.\n"
      "برای نقطه عطف می‌گویی It was a **turning point**، و برای لحظه سختی I almost **gave up**.\n"
      "بعد ادامه‌اش را با But I **kept going** and **stuck with** it می‌گویی.\n"
      "و برای نتیجه، The effort **paid off** و It was **worthwhile** را داری.\n"
      "یک نکته ساختاری هم هست: بعد از give up و keep فعل با ing می‌آید، پس می‌گویی I gave up **trying** و I kept **going**.",
      "It was a turning point.\nI almost gave up trying.\nI kept going anyway.\nThe effort paid off.",
      "کدام درست است؟", "I kept going anyway.",
      "I kept going anyway. | I kept to go anyway.",
      "I kept to go anyway. ← بعد از keep فعل ing می‌آید.")],

 16:[("نقل قول پرسش",
      "وقتی پرسشی را بازگو می‌کنی، ترتیب کلمه‌ها از پرسشی به خبری برمی‌گردد.\n"
      "پس «Where do you live?» می‌شود She asked me where I **lived** و نه she asked me where did I live.\n"
      "در این تبدیل، فعل کمکیِ do و did حذف می‌شود و علامت سؤال هم دیگر نمی‌آید، چون جمله دیگر پرسش نیست بلکه گزارشِ یک پرسش است.\n"
      "زمان فعل هم مثل بقیه نقل قول‌ها یک پله عقب می‌رود و live به lived تبدیل می‌شود.",
      "She asked me where I lived.\nHe asked what I wanted.\nThey asked when I had started.\nShe asked how long I had worked there.",
      "کدام درست است؟", "She asked me where I lived.",
      "She asked me where I lived. | She asked me where did I live.",
      "She asked me where did I live. ← در نقل قول، ترتیب خبری می‌شود و did حذف می‌شود."),
     ("پرسش بله/خیر با if",
      "اگر پرسشی که بازگو می‌کنی کلمه پرسشی نداشته باشد، با **if** یا **whether** شروعش می‌کنی.\n"
      "پس «Are you free?» می‌شود He asked **if** I was free، و «Did you finish?» می‌شود She asked **whether** I had finished.\n"
      "اینجا هم مثل حالت قبل ترتیب جمله خبری است و فعل یک پله عقب می‌رود، پس همان دو قاعده را دوباره به کار می‌بری.",
      "He asked if I was free.\nShe asked whether I had finished.\nThey asked if we could wait.\nHe asked if I had seen it.",
      "کدام درست است؟", "He asked if I was free.",
      "He asked if I was free. | He asked if was I free.",
      "He asked if was I free. ← بعد از if ترتیب خبری می‌آید.")],

 17:[("افعال گزارشی + ing یا to",
      "هر فعل گزارشی تعیین می‌کند که بعد از خودش چه ساختاری بیاید، و همین جاست که بیشتر اشتباه‌ها رخ می‌دهد.\n"
      "افعال suggest و recommend و deny و avoid بعدشان فعل با ing می‌گیرند، مثل He suggested **waiting**.\n"
      "افعال agree و offer و promise و refuse و decide بعدشان to می‌گیرند، مثل He offered **to help**.\n"
      "و افعال advise و remind و tell و ask اول مفعول می‌خواهند و بعد to، مثل They advised **us to wait**.\n"
      "حواست باشد که suggest در این گروه آخر نیست، پس suggest me to wait نادرست است.",
      "He suggested waiting.\nHe offered to help.\nThey advised us to wait.\nShe reminded me to call.",
      "کدام درست است؟", "He suggested waiting.",
      "He suggested waiting. | He suggested to wait. | He suggested me to wait.",
      "He suggested to wait. ← بعد از suggest فعل ing می‌آید.\n"
      "He suggested me to wait. ← suggest مفعول مستقیم نمی‌گیرد."),
     ("زبان جلسه",
      "چند عبارت هست که در جلسه کاری بیش از همه به کار می‌آیند و بدون آن‌ها انگلیسیِ جلسه‌ات ساده به نظر می‌رسد.\n"
      "برای شروع می‌پرسی What's on the **agenda**؟\n"
      "در میانه جلسه می‌گویی He **brought up** the cost و She **pointed out** the risk و We'll **look into** it.\n"
      "و برای بستن بحث، Let me **sum up** و There are three **action points** را داری.\n"
      "این‌ها فعل عبارتی‌اند و معنی‌شان از تک‌تک اجزایشان درنمی‌آید، پس باید هرکدام را به‌صورت یک واحد کامل حفظ کنی.",
      "What's on the agenda?\nHe brought up the cost.\nShe pointed out the risk.\nLet's go over it again.",
      "کدام یعنی «بررسی می‌کنیم»؟", "We'll look into it.",
      "We'll look into it. | We'll look at it up.",
      "We'll look at it up. ← چنین ترکیبی وجود ندارد؛ فعل درست look into است.")],

 18:[("گزارش مخالفت",
      "افعالی که مخالفت را گزارش می‌کنند هرکدام ساختار خودشان را دارند.\n"
      "فعل **refuse to** بعدش فعل ساده می‌گیرد، مثل They refused **to sign**.\n"
      "فعل **object to** بعدش اسم یا فعل با ing می‌گیرد، مثل She objected **to the change**.\n"
      "فعل **deny** همیشه با ing می‌آید، مثل He denied **taking** it.\n"
      "و **disagree with** بعدش اسم می‌خواهد، مثل I disagree **with that**.\n"
      "در object to و disagree with حرف اضافه ثابت است و بعد از آن فعل ساده نمی‌نشیند؛ همین نکته بیشترین اشتباه را می‌سازد.",
      "They refused to sign.\nShe objected to the change.\nHe denied taking it.\nI disagree with that.",
      "کدام درست است؟", "She objected to the change.",
      "She objected to the change. | She objected the change.",
      "She objected the change. ← بعد از object حرف اضافه to لازم است."),
     ("زبان مذاکره",
      "در مذاکره چهار حالت رایج داری که هرکدام عبارت خودش را می‌طلبد.\n"
      "برای پافشاری **insist on** را با اسم یا فعل ing می‌آوری، مثل She insisted **on** a written reply.\n"
      "برای کوتاه آمدن **give in** را داری، مثل In the end he gave in.\n"
      "برای رسیدن به توافقی میانه **meet halfway** به کار می‌رود و برای توافق نهایی **come to terms**.\n"
      "و اگر مذاکره به جایی نرسد، می‌گویی The talks **broke down**.",
      "She insisted on a written reply.\nIn the end he gave in.\nLet's meet halfway.\nThe talks broke down.",
      "کدام درست است؟", "She insisted on a reply.",
      "She insisted on a reply. | She insisted a reply.",
      "She insisted a reply. ← بعد از insist حرف اضافه on لازم است.")],

 19:[("بازگویی و ساده کردن",
      "وقتی طرف مقابل حرفت را نگرفت، به‌جای تکرار همان جمله، آن را با عبارت دیگری باز می‌گویی.\n"
      "با **In other words**, the plan has changed حرفت را به شکل دیگری می‌گویی.\n"
      "با **That is to say**, it's finished توضیحش می‌دهی.\n"
      "با **To put it simply**, we start later ساده‌اش می‌کنی.\n"
      "و اگر بخواهی از نو شروع کنی، **Let me rephrase that** را می‌گویی.\n"
      "همه این‌ها ابتدای جمله می‌آیند و بعدشان ویرگول می‌گذاری.",
      "In other words, the plan has changed.\nThat is to say, it's finished.\nTo put it simply, we start later.\nLet me rephrase that.",
      "کدام درست است؟", "In other words, it's finished.",
      "In other words, it's finished. | With other words, it's finished.",
      "With other words, it's finished. ← عبارت ثابت in other words است."),
     ("مثال زدن و خلاصه کردن",
      "دو عبارت **for example** و **such as** هر دو مثال می‌آورند اما جایشان در جمله یکی نیست.\n"
      "عبارت for example اول جمله یا وسط آن می‌نشیند و با ویرگول جدا می‌شود.\n"
      "اما such as بلافاصله بعد از اسم می‌آید و ویرگول نمی‌خواهد، مثل Tools **such as** this one.\n"
      "برای جمع‌بندی هم دو عبارت داری: **In short**, we agreed و **In general**, it works well.",
      "For example, take this case.\nTools such as this one.\nIn short, we agreed.\nIn general, it works well.",
      "کدام درست است؟", "Tools such as this one.",
      "Tools such as this one. | Tools such this one.",
      "Tools such this one. ← عبارت ثابت such as است.")],

 20:[("مجهول با فاعل مبهم",
      "در متن رسمی، وقتی کننده کار معلوم نیست یا اهمیتی ندارد، جمله را مجهول می‌سازی.\n"
      "پس می‌گویی The report **was written** last month یا A new manager **was appointed**.\n"
      "شکل پرکاربردترش وقتی است که مفعول شخص باشد، مثل We **were given** two weeks و We **were told** to wait.\n"
      "همین شکل آخر برای فارسی‌زبان‌ها دردسرساز است، چون در فارسی می‌گوییم «به ما دو هفته دادند» و همین باعث می‌شود اشتباهاً بگویند was given to us.",
      "The report was written last month.\nA new manager was appointed.\nWe were given two weeks.\nWe were told to wait.",
      "کدام درست است؟", "We were told to wait.",
      "We were told to wait. | It was told to us to wait.",
      "It was told to us to wait. ← در انگلیسی خودِ شخص فاعل جمله مجهول می‌شود."),
     ("در حال انجام و مجهول",
      "مجهول با زمان‌های استمراری هم ساخته می‌شود و ساختارش be به‌علاوه **being** و قسمت سوم فعل است.\n"
      "در حال استمراری می‌گویی The plan **is being reviewed**، و در گذشته استمراری It **was being tested** yesterday.\n"
      "فراموش نکردن being مهم است، چون بودن و نبودنش معنی را عوض می‌کند.\n"
      "جمله The plan is reviewed یعنی این کار به‌طور معمول انجام می‌شود، اما The plan is being reviewed یعنی همین حالا در جریان است.",
      "The plan is being reviewed.\nIt was being tested yesterday.\nThe figures are being checked.\nThe room is being cleaned.",
      "کدام «همین حالا» را می‌رساند؟", "The plan is being reviewed.",
      "The plan is being reviewed. | The plan is reviewed.",
      "The plan is reviewed. ← این کار معمول و همیشگی را می‌رساند، نه همین حالا.")],
}

EX = {
 11:[(R,"چندگزینه‌ای","We'll go ___ it rains.","unless","","unless | if not | in case","unless","unless یعنی «اگر … نه»."),
     (R,"تطبیق","precaution","احتیاط","","precaution | guarantee | backup","precaution","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","Take a coat ___ ___ it's cold.","in case","","","in case","«محض احتیاط» یعنی in case."),
     (P,"متن آزاد","درست کنید: We'll go unless it will rain.","We'll go unless it rains.","","","unless","بعد از unless فعل حال ساده می‌آید."),
     (P,"متن آزاد","با as long as بنویسید: it's fine / you / be careful","It's fine as long as you're careful.","","","as long as","نیمه دوم حال ساده می‌گیرد."),
     (P,"مرتب‌سازی","time / just / we / in / arrived","We arrived just in time.","","","just in time","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","برای تأخیر آماده باش.","Be prepared for delays.","","","be prepared","جمله امری با فعل شروع می‌شود."),
     (P,"گفتار","In the worst case, we'll walk.","In the worst case, we'll walk.","","","worst case","جمله را واضح تلفظ کنید.")],

 12:[(R,"چندگزینه‌ای","He ___ forgotten.","must have","","must have | must had | must have forgot","must have","ساختار مدال + have + قسمت سوم است."),
     (R,"تطبیق","clue","سرنخ","","clue | doubt | theory","clue","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","She ___ have known. (محال است)","can't","","","can't have","منفی حدس با can't have بیان می‌شود."),
     (P,"متن آزاد","درست کنید: He must had forgotten.","He must have forgotten.","","","must have","بعد از مدال have می‌آید نه had."),
     (P,"متن آزاد","حدس بزنید: they / leave early (شاید)","They might have left early.","They may have left early.","","might have","برای احتمال might have می‌آید."),
     (P,"مرتب‌سازی","sense / that / makes","That makes sense.","","","make sense","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","سر در نمی‌آورم.","I can't figure it out.","","","figure out","با فاعل I شروع کن."),
     (P,"گفتار","No wonder he was tired.","No wonder he was tired.","","","no wonder","جمله را واضح تلفظ کنید.")],

 13:[(R,"چندگزینه‌ای","You ___ told me.","should have","","should have | should | should have tell","should have","ساختار should + have + قسمت سوم است."),
     (R,"تطبیق","fault","تقصیر","","fault | excuse | complaint","fault","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","He took responsibility ___ it.","for","","","responsibility for","حرف اضافه ثابت for است."),
     (P,"متن آزاد","درست کنید: You should told me.","You should have told me.","","","should have","have حذف نمی‌شود."),
     (P,"متن آزاد","پشیمانی بنویسید: I / wait / so long","I shouldn't have waited so long.","","","shouldn't have","برای کار انجام‌شده و نادرست shouldn't have می‌آید."),
     (P,"مرتب‌سازی","for / it / up / make / I'll","I'll make up for it.","","","make up for","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","خودت را سرزنش نکن.","Don't blame yourself.","","","blame","جمله امری منفی با Don't شروع می‌شود."),
     (P,"گفتار","With hindsight, I was wrong.","With hindsight, I was wrong.","","","with hindsight","جمله را واضح تلفظ کنید.")],

 14:[(R,"چندگزینه‌ای","It ___ been worse.","could have","","could have | should have | must have","could have","برای امکانِ نرفته could have می‌آید."),
     (R,"تطبیق","assumption","فرض","","assumption | expectation | outcome","assumption","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","It was worth ___ . (try)","trying","","","worth trying","بعد از worth فعل ing می‌آید."),
     (P,"متن آزاد","درست کنید: It was worth to try.","It was worth trying.","","","worth trying","بعد از worth فعل ing می‌آید."),
     (P,"متن آزاد","با In retrospect بنویسید: it / be / fine","In retrospect, it was fine.","","","in retrospect","بعد از عبارت ابتدایی ویرگول می‌آید."),
     (P,"مرتب‌سازی","work / underestimated / the / we","We underestimated the work.","","","underestimate","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","زمان‌بندی می‌توانست بهتر باشد.","The timing could have been better.","","","timing","با فاعل شروع کن."),
     (P,"گفتار","All things considered, it went well.","All things considered, it went well.","","","all things considered","جمله را واضح تلفظ کنید.")],

 15:[(R,"چندگزینه‌ای","I kept ___ anyway.","going","","going | to go | go","keep going","بعد از keep فعل ing می‌آید."),
     (R,"تطبیق","determination","عزم","","determination | motivation | setback","determination","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","The effort paid ___ .","off","","","pay off","فعل عبارتی pay off است."),
     (P,"متن آزاد","درست کنید: I kept to go anyway.","I kept going anyway.","","","keep going","بعد از keep فعل ing می‌آید."),
     (P,"متن آزاد","بنویسید: it / be / a turning point","It was a turning point.","","","turning point","با فاعل It شروع کن."),
     (P,"مرتب‌سازی","up / gave / almost / I","I almost gave up.","","","give up","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","پشیمان نیستم.","I have no regrets.","","","no regrets","با فاعل I شروع کن."),
     (P,"گفتار","In hindsight, it helped.","In hindsight, it helped.","","","in hindsight","جمله را واضح تلفظ کنید.")],

 16:[(R,"چندگزینه‌ای","She asked me where ___ .","I lived","","I lived | did I live | do I live","interview","در نقل قول ترتیب خبری می‌شود."),
     (R,"تطبیق","candidate","داوطلب","","candidate | qualification | strength","candidate","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","He asked ___ I was free.","if","whether","","enquire","برای پرسش بله/خیر if می‌آید."),
     (P,"متن آزاد","درست کنید: She asked me where did I live.","She asked me where I lived.","","","wonder","در نقل قول did حذف می‌شود."),
     (P,"متن آزاد","بازگو کنید: \"Did you finish?\" (she)","She asked if I had finished.","She asked whether I had finished.","","clarify","با if یا whether شروع کن."),
     (P,"مرتب‌سازی","up / follow / I'll / week / next","I'll follow up next week.","","","follow up","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","به شما جواب می‌دهیم.","We'll get back to you.","","","get back to","با فاعل We شروع کن."),
     (P,"گفتار","I felt well prepared.","I felt well prepared.","","","prepared","جمله را واضح تلفظ کنید.")],

 17:[(R,"چندگزینه‌ای","He suggested ___ .","waiting","","waiting | to wait | me to wait","suggest","بعد از suggest فعل ing می‌آید."),
     (R,"تطبیق","agenda","دستور جلسه","","agenda | proposal | summary","agenda","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","They advised us ___ wait.","to","","","advise","بعد از advise مفعول و سپس to می‌آید."),
     (P,"متن آزاد","درست کنید: He suggested me to wait.","He suggested waiting.","He suggested that I wait.","","suggest","suggest مفعول مستقیم نمی‌گیرد."),
     (P,"متن آزاد","با offer بنویسید: he / help","He offered to help.","","","offer to","بعد از offer فعل با to می‌آید."),
     (P,"مرتب‌سازی","risk / out / she / the / pointed","She pointed out the risk.","","","point out","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","بررسی‌اش می‌کنیم.","We'll look into it.","","","look into","با فاعل We شروع کن."),
     (P,"گفتار","It was a productive meeting.","It was a productive meeting.","","","productive","جمله را واضح تلفظ کنید.")],

 18:[(R,"چندگزینه‌ای","They refused ___ .","to sign","","to sign | signing | sign","refuse","بعد از refuse فعل با to می‌آید."),
     (R,"تطبیق","contract","قرارداد","","contract | dispute | condition","contract","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","She objected ___ the change.","to","","","object to","حرف اضافه ثابت to است."),
     (P,"متن آزاد","درست کنید: She insisted a written reply.","She insisted on a written reply.","","","insist on","بعد از insist حرف اضافه on لازم است."),
     (P,"متن آزاد","بنویسید: let's / meet halfway","Let's meet halfway.","","","meet halfway","با Let's شروع کن."),
     (P,"مرتب‌سازی","down / talks / broke / the","The talks broke down.","","","break down","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","می‌توانیم انعطاف‌پذیر باشیم.","We can be flexible.","","","flexible","با فاعل We شروع کن."),
     (P,"گفتار","It was a reasonable offer.","It was a reasonable offer.","","","reasonable offer","جمله را واضح تلفظ کنید.")],

 19:[(R,"چندگزینه‌ای","___ , the plan has changed.","In other words","","In other words | With other words | On other words","in other words","عبارت ثابت in other words است."),
     (R,"تطبیق","clarification","شفاف‌سازی","","clarification | summary | main point","clarification","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","Tools ___ ___ this one.","such as","","","such as","عبارت ثابت such as است."),
     (P,"متن آزاد","درست کنید: With other words, it's finished.","In other words, it's finished.","","","in other words","عبارت ثابت in other words است."),
     (P,"متن آزاد","ساده بگویید: we / start later","To put it simply, we start later.","","","to put it simply","با عبارت ثابت شروع کن."),
     (P,"مرتب‌سازی","point / the / I / get","I get the point.","","","get the point","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","خلاصه اینکه توافق کردیم.","In short, we agreed.","","","in short","با عبارت ثابت شروع کن."),
     (P,"گفتار","Let me make it clear.","Let me make it clear.","","","make it clear","جمله را واضح تلفظ کنید.")],

 20:[(R,"چندگزینه‌ای","The plan ___ reviewed at the moment.","is being","","is being | is | has being","review","مجهول استمراری با is being ساخته می‌شود."),
     (R,"تطبیق","committee","کمیته","","committee | authority | regulation","committee","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","We ___ told to wait.","were","","","be told","مجهول گذشته با were ساخته می‌شود."),
     (P,"متن آزاد","درست کنید: It was told to us to wait.","We were told to wait.","","","be told","خودِ شخص فاعل جمله مجهول می‌شود."),
     (P,"متن آزاد","مجهول کنید: They appointed a new manager.","A new manager was appointed.","","","appoint","مفعول به ابتدای جمله می‌رود."),
     (P,"مرتب‌سازی","released / figures / were / the","The figures were released.","","","release","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","به ما دو هفته داده شد.","We were given two weeks.","","","be given","با فاعل We شروع کن."),
     (P,"گفتار","A survey was carried out.","A survey was carried out.","","","survey","جمله را واضح تلفظ کنید.")],
}
