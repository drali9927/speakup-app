# -*- coding: utf-8 -*-
"""مکالمه، گرامر و تمرین دروس ۱ تا ۱۰ سطح B2."""

P, R = "تولیدی", "تشخیصی"

DLG = {
 1: [("A","I don't think we're disagreeing.","فکر نمی‌کنم اختلافی داشته باشیم."),
     ("B","It depends on your perspective.","به دیدگاهت بستگی دارد."),
     ("A","In essence, we want the same thing.","در اصل یک چیز می‌خواهیم."),
     ("B","True, but there's a nuance here.","درست است، اما اینجا ظرافتی هست."),
     ("A","Bear in mind the deadline.","مهلت را در نظر داشته باش."),
     ("B","It boils down to what we prioritise.","خلاصه‌اش این است که چه چیزی را اولویت بدانیم.")],
 2: [("A","What do the figures show?","ارقام چه نشان می‌دهند؟"),
     ("B","Sales increased sharply in spring.","فروش در بهار به‌شدت افزایش یافت."),
     ("A","And after that?","و بعد از آن؟"),
     ("B","They peaked in June, then levelled off.","خرداد به اوج رسید، بعد صاف شد."),
     ("A","What about costs?","هزینه‌ها چطور؟"),
     ("B","They declined steadily. The overall trend is positive.","پیوسته کاهش یافت. روند کلی مثبت است.")],
 3: [("A","What caused the delay?","چه چیزی باعث تأخیر شد؟"),
     ("B","It stems from a supply problem.","ریشه در مشکل تأمین دارد."),
     ("A","Did that account for everything?","این همه‌چیز را توضیح می‌دهد؟"),
     ("B","No. Several factors contributed to it.","نه. چند عامل در آن سهم داشتند."),
     ("A","Was there a knock-on effect?","اثر زنجیره‌ای داشت؟"),
     ("B","Yes. As a consequence, prices rose.","بله. در نتیجه قیمت‌ها بالا رفت.")],
 4: [("A","Why didn't you come?","چرا نیامدی؟"),
     ("B","If I had known, I'd have come.","اگر می‌دانستم می‌آمدم."),
     ("A","Nobody told you?","کسی بهت نگفت؟"),
     ("B","No. It was entirely avoidable.","نه. کاملاً قابل اجتناب بود."),
     ("A","Don't dwell on it.","زیاد بهش فکر نکن."),
     ("B","You're right. It's water under the bridge.","حق با توست. گذشته‌ها گذشته.")],
 5: [("A","What's the worst-case scenario?","بدترین سناریو چیست؟"),
     ("B","In the worst case, the deal falls through.","در بدترین حالت معامله به نتیجه نمی‌رسد."),
     ("A","Can we rule that out?","می‌توانیم کنارش بگذاریم؟"),
     ("B","No, but the likelihood is low.","نه، اما احتمالش کم است."),
     ("A","Do we have a contingency?","پیش‌بینی احتیاطی داریم؟"),
     ("B","We do. On reflection, the risk paid off.","بله. پس از تأمل، ریسک نتیجه داد.")],
 6: [("A","Do you regret the choice?","از انتخابت پشیمانی؟"),
     ("B","If I had studied law, I'd be in a different job now.","اگر حقوق خوانده بودم حالا شغل دیگری داشتم."),
     ("A","Would you be better off?","وضعت بهتر بود؟"),
     ("B","Maybe. But I ended up here, and I'm glad.","شاید. اما سر از اینجا درآوردم و خوشحالم."),
     ("A","That was the turning point, then.","پس نقطه عطف همان بود."),
     ("B","There's no point in regretting it.","پشیمانی فایده‌ای ندارد.")],
 7: [("A","We nearly missed the flight.","نزدیک بود پرواز را از دست بدهیم."),
     ("B","But for the traffic, we'd have been early.","اگر ترافیک نبود زود رسیده بودیم."),
     ("A","That was a close call.","مو لای درزش نمی‌رفت."),
     ("B","Thanks to her, we made it.","به لطف او رسیدیم."),
     ("A","Next time we should leave earlier.","دفعه بعد باید زودتر راه بیفتیم."),
     ("B","Agreed — otherwise we'll do this again.","موافقم — وگرنه دوباره همین می‌شود.")],
 8: [("A","Shall I sign it now?","حالا امضایش کنم؟"),
     ("B","You'd better ask first.","بهتر است اول بپرسی."),
     ("A","I'd rather get it over with.","ترجیح می‌دهم تمامش کنم."),
     ("B","I'd advise against it.","توصیه می‌کنم انجامش ندهی."),
     ("A","On second thoughts, you're right.","با فکر دوباره، حق با توست."),
     ("B","Think twice before you accept.","قبل از پذیرفتن دو بار فکر کن.")],
 9: [("A","I've complained three times.","سه بار شکایت کرده‌ام."),
     ("B","It's no use complaining.","شکایت کردن فایده‌ای ندارد."),
     ("A","I can't help feeling frustrated.","نمی‌توانم جلوی کلافگی‌ام را بگیرم."),
     ("B","I understand your frustration.","کلافگی‌ات را می‌فهمم."),
     ("A","So what do we do?","پس چه کار کنیم؟"),
     ("B","Let's make the best of it and move on.","بیا بهترین استفاده را بکنیم و بگذریم.")],
 10:[("A","Could you explain the process?","می‌شود فرایند را توضیح دهید؟"),
     ("B","It's too complex to explain in a minute.","پیچیده‌تر از آن است که در یک دقیقه توضیح داده شود."),
     ("A","Could you put it in layman's terms?","می‌شود به زبان ساده بگویید؟"),
     ("B","Let me break it down.","بگذارید تجزیه‌اش کنم."),
     ("A","Now I grasp it.","حالا درکش می‌کنم."),
     ("B","Good. Let me summarise the rest.","خوب. بگذارید بقیه را خلاصه کنم.")],
}

