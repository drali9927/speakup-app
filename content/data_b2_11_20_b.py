# -*- coding: utf-8 -*-
"""مکالمه، گرامر و تمرین دروس ۱۱ تا ۲۰ سطح B2."""

P, R = "تولیدی", "تشخیصی"

DLG = {
 11:[("A","Have you read the draft?","پیش‌نویس را خوانده‌ای؟"),
     ("B","I have. It's too wordy.","بله. زیادی پرگوست."),
     ("A","Can you condense it?","می‌شود فشرده‌اش کنی؟"),
     ("B","Having read it twice, I'd cut two paragraphs.","پس از دو بار خواندن، دو پاراگراف حذف می‌کنم."),
     ("A","Some parts are redundant.","بعضی بخش‌ها زائدند."),
     ("B","Agreed. I'll polish it and keep it concise.","موافقم. صیقلش می‌دهم و موجز نگه می‌دارم.")],
 12:[("A","What did you think of the result?","نظرت درباره نتیجه چه بود؟"),
     ("B","Never have I seen such a change.","هرگز چنین تغییری ندیده بودم."),
     ("A","Was it that unusual?","این‌قدر غیرعادی بود؟"),
     ("B","Not only did it rise, it doubled.","نه‌تنها بالا رفت، دو برابر شد."),
     ("A","Rarely does that happen.","به‌ندرت چنین چیزی رخ می‌دهد."),
     ("B","Indeed. It was unprecedented.","به‌راستی. بی‌سابقه بود.")],
 13:[("A","Who suggested the change?","چه کسی تغییر را پیشنهاد داد؟"),
     ("B","It was you who said it first.","این تو بودی که اول گفتی."),
     ("A","I was misquoted, actually.","راستش حرفم نادرست نقل شد."),
     ("B","Then let's set the record straight.","پس بیا حقیقت را روشن کنیم."),
     ("A","It wasn't deliberate.","عمدی نبود."),
     ("B","I know. She deserves the credit anyway.","می‌دانم. به‌هرحال اعتبارش به او می‌رسد.")],
 14:[("A","What's most important here?","اینجا چه چیزی از همه مهم‌تر است؟"),
     ("B","What matters is trust.","آنچه مهم است اعتماد است."),
     ("A","More than speed?","بیشتر از سرعت؟"),
     ("B","Trust outweighs speed. Safety comes first.","اعتماد بر سرعت می‌چربد. ایمنی اول است."),
     ("A","So cost is secondary.","پس هزینه فرعی است."),
     ("B","Exactly. Above all, be honest with the client.","دقیقاً. بیش از همه با مشتری صادق باش.")],
 15:[("A","How should I raise it?","چطور مطرحش کنم؟"),
     ("B","Get straight to the point.","مستقیم سر اصل مطلب برو."),
     ("A","I don't want to sound aggressive.","نمی‌خواهم پرخاشگر به‌نظر برسم."),
     ("B","Be assertive, not aggressive.","قاطع باش، نه پرخاشگر."),
     ("A","And if he pushes back?","و اگر مقاومت کرد؟"),
     ("B","Stand your ground, politely but firmly.","بر موضعت بایست، مؤدبانه اما محکم.")],
 16:[("A","Does this always happen?","این همیشه اتفاق می‌افتد؟"),
     ("B","It tends to be the case in winter.","زمستان معمولاً چنین است."),
     ("A","Is the data reliable?","داده‌ها قابل اتکاست؟"),
     ("B","The data suggests a rise, broadly.","داده‌ها به‌طور کلی حاکی از افزایش‌اند."),
     ("A","So we can be confident?","پس می‌توانیم مطمئن باشیم؟"),
     ("B","I wouldn't overstate it. Generally speaking, yes.","بزرگ‌نمایی نمی‌کنم. به‌طور کلی، بله.")],
 17:[("A","How many people came?","چند نفر آمدند؟"),
     ("B","Approximately two hundred.","تقریباً دویست نفر."),
     ("A","Do you have a precise figure?","رقم دقیق داری؟"),
     ("B","Not yet. Roughly speaking, well over a hundred and fifty.","هنوز نه. تقریباً بگویم خیلی بیشتر از صد و پنجاه."),
     ("A","And the cost?","و هزینه؟"),
     ("B","It exceeded our estimate, but by a negligible margin.","از تخمینمان فراتر رفت، اما با اختلاف ناچیز.")],
 18:[("A","Assuming that's true, what follows?","با فرض درست بودنش، نتیجه چیست؟"),
     ("B","It follows that we must delay.","نتیجه می‌شود که باید عقب بیندازیم."),
     ("A","Isn't that jumping to conclusions?","این نتیجه‌گیری شتاب‌زده نیست؟"),
     ("B","There's a flaw in the premise, I admit.","می‌پذیرم که پیش‌فرض ایرادی دارد."),
     ("A","The two reports are contradictory.","دو گزارش متناقض‌اند."),
     ("B","Then let's verify the claim first.","پس اول ادعا را راستی‌آزمایی کنیم.")],
 19:[("A","Do you agree with the plan?","با برنامه موافقی؟"),
     ("B","To some extent, yes.","تا حدی، بله."),
     ("A","Only to some extent?","فقط تا حدی؟"),
     ("B","In principle I agree, but in practice it's harder.","در اصل موافقم، اما در عمل سخت‌تر است."),
     ("A","That's a fair point.","نکته منصفانه‌ای است."),
     ("B","It isn't black and white. Let's find middle ground.","سیاه و سفید نیست. بیا حد وسط را پیدا کنیم.")],
 20:[("A","How long is your talk?","ارائه‌ات چقدر است؟"),
     ("B","Twenty minutes. I'd like to begin by outlining the plan.","بیست دقیقه. می‌خواهم با گفتن کلیات برنامه شروع کنم."),
     ("A","Will you take questions?","به سؤالات پاسخ می‌دهی؟"),
     ("B","At the end. I don't want to go over time.","پایان کار. نمی‌خواهم از وقت بگذرم."),
     ("A","Did you rehearse?","تمرین کردی؟"),
     ("B","Twice. The key message is on the last slide.","دو بار. پیام کلیدی روی اسلاید آخر است.")],
}

