# -*- coding: utf-8 -*-
"""مکالمه، گرامر و تمرین دروس ۲۱ تا ۳۰ سطح B2."""

P, R = "تولیدی", "تشخیصی"

DLG = {
 21:[("A","Did you send the email?","ایمیل را فرستادی؟"),
     ("B","Yes. I began with 'I am writing to confirm'.","بله. با «بدین‌وسیله برای تأیید می‌نویسم» شروع کردم."),
     ("A","Did you attach the report?","گزارش را پیوست کردی؟"),
     ("B","Yes — please find attached.","بله — به پیوست تقدیم شده."),
     ("A","Ask for a reply soon.","بخواه زود پاسخ دهند."),
     ("B","I wrote 'at your earliest convenience'.","نوشتم «در اولین فرصت».")],
 22:[("A","What do the findings show?","یافته‌ها چه نشان می‌دهند؟"),
     ("B","The data suggests a substantial rise.","داده‌ها حاکی از افزایش قابل توجهی است."),
     ("A","Was the sample large enough?","نمونه به‌اندازه کافی بزرگ بود؟"),
     ("B","No. That's one limitation.","نه. این یکی از محدودیت‌هاست."),
     ("A","So the evidence isn't conclusive.","پس شواهد قطعی نیست."),
     ("B","Correct. These are preliminary results.","درست است. این‌ها نتایج مقدماتی‌اند.")],
 23:[("A","Is this email too stiff?","این ایمیل خیلی خشک است؟"),
     ("B","A little. Soften the wording.","کمی. نگارش را نرم کن."),
     ("A","Should I keep it formal?","رسمی نگهش دارم؟"),
     ("B","Keep the tone neutral. Avoid slang.","لحن را خنثی نگه دار. از زبان عامیانه پرهیز کن."),
     ("A","And the jargon?","و اصطلاحات صنفی؟"),
     ("B","Cut it. Write in plain English.","حذفش کن. به انگلیسی ساده بنویس.")],
 24:[("A","How was the exam?","امتحان چطور بود؟"),
     ("B","A piece of cake, honestly.","راستش خیلی آسان."),
     ("A","Lucky you. Mine cost me an arm and a leg.","خوش به حالت. مال من خیلی برایم گران تمام شد."),
     ("B","We're all in the same boat this term.","این ترم همه هم‌سرنوشتیم."),
     ("A","Shall we stop for today?","امروز بس کنیم؟"),
     ("B","Yes, let's call it a day.","بله، کار را تمام کنیم.")],
 25:[("A","Have you made a decision?","تصمیم گرفته‌ای؟"),
     ("B","Not yet. We did research first.","هنوز نه. اول تحقیق کردیم."),
     ("A","Are you making progress?","پیشرفت می‌کنی؟"),
     ("B","Yes, and we'll meet the deadline.","بله، و به مهلت می‌رسیم."),
     ("A","Did anyone raise a concern?","کسی نگرانی مطرح کرد؟"),
     ("B","One. But we reached an agreement.","یکی. اما به توافق رسیدیم.")],
 26:[("A","How's the new role?","نقش تازه چطور است؟"),
     ("B","I took on more than I expected.","بیشتر از آنچه انتظار داشتم بر عهده گرفتم."),
     ("A","Are you falling behind?","عقب می‌افتی؟"),
     ("B","A bit. I need to catch up on paperwork.","کمی. باید عقب‌ماندگی کاغذبازی را جبران کنم."),
     ("A","Did you run into problems?","به مشکلی برخوردی؟"),
     ("B","A few, but it worked out.","چندتایی، اما درست شد.")],
 27:[("A","Why does my English sound odd?","چرا انگلیسی‌ام ناجور به گوش می‌رسد؟"),
     ("B","You're using literal translation.","داری لفظ‌به‌لفظ ترجمه می‌کنی."),
     ("A","So what should I do?","پس چه کار کنم؟"),
     ("B","Learn words in context, in chunks.","واژه‌ها را در بافت و قطعه‌قطعه یاد بگیر."),
     ("A","Does imitating help?","تقلید کمک می‌کند؟"),
     ("B","A lot. Shadowing improves your rhythm.","خیلی. تکرار همزمان ریتمت را بهتر می‌کند.")],
 28:[("A","Can we build a case for it?","می‌توانیم برایش استدلال بسازیم؟"),
     ("B","Yes, but we need convincing evidence.","بله، اما به شواهد قانع‌کننده نیاز داریم."),
     ("A","The cost is a weak point.","هزینه نقطه ضعف است."),
     ("B","Then back it up with the long-term benefit.","پس با فایده بلندمدت پشتیبانی‌اش کن."),
     ("A","Will the board agree?","هیئت‌مدیره موافقت می‌کند؟"),
     ("B","If we win over the sceptics, yes.","اگر تردیدکنندگان را جلب کنیم، بله.")],
 29:[("A","They'll say it's too expensive.","می‌گویند خیلی گران است."),
     ("B","Admittedly, it is. But that's a valid concern.","باید اعتراف کرد که هست. اما نگرانی بجایی است."),
     ("A","How do we answer it?","چطور پاسخش را بدهیم؟"),
     ("B","Address it early, before they raise it.","زود بهش بپرداز، پیش از اینکه مطرحش کنند."),
     ("A","And if they don't accept that?","و اگر نپذیرند؟"),
     ("B","We give some ground and make one concession.","کمی عقب می‌نشینیم و یک امتیاز می‌دهیم.")],
 30:[("A","You've finished upper intermediate.","سطح متوسط بالا را تمام کرده‌ای."),
     ("B","I've started to think in English.","شروع کرده‌ام به انگلیسی فکر کنم."),
     ("A","Do you still translate in your head?","هنوز در ذهنت ترجمه می‌کنی؟"),
     ("B","Rarely. But I've hit a plateau.","به‌ندرت. اما به فلات رسیده‌ام."),
     ("A","Everyone does. Push through it.","هرکسی می‌رسد. از سدش بگذر."),
     ("B","I will. Daily exposure matters most.","می‌گذرم. مواجهه روزانه از همه مهم‌تر است.")],
}