GR = {
 1: [("مرور زمان‌های کامل",
      "تا اینجا سه زمان کامل را یاد گرفته‌ای و در این سطح باید بدون فکر کردن به کارشان ببری.\n"
      "**حال کامل** وقتی می‌آید که کاری تمام شده اما نتیجه‌اش هنوز هست: وقتی می‌گویی I've finished، یعنی کار تمام است و همین حالا هم تمام مانده.\n"
      "**حال کامل استمراری** روی مدت زمان تأکید دارد، نه روی تمام شدن: جمله I've been working here می‌گوید مدتی است مشغولم.\n"
      "**گذشته کامل** برای کاری است که پیش از یک لحظه دیگر در گذشته تمام شده بود: She had already left یعنی پیش از رسیدن ما رفته بود.\n"
      "نکته‌ای که این سطح را از سطح قبل جدا می‌کند این است که انتخاب میان این سه، فقط زمان را عوض نمی‌کند؛ لحن جمله را هم عوض می‌کند.\n"
      "اگر بگویی I've read it، شنونده می‌فهمد خوانده‌ای و تمام شده. اما اگر بگویی I've been reading it، می‌فهمد مدتی مشغولش بوده‌ای و شاید هنوز تمامش نکرده‌ای.",
      "I've finished the report.\nI've been reading it all week.\nShe had already left when I arrived.\nWe've known each other for years.",
      "کدام بر ادامه‌داشتن تأکید می‌کند؟", "I've been reading it.",
      "I've been reading it. | I've read it.",
      "I've read it. ← این بر نتیجه تأکید دارد نه بر ادامه."),
     ("زبان انتزاعی",
      "تفاوت اصلی این سطح با سطح‌های پیش این است که دیگر فقط درباره اتفاق‌ها حرف نمی‌زنی، بلکه درباره مفهوم‌ها هم حرف می‌زنی. برای این کار به چند عبارت ثابت نیاز داری که ستون فقرات چنین جمله‌هایی هستند.\n"
      "وقتی می‌خواهی بگویی موضوع را از یک زاویه مشخص می‌سنجی، از **in terms of** استفاده کن: In terms of cost, it's fine.\n"
      "برای اشاره کردن به یک موضوع مشخص، **with regard to** به کار می‌رود: With regard to timing, we agree.\n"
      "اگر بخواهی بگویی همه بحث سرِ یک چیز است، **It boils down to** درست است: It boils down to trust.\n"
      "و برای گفتن اینکه چیزی را در نظر بگیر، **Bear in mind** یا **Take it into account** می‌آید.\n"
      "در همه این عبارت‌ها حرف اضافه ثابت است و عوض کردنش جمله را غلط می‌کند؛ باید همان‌طور که هستند حفظ شوند.",
      "In terms of cost, it's fine.\nWith regard to timing, we agree.\nIt boils down to trust.\nBear in mind the deadline.",
      "کدام درست است؟", "In terms of cost, it's fine.",
      "In terms of cost, it's fine. | In term of cost, it's fine. | In terms for cost, it's fine.",
      "In term of cost, it's fine. ← عبارت ثابت جمع است: in terms of.\n"
      "In terms for cost, it's fine. ← حرف اضافه درست of است.")],

 2: [("توصیف روند",
      "برای گزارش دادن از روند یک عدد، دو الگو داری و هر دو یک معنی می‌دهند.\n"
      "در الگوی اول، فعل را می‌آوری و با یک قید توصیفش می‌کنی: Sales **increased sharply** یعنی فروش به‌شدت بالا رفت.\n"
      "در الگوی دوم، همان حرف را با اسم می‌زنی و صفت جای قید می‌نشیند: There was **a sharp increase** in sales.\n"
      "الگوی دوم رسمی‌تر است و در گزارش نوشتاری بیشتر به کار می‌رود، اما در گفتار الگوی اول طبیعی‌تر است.\n"
      "یک نکته که اشتباهش رایج است: بعد از increase و decrease همیشه حرف اضافه **in** می‌آید و نه of. پس درست این است که بگویی an increase in sales.",
      "Sales increased sharply.\nThere was a sharp increase in sales.\nNumbers declined slowly.\nThere was a slight decline in numbers.",
      "کدام درست است؟", "There was an increase in sales.",
      "There was an increase in sales. | There was an increase of sales.",
      "There was an increase of sales. ← حرف اضافه درست in است."),
     ("درجه تغییر",
      "قیدهای توصیف تغییر یک طیف می‌سازند و بهتر است از تند به آرام بشناسی‌شان.\n"
      "تندترینشان **dramatically** و **sharply** هستند که تغییری ناگهانی را نشان می‌دهند.\n"
      "یک پله پایین‌تر **significantly** و **considerably** می‌آیند که از تغییری چشمگیر می‌گویند.\n"
      "قید **steadily** تغییری پیوسته و یکنواخت را می‌رساند، و **slightly** و **gradually** تغییری کم و آرام.\n"
      "برای پایان یک روند هم سه فعل داری: **level off** یعنی به ثبات نزدیک شدن، **stabilise** یعنی ثابت شدن، و **peak** یعنی به اوج رسیدن.",
      "It rose sharply.\nIt improved significantly.\nIt rose steadily.\nIt changed slightly.\nGrowth levelled off.",
      "کدام کمترین تغییر را نشان می‌دهد؟", "It changed slightly.",
      "It changed slightly. | It changed sharply.",
      "It changed sharply. ← این تغییر تند و بزرگ را نشان می‌دهد.")],

 3: [("زبان علت",
      "برای گفتن علت، چهار ساختار داری که معنایشان نزدیک است اما دستور زبانشان یکی نیست.\n"
      "بعد از **because** یک جمله کامل می‌آید، مثل because it rained.\n"
      "اما بعد از **because of** و **due to** و **owing to** فقط اسم می‌نشیند، مثل due to the rain.\n"
      "افعال **result in** و **lead to** هم اسم می‌گیرند، مثل It resulted in a delay.\n"
      "و **stem from** برای ریشه داشتن به کار می‌رود، مثل The issue stems from cost.\n"
      "نکته‌ای که باید بسپاری این است که due to پیش از جمله کامل نمی‌آید و همین‌جا اشتباه رایج می‌شود.",
      "It was cancelled due to the storm.\nIt resulted in a delay.\nThe issue stems from cost.\nPoor planning leads to problems.",
      "کدام درست است؟", "It was cancelled due to the storm.",
      "It was cancelled due to the storm. | It was cancelled due to it stormed.",
      "It was cancelled due to it stormed. ← بعد از due to اسم می‌آید نه جمله."),
     ("زبان معلول",
      "در طرف نتیجه هم چند عبارت هست که هرکدام لحن خودشان را دارند.\n"
      "برای گفتن نتیجه‌ای مستقیم **As a result** یا **As a consequence**, prices rose را می‌آوری.\n"
      "دو گزینه رسمی‌تر هم داری که **Therefore** و **Consequently** هستند.\n"
      "برای تأثیر گذاشتن، **have an impact on** به کار می‌رود، و **account for** یعنی توضیح دادن یا تشکیل دادن.\n"
      "حواست به حرف اضافه‌ها باشد چون ثابت‌اند: impact **on** و effect **on** می‌آید، اما affect خودش بدون حرف اضافه می‌نشیند.",
      "As a consequence, prices rose.\nIt had an impact on sales.\nThat accounts for the delay.\nIt affected the result.",
      "کدام درست است؟", "It had an impact on sales.",
      "It had an impact on sales. | It had an impact in sales.",
      "It had an impact in sales. ← حرف اضافه درست on است.")],

 4: [("شرطی نوع سوم",
      "شرطی نوع سوم درباره گذشته‌ای است که اصلاً رخ نداد، و ساختش این است که بعد از if گذشته کامل بیاوری و در نیمه دیگر **would have** به‌علاوه قسمت سوم فعل.\n"
      "مثلاً **If I had known**, I **would have come** یعنی نمی‌دانستم و به همین دلیل نیامدم.\n"
      "دو اشتباه اینجا از همه پرتکرارتر است. یکی اینکه would را داخل نیمه if بیاوری و بگویی If I would have known، و دیگری اینکه در نیمه دوم قسمت سوم را جا بیندازی و بگویی I would came.\n"
      "در گفتار هم کوتاه‌نویسی‌اش را زیاد می‌شنوی، چون I'd have همان I would have است.",
      "If I had known, I'd have come.\nIf we had left earlier, we wouldn't have missed it.\nIf she had asked, I would have helped.",
      "کدام درست است؟", "If I had known, I'd have come.",
      "If I had known, I'd have come. | If I would have known, I'd have come. | If I had known, I'd came.",
      "If I would have known, I'd have come. ← بعد از if، would نمی‌آید.\n"
      "If I had known, I'd came. ← نیمه دوم would have + قسمت سوم می‌خواهد."),
     ("افسوس گذشته",
      "برای گفتن یک افسوس، سه راه داری که شدتشان فرق می‌کند.\n"
      "پررنگ‌ترینشان **If only I had asked** است که حسرت را مستقیم نشان می‌دهد.\n"
      "آرام‌تر از آن **I wish I had asked** می‌آید.\n"
      "و **I should have asked** بیشتر بوی خودسرزنشی می‌دهد تا افسوس ساده.\n"
      "اگر خواستی از موضوع بگذری هم دو عبارت آماده داری: **It's water under the bridge** و **Don't dwell on it**.",
      "If only I had asked.\nI wish I had asked.\nI should have asked.\nIt's water under the bridge.",
      "کدام درست است؟", "I wish I had asked.",
      "I wish I had asked. | I wish I would have asked.",
      "I wish I would have asked. ← بعد از wish برای گذشته، had + قسمت سوم می‌آید.")],

 5: [("مرور سه شرطی",
      "سه شرطی این زبان را می‌شود در یک نگاه کنار هم دید.\n"
      "شرطی اول درباره چیزی است که ممکن است رخ دهد و به حال و آینده مربوط می‌شود، مثل If it **rains**, we**'ll** stay.\n"
      "شرطی دوم فرضی است و به حال مربوط می‌شود، مثل If it **rained**, we**'d** stay.\n"
      "و شرطی سوم غیرواقعی است و به گذشته برمی‌گردد، مثل If it **had rained**, we**'d have** stayed.\n"
      "الگویشان هم ساده است: با هر پله که پایین می‌روی، فعل یک زمان عقب‌تر می‌رود. همین یک نکته را که ببینی، هر سه را با هم داری.",
      "If it rains, we'll stay.\nIf it rained, we'd stay.\nIf it had rained, we'd have stayed.",
      "کدام درباره گذشته غیرواقعی است؟", "If it had rained, we'd have stayed.",
      "If it had rained, we'd have stayed. | If it rained, we'd stay.",
      "If it rained, we'd stay. ← این فرضی در زمان حال است، نه گذشته."),
     ("زبان فرض",
      "گاهی می‌خواهی فرضی بسازی بدون اینکه از if استفاده کنی، و برای این کار چند ابزار داری.\n"
      "با **Suppose** we start today یا **Supposing** it fails? فرض را مطرح می‌کنی.\n"
      "با **Assuming** that's true, we agree چیزی را مفروض می‌گیری و بحث را ادامه می‌دهی.\n"
      "و با **In the event of** a delay, call me از احتمالی رسمی حرف می‌زنی.\n"
      "حواست باشد که بعد از in the event of اسم می‌آید و نه جمله کامل.",
      "Suppose we start today.\nAssuming that's true, we agree.\nIn the event of a delay, call me.\nWhat if it fails?",
      "کدام درست است؟", "In the event of a delay, call me.",
      "In the event of a delay, call me. | In the event of it delays, call me.",
      "In the event of it delays, call me. ← بعد از این عبارت اسم می‌آید.")],

 6: [("شرطی مختلط",
      "شرطی مختلط وقتی به کار می‌آید که شرط در گذشته باشد اما نتیجه‌اش همین امروز.\n"
      "ساختش این است که بعد از if گذشته کامل بیاوری و در نیمه دیگر **would** به‌علاوه فعل ساده.\n"
      "مثلاً **If I had studied** law, I **would be** a lawyer now یعنی آن موقع نخواندم و به همین دلیل الان وکیل نیستم.\n"
      "حالت برعکسش هم وجود دارد، یعنی شرطی در حال با نتیجه‌ای در گذشته: **If I were** more careful, I **wouldn't have made** that mistake.",
      "If I had studied law, I'd be a lawyer now.\nIf we had saved, we'd be better off today.\nIf I were more careful, I wouldn't have made that mistake.",
      "کدام درست است؟", "If I had studied, I'd be a lawyer now.",
      "If I had studied, I'd be a lawyer now. | If I had studied, I'd have been a lawyer now.",
      "If I had studied, I'd have been a lawyer now. ← با now نتیجه در حال است و would be می‌خواهد."),
     ("پیامد امروزِ گذشته",
      "برای حرف زدن از پیامد امروزیِ گذشته، چند عبارت هست که این نگاه را طبیعی می‌کند.\n"
      "با I **ended up** here می‌گویی سر آخر کارت به کجا کشید، و با That was the **turning point** نقطه چرخش را نشان می‌دهی.\n"
      "برای مقایسه وضع فعلی، We'd be **better off** یا **worse off** now را داری.\n"
      "و برای بستن بحث می‌گویی There's **no point in** regretting it.\n"
      "یک نکته ساختاری هم هست: بعد از no point in فعل با ing می‌آید.",
      "I ended up here.\nThat was the turning point.\nWe'd be better off now.\nThere's no point in regretting it.",
      "کدام درست است؟", "There's no point in regretting it.",
      "There's no point in regretting it. | There's no point to regret it.",
      "There's no point to regret it. ← بعد از no point in فعل ing می‌آید.")],

 7: [("but for و were it not for",
      "دو راه رسمی برای گفتن «اگر … نبود» داری که هر دو جای if را می‌گیرند.\n"
      "یکی **But for** the rain, we'd have gone است و دیگری **Were it not for** her, we'd have failed.\n"
      "هر دو با اسم می‌آیند و همین ویژگی، آن‌ها را از if جدا می‌کند.\n"
      "اگر لحن غیررسمی‌تری خواستی، **If it hadn't been for** the rain… را به کار ببر.\n"
      "نکته‌ای که نباید فراموش کنی این است که but for حرف اضافه است، پس بعدش جمله کامل نمی‌نشیند.",
      "But for the rain, we'd have gone.\nWere it not for her, we'd have failed.\nIf it hadn't been for the traffic, we'd have been early.",
      "کدام درست است؟", "But for the rain, we'd have gone.",
      "But for the rain, we'd have gone. | But for it rained, we'd have gone.",
      "But for it rained, we'd have gone. ← بعد از but for اسم می‌آید."),
     ("شرط و استثنا",
      "برای محدود کردن یک قول یا گذاشتن استثنا، چهار ابزار در اختیار داری.\n"
      "عبارت‌های **provided that** و **on condition that** یعنی به شرط اینکه، و رسمی‌ترین حالت را می‌سازند.\n"
      "عبارت **as long as** یعنی تا وقتی که و لحن روزمره‌تری دارد.\n"
      "عبارت **regardless of** شرط را بی‌اثر می‌کند و یعنی صرف‌نظر از.\n"
      "و **failing that** برای وقتی است که راه اول نتیجه ندهد.\n"
      "حواست باشد که regardless of با اسم می‌آید، مثل regardless **of the cost**.",
      "Provided that you agree, we'll start.\nAs long as it's safe, fine.\nWe'll go regardless of the cost.\nFailing that, we'll walk.",
      "کدام درست است؟", "regardless of the cost",
      "regardless of the cost | regardless the cost",
      "regardless the cost ← حرف اضافه of لازم است.")],

 8: [("would rather و had better",
      "این دو ساختار شبیه هم به نظر می‌رسند اما کارشان یکی نیست.\n"
      "عبارت **would rather** ترجیح را نشان می‌دهد، مثل I'**d rather** stay.\n"
      "اما **had better** توصیه‌ای قاطع است که ته‌مایه هشدار دارد، مثل You'**d better** ask.\n"
      "نکته مشترکشان این است که بعد از هر دو فعل ساده و بدون to می‌آید، پس نه I'd rather to stay درست است و نه You'd better to ask.\n"
      "منفیِ had better هم شکل خودش را دارد و می‌شود You'd **better not** ask.",
      "I'd rather stay here.\nYou'd better ask first.\nI'd rather not go.\nYou'd better not be late.",
      "کدام درست است؟", "You'd better ask first.",
      "You'd better ask first. | You'd better to ask first. | You had better asking first.",
      "You'd better to ask first. ← بعد از had better حرف to نمی‌آید.\n"
      "You had better asking first. ← فعل ساده می‌آید، نه ing."),
     ("would rather + فاعل دوم",
      "اگر ترجیح تو درباره کار شخص دیگری باشد، فعل بعد از would rather به گذشته می‌رود.\n"
      "پس می‌گویی I'd rather **you didn't** tell him، یعنی ترجیح می‌دهم به او نگویی.\n"
      "و به همین شکل می‌گویی I'd rather **she came** tomorrow.\n"
      "این گذشته، گذشته واقعی نیست و درست مثل شرطی دوم فقط نشانه فرضی بودن است.\n"
      "به همین دلیل I'd rather you don't tell him نادرست است، هرچند در نگاه اول طبیعی به نظر می‌رسد.",
      "I'd rather you didn't tell him.\nI'd rather she came tomorrow.\nI'd rather we waited.",
      "کدام درست است؟", "I'd rather you didn't tell him.",
      "I'd rather you didn't tell him. | I'd rather you don't tell him.",
      "I'd rather you don't tell him. ← با فاعل دوم، فعل به گذشته می‌رود.")],

 9: [("اسم‌مصدر در ساختارهای ثابت",
      "چند عبارت ثابت هست که همیشه بعدشان فعل با ing می‌آید و هیچ‌وقت to نمی‌گیرند.\n"
      "می‌گویی **It's no use** complaining، یعنی شکایت کردن فایده‌ای ندارد.\n"
      "می‌گویی **There's no point in** waiting، یعنی صبر کردن دلیلی ندارد.\n"
      "می‌گویی **It's worth** doing properly، یعنی ارزش دارد که درست انجام شود.\n"
      "و می‌گویی **I can't help** laughing، یعنی نمی‌توانم جلوی خنده‌ام را بگیرم.\n"
      "این فهرست کوتاه است و بهتر است همین‌طور که هست حفظش کنی.",
      "It's no use complaining.\nThere's no point in waiting.\nIt's worth doing properly.\nI can't help laughing.",
      "کدام درست است؟", "It's no use complaining.",
      "It's no use complaining. | It's no use to complain.",
      "It's no use to complain. ← این عبارت فعل ing می‌گیرد."),
     ("to که حرف اضافه است",
      "بزرگ‌ترین دام این سطح جایی است که to حرف اضافه است و نه نشانه مصدر، و به همین دلیل بعدش فعل با ing می‌آید.\n"
      "پس درست این است که بگویی **look forward to** hear**ing** from you.\n"
      "و به همین شکل **be used to** work**ing** late و **object to** be**ing** rushed و **get around to** fix**ing** it.\n"
      "جمله «I look forward to hear from you» پرتکرارترین اشتباه ایمیل کاری است، و همین یک نکته اگر جا بیفتد، نوشته‌ات را حرفه‌ای‌تر نشان می‌دهد.",
      "I look forward to hearing from you.\nI'm used to working late.\nI object to being rushed.\nI got around to fixing it.",
      "کدام درست است؟", "I look forward to hearing from you.",
      "I look forward to hearing from you. | I look forward to hear from you.",
      "I look forward to hear from you. ← اینجا to حرف اضافه است و فعل ing می‌گیرد.")],

 10:[("too … to و enough to",
      "این دو ساختار قرینه یکدیگرند و با هم یاد گرفتنشان آسان‌تر است.\n"
      "الگوی **too** به‌علاوه صفت و **to** یعنی بیش از آن اندازه که بشود کاری کرد، مثل It's **too complex to** explain که یعنی نمی‌شود توضیحش داد.\n"
      "الگوی صفت به‌علاوه **enough to** برعکس آن است و یعنی به‌اندازه کافی، مثل It's clear **enough to** follow که یعنی می‌شود دنبالش کرد.\n"
      "جای enough را هم به خاطر بسپار: بعد از صفت می‌آید اما پیش از اسم می‌نشیند، مثل enough **time**.",
      "It's too complex to explain.\nIt's clear enough to follow.\nWe don't have enough time.\nIt's too technical to follow.",
      "کدام درست است؟", "It's clear enough to follow.",
      "It's clear enough to follow. | It's enough clear to follow.",
      "It's enough clear to follow. ← enough بعد از صفت می‌آید."),
     ("مصدر هدف",
      "برای گفتن اینکه کاری را چرا انجام دادی، چند شکل داری که رسمیتشان فرق می‌کند.\n"
      "ساده‌ترینش **to** به‌علاوه فعل ساده است، مثل I wrote it down **to remember**.\n"
      "رسمی‌تر از آن **in order to** و **so as to** می‌آیند، مثل **in order to** save time.\n"
      "و اگر بعدش جمله کامل می‌خواهی، **so that** را به کار ببر، مثل **so that** I'd remember.\n"
      "برای منفی کردن هم شکل خودش را دارد و می‌شود **so as not to** یا **in order not to**.\n"
      "دو چیز را ننویس: for to remember نادرست است و for remembering هم هدف را نمی‌رساند.",
      "I wrote it down to remember.\nIn order to save time, we split up.\nWe left early so as to avoid traffic.\nI wrote it down so that I'd remember.",
      "کدام درست است؟", "I wrote it down to remember.",
      "I wrote it down to remember. | I wrote it down for remember. | I wrote it down for to remember.",
      "I wrote it down for remember. ← برای بیان هدف، مصدر با to می‌آید.\n"
      "I wrote it down for to remember. ← for و to با هم نمی‌آیند.")],
}