GR = {
 11:[("عبارت وجه وصفی",
      "وقتی دو جمله فاعل مشترک دارند، می‌توانی آن‌ها را در یک جمله فشرده کنی.\n"
      "مثلاً به‌جای She finished the report and then she left می‌گویی **Having finished** the report, she left.\n"
      "اگر دو کار همزمان باشند، شکل دیگری به کار می‌رود، مثل **While working**, he noticed it.\n"
      "شرط اصلی این فشرده‌سازی همان یکی بودن فاعل است و اگر رعایتش نکنی جمله معنای عجیبی پیدا می‌کند.\n"
      "جمله Having finished, the room was quiet دقیقاً همین مشکل را دارد، چون در ظاهر می‌گوید اتاق گزارش را تمام کرد.",
      "Having finished, she left.\nWhile working, he noticed it.\nHaving read it twice, I'd cut two paragraphs.\nUpon arrival, please register.",
      "کدام درست است؟", "Having finished the report, she left.",
      "Having finished the report, she left. | Having finished the report, the room was quiet.",
      "Having finished the report, the room was quiet. ← فاعل دو بخش یکی نیست؛ اتاق گزارش را تمام نکرده."),
     ("عبارت مجهول و کوتاه",
      "همین فشرده‌سازی در حالت مجهول هم انجام می‌شود و متن را کوتاه‌تر می‌کند.\n"
      "می‌گویی **Once completed**, send it to me که همان once it is completed است.\n"
      "و می‌گویی **Written in 1990**, the book is still popular که به جمله‌ای وصفی درباره کتاب اشاره دارد.\n"
      "این شکل‌ها متن را رسمی و موجز می‌کنند و در گزارش‌نویسی زیاد دیده می‌شوند.\n"
      "اما در گفتار روزمره کمتر به کارشان ببر، چون لحن را سنگین و کتابی می‌کنند.",
      "Once completed, send it to me.\nWritten in 1990, the book is still popular.\nGiven more time, we'd have finished.",
      "کدام درست است؟", "Once completed, send it to me.",
      "Once completed, send it to me. | Once completing, send it to me.",
      "Once completing, send it to me. ← معنی مجهول است و قسمت سوم فعل می‌خواهد.")],

 12:[("وارونگی برای تأکید",
      "وقتی جمله را با یک قید منفی شروع کنی، فعل کمکی پیش از فاعل می‌آید و ترتیب جمله شبیه پرسش می‌شود.\n"
      "پس می‌گویی **Never have I** seen such a thing و **Rarely does he** complain و **Seldom do we** agree.\n"
      "همین ترتیب است که این ساختار را می‌سازد، بنابراین Never I have seen و Rarely he complains نادرست‌اند.\n"
      "حواست به لحنش باشد، چون این ساختار رسمی و تأکیدی است و در گفتار روزمره جایی ندارد.",
      "Never have I seen such a thing.\nRarely does he complain.\nSeldom do we agree.\nAt no time did he agree.",
      "کدام درست است؟", "Never have I seen it.",
      "Never have I seen it. | Never I have seen it. | Never I saw it.",
      "Never I have seen it. ← با قید منفیِ آغازین، فعل کمکی پیش از فاعل می‌آید.\n"
      "Never I saw it. ← ترتیب وارونه لازم است: Never did I see it."),
     ("no sooner و not only",
      "دو الگوی پرکاربرد هست که هر دو همین وارونگی را می‌خواهند اما جفت‌کلمه‌شان فرق می‌کند.\n"
      "در الگوی اول می‌گویی **No sooner had** we left **than** it rained، و اینجا همیشه than می‌آید و نه when.\n"
      "در الگوی دوم می‌گویی **Hardly had** he spoken **when** the phone rang، و اینجا برعکس، when درست است.\n"
      "الگوی سوم هم **Not only did** he help, he stayed late است.\n"
      "بهترین کار این است که همان دو جفت را با هم حفظ کنی: no sooner با than و hardly با when.",
      "No sooner had we left than it rained.\nHardly had he spoken when the phone rang.\nNot only did he help, he stayed late.",
      "کدام درست است؟", "No sooner had we left than it rained.",
      "No sooner had we left than it rained. | No sooner had we left when it rained.",
      "No sooner had we left when it rained. ← جفت درست no sooner … than است.")],

 13:[("جمله شکافته با it",
      "وقتی می‌خواهی روی یک بخش از جمله تأکید کنی، آن بخش را با it جدا می‌کنی و جلو می‌آوری.\n"
      "پس You said it تبدیل می‌شود به **It was you who** said it، و The problem starts here می‌شود **It is here that** the problem starts.\n"
      "ساختارش همیشه یکی است: it می‌آید، بعد فعل be، بعد بخشی که رویش تأکید داری، بعد who یا that، و در آخر بقیه جمله.\n"
      "برای افراد who یا that می‌آوری و برای باقی چیزها فقط that.",
      "It was you who said it.\nIt is here that the problem starts.\nIt was the cost that worried us.\nIt was in May that we started.",
      "کدام درست است؟", "It was you who said it.",
      "It was you who said it. | It was you which said it. | It was you said it.",
      "It was you which said it. ← برای افراد who یا that می‌آید.\n"
      "It was you said it. ← حلقه who/that حذف نمی‌شود."),
     ("تأکید بدون شکافتن",
      "سه راه دیگر هم هست که همان تأکید را بدون این ساختار می‌سازند.\n"
      "با **The reason why** I left is simple دلیل را برجسته می‌کنی.\n"
      "با **The thing is**, we have no time نکته اصلی را جلو می‌اندازی.\n"
      "و با **All I want** is a clear answer خواسته‌ات را متمرکز می‌کنی.\n"
      "یک نکته دستوری در الگوی آخر هست که اشتباهش رایج است: بعد از All I want فعل مفرد می‌آید، حتی اگر آنچه می‌خواهی جمع باشد. پس درست این است که بگویی All I want **is** two answers.",
      "The reason why I left is simple.\nThe thing is, we have no time.\nAll I want is a clear answer.",
      "کدام درست است؟", "All I want is a clear answer.",
      "All I want is a clear answer. | All I want are a clear answer.",
      "All I want are a clear answer. ← فعل با All I want مفرد می‌آید.")],

 14:[("جمله شکافته با what",
      "برای تأکید روی موضوع اصلی جمله، آن را با what جلو می‌آوری.\n"
      "پس Trust matters تبدیل می‌شود به **What matters is** trust، و I mean we should wait می‌شود **What I mean is**, we should wait.\n"
      "ساختارش این است که با what شروع می‌کنی، بعد یک جمله می‌آوری، بعد is یا was، و در آخر بخشی که رویش تأکید داری.\n"
      "یک نکته را جا نینداز: فعل بعد از بند what همیشه مفرد است، پس می‌گویی What matters **is**….",
      "What matters is trust.\nWhat I mean is, we should wait.\nWhat happened was simple.\nWhat we need is more time.",
      "کدام درست است؟", "What matters is trust.",
      "What matters is trust. | What matter is trust. | What matters are trust.",
      "What matter is trust. ← فعل what مفرد است: matters.\n"
      "What matters are trust. ← فعل be هم مفرد می‌آید."),
     ("زبان ارزش‌ها",
      "چند ترکیب ثابت هست که برای حرف زدن از ارزش‌ها به کار می‌آیند و اجزایشان از هم جدا نمی‌شوند.\n"
      "با **at the heart of** می‌گویی چیزی در مرکز موضوع است، مثل Trust is at the heart of it.\n"
      "با **come first** اولویت را نشان می‌دهی، مثل Safety comes first.\n"
      "با **above all** مهم‌ترین نکته را جدا می‌کنی، مثل Above all, be honest.\n"
      "و با **stand for** از باورهای کسی می‌پرسی، مثل What do you stand for؟\n"
      "یک نکته هم درباره outweigh هست: این فعل حرف اضافه نمی‌گیرد، پس می‌گویی Trust outweighs speed.",
      "Trust is at the heart of it.\nSafety comes first.\nAbove all, be honest.\nTrust outweighs speed.",
      "کدام درست است؟", "Trust outweighs speed.",
      "Trust outweighs speed. | Trust outweighs on speed.",
      "Trust outweighs on speed. ← این فعل حرف اضافه نمی‌گیرد.")],

 15:[("لحن قاطع در برابر پرخاشگر",
      "تفاوت لحن قاطع با لحن پرخاشگر در ساختار جمله است و نه فقط در تن صدا.\n"
      "جمله‌های You're wrong و You never listen پرخاشگرانه‌اند، چون انگشت اتهام را مستقیم به طرف مقابل می‌گیرند.\n"
      "در مقابل، **I see your point, but** I disagree همان مخالفت را می‌رساند بدون اینکه گفت‌وگو را ببندد.\n"
      "دو عبارت دیگر هم داری که همین کار را می‌کنند: **I'd prefer** we waited و **I'm not comfortable with** that.\n"
      "قاعده کلی‌اش این است که جمله‌های I محترمانه‌تر از جمله‌های You هستند، پس به‌جای «You ignored me» بگو «I felt ignored».",
      "I see your point, but I disagree.\nI'd prefer we waited.\nI'm not comfortable with that.\nI felt ignored.",
      "کدام قاطع است اما پرخاشگر نیست؟", "I see your point, but I disagree.",
      "I see your point, but I disagree. | You're completely wrong.",
      "You're completely wrong. ← این پرخاشگرانه است و گفت‌وگو را می‌بندد."),
     ("مرور تأکید",
      "چهار ابزار تأکید این چند درس را بهتر است کنار هم ببینی، چون هرکدام جای خودشان را دارند.\n"
      "با **وارونگی** لحنی رسمی و نوشتاری می‌سازی، مثل Never have I seen it.\n"
      "با ساختار **it-cleft** روی یک بخش مشخص تأکید می‌کنی، مثل It was you who said it.\n"
      "با ساختار **what-cleft** موضوع اصلی را برجسته می‌کنی، مثل What matters is trust.\n"
      "و **تکرار و مکث** ابزار گفتار است و در نوشتار به کار نمی‌آید.\n"
      "انتخاب میان این‌ها یعنی کنترل لحن، و همین چیزی است که این سطح را می‌سازد.",
      "Never have I seen it.\nIt was you who said it.\nWhat matters is trust.\nLet me stress this point.",
      "کدام برای تأکید بر موضوع اصلی است؟", "What matters is trust.",
      "What matters is trust. | It was you who said it.",
      "It was you who said it. ← این بر یک بخش (فاعل) تأکید دارد، نه بر موضوع.")],

 16:[("زبان احتیاطی",
      "در انگلیسی حرفه‌ای، ادعای مطلق کم گفته می‌شود و چند ساختار هست که ادعا را نرم می‌کنند بی‌آنکه از دقتش کم شود.\n"
      "با **tend to** از گرایشی کلی می‌گویی، مثل Prices tend to rise in winter.\n"
      "با **appear to** و **seem to** برداشتت را محتاطانه بیان می‌کنی، مثل It appears to be working.\n"
      "و با **suggest** و **indicate** می‌گویی که شواهد به چیزی اشاره دارند، مثل The data suggests a rise.\n"
      "یک نکته ساختاری هم هست: بعد از tend to فعل ساده می‌آید، پس می‌گویی tend **to rise**.",
      "Prices tend to rise in winter.\nIt appears to be working.\nThe data suggests a rise.\nThe results indicate a problem.",
      "کدام درست است؟", "Prices tend to rise.",
      "Prices tend to rise. | Prices tend rising. | Prices tend rise.",
      "Prices tend rising. ← بعد از tend فعل با to می‌آید.\n"
      "Prices tend rise. ← حرف to لازم است."),
     ("تعمیم محتاطانه",
      "برای گفتن «معمولاً» بدون اینکه ادعایی مطلق کرده باشی، چند عبارت آماده داری.\n"
      "می‌توانی با **On the whole** یا **Generally speaking** یا **As a rule** جمله را شروع کنی.\n"
      "سه گزینه دیگر هم داری که **In most cases** و **By and large** و **More often than not** هستند.\n"
      "و برای کنار گذاشتن استثنا، **with the exception of** را با اسم می‌آوری.\n"
      "بیشتر این‌ها اول جمله می‌نشینند و بعدشان ویرگول می‌گذاری.",
      "On the whole, it works.\nGenerally speaking, it's safe.\nIn most cases, it's fine.\nWith the exception of Friday, we're free.",
      "کدام درست است؟", "With the exception of Friday, we're free.",
      "With the exception of Friday, we're free. | With exception of Friday, we're free.",
      "With exception of Friday, we're free. ← عبارت ثابت the می‌خواهد.")],

 17:[("تخمین و تقریب",
      "وقتی نمی‌خواهی عدد دقیق بدهی، چند راه برای تقریب زدن داری.\n"
      "ساده‌ترینشان آوردن **approximately** یا **roughly** یا **about** پیش از عدد است.\n"
      "برای لحن رسمی‌تر **in the region of** a thousand را داری.\n"
      "عبارت **give or take** بعد از عدد می‌نشیند، مثل ten days, give or take.\n"
      "و با **well over** و **just under** می‌گویی که عدد کمی بالاتر یا پایین‌تر است، مثل well over a hundred.\n"
      "حواست باشد که approximately پیش از عدد می‌آید و نه بعد از آن.",
      "Approximately two hundred came.\nIn the region of a thousand.\nTen days, give or take.\nWell over a hundred applied.",
      "کدام درست است؟", "Approximately two hundred came.",
      "Approximately two hundred came. | Two hundred approximately came.",
      "Two hundred approximately came. ← این قید پیش از عدد می‌آید."),
     ("مقایسه با هدف",
      "برای گفتن اینکه نتیجه نسبت به انتظار کجا ایستاده، چند فعل و عبارت ثابت داری.\n"
      "فعل **exceed** یعنی فراتر رفتن، مثل It exceeded our estimate.\n"
      "عبارت **fall short of** یعنی کمتر بودن از، مثل It fell short of the target.\n"
      "با **by a margin** اندازه اختلاف را می‌گویی، مثل by a small margin.\n"
      "و برای سقف و کف، **at most** و **at least** و **up to** را داری.\n"
      "یک نکته را بسپار: exceed حرف اضافه نمی‌گیرد، پس exceed **the target** درست است و exceed from نادرست.",
      "It exceeded our estimate.\nIt fell short of the target.\nWe won by a small margin.\nUp to fifty people can attend.",
      "کدام درست است؟", "It exceeded our estimate.",
      "It exceeded our estimate. | It exceeded from our estimate.",
      "It exceeded from our estimate. ← این فعل حرف اضافه نمی‌گیرد.")],

 18:[("فرض و استنتاج",
      "یک استدلال منظم سه حلقه دارد و هر حلقه عبارت‌های خودش را می‌طلبد.\n"
      "در حلقه فرض، Assuming that's true… و Given that it's late… را می‌آوری.\n"
      "در حلقه استنتاج، It follows that… و We can infer that… به کار می‌روند.\n"
      "و در حلقه نتیجه، Therefore… و As a corollary… می‌نشینند.\n"
      "یک تفاوت ساختاری را هم به یاد داشته باش: بعد از assuming و given that جمله کامل می‌آید، اما بعد از in light of فقط اسم.",
      "Assuming that's true, we agree.\nGiven that it's late, let's stop.\nIt follows that we must wait.\nIn light of the results, we changed.",
      "کدام درست است؟", "In light of the results, we changed.",
      "In light of the results, we changed. | In light of the results were bad, we changed.",
      "In light of the results were bad, we changed. ← بعد از in light of اسم می‌آید نه جمله."),
     ("نقد یک استدلال",
      "برای نقد کردن یک استدلال، چند عبارت هست که حرفت را دقیق و حرفه‌ای می‌کند.\n"
      "با There's a **flaw in** the argument می‌گویی استدلال ایرادی دارد.\n"
      "با The **premise** is questionable خودِ مقدمه را زیر سؤال می‌بری.\n"
      "با The two reports are **contradictory** تناقض دو منبع را نشان می‌دهی.\n"
      "و با Don't **jump to conclusions** هشدار می‌دهی که زود نتیجه نگیرند.\n"
      "برای تأیید کردن هم دو عبارت داری: That's a **valid** point و The logic is **sound**.",
      "There's a flaw in the argument.\nThe premise is questionable.\nDon't jump to conclusions.\nThe logic is sound.",
      "کدام درست است؟", "There's a flaw in the argument.",
      "There's a flaw in the argument. | There's a flaw of the argument.",
      "There's a flaw of the argument. ← حرف اضافه درست in است.")],

 19:[("درجه‌بندی موافقت",
      "در انگلیسی حرفه‌ای، موافقت یک بله یا خیر ساده نیست و روی یک طیف می‌نشیند.\n"
      "در یک سر طیف **I completely agree** است که موافقت کامل را می‌رساند.\n"
      "یک پله پایین‌تر **Broadly, yes** یا **largely, yes** می‌آید که یعنی در بیشتر موارد.\n"
      "برای موافقت جزئی **To some extent** و **Up to a point** را داری.\n"
      "و برای موافقت مشروط، **In principle, but in practice…** به کار می‌رود.\n"
      "حواست به شکل درستش باشد: to some extent می‌گویند و نه in some extent.",
      "I broadly agree.\nTo some extent, yes.\nI agree up to a point.\nIn principle I agree, but in practice it's harder.",
      "کدام درست است؟", "To some extent, yes.",
      "To some extent, yes. | In some extent, yes.",
      "In some extent, yes. ← حرف اضافه درست to است."),
     ("مشروط کردن گفته",
      "چند عبارت هست که ادعایت را از حالت مطلق درمی‌آورند و حرفت را سنجیده‌تر نشان می‌دهند.\n"
      "با **with reservations** می‌گویی که با تردید می‌پذیری، مثل I accept it with reservations.\n"
      "با **I concede that point** نکته‌ای از طرف مقابل را قبول می‌کنی.\n"
      "با **It isn't black and white** می‌گویی موضوع دو سر مطلق ندارد.\n"
      "و با **It depends on how you define it** بحث را به تعریف‌ها می‌بری.\n"
      "این‌ها نشانه ضعف در استدلال نیستند، بلکه نشانه دقت‌اند.",
      "I accept it with reservations.\nI concede that point.\nIt isn't black and white.\nIt depends on how you define it.",
      "کدام درست است؟", "I concede that point.",
      "I concede that point. | I concede to that point.",
      "I concede to that point. ← این فعل در این معنا حرف اضافه نمی‌گیرد.")],

 20:[("ساختار یک ارائه",
      "هر ارائه چهار جای ثابت دارد که شنونده ناخودآگاه منتظرشان است.\n"
      "برای شروع می‌گویی I'd like to begin by outlining the plan.\n"
      "برای گذار از بخشی به بخش دیگر، Let's move on to… و Turning to the second point… را داری.\n"
      "برای اشاره به چیزی روی صفحه می‌گویی As you can see, sales rose.\n"
      "و برای بستن ارائه، To sum up… و Let's wrap up here به کار می‌آیند.\n"
      "یک نکته ساختاری هم هست: بعد از begin by فعل با ing می‌آید، پس می‌گویی begin by **outlining**.",
      "I'd like to begin by outlining the plan.\nLet's move on to the figures.\nAs you can see, sales rose.\nTo sum up, we're on track.",
      "کدام درست است؟", "I'd like to begin by outlining the plan.",
      "I'd like to begin by outlining the plan. | I'd like to begin by outline the plan.",
      "I'd like to begin by outline the plan. ← بعد از by فعل ing می‌آید."),
     ("مدیریت پرسش و زمان",
      "برای اداره کردن پرسش‌ها و زمان، چند جمله آماده داشته باش تا در لحظه دنبال کلمه نگردی.\n"
      "با **I'll take questions at the end** جای پرسش‌ها را مشخص می‌کنی.\n"
      "اگر پرسشی وقت زیادی می‌برد، می‌گویی **That's a good question. Let me come back to it.**\n"
      "وقتی وقت کم می‌آوری، **I'm afraid we're short of time** را به کار ببر.\n"
      "و برای تمام کردن، **Let's wrap up here** کافی است.\n"
      "یک نکته را هم بسپار: فعل درست take questions است و نه answer to questions.",
      "I'll take questions at the end.\nLet me come back to it.\nI'm afraid we're short of time.\nLet's wrap up here.",
      "کدام درست است؟", "I'll take questions at the end.",
      "I'll take questions at the end. | I'll answer to questions at the end.",
      "I'll answer to questions at the end. ← فعل answer حرف اضافه to نمی‌گیرد.")],
}