GR = {
 21:[("ساختار نامه رسمی",
      "هر ایمیل کاری چهار جای ثابت دارد که خواننده منتظرشان است.\n"
      "برای شروع می‌نویسی I am writing to confirm… یا Further to our call….\n"
      "برای درخواست، I would be grateful if you could… را می‌آوری که مؤدبانه‌ترین شکل است.\n"
      "برای اشاره به پیوست Please find attached… به کار می‌رود.\n"
      "و برای پایان، I look forward to hearing from you و بعد Yours sincerely, می‌نویسی.\n"
      "یک نکته لحنی را رعایت کن: در متن رسمی کوتاه‌نویسی نمی‌آید، پس I am می‌نویسی و نه I'm.",
      "I am writing to confirm the order.\nI would be grateful if you could confirm.\nPlease find attached the report.\nI look forward to hearing from you.",
      "کدام درست است؟", "I am writing to confirm the order.",
      "I am writing to confirm the order. | I write to confirm the order. | I'm writing for confirm the order.",
      "I write to confirm the order. ← در این عبارت ثابت، حال استمراری می‌آید.\n"
      "I'm writing for confirm the order. ← برای بیان هدف مصدر با to می‌آید، و در متن رسمی کوتاه‌نویسی نمی‌آید."),
     ("درخواست غیرمستقیم",
      "هرچه ساختار درخواستت غیرمستقیم‌تر باشد، مؤدبانه‌تر شنیده می‌شود.\n"
      "جمله Send it today دستور است و در ایمیل کاری تند به نظر می‌رسد.\n"
      "یک پله بالاتر **Could you** send it today? می‌آید که مؤدبانه است.\n"
      "و رسمی‌ترین شکل **I would be grateful if you could** send it today است.\n"
      "دو نکته را جا نینداز: بعد از would be grateful if فعل could می‌آید و نه will، و در پایان نامه هم I look forward to hear**ing** با ing نوشته می‌شود.",
      "Could you send it today?\nI would be grateful if you could send it.\nI look forward to hearing from you.\nPlease acknowledge receipt.",
      "کدام درست است؟", "I look forward to hearing from you.",
      "I look forward to hearing from you. | I look forward to hear from you.",
      "I look forward to hear from you. ← اینجا to حرف اضافه است و فعل ing می‌گیرد.")],

 22:[("زبان گزارش",
      "در گزارش، ادعا را به داده می‌چسبانند و نه به خودِ نویسنده، چون این کار حرف را قابل اتکاتر می‌کند.\n"
      "پس به‌جای I think costs rose می‌نویسی **The data suggests that** costs rose.\n"
      "به همین شکل **The findings indicate** a rise را داری.\n"
      "و وقتی می‌خواهی پایه توصیه‌ات را نشان دهی، **Based on** the sample, we recommend waiting می‌نویسی.\n"
      "یک نکته دستوری هم هست: در انگلیسی امروزی فعل همراه data معمولاً مفرد می‌آید، پس the data **suggests** درست است.",
      "The data suggests a rise.\nThe findings indicate a problem.\nBased on the data, we recommend waiting.\nAs shown in the table…",
      "کدام درست است؟", "The data suggests a rise.",
      "The data suggests a rise. | The data suggest that rise.",
      "The data suggest that rise. ← در کاربرد امروزی فعل مفرد می‌آید و that اینجا جا ندارد."),
     ("محدودیت و احتیاط در گزارش",
      "گزارش خوب ضعف‌های خودش را هم می‌گوید و همین است که به آن اعتبار می‌دهد.\n"
      "می‌نویسی The study has **limitations** تا محدوده کار روشن باشد.\n"
      "می‌نویسی The evidence isn't **conclusive** وقتی شواهد قطعی نیستند.\n"
      "می‌نویسی These are **preliminary** results اگر کار هنوز تمام نشده.\n"
      "و می‌نویسی Figures are **subject to change** وقتی ارقام ممکن است عوض شوند.\n"
      "برخلاف تصور، این جمله‌ها اعتبار گزارش را بالا می‌برند و نه پایین.",
      "The study has limitations.\nThe evidence isn't conclusive.\nThese are preliminary results.\nFigures are subject to change.",
      "کدام درست است؟", "Figures are subject to change.",
      "Figures are subject to change. | Figures are subject for change.",
      "Figures are subject for change. ← عبارت ثابت subject to است.")],

 23:[("ثبت رسمی و غیررسمی",
      "یک پیام واحد را می‌شود با سه لحن متفاوت گفت و انتخاب میان آن‌ها به موقعیت بستگی دارد.\n"
      "در لحن غیررسمی می‌گویی **Can you** send it؟\n"
      "در لحن خنثی **Could you** send it, please? را به کار می‌بری.\n"
      "و در لحن رسمی **I would be grateful if you could** send it می‌نویسی.\n"
      "نشانه‌های رسمی بودن را هم بشناس: کوتاه‌نویسی نمی‌آید، فعل‌های لاتین‌تبار جای فعل‌های ساده می‌نشینند مثل request به‌جای ask for، و جمله‌ها بلندتر می‌شوند.",
      "Can you send it?\nCould you send it, please?\nI would be grateful if you could send it.\nWe request confirmation.",
      "کدام رسمی‌ترین است؟", "I would be grateful if you could send it.",
      "I would be grateful if you could send it. | Can you send it?",
      "Can you send it? ← این غیررسمی‌ترین شکل است."),
     ("چرا انگلیسی غیرمستقیم‌تر است",
      "در فارسی جمله مستقیم مؤدبانه به گوش می‌رسد، اما در محیط کاری انگلیسی‌زبان همان جمله تند و گاهی بی‌ادبانه شنیده می‌شود.\n"
      "به‌جای «This is wrong» می‌گویی **I'm not sure this is right**.\n"
      "و به‌جای «You didn't send it» می‌گویی **I don't seem to have received it**.\n"
      "این نرم‌سازی که به آن hedging می‌گویند، در انگلیسی کاری قاعده است و نه تعارف، پس نبودش بیشتر جلب توجه می‌کند تا بودنش.",
      "I'm not sure this is right.\nI don't seem to have received it.\nThere may be a small issue.\nIt might come across as rude.",
      "کدام نرم‌تر است؟", "I'm not sure this is right.",
      "I'm not sure this is right. | This is wrong.",
      "This is wrong. ← این در محیط کاری انگلیسی تند به‌نظر می‌رسد.")],

 24:[("اصطلاح چیست",
      "اصطلاح عبارتی است که معنایش از اجزایش درنمی‌آید و باید یکجا یادش بگیری.\n"
      "عبارت **a piece of cake** یعنی خیلی آسان و ربطی به تکه کیک ندارد.\n"
      "عبارت **under the weather** یعنی کسالت داشتن.\n"
      "و **hit the roof** یعنی از کوره در رفتن.\n"
      "نکته مهم این است که اصطلاح‌ها ثابت‌اند و اجزایشان عوض نمی‌شود، پس اگر بگویی a slice of cake دیگر آن معنی را نمی‌دهد.",
      "The test was a piece of cake.\nI'm a bit under the weather.\nHe hit the roof.\nWe're all in the same boat.",
      "کدام درست است؟", "It was a piece of cake.",
      "It was a piece of cake. | It was a slice of cake.",
      "It was a slice of cake. ← اجزای اصطلاح ثابت‌اند و عوض نمی‌شوند."),
     ("کجا اصطلاح به کار ببریم",
      "اصطلاح در گفتار دوستانه و جلسه‌های غیررسمی طبیعی است، اما در گزارش و نامه رسمی بی‌جا به نظر می‌رسد.\n"
      "قاعده ساده‌اش این است که هر جا لحن رسمی شد، اصطلاح را کنار بگذار.\n"
      "نکته دوم از این هم مهم‌تر است: در اصطلاح زیاده‌روی نکن.\n"
      "دو اصطلاح در یک گفت‌وگو طبیعی است، اما پنج‌تا مصنوعی به گوش می‌رسد و اثر برعکس می‌گذارد.",
      "Let's call it a day. (گفتار)\nWe are terminating the session. (رسمی)\nIn a nutshell, it worked.\nLet's touch base on Monday.",
      "کدام برای گزارش رسمی مناسب نیست؟", "It was a piece of cake.",
      "It was a piece of cake. | The task proved straightforward.",
      "The task proved straightforward. ← این دقیقاً شکل مناسب گزارش رسمی است.")],

 25:[("make یا do",
      "این دو فعل در فارسی هر دو «کردن» ترجمه می‌شوند و به همین دلیل جایشان مدام عوض می‌شود.\n"
      "فعل **make** بیشتر برای ساختن و پدید آوردن چیزی می‌آید، مثل make a decision و make progress و make an effort و make a mistake.\n"
      "فعل **do** بیشتر برای انجام دادن کار و فعالیت به کار می‌رود، مثل do research و do business و do a favour و do the housework.\n"
      "قاعده قطعی‌ای در کار نیست که بشود همیشه به آن تکیه کرد، پس بهترین راه این است که هر ترکیب را به‌صورت یک واحد حفظ کنی.",
      "We made a decision.\nWe did research first.\nShe made an effort.\nCould you do me a favour?",
      "کدام درست است؟", "We made a decision.",
      "We made a decision. | We did a decision.",
      "We did a decision. ← ترکیب درست make a decision است."),
     ("هم‌آیی واژگانی",
      "بعضی واژه‌ها فقط با شریک‌های مشخصی می‌آیند و جایگزین کردن آن شریک، جمله را غیرطبیعی می‌کند.\n"
      "می‌گویی **heavy** traffic و نه big traffic.\n"
      "می‌گویی **strong** argument و نه powerful argument.\n"
      "و ترکیب‌هایی مثل **deeply** concerned و **highly** likely و **widely** accepted هم از همین جنس‌اند.\n"
      "به این‌ها هم‌آیی می‌گویند، و نکته‌شان این است که درست بودن گرامری کافی نیست؛ جمله باید طبیعی هم به گوش برسد.",
      "We hit heavy traffic.\nThat's a strong argument.\nWe're deeply concerned.\nIt's highly likely.",
      "کدام درست است؟", "We hit heavy traffic.",
      "We hit heavy traffic. | We hit big traffic.",
      "We hit big traffic. ← هم‌آیی درست heavy traffic است.")],

 26:[("افعال عبارتی چندمعنایی",
      "یک فعل با حرف اضافه‌های مختلف، معناهایی کاملاً متفاوت می‌سازد.\n"
      "عبارت **get by** یعنی گذران کردن و **get through** یعنی به پایان رساندن.\n"
      "عبارت **get over** یعنی کنار آمدن با چیزی و **get on with** یعنی کنار آمدن با کسی.\n"
      "همین الگو برای take و put و bring هم برقرار است، پس با یک فعل ده‌ها معنی می‌سازی.\n"
      "به همین دلیل حدس زدن معنی از روی اجزا کار نمی‌کند و باید هرکدام را یکجا حفظ کنی.",
      "We get by on a small budget.\nI got through the whole report.\nShe got over the setback.\nWe get on with our neighbours.",
      "کدام یعنی «به پایان رساندم»؟", "I got through the report.",
      "I got through the report. | I got by the report.",
      "I got by the report. ← get by یعنی گذران کردن، نه به پایان رساندن."),
     ("جدایی‌پذیر یا نه",
      "افعال عبارتی دو دسته‌اند و تفاوتشان در این است که مفعول کجا می‌نشیند.\n"
      "دسته جدایی‌پذیر مفعول را وسط می‌گیرند، مثل put **it** off و take **it** on و bring **it** up.\n"
      "در این دسته، اگر مفعول ضمیر باشد حتماً باید وسط بیاید، پس put it off درست است و put off it نادرست.\n"
      "دسته جدایی‌ناپذیر مفعول را وسط نمی‌گیرند، مثل look into it و run into a problem و get over it.\n"
      "در این دسته برعکس عمل می‌کنی و چیزی وسط نمی‌گذاری، پس look it into نادرست است.",
      "We put it off.\nHe brought it up.\nWe'll look into it.\nWe ran into a problem.",
      "کدام درست است؟", "We put it off.",
      "We put it off. | We put off it.",
      "We put off it. ← با ضمیر، مفعول بین فعل و حرف اضافه می‌آید.")],

 27:[("چرا طبیعی به گوش نمی‌رسد",
      "وقتی جمله‌ات درست است اما طبیعی به گوش نمی‌رسد، معمولاً یکی از سه دلیل زیر در کار است.\n"
      "دلیل اول و مهم‌ترینش ترجمه لفظ‌به‌لفظ از فارسی است.\n"
      "دلیل دوم هم‌آیی نادرست است، مثل اینکه به‌جای heavy traffic بگویی big traffic.\n"
      "و دلیل سوم الگوی نادرست تکیه و آهنگ جمله است.\n"
      "راه‌حل هر سه یکی است: یادگیری قطعه‌ای، یعنی اینکه عبارت کامل را حفظ کنی و نه واژه تنها را.",
      "Learn language in chunks.\nLearn words in context.\nAvoid literal translation.\nImitate the rhythm.",
      "کدام درست است؟", "Learn words in context.",
      "Learn words in context. | Learn words in the context of.",
      "Learn words in the context of. ← عبارت ناقص است؛ in context عبارت ثابت است."),
     ("مرور اصطلاح و هم‌آیی",
      "سه چیزی که در این چند درس یاد گرفتی شبیه هم‌اند اما یکی نیستند و بهتر است از هم جدایشان کنی.\n"
      "در **اصطلاح**، معنی از اجزا درنمی‌آید، مثل a piece of cake.\n"
      "در **هم‌آیی**، معنی روشن است اما شریک واژه ثابت است، مثل heavy traffic.\n"
      "و در **فعل عبارتی**، فعل با حرف اضافه معنایی تازه می‌سازد، مثل put off.\n"
      "هر سه یک راه یادگیری دارند و آن هم این است که در دل جمله یادشان بگیری و نه به‌تنهایی.",
      "a piece of cake (اصطلاح)\nheavy traffic (هم‌آیی)\nput off (فعل عبارتی)\nIn a nutshell, it worked.",
      "کدام هم‌آیی است؟", "heavy traffic",
      "heavy traffic | a piece of cake",
      "a piece of cake ← این اصطلاح است؛ معنی‌اش از اجزایش درنمی‌آید.")],

 28:[("ساختن یک استدلال متقاعدکننده",
      "یک استدلال متقاعدکننده چهار حلقه دارد که به همین ترتیب می‌آیند.\n"
      "حلقه اول ادعاست، مثل We should delay the launch.\n"
      "حلقه دوم دلیل است، مثل because the sample was too small.\n"
      "حلقه سوم شاهد است، مثل The data suggests a bias.\n"
      "و حلقه چهارم فایده است، مثل This protects our reputation.\n"
      "بیشتر آدم‌ها حلقه چهارم را جا می‌اندازند، و بدون آن استدلال منطقی می‌ماند اما اقناع‌کننده نمی‌شود.",
      "We should delay the launch.\nThe data suggests a bias.\nBack it up with evidence.\nEveryone benefits from it.",
      "کدام «فایده» را می‌رساند؟", "Everyone benefits from it.",
      "Everyone benefits from it. | The data suggests a bias.",
      "The data suggests a bias. ← این شاهد است، نه فایده."),
     ("زبان اقناع",
      "چند فعل کلیدی در زبان اقناع هست که حرف اضافه‌شان ثابت است.\n"
      "با **make a case for** به‌علاوه اسم، از چیزی دفاع می‌کنی.\n"
      "با **appeal to** common sense به عقل سلیم مخاطب رجوع می‌کنی.\n"
      "با **back up** a claim **with** evidence ادعایت را با شاهد پشتیبانی می‌کنی.\n"
      "و با **win over** the sceptics مخالفان را با خودت همراه می‌کنی.\n"
      "یک نکته ساختاری هم هست: فعل persuade اول مفعول می‌خواهد و بعد to، پس می‌گویی persuade **us to wait**.",
      "She made a case for delay.\nHe appealed to common sense.\nBack it up with data.\nHe persuaded us to wait.",
      "کدام درست است؟", "He persuaded us to wait.",
      "He persuaded us to wait. | He persuaded to us waiting.",
      "He persuaded to us waiting. ← ساختار درست persuade + مفعول + to است.")],

 29:[("پیش‌بینی اعتراض",
      "قوی‌ترین حرکت در یک بحث این است که اعتراض طرف مقابل را خودت زودتر مطرح کنی.\n"
      "می‌گویی **Admittedly**, it's expensive و بلافاصله **Nonetheless**, it saves time را می‌آوری.\n"
      "یا می‌گویی **Granted**, it takes time و بعد **That said**, the result lasts.\n"
      "شکل دیگرش این است که بگویی **Critics argue** it's slow, **but** the data shows otherwise.\n"
      "الگو در هر سه یکی است: اول پذیرشی کوتاه، بعد پاسخ.",
      "Admittedly, it's expensive. Nonetheless, it saves time.\nGranted, it takes time.\nThat said, there's a risk.\nCritics argue it's too slow.",
      "کدام پذیرش کوتاه است؟", "Admittedly, it's expensive.",
      "Admittedly, it's expensive. | Nonetheless, it saves time.",
      "Nonetheless, it saves time. ← این پاسخِ بعد از پذیرش است، نه خود پذیرش."),
     ("پاسخ دادن بدون درگیری",
      "سه حرکت هست که در پاسخ به اعتراض، بحث را باز نگه می‌دارد و به درگیری نمی‌کشاند.\n"
      "با **That's a valid concern** نشان می‌دهی نگرانی طرف مقابل را جدی گرفته‌ای.\n"
      "با **Let me address that** تعهد می‌دهی که پاسخش را می‌دهی.\n"
      "و با **We could make one concession** امتیازی محدود می‌دهی تا بحث پیش برود.\n"
      "حواست به ترکیب درست باشد: address a concern گفته می‌شود و نه answer to a concern.",
      "That's a valid concern.\nLet me address that concern.\nWe made one concession.\nLet me reassure you.",
      "کدام درست است؟", "Let me address that concern.",
      "Let me address that concern. | Let me answer to that concern.",
      "Let me answer to that concern. ← فعل answer حرف اضافه to نمی‌گیرد و اینجا address طبیعی‌تر است.")],

 30:[("جمع‌بندی B2 — ساختارها",
      "این سطح روی پنج ساختار بنا شده که همه‌شان یک هدف مشترک دارند.\n"
      "با **شرطی سوم و مختلط** از گذشته‌ای می‌گویی که رخ نداد و اثرش تا امروز مانده، مثل If I had known… و …I'd be there now.\n"
      "با **وارونگی** به جمله لحنی رسمی و تأکیدی می‌دهی، مثل Never have I seen it.\n"
      "با **جمله شکافته** موضوع اصلی را برجسته می‌کنی، مثل What matters is trust.\n"
      "با **عبارت وجه وصفی** دو جمله را در یکی فشرده می‌کنی، مثل Having finished, she left.\n"
      "و با **زبان احتیاطی** ادعایت را نرم و دقیق نگه می‌داری، مثل It tends to be the case.\n"
      "نکته مشترک هر پنج این است که ابزار کنترل لحن‌اند و نه فقط گرامر.",
      "If I had known, I'd have come.\nNever have I seen it.\nWhat matters is trust.\nHaving finished, she left.\nIt tends to be the case.",
      "کدام برای تأکید است؟", "Never have I seen it.",
      "Never have I seen it. | It tends to be the case.",
      "It tends to be the case. ← این زبان احتیاطی است، نه تأکید."),
     ("جمع‌بندی B2 — کارکردها",
      "کنار آن ساختارها، حالا چهار کار هست که می‌توانی به انگلیسی انجام دهی.\n"
      "می‌توانی ارائه بدهی، با جمله‌هایی مثل I'd like to begin by….\n"
      "می‌توانی نامه رسمی بنویسی، با I am writing to….\n"
      "می‌توانی استدلال کنی و طرف مقابل را قانع کنی، با Admittedly… Nonetheless….\n"
      "و می‌توانی لحنت را تنظیم کنی، یعنی یک پیام واحد را رسمی یا دوستانه بگویی.\n"
      "قدم بعدی C1 است که سه چیز تازه می‌آورد: زبان تخصصی، طنز، و ظرافت فرهنگی.",
      "I'd like to begin by outlining the plan.\nI am writing to confirm.\nAdmittedly, it's expensive.\nLet's soften the wording.",
      "کدام برای نامه رسمی است؟", "I am writing to confirm.",
      "I am writing to confirm. | Let's call it a day.",
      "Let's call it a day. ← این اصطلاح گفتاری است و در نامه رسمی جا ندارد.")],
}