EX = {
 1: [(R,"چندگزینه‌ای","___ cost, it's fine.","In terms of","","In terms of | In term of | In terms for","in terms of","عبارت ثابت in terms of است."),
     (R,"تطبیق","nuance","ظرافت معنایی","","nuance | context | framework","nuance","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","It ___ down to trust.","boils","","","boil down to","عبارت ثابت boil down to است."),
     (P,"متن آزاد","درست کنید: In term of cost, it's fine.","In terms of cost, it's fine.","","","in terms of","عبارت جمع است: terms."),
     (P,"متن آزاد","با bear in mind بنویسید: the deadline","Bear in mind the deadline.","","","bear in mind","جمله امری با فعل شروع می‌شود."),
     (P,"مرتب‌سازی","account / took / into / cost / we / the","We took the cost into account.","","","take into account","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","علت زیربنایی متفاوت است.","The underlying cause is different.","","","underlying","با فاعل شروع کن."),
     (P,"گفتار","It depends on your perspective.","It depends on your perspective.","","","perspective","جمله را واضح تلفظ کنید.")],

 2: [(R,"چندگزینه‌ای","There was an increase ___ sales.","in","","in | of | for","increase","حرف اضافه ثابت in است."),
     (R,"تطبیق","proportion","نسبت","","proportion | majority | average","proportion","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","Sales increased ___ . (به‌شدت)","sharply","","","sharply","قید بعد از فعل می‌آید."),
     (P,"متن آزاد","درست کنید: There was an increase of sales.","There was an increase in sales.","","","increase","حرف اضافه ثابت in است."),
     (P,"متن آزاد","با اسم بنویسید: Numbers declined slowly.","There was a slow decline in numbers.","","","decline","صفت جای قید را می‌گیرد."),
     (P,"مرتب‌سازی","off / growth / levelled","Growth levelled off.","","","level off","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","روند کلی مثبت است.","The overall trend is positive.","","","overall trend","با فاعل شروع کن."),
     (P,"گفتار","It peaked in summer, then stabilised.","It peaked in summer, then stabilised.","","","peak","جمله را واضح تلفظ کنید.")],

 3: [(R,"چندگزینه‌ای","It was cancelled ___ the storm.","due to","","due to | due | because","due to","بعد از due to اسم می‌آید."),
     (R,"تطبیق","factor","عامل","","factor | impact | outcome","factor","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","It had an impact ___ sales.","on","","","impact","حرف اضافه ثابت on است."),
     (P,"متن آزاد","درست کنید: It was cancelled due to it stormed.","It was cancelled due to the storm.","","","due to","بعد از due to اسم می‌آید نه جمله."),
     (P,"متن آزاد","با stem from بنویسید: the issue / cost","The issue stems from cost.","","","stem from","با فاعل شروع کن."),
     (P,"مرتب‌سازی","delay / resulted / a / it / in","It resulted in a delay.","","","result in","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","در نتیجه قیمت‌ها بالا رفت.","As a consequence, prices rose.","","","as a consequence","با عبارت ثابت شروع کن."),
     (P,"گفتار","We found the root cause.","We found the root cause.","","","root cause","جمله را واضح تلفظ کنید.")],

 4: [(R,"چندگزینه‌ای","If I ___ known, I'd have come.","had","","had | would have | have","had known","بعد از if گذشته کامل می‌آید."),
     (R,"تطبیق","hindsight","نگاه به گذشته","","hindsight | missed opportunity | regretful","hindsight","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","If she had asked, I ___ have helped.","would","'d","","would have","نیمه دوم با would have می‌آید."),
     (P,"متن آزاد","درست کنید: If I would have known, I'd have come.","If I had known, I'd have come.","","","had known","بعد از if، would نمی‌آید."),
     (P,"متن آزاد","افسوس بنویسید با If only: I / ask","If only I had asked.","","","if only","برای گذشته had + قسمت سوم می‌آید."),
     (P,"مرتب‌سازی","it / on / dwell / don't","Don't dwell on it.","","","dwell on","جمله امری منفی با Don't شروع می‌شود."),
     (P,"ترجمه به انگلیسی","گذشته‌ها گذشته.","It's water under the bridge.","","","water under the bridge","با فاعل It شروع کن."),
     (P,"گفتار","The mistake was entirely avoidable.","The mistake was entirely avoidable.","","","avoidable","جمله را واضح تلفظ کنید.")],

 5: [(R,"چندگزینه‌ای","___ that's true, we agree.","Assuming","","Assuming | Assume | Assumed","assuming","برای فرض، Assuming می‌آید."),
     (R,"تطبیق","contingency","پیش‌بینی احتیاطی","","contingency | likelihood | scenario","contingency","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","In the event ___ a delay, call me.","of","","","in the event of","عبارت ثابت in the event of است."),
     (P,"متن آزاد","درست کنید: In the event of it delays, call me.","In the event of a delay, call me.","","","in the event of","بعد از این عبارت اسم می‌آید."),
     (P,"متن آزاد","شرطی سوم بسازید: we / leave earlier — we / not miss it","If we had left earlier, we wouldn't have missed it.","","","foresee","شرطی سوم: had + قسمت سوم، would have."),
     (P,"مرتب‌سازی","through / deal / fell / the","The deal fell through.","","","fall through","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","نمی‌توانیم کنارش بگذاریم.","We can't rule it out.","","","rule out","با فاعل We شروع کن."),
     (P,"گفتار","On reflection, it was right.","On reflection, it was right.","","","on reflection","جمله را واضح تلفظ کنید.")],

 6: [(R,"چندگزینه‌ای","If I had studied law, I ___ a lawyer now.","would be","","would be | would have been | will be","career path","با now نتیجه در حال است."),
     (R,"تطبیق","degree","مدرک دانشگاهی","","degree | qualification | turning point","degree","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","There's no point ___ regretting it.","in","","","no point in","بعد از point حرف اضافه in می‌آید."),
     (P,"متن آزاد","درست کنید: There's no point to regret it.","There's no point in regretting it.","","","no point in","بعد از in فعل ing می‌آید."),
     (P,"متن آزاد","شرطی مختلط بسازید: we / save — we / be better off today","If we had saved, we'd be better off today.","","","well off","نیمه دوم would + فعل ساده می‌گیرد."),
     (P,"مرتب‌سازی","here / up / I / ended","I ended up here.","","","end up","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","نقطه عطف همان بود.","That was the turning point.","","","turning point","با فاعل That شروع کن."),
     (P,"گفتار","I look back with gratitude.","I look back with gratitude.","","","look back with","جمله را واضح تلفظ کنید.")],

 7: [(R,"چندگزینه‌ای","___ the rain, we'd have gone.","But for","","But for | But | Despite","but for","بعد از but for اسم می‌آید."),
     (R,"تطبیق","crucial","حیاتی","","crucial | essential | optional","crucial","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","We'll go regardless ___ the cost.","of","","","regardless of","حرف اضافه of لازم است."),
     (P,"متن آزاد","درست کنید: But for it rained, we'd have gone.","But for the rain, we'd have gone.","","","but for","بعد از but for اسم می‌آید."),
     (P,"متن آزاد","با Were it not for بنویسید: her / we fail","Were it not for her, we'd have failed.","","","were it not for","نیمه دوم would have می‌گیرد."),
     (P,"مرتب‌سازی","call / a / that / was / close","That was a close call.","","","close call","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","به لطف او تمام کردیم.","Thanks to her, we finished.","","","thanks to","با عبارت ثابت شروع کن."),
     (P,"گفتار","Failing that, we'll walk.","Failing that, we'll walk.","","","failing that","جمله را واضح تلفظ کنید.")],

 8: [(R,"چندگزینه‌ای","You'd better ___ first.","ask","","ask | to ask | asking","had better","بعد از had better فعل ساده می‌آید."),
     (R,"تطبیق","reluctant","بی‌میل","","reluctant | keen | advisable","reluctant","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","I'd rather you ___ tell him. (نگویی)","didn't","","","would rather","با فاعل دوم فعل به گذشته می‌رود."),
     (P,"متن آزاد","درست کنید: I'd rather to stay here.","I'd rather stay here.","","","would rather","بعد از would rather حرف to نمی‌آید."),
     (P,"متن آزاد","منفی کنید: You'd better be late.","You'd better not be late.","","","had better","منفی با better not ساخته می‌شود."),
     (P,"مرتب‌سازی","against / it / I'd / advise","I'd advise against it.","","","advise against","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","قبل از پذیرفتن دو بار فکر کن.","Think twice before you accept.","","","think twice","جمله امری با فعل شروع می‌شود."),
     (P,"گفتار","On second thoughts, let's wait.","On second thoughts, let's wait.","","","on second thoughts","جمله را واضح تلفظ کنید.")],

 9: [(R,"چندگزینه‌ای","I look forward to ___ from you.","hearing","","hearing | hear | to hear","look forward to","اینجا to حرف اضافه است."),
     (R,"تطبیق","frustration","کلافگی","","frustration | acceptance | patience","frustration","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","It's no use ___ . (complain)","complaining","","","it's no use","این عبارت فعل ing می‌گیرد."),
     (P,"متن آزاد","درست کنید: I look forward to hear from you.","I look forward to hearing from you.","","","look forward to","بعد از این to فعل ing می‌آید."),
     (P,"متن آزاد","با There's no point بنویسید: wait","There's no point in waiting.","","","there's no point","بعد از in فعل ing می‌آید."),
     (P,"مرتب‌سازی","it / with / terms / came / he / to","He came to terms with it.","","","come to terms with","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","بیا بهترین استفاده را از آن بکنیم.","Let's make the best of it.","","","make the best of","با Let's شروع کن."),
     (P,"گفتار","I understand your frustration.","I understand your frustration.","","","frustration","جمله را واضح تلفظ کنید.")],

 10:[(R,"چندگزینه‌ای","It's ___ complex to explain.","too","","too | enough | very","too … to","ساختار too + صفت + to است."),
     (R,"تطبیق","ambiguity","ابهام","","ambiguity | clarity | terminology","ambiguity","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","It's clear ___ to follow.","enough","","","enough to","enough بعد از صفت می‌آید."),
     (P,"متن آزاد","درست کنید: It's enough clear to follow.","It's clear enough to follow.","","","enough to","enough بعد از صفت می‌آید."),
     (P,"متن آزاد","هدف بنویسید با in order to: we / split up — save time","In order to save time, we split up.","","","in order to","بعد از عبارت ابتدایی ویرگول می‌آید."),
     (P,"مرتب‌سازی","down / break / let's / it","Let's break it down.","","","break down","با Let's شروع کن."),
     (P,"ترجمه به انگلیسی","می‌شود به زبان ساده بگویید؟","Could you put it in layman's terms?","","","layman's terms","با Could you شروع کن."),
     (P,"گفتار","Could you elaborate on that?","Could you elaborate on that?","","","elaborate","جمله را واضح تلفظ کنید.")],
}