EX = {
 11:[(R,"چندگزینه‌ای","___ finished, she left.","Having","","Having | Have | Had","having finished","عبارت وجه وصفی با Having ساخته می‌شود."),
     (R,"تطبیق","concise","موجز","","concise | redundant | wordy","concise","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","___ completed, send it to me.","Once","","","once completed","برای معنی مجهول Once completed می‌آید."),
     (P,"متن آزاد","درست کنید: Having finished, the room was quiet.","Having finished, she left the room.","","","having finished","فاعل دو بخش باید یکی باشد."),
     (P,"متن آزاد","فشرده کنید: She read it twice and then she rewrote it.","Having read it twice, she rewrote it.","","","revision","با Having شروع کن."),
     (P,"مرتب‌سازی","down / half / by / cut / it","Cut it down by half.","","","cut down","جمله امری با فعل شروع می‌شود."),
     (P,"ترجمه به انگلیسی","دو بخش را ادغام کردیم.","We merged the two sections.","","","merge","با فاعل شروع کن."),
     (P,"گفتار","The draft is too wordy.","The draft is too wordy.","","","wordy","جمله را واضح تلفظ کن.")],

 12:[(R,"چندگزینه‌ای","Never ___ such a thing.","have I seen","","have I seen | I have seen | I saw","never have I","با قید منفی آغازین، وارونگی لازم است."),
     (R,"تطبیق","unprecedented","بی‌سابقه","","unprecedented | remarkable | striking","unprecedented","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","No sooner had we left ___ it rained.","than","","","no sooner","جفت درست no sooner … than است."),
     (P,"متن آزاد","درست کنید: Never I have seen it.","Never have I seen it.","","","never have I","فعل کمکی پیش از فاعل می‌آید."),
     (P,"متن آزاد","با Rarely بنویسید: he complains","Rarely does he complain.","","","rarely","با قید منفی آغازین، does می‌آید."),
     (P,"مرتب‌سازی","stress / to / point / I / this / want","I want to stress this point.","","","stress","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","به‌هیچ‌وجه قطعی نیست.","It's by no means certain.","","","by no means","با فاعل It شروع کن."),
     (P,"گفتار","Not only did he help, he stayed late.","Not only did he help, he stayed late.","","","not only","جمله را واضح تلفظ کن.")],

 13:[(R,"چندگزینه‌ای","It was you ___ said it.","who","","who | which | what","it was you who","برای افراد who می‌آید."),
     (R,"تطبیق","credit","اعتبار، سهم","","credit | insistence | blame someone for","credit","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","All I want ___ a clear answer.","is","","","all I want","فعل با All I want مفرد است."),
     (P,"متن آزاد","درست کنید: It was you said it.","It was you who said it.","","","it was you who","حلقه who حذف نمی‌شود."),
     (P,"متن آزاد","تأکید کنید بر «here»: The problem starts here.","It is here that the problem starts.","","","it is here that","با It is شروع کن."),
     (P,"مرتب‌سازی","straight / the / record / set / let's","Let's set the record straight.","","","set the record straight","با Let's شروع کن."),
     (P,"ترجمه به انگلیسی","عمدی نبود.","It wasn't deliberate.","","","deliberate","با فاعل It شروع کن."),
     (P,"گفتار","She drew attention to the cost.","She drew attention to the cost.","","","draw attention to","جمله را واضح تلفظ کن.")],

 14:[(R,"چندگزینه‌ای","What ___ is trust.","matters","","matters | matter | mattering","what matters is","فعل بعد از what مفرد است."),
     (R,"تطبیق","integrity","درستکاری","","integrity | loyalty | fairness","integrity","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","Trust ___ speed.","outweighs","","","outweigh","این فعل حرف اضافه نمی‌گیرد."),
     (P,"متن آزاد","درست کنید: What matters are trust.","What matters is trust.","","","what matters is","فعل be هم مفرد می‌آید."),
     (P,"متن آزاد","تأکید کنید: Details matter.","What matters is the details.","What matters are details.","","matter","با What شروع کن."),
     (P,"مرتب‌سازی","first / safety / comes","Safety comes first.","","","come first","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","بیش از همه صادق باش.","Above all, be honest.","","","above all","با عبارت ثابت شروع کن."),
     (P,"گفتار","Trust is at the heart of it.","Trust is at the heart of it.","","","at the heart of","جمله را واضح تلفظ کن.")],

 15:[(R,"چندگزینه‌ای","Be ___ , not aggressive.","assertive","","assertive | blunt | aggressive","assertive","قاطع بودن با assertive بیان می‌شود."),
     (R,"تطبیق","tactful","با ملاحظه","","tactful | diplomatic | blunt","tactful","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","She stood her ___ .","ground","","","stand your ground","عبارت ثابت stand your ground است."),
     (P,"متن آزاد","نرم کنید: You ignored me.","I felt ignored.","","","tone","جمله I محترمانه‌تر است."),
     (P,"متن آزاد","با politely but firmly بنویسید: I / refuse","I refused politely but firmly.","","","politely but firmly","قید بعد از فعل می‌آید."),
     (P,"مرتب‌سازی","bush / the / around / beat / don't","Don't beat around the bush.","","","beat around the bush","جمله امری منفی با Don't شروع می‌شود."),
     (P,"ترجمه به انگلیسی","بیا مستقیم سر اصل مطلب برویم.","Let's get straight to the point.","","","get straight to the point","با Let's شروع کن."),
     (P,"گفتار","He came across as confident.","He came across as confident.","","","come across as","جمله را واضح تلفظ کن.")],

 16:[(R,"چندگزینه‌ای","Prices tend ___ in winter.","to rise","","to rise | rising | rise","tend to","بعد از tend فعل با to می‌آید."),
     (R,"تطبیق","arguably","می‌توان گفت","","arguably | seemingly | relatively","arguably","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","___ speaking, it's safe.","Generally","","","generally speaking","عبارت ثابت generally speaking است."),
     (P,"متن آزاد","درست کنید: Prices tend rising in winter.","Prices tend to rise in winter.","","","tend to","بعد از tend حرف to لازم است."),
     (P,"متن آزاد","محتاطانه بنویسید: costs rose (بر اساس داده)","The data suggests that costs rose.","","","suggest that","با The data شروع کن."),
     (P,"مرتب‌سازی","whole / works / on / it / the","On the whole, it works.","","","on the whole","با On the whole شروع کن."),
     (P,"ترجمه به انگلیسی","به‌جز جمعه آزادیم.","With the exception of Friday, we're free.","","","with the exception of","با عبارت ثابت شروع کن."),
     (P,"گفتار","More often than not, it works.","More often than not, it works.","","","more often than not","جمله را واضح تلفظ کن.")],

 17:[(R,"چندگزینه‌ای","___ two hundred came.","Approximately","","Approximately | Approximate | Approximately of","approximately","این قید پیش از عدد می‌آید."),
     (R,"تطبیق","negligible","ناچیز","","negligible | precise figure | margin","negligible","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","It ___ our estimate.","exceeded","","","exceed","این فعل حرف اضافه نمی‌گیرد."),
     (P,"متن آزاد","درست کنید: It exceeded from our estimate.","It exceeded our estimate.","","","exceed","exceed حرف اضافه نمی‌گیرد."),
     (P,"متن آزاد","تقریبی بنویسید: ten days","Ten days, give or take.","","","give or take","عبارت ثابت بعد از عدد می‌آید."),
     (P,"مرتب‌سازی","target / short / it / of / the / fell","It fell short of the target.","","","fall short of","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","خیلی بیشتر از صد نفر درخواست دادند.","Well over a hundred applied.","","","well over","با عبارت ثابت شروع کن."),
     (P,"گفتار","Give me a ballpark number.","Give me a ballpark number.","","","ballpark","جمله را واضح تلفظ کن.")],

 18:[(R,"چندگزینه‌ای","___ the results, we changed the plan.","In light of","","In light of | In light | In light that","in light of","بعد از این عبارت اسم می‌آید."),
     (R,"تطبیق","premise","مقدمه، پیش‌فرض","","premise | logic | hypothesis","premise","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","There's a flaw ___ the argument.","in","","","flaw","حرف اضافه ثابت in است."),
     (P,"متن آزاد","درست کنید: There's a flaw of the argument.","There's a flaw in the argument.","","","flaw","حرف اضافه درست in است."),
     (P,"متن آزاد","استنتاج کنید: we / must wait","It follows that we must wait.","","","it follows that","با It follows that شروع کن."),
     (P,"مرتب‌سازی","conclusions / to / jump / don't","Don't jump to conclusions.","","","jump to conclusions","جمله امری منفی با Don't شروع می‌شود."),
     (P,"ترجمه به انگلیسی","دو گزارش متناقض‌اند.","The two reports are contradictory.","","","contradictory","با فاعل شروع کن."),
     (P,"گفتار","The logic is sound.","The logic is sound.","","","sound reasoning","جمله را واضح تلفظ کن.")],

 19:[(R,"چندگزینه‌ای","___ some extent, yes.","To","","To | In | At","to some extent","حرف اضافه درست to است."),
     (R,"تطبیق","reservation","تحفظ، تردید","","reservation | common ground | middle ground","reservation","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","I agree up to a ___ .","point","","","up to a point","عبارت ثابت up to a point است."),
     (P,"متن آزاد","درست کنید: In some extent, yes.","To some extent, yes.","","","to some extent","حرف اضافه درست to است."),
     (P,"متن آزاد","مشروط کنید: I agree (اصل بله، عمل سخت)","In principle I agree, but in practice it's harder.","","","in principle","دو نیمه با but وصل می‌شوند."),
     (P,"مرتب‌سازی","point / concede / that / I","I concede that point.","","","concede","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","سیاه و سفید نیست.","It isn't black and white.","","","black and white","با فاعل It شروع کن."),
     (P,"گفتار","I can see both sides.","I can see both sides.","","","see both sides","جمله را واضح تلفظ کن.")],

 20:[(R,"چندگزینه‌ای","I'd like to begin by ___ the plan.","outlining","","outlining | outline | to outline","I'd like to begin by","بعد از by فعل ing می‌آید."),
     (R,"تطبیق","audience","حضار","","audience | handout | slide","audience","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","I'll ___ questions at the end.","take","","","take questions","فعل درست take questions است."),
     (P,"متن آزاد","درست کنید: I'll answer to questions at the end.","I'll take questions at the end.","I'll answer questions at the end.","","take questions","answer حرف اضافه to نمی‌گیرد."),
     (P,"متن آزاد","گذار بنویسید: the figures","Let's move on to the figures.","","","move on to","با Let's شروع کن."),
     (P,"مرتب‌سازی","here / up / wrap / let's","Let's wrap up here.","","","wrap up","با Let's شروع کن."),
     (P,"ترجمه به انگلیسی","برای جمع‌بندی، در مسیریم.","To sum up, we're on track.","","","to sum up","با عبارت ثابت شروع کن."),
     (P,"گفتار","As you can see, sales rose.","As you can see, sales rose.","","","as you can see","جمله را واضح تلفظ کن.")],
}