EX = {
 21:[(R,"چندگزینه‌ای","I ___ to confirm the order.","am writing","","am writing | write | am write","I am writing to","در این عبارت ثابت حال استمراری می‌آید."),
     (R,"تطبیق","attachment","پیوست","","attachment | recipient | enquiry","attachment","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","I would be grateful if you ___ confirm.","could","","","I would be grateful if","بعد از این عبارت could می‌آید."),
     (P,"متن آزاد","درست کنید: I look forward to hear from you.","I look forward to hearing from you.","","","further to","اینجا to حرف اضافه است."),
     (P,"متن آزاد","رسمی کنید: Send me the report today.","I would be grateful if you could send me the report today.","","","prompt","با I would be grateful شروع کن."),
     (P,"مرتب‌سازی","receipt / please / acknowledge","Please acknowledge receipt.","","","acknowledge receipt","با Please شروع کن."),
     (P,"ترجمه به انگلیسی","لطفاً در اولین فرصت پاسخ دهید.","Please reply at your earliest convenience.","","","at your earliest convenience","با Please شروع کن."),
     (P,"گفتار","Please find attached the report.","Please find attached the report.","","","please find attached","جمله را واضح تلفظ کن.")],

 22:[(R,"چندگزینه‌ای","The data ___ a rise.","suggests","","suggests | suggest | is suggest","the data suggests","با data فعل مفرد به کار می‌رود."),
     (R,"تطبیق","methodology","روش‌شناسی","","methodology | sample | correlation","methodology","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","Figures are subject ___ change.","to","","","subject to change","عبارت ثابت subject to است."),
     (P,"متن آزاد","درست کنید: Figures are subject for change.","Figures are subject to change.","","","subject to change","حرف اضافه درست to است."),
     (P,"متن آزاد","محتاطانه بنویسید: results / not final","These are preliminary results.","","","preliminary","با These are شروع کن."),
     (P,"مرتب‌سازی","limitations / study / has / the","The study has limitations.","","","limitation","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","خلاصه اینکه روند صعودی است.","In summary, the trend is upward.","","","in summary","با عبارت ثابت شروع کن."),
     (P,"گفتار","The evidence isn't conclusive.","The evidence isn't conclusive.","","","conclusive","جمله را واضح تلفظ کن.")],

 23:[(R,"چندگزینه‌ای","Keep the tone ___ .","neutral","","neutral | stiff | colloquial","neutral","لحن خنثی با neutral بیان می‌شود."),
     (R,"تطبیق","jargon","اصطلاحات صنفی","","jargon | slang | plain English","jargon","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","It might come ___ as rude.","across","","","come across as rude","عبارت ثابت come across as است."),
     (P,"متن آزاد","نرم کنید: This is wrong.","I'm not sure this is right.","","","soften","با I'm not sure شروع کن."),
     (P,"متن آزاد","رسمی کنید: Can you send it?","Could you send it, please?","I would be grateful if you could send it.","","formality","با Could you شروع کن."),
     (P,"مرتب‌سازی","English / plain / in / write","Write in plain English.","","","plain English","جمله امری با فعل شروع می‌شود."),
     (P,"ترجمه به انگلیسی","بیا نگارش را نرم کنیم.","Let's soften the wording.","","","soften","با Let's شروع کن."),
     (P,"گفتار","The email strikes the right note.","The email strikes the right note.","","","strike the right note","جمله را واضح تلفظ کن.")],

 24:[(R,"چندگزینه‌ای","It was a ___ of cake.","piece","","piece | slice | part","a piece of cake","اجزای اصطلاح ثابت‌اند."),
     (R,"تطبیق","break the ice","یخ را شکستن","","break the ice | call it a day | cut corners","break the ice","اصطلاح را به معنی درست وصل کن."),
     (P,"جای خالی","We're all in the same ___ .","boat","","","in the same boat","اصطلاح ثابت in the same boat است."),
     (P,"متن آزاد","درست کنید: It was a slice of cake.","It was a piece of cake.","","","a piece of cake","اجزای اصطلاح عوض نمی‌شوند."),
     (P,"متن آزاد","با اصطلاح بنویسید: بیا کار امروز را تمام کنیم","Let's call it a day.","","","call it a day","با Let's شروع کن."),
     (P,"مرتب‌سازی","time / an / eye / on / keep / the","Keep an eye on the time.","","","keep an eye on","جمله امری با فعل شروع می‌شود."),
     (P,"ترجمه به انگلیسی","به نقطه اول برگشتیم.","We're back to square one.","","","back to square one","با فاعل We شروع کن."),
     (P,"گفتار","In a nutshell, it worked.","In a nutshell, it worked.","","","in a nutshell","جمله را واضح تلفظ کن.")],

 25:[(R,"چندگزینه‌ای","We ___ a decision quickly.","made","","made | did | took","make a decision","ترکیب درست make a decision است."),
     (R,"تطبیق","collocation","هم‌آیی واژگانی","","collocation | strong argument | heavy traffic","collocation","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","We ___ research first.","did","","","do research","ترکیب درست do research است."),
     (P,"متن آزاد","درست کنید: We hit big traffic.","We hit heavy traffic.","","","heavy traffic","هم‌آیی درست heavy traffic است."),
     (P,"متن آزاد","با make بنویسید: she / a real effort","She made a real effort.","","","make an effort","ترکیب make an effort است."),
     (P,"مرتب‌سازی","deadline / met / we / the","We met the deadline.","","","meet a deadline","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","به توافق رسیدند.","They reached an agreement.","","","reach an agreement","با فاعل They شروع کن."),
     (P,"گفتار","That's a strong argument.","That's a strong argument.","","","strong argument","جمله را واضح تلفظ کن.")],

 26:[(R,"چندگزینه‌ای","We put ___ off.","it","","it | off it | it off","put off","با ضمیر، مفعول وسط می‌آید."),
     (R,"تطبیق","take on","بر عهده گرفتن","","take on | take over | take up","take on","فعل عبارتی را به معنی درست وصل کن."),
     (P,"جای خالی","I need to catch up ___ work.","on","","","catch up on","حرف اضافه ثابت on است."),
     (P,"متن آزاد","درست کنید: We put off it.","We put it off.","","","put off","با ضمیر، مفعول وسط می‌آید."),
     (P,"متن آزاد","با come up with بنویسید: she / a solution","She came up with a solution.","","","come up with","با فاعل شروع کن."),
     (P,"مرتب‌سازی","problem / a / into / we / ran","We ran into a problem.","","","run into","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","از برنامه عقب افتادیم.","We fell behind schedule.","","","fall behind","با فاعل We شروع کن."),
     (P,"گفتار","Her work stands out.","Her work stands out.","","","stand out","جمله را واضح تلفظ کن.")],

 27:[(R,"چندگزینه‌ای","Learn words ___ context.","in","","in | on | at","in context","عبارت ثابت in context است."),
     (R,"تطبیق","intonation","آهنگ کلام","","intonation | stress pattern | chunk","intonation","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","Avoid ___ translation.","literal","","","literal translation","ترجمه لفظ‌به‌لفظ یعنی literal translation."),
     (P,"متن آزاد","درست کنید: Learn words in the context of.","Learn words in context.","","","in context","عبارت ثابت in context است."),
     (P,"متن آزاد","بنویسید: memorise / whole phrases","Memorise whole phrases.","","","memorise","جمله امری با فعل شروع می‌شود."),
     (P,"مرتب‌سازی","rhythm / the / imitate","Imitate the rhythm.","","","imitate","جمله امری با فعل شروع می‌شود."),
     (P,"ترجمه به انگلیسی","از فیلم‌ها یادش گرفتم.","I picked it up from films.","","","pick up","با فاعل I شروع کن."),
     (P,"گفتار","It sounds natural now.","It sounds natural now.","","","sound natural","جمله را واضح تلفظ کن.")],

 28:[(R,"چندگزینه‌ای","He persuaded ___ wait.","us to","","us to | to us | us for","persuade","ساختار persuade + مفعول + to است."),
     (R,"تطبیق","stakeholder","ذی‌نفع","","stakeholder | proposal | rebuttal","stakeholder","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","Back it up ___ data.","with","","","back up","حرف اضافه ثابت with است."),
     (P,"متن آزاد","درست کنید: He persuaded to us waiting.","He persuaded us to wait.","","","persuade","ساختار persuade + مفعول + to است."),
     (P,"متن آزاد","با make a case for بنویسید: she / delay","She made a case for delay.","","","make a case for","با فاعل شروع کن."),
     (P,"مرتب‌سازی","sceptics / over / won / she / the","She won over the sceptics.","","","win over","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","به فایده بلندمدت فکر کن.","Think about the long-term benefit.","","","long-term","جمله امری با فعل شروع می‌شود."),
     (P,"گفتار","It's a compelling argument.","It's a compelling argument.","","","compelling","جمله را واضح تلفظ کن.")],

 29:[(R,"چندگزینه‌ای","___ , it's expensive. Nonetheless, it saves time.","Admittedly","","Admittedly | Nonetheless | That said","admittedly","پذیرش کوتاه با Admittedly بیان می‌شود."),
     (R,"تطبیق","concession","امتیاز دادن","","concession | objection | rebuttal","concession","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","Let me ___ that concern.","address","","","address a concern","فعل درست address است."),
     (P,"متن آزاد","درست کنید: Let me answer to that concern.","Let me address that concern.","","","address a concern","answer حرف اضافه to نمی‌گیرد."),
     (P,"متن آزاد","پذیرش و پاسخ بنویسید: it takes time / the result lasts","Granted, it takes time. That said, the result lasts.","","","granted","اول پذیرش، بعد پاسخ."),
     (P,"مرتب‌سازی","concern / a / that's / valid","That's a valid concern.","","","valid concern","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","بگذارید به شما اطمینان بدهم.","Let me reassure you.","","","reassure","با Let me شروع کن."),
     (P,"گفتار","Critics argue it's too slow.","Critics argue it's too slow.","","","critics argue","جمله را واضح تلفظ کن.")],

 30:[(R,"چندگزینه‌ای","Never ___ such a change.","have I seen","","have I seen | I have seen | I saw","upper intermediate","با قید منفی آغازین وارونگی لازم است."),
     (R,"تطبیق","proficiency","تسلط","","proficiency | plateau | consolidation","proficiency","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","I've started to think ___ English.","in","","","think in English","عبارت ثابت think in English است."),
     (P,"متن آزاد","درست کنید: What matters are trust.","What matters is trust.","","","abstract topic","فعل با what مفرد است."),
     (P,"متن آزاد","شرطی سوم بنویسید: I / know — I / come","If I had known, I'd have come.","","","hold your own","شرطی سوم: had + قسمت سوم، would have."),
     (P,"مرتب‌سازی","through / plateau / push / the","Push through the plateau.","","","push through","جمله امری با فعل شروع می‌شود."),
     (P,"ترجمه به انگلیسی","مواجهه روزانه از همه مهم‌تر است.","Daily exposure matters most.","","","daily exposure","با فاعل شروع کن."),
     (P,"گفتار","Congratulations on finishing the level.","Congratulations on finishing the level.","","","congratulations","جمله را واضح تلفظ کن.")],
}
