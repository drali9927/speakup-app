# -*- coding: utf-8 -*-
"""مکالمه، گرامر و تمرین دروس ۲۱ تا ۳۰ سطح A2."""

P, R = "تولیدی", "تشخیصی"

DLG = {
 21:[("A","You don't look well.","حالت خوب به نظر نمی‌رسد."),
     ("B","I have a headache and a fever.","سردرد و تب دارم."),
     ("A","You should see a doctor.","باید بروی دکتر."),
     ("B","I have to finish this report first.","اول باید این گزارش را تمام کنم."),
     ("A","No, you must rest today.","نه، امروز باید استراحت کنی."),
     ("B","All right. Thanks for the advice.","باشد. بابت نصیحتت ممنون.")],
 22:[("A","Can I park here?","می‌شود اینجا پارک کنم؟"),
     ("B","No, you mustn't park here.","نه، اینجا نباید پارک کنی."),
     ("A","Why? There's no sign.","چرا؟ تابلویی نیست."),
     ("B","It's a private area. There's a fine.","محوطه خصوصی است. جریمه دارد."),
     ("A","Where's the public car park?","پارکینگ عمومی کجاست؟"),
     ("B","Past the entrance, on the left.","بعد از ورودی، سمت چپ.")],
 23:[("A","Excuse me, could you help me?","ببخشید، می‌شود کمکم کنید؟"),
     ("B","Certainly. What do you need?","حتماً. چه لازم دارید؟"),
     ("A","Would you mind holding this?","اشکالی ندارد این را نگه دارید؟"),
     ("B","No problem at all.","اصلاً مشکلی نیست."),
     ("A","And could I borrow a pen?","و می‌شود خودکاری قرض بگیرم؟"),
     ("B","Of course. Here you are.","البته. بفرمایید.")],
 24:[("A","Good evening. I'd like to book a room.","عصر بخیر. مایلم اتاقی رزرو کنم."),
     ("B","Single or double?","یک‌نفره یا دونفره؟"),
     ("A","A double room for three nights.","یک اتاق دونفره برای سه شب."),
     ("B","Certainly. Is breakfast included?","حتماً. صبحانه شامل می‌شود؟"),
     ("A","Yes, it is. Here's your key.","بله. این کلید شماست."),
     ("B","Thank you. Could we have extra towels?","ممنون. می‌شود حوله بیشتری بدهید؟")],
 25:[("A","What are your symptoms?","علائمتان چیست؟"),
     ("B","I have a cough and my back hurts.","سرفه دارم و کمرم درد می‌کند."),
     ("A","How long have you felt like this?","چه مدت است این‌طور هستید؟"),
     ("B","Since Monday. I couldn't sleep.","از دوشنبه. نتوانستم بخوابم."),
     ("A","I'll write you a prescription.","برایتان نسخه می‌نویسم."),
     ("B","Thank you. Where's the pharmacy?","ممنون. داروخانه کجاست؟")],
 26:[("A","What do you do at the weekend?","آخر هفته چه کار می‌کنی؟"),
     ("B","I enjoy reading and cooking.","از کتاب خواندن و آشپزی لذت می‌برم."),
     ("A","I've decided to learn to swim.","تصمیم گرفته‌ام شنا یاد بگیرم."),
     ("B","Really? I hate swimming!","واقعاً؟ من از شنا متنفرم!"),
     ("A","Why? It's good exercise.","چرا؟ ورزش خوبی است."),
     ("B","True. Maybe I should practise more.","درست است. شاید باید بیشتر تمرین کنم.")],
 27:[("A","What did the doctor say?","دکتر چه گفت؟"),
     ("B","She said I was fine.","گفت حالم خوب است."),
     ("A","And the test?","و آزمایش؟"),
     ("B","She told me to come back next week.","به من گفت هفته بعد برگردم."),
     ("A","Did you ask about the medicine?","درباره دارو پرسیدی؟"),
     ("B","Yes. She explained that it was safe.","بله. توضیح داد که بی‌خطر است.")],
 28:[("A","What do you think of this one?","نظرت درباره این یکی چیست؟"),
     ("B","It's nice, but it's too expensive.","قشنگ است، اما بیش از حد گران است."),
     ("A","And the other one?","و آن یکی؟"),
     ("B","Too tight. It doesn't fit me.","خیلی تنگ است. اندازه‌ام نیست."),
     ("A","We don't have enough time today.","امروز وقت کافی نداریم."),
     ("B","Let's come back when it's less crowded.","بیا وقتی خلوت‌تر شد برگردیم.")],
 29:[("A","Someone left a bag here.","یک نفر کیفی اینجا گذاشته."),
     ("B","Is there anything inside?","چیزی داخلش هست؟"),
     ("A","Nothing important. Just a notebook.","چیز مهمی نیست. فقط یک دفتر."),
     ("B","Did anyone see the owner?","کسی صاحبش را دید؟"),
     ("A","No one did. I looked everywhere.","هیچ‌کس ندید. همه‌جا را گشتم."),
     ("B","Take it to lost property.","ببرش به بخش اشیای گمشده.")],
 30:[("A","You've finished the whole level!","تمام سطح را تمام کرده‌ای!"),
     ("B","I can't believe it. It was a real challenge.","باورم نمی‌شود. چالش واقعی بود."),
     ("A","Do you feel more confident now?","حالا اعتماد به نفس بیشتری داری؟"),
     ("B","Much more. Mistakes happen, but I kept going.","خیلی بیشتر. اشتباه پیش می‌آید، اما ادامه دادم."),
     ("A","Well done. So, what's next?","آفرین. خب، بعدش چه؟"),
     ("B","The next level. Step by step.","سطح بعد. قدم‌به‌قدم.")],
}

GR = {
 21:[("should برای توصیه",
      "برای نصیحت و پیشنهاد:\n"
      "should + فعل ساده\n"
      "You **should** rest. · You **shouldn't** work today.\n"
      "⚠️ بعد از should حرف to نمی‌آید و فعل s نمی‌گیرد:\n"
      "نادرست: You should to rest · He should rests\n"
      "پرسش: **Should I** see a doctor?",
      "You should rest.\nYou shouldn't work today.\nShould I see a doctor?\nHe should drink more water.",
      "کدام درست است؟", "You should rest.",
      "You should rest. | You should to rest. | You shoulds rest.",
      "You should to rest. ← بعد از should حرف to نمی‌آید.\n"
      "You shoulds rest. ← فعل کمکی s نمی‌گیرد."),
     ("must و have to",
      "هر دو یعنی «باید»، اما منبع الزام فرق دارد:\n"
      "**must** — الزام از درون خودت: I must finish this today.\n"
      "**have to** — الزام از بیرون (قانون، رئیس، شرایط): I have to work tomorrow.\n"
      "سوم‌شخص: he **has to** work.\n"
      "گذشته هر دو یکی است: **had to**\n"
      "I had to wait. (گذشته must هم همین است)",
      "I must finish this today.\nI have to work tomorrow.\nShe has to leave early.\nWe had to wait an hour.",
      "کدام درست است؟", "She has to leave early.",
      "She has to leave early. | She have to leave early.",
      "She have to leave early. ← با she فعل has می‌آید.")],

 22:[("mustn't در برابر don't have to",
      "این دو شبیه‌اند اما **معنی متضاد** دارند:\n"
      "**mustn't** = ممنوع است — You mustn't park here.\n"
      "**don't have to** = لازم نیست — You don't have to pay.\n"
      "⚠️ پرتکرارترین اشتباه: «لازم نیست» را با mustn't گفتن.\n"
      "اگر منظورت «اشکالی ندارد نکنی» است، don't have to درست است.",
      "You mustn't park here. (ممنوع)\nYou don't have to pay. (لازم نیست)\nYou mustn't touch this.\nWe don't have to hurry.",
      "«لازم نیست پول بدهی» کدام است؟", "You don't have to pay.",
      "You don't have to pay. | You mustn't pay.",
      "You mustn't pay. ← این یعنی پرداخت ممنوع است، نه اینکه لازم نیست."),
     ("can و be allowed to",
      "برای اجازه:\n"
      "**can** — گفتاری و ساده: You **can** wait here.\n"
      "**be allowed to** — رسمی، مثل قانون: Photos **aren't allowed**.\n"
      "منفی: You **can't** enter. · It **isn't allowed**.\n"
      "پرسش اجازه: **Can I** park here? · **Am I allowed to** park here?",
      "You can wait here.\nYou can't enter.\nPhotos aren't allowed.\nAm I allowed to park here?",
      "کدام درست است؟", "Photos aren't allowed.",
      "Photos aren't allowed. | Photos aren't allow.",
      "Photos aren't allow. ← بعد از be باید صورت اسم‌مفعولی allowed بیاید.")],

 23:[("درخواست مؤدبانه",
      "از غیررسمی به رسمی:\n"
      "**Can you** help me? (دوستانه)\n"
      "**Could you** help me? (مؤدب — امن‌ترین انتخاب)\n"
      "**Would you** wait a moment? (مؤدب)\n"
      "برای درخواست برای خودت: **Could I** borrow your pen?\n"
      "⚠️ بعد از could و would فعل ساده می‌آید، بدون to.",
      "Could you help me?\nWould you wait a moment?\nCould I borrow your pen?\nCan you pass the salt?",
      "کدام مؤدبانه‌تر است؟", "Could you help me?",
      "Could you help me? | Can you help me?",
      "Can you help me? ← درست است اما دوستانه و کم‌رسمی‌تر؛ در موقعیت رسمی could بهتر است."),
     ("Would you mind …?",
      "این ساختار مؤدب‌ترین است، اما دو دام دارد:\n"
      "۱) بعدش فعل **ing** می‌آید: Would you mind **waiting**?\n"
      "۲) پاسخ **برعکس** به‌نظر می‌رسد:\n"
      "**No, not at all.** = اشکالی ندارد، بفرما ✅\n"
      "**Yes, I do mind.** = بله، ناراحت می‌شوم ❌\n"
      "چون mind یعنی «ناراحت شدن»، نه «موافق بودن».",
      "Would you mind waiting?\nWould you mind closing the door?\nNo, not at all.\nSorry, I do mind.",
      "پاسخ موافق کدام است؟", "No, not at all.",
      "No, not at all. | Yes, of course.",
      "Yes, of course. ← با mind، «بله» یعنی ناراحت می‌شوم؛ پاسخ موافق «نه» است.")],

 24:[("would like",
      "**would like** یعنی «مایلم» و از want مؤدب‌تر است:\n"
      "I'd like **to book** a room. (بعدش فعل با to)\n"
      "I'd like **a double room**. (بعدش اسم)\n"
      "کوتاه‌نویسی: I would → **I'd**\n"
      "پرسش: **Would you like** some tea?\n"
      "⚠️ پاسخ کوتاه: Yes, please. / No, thank you.",
      "I'd like to book a room.\nI'd like a double room.\nWould you like some tea?\nYes, please.",
      "کدام درست است؟", "I'd like to book a room.",
      "I'd like to book a room. | I'd like book a room. | I'd like booking a room.",
      "I'd like book a room. ← بعد از would like فعل با to می‌آید.\n"
      "I'd like booking a room. ← اینجا ing نمی‌آید."),
     ("در موقعیت خدمات",
      "چند الگوی آماده که همان روز به کار می‌آیند:\n"
      "**Could I have** the bill, please?\n"
      "**Is breakfast included?**\n"
      "**Do you have** a room for three nights?\n"
      "**How much is** it per night?\n"
      "و برای شکایت مؤدبانه:\n"
      "I'm afraid the room is too noisy.",
      "Could I have the bill, please?\nIs breakfast included?\nHow much is it per night?\nI'm afraid the room is too noisy.",
      "کدام برای گرفتن صورت‌حساب درست است؟", "Could I have the bill, please?",
      "Could I have the bill, please? | Could I take the bill, please?",
      "Could I take the bill, please? ← در این موقعیت have رایج است، نه take.")],

 25:[("مرور مدال‌ها",
      "هر مدال یک کار مشخص دارد:\n"
      "**can** توانایی و اجازه · **should** توصیه\n"
      "**must / have to** الزام · **mustn't** ممنوعیت\n"
      "**may / might** احتمال · **could / would** درخواست مؤدبانه\n"
      "قانون مشترک همه: بعدشان **فعل ساده** می‌آید و **s نمی‌گیرند**.",
      "She can speak English.\nYou should rest.\nI have to work.\nYou mustn't smoke here.\nIt might rain.",
      "کدام درست است؟", "She should see a doctor.",
      "She should see a doctor. | She should sees a doctor.",
      "She should sees a doctor. ← بعد از مدال فعل s نمی‌گیرد."),
     ("گفتن حال خود",
      "الگوهای بیمار و پزشک:\n"
      "**I have a** headache / a cough / a cold.\n"
      "**My** back **hurts**. (نه My back is hurt)\n"
      "**I feel** tired / better / worse.\n"
      "و پرسش پزشک: **How long have you felt like this?**\n"
      "پاسخ با for یا since: Since Monday. · For three days.",
      "I have a headache.\nMy back hurts.\nI feel much better.\nHow long have you felt like this?",
      "کدام درست است؟", "My back hurts.",
      "My back hurts. | My back is hurt.",
      "My back is hurt. ← hurt خودش فعل است و به to be نیاز ندارد.")],

 26:[("فعل + ing",
      "بعضی فعل‌ها همیشه فعلِ بعدی را با **ing** می‌گیرند:\n"
      "enjoy · avoid · finish · mind · practise · keep\n"
      "I enjoy **reading**. · I avoid **driving** at night.\n"
      "⚠️ نادرست: I enjoy to read\n"
      "همچنین بعد از هر **حرف اضافه** فعل ing می‌گیرد:\n"
      "good **at** cooking · keen **on** swimming",
      "I enjoy reading.\nI avoid driving at night.\nI don't mind waiting.\nShe's good at cooking.",
      "کدام درست است؟", "I enjoy reading.",
      "I enjoy reading. | I enjoy to read.",
      "I enjoy to read. ← بعد از enjoy فعل ing می‌گیرد."),
     ("فعل + to",
      "و بعضی فعل‌ها فعلِ بعدی را با **to** می‌گیرند:\n"
      "decide · hope · want · need · learn · promise · try\n"
      "I decided **to study** more. · I hope **to travel** soon.\n"
      "این فهرست حفظ‌کردنی است؛ قاعده ساده‌ای ندارد.\n"
      "راه یادگیری بهتر: هر فعل تازه را با یک جمله کامل حفظ کن،\n"
      "نه به‌تنهایی.",
      "I decided to study more.\nI hope to travel soon.\nShe wants to learn English.\nHe promised to call.",
      "کدام درست است؟", "I decided to study.",
      "I decided to study. | I decided studying.",
      "I decided studying. ← بعد از decide فعل با to می‌آید.")],

 27:[("نقل قول با told و said",
      "تفاوت این دو فقط یک چیز است — **مخاطب**:\n"
      "**told** حتماً مخاطب می‌خواهد: He **told me** to wait.\n"
      "**said** مخاطب نمی‌گیرد: She **said** she was busy.\n"
      "اگر بخواهی با said مخاطب بیاوری، to لازم است:\n"
      "She said **to me** that…\n"
      "⚠️ نادرست: He said me to wait.",
      "He told me to wait.\nShe said she was busy.\nThey told us the news.\nHe said that it was late.",
      "کدام درست است؟", "He told me to wait.",
      "He told me to wait. | He said me to wait.",
      "He said me to wait. ← said مخاطب مستقیم نمی‌گیرد؛ اینجا told درست است."),
     ("عقب رفتن زمان",
      "وقتی حرف کسی را بازگو می‌کنی، زمان یک پله عقب می‌رود:\n"
      "am / is → **was** · are → **were**\n"
      "have → **had** · will → **would** · can → **could**\n"
      "«I am busy» → She said she **was** busy.\n"
      "و ضمیرها هم عوض می‌شوند: I → she · my → her",
      "\"I am busy.\" → She said she was busy.\n\"I will call.\" → He said he would call.\n\"I can help.\" → She said she could help.",
      "«I am tired» بازگو شود:", "He said he was tired.",
      "He said he was tired. | He said he is tired.",
      "He said he is tired. ← در نقل قول، زمان یک پله عقب می‌رود.")],

 28:[("too در برابر very",
      "این دو یکی نیستند:\n"
      "**very** فقط شدت را بالا می‌برد: It's very expensive. (اما شاید بخرم)\n"
      "**too** یعنی **بیش از حدِ لازم** و بار منفی دارد:\n"
      "It's too expensive. (نمی‌خرم)\n"
      "ساختار: too + صفت · too + صفت + to + فعل\n"
      "It's too hot **to drink**.",
      "It's very expensive.\nIt's too expensive.\nThe soup is too hot to drink.\nThese shoes are too tight.",
      "کدام یعنی «نمی‌توانم بخرم»؟", "It's too expensive.",
      "It's too expensive. | It's very expensive.",
      "It's very expensive. ← فقط گران بودن را می‌گوید، نه اینکه بیش از حد است."),
     ("enough و جای آن",
      "**enough** یعنی «به‌اندازه کافی» و جایش دقیق است:\n"
      "بعد از **صفت**: It isn't big **enough**.\n"
      "پیش از **اسم**: We don't have **enough** time.\n"
      "⚠️ نادرست: enough big · time enough\n"
      "و برای «بیش از حد کم»: not … enough\n"
      "We aren't fast **enough**.",
      "It isn't big enough.\nWe don't have enough time.\nThere isn't enough space.\nWe aren't fast enough.",
      "کدام درست است؟", "It isn't big enough.",
      "It isn't big enough. | It isn't enough big.",
      "It isn't enough big. ← enough بعد از صفت می‌آید.")],

 29:[("some، any و no",
      "سه خانواده ضمیر نامعین:\n"
      "**some-** در جمله مثبت: **Someone** left this. · I heard **something**.\n"
      "**any-** در منفی و پرسش: I didn't buy **anything**. · Is **anyone** there?\n"
      "**no-** خودش منفی است و فعل مثبت می‌ماند:\n"
      "**No one** answered. (نه: No one didn't answer)\n"
      "⚠️ برای تعارف و پیشنهاد، some در پرسش هم می‌آید:\n"
      "Would you like **something** to drink?",
      "Someone left this here.\nI didn't buy anything.\nIs anyone there?\nNo one answered.",
      "کدام درست است؟", "I didn't buy anything.",
      "I didn't buy anything. | I didn't buy nothing.",
      "I didn't buy nothing. ← nothing خودش منفی است و با فعل منفی نمی‌آید."),
     ("فعل مفرد می‌آید",
      "این ضمیرها به‌نظر جمع می‌آیند اما فعل **مفرد** می‌گیرند:\n"
      "**Everybody is** here. (نه everybody are)\n"
      "**Everything is** ready. · **Nothing is** easy.\n"
      "برای مکان هم همین سه خانواده هست:\n"
      "somewhere · anywhere · nowhere · everywhere\n"
      "I looked **everywhere**. · There's **nowhere** to sit.",
      "Everybody is here.\nEverything is ready.\nNothing is easy.\nI looked everywhere.",
      "کدام درست است؟", "Everybody is here.",
      "Everybody is here. | Everybody are here.",
      "Everybody are here. ← این ضمیرها فعل مفرد می‌گیرند."),
     ],

 30:[("جمع‌بندی A2 — زمان‌ها",
      "چهار زمانی که در این سطح ساختی:\n"
      "**گذشته استمراری** — I was cooking when she called.\n"
      "**آینده** — will (تصمیم) · going to (برنامه) · حال استمراری (قرار)\n"
      "**حال کامل** — I've lived here for six years.\n"
      "**مجهول** — It was made in Iran.\n"
      "اگر هر چهار را می‌شناسی، A2 را واقعاً تمام کرده‌ای.",
      "I was cooking when she called.\nWe're meeting at six.\nI've lived here for six years.\nIt was made in Iran.",
      "کدام حال کامل است؟", "I've lived here for six years.",
      "I've lived here for six years. | I was living here for six years.",
      "I was living here for six years. ← این گذشته استمراری است و ادامه تا حالا را نشان نمی‌دهد."),
     ("جمع‌بندی A2 — کارکردها",
      "و چهار کاری که حالا می‌توانی انجام دهی:\n"
      "**مقایسه** — cheaper than · as big as · the best\n"
      "**توصیه و الزام** — should · must · have to · mustn't\n"
      "**درخواست مؤدبانه** — Could you…? · Would you mind…?\n"
      "**روایت** — although · however · meanwhile · at last\n"
      "قدم بعدی B1 است: بیان نظر، فرض و موقعیت‌های پیچیده‌تر.",
      "This is cheaper than that.\nYou should rest.\nCould you help me?\nAlthough it was late, we continued.",
      "کدام درخواست مؤدبانه است؟", "Could you help me?",
      "Could you help me? | You must help me.",
      "You must help me. ← این الزام است و درخواست مؤدبانه نیست.")],
}

EX = {
 21:[(R,"چندگزینه‌ای","You ___ see a doctor.","should","","should | should to | shoulds","should","بعد از should فعل ساده می‌آید."),
     (R,"تطبیق","advice","نصیحت","","advice | medicine | pain","advice","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","She ___ to work tomorrow. (مجبور است)","has","","","have to","با she فعل has to می‌آید."),
     (P,"متن آزاد","درست کنید: You should to rest.","You should rest.","","","rest","بعد از should حرف to نمی‌آید."),
     (P,"متن آزاد","منفی کنید: You should work today.","You shouldn't work today.","You should not work today.","","should","منفی should همان shouldn't است."),
     (P,"مرتب‌سازی","today / finish / must / I / this","I must finish this today.","","","must","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","باید استراحت کنی.","You should rest.","","","rest","برای توصیه should می‌آید."),
     (P,"گفتار","You should see a doctor.","You should see a doctor.","","","advice","جمله را واضح تلفظ کن.")],

 22:[(R,"چندگزینه‌ای","You ___ park here. (ممنوع است)","mustn't","","mustn't | don't have to | shouldn't","mustn't","برای ممنوعیت mustn't می‌آید."),
     (R,"تطبیق","forbidden","ممنوع","","forbidden | allowed | warning","forbidden","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","Photos aren't ___ here.","allowed","","","allowed","بعد از be صورت allowed می‌آید."),
     (P,"متن آزاد","درست کنید: You mustn't pay, it's free.","You don't have to pay, it's free.","","","permission","«لازم نیست» با don't have to گفته می‌شود."),
     (P,"متن آزاد","با can't بنویسید: enter / you / here","You can't enter here.","","","enter","با فاعل شروع کن."),
     (P,"مرتب‌سازی","touch / please / don't / this","Please don't touch this.","","","touch","با Please شروع کن."),
     (P,"ترجمه به انگلیسی","اینجا سیگار کشیدن ممنوع است.","Smoking is forbidden here.","","","forbidden","با اسم فعل شروع کن."),
     (P,"گفتار","It's for your safety.","It's for your safety.","","","safety","جمله را واضح تلفظ کن.")],

 23:[(R,"چندگزینه‌ای","___ you help me, please?","Could","","Could | Should | Must","could","برای درخواست مؤدبانه could می‌آید."),
     (R,"تطبیق","borrow","قرض گرفتن","","borrow | lend | pass","borrow","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","Would you mind ___ ? (wait)","waiting","","","mind","بعد از mind فعل ing می‌گیرد."),
     (P,"متن آزاد","درست کنید: Could you to help me?","Could you help me?","","","could","بعد از could حرف to نمی‌آید."),
     (P,"متن آزاد","مؤدبانه کنید: Give me the salt.","Could you pass the salt, please?","Could you pass me the salt, please?","","pass","با Could you شروع کن."),
     (P,"مرتب‌سازی","pen / borrow / I / could / your","Could I borrow your pen?","","","borrow","با Could شروع کن."),
     (P,"ترجمه به انگلیسی","می‌شود بلندتر صحبت کنید؟","Could you speak up, please?","Could you speak up?","","speak up","با Could you شروع کن."),
     (P,"گفتار","Excuse me, could I ask something?","Excuse me, could I ask something?","","","excuse me","جمله را واضح تلفظ کن.")],

 24:[(R,"چندگزینه‌ای","I'd like ___ a room.","to book","","to book | book | booking","would like","بعد از would like فعل با to می‌آید."),
     (R,"تطبیق","reservation","رزرو","","reservation | luggage | bill","reservation","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","Is breakfast ___ ?","included","","","included","«شامل می‌شود» یعنی included."),
     (P,"متن آزاد","درست کنید: I'd like booking a room.","I'd like to book a room.","","","would like","بعد از would like فعل با to می‌آید."),
     (P,"متن آزاد","مؤدبانه بخواهید: the bill","Could I have the bill, please?","","","bill","با Could I have شروع کن."),
     (P,"مرتب‌سازی","nights / a / for / room / three / double","A double room for three nights.","","","double room","با اسم شروع کن."),
     (P,"ترجمه به انگلیسی","مایلم اتاقی رزرو کنم.","I'd like to book a room.","I would like to book a room.","","reservation","با I'd like شروع کن."),
     (P,"گفتار","Could we have extra towels?","Could we have extra towels?","","","towel","جمله را واضح تلفظ کن.")],

 25:[(R,"چندگزینه‌ای","My back ___ .","hurts","","hurts | is hurt | hurting","hurt","hurt خودش فعل است."),
     (R,"تطبیق","prescription","نسخه","","prescription | pharmacy | treatment","prescription","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","I ___ a cough. (دارم)","have","","","cough","برای بیان علامت have می‌آید."),
     (P,"متن آزاد","درست کنید: She should sees a doctor.","She should see a doctor.","","","should","بعد از مدال فعل s نمی‌گیرد."),
     (P,"متن آزاد","سوال بسازید: I've felt like this since Monday.","How long have you felt like this?","","","symptom","با How long شروع کن."),
     (P,"مرتب‌سازی","better / feel / much / I","I feel much better.","","","feel","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","سرفه بدی دارم.","I have a bad cough.","","","cough","با فاعل I شروع کن."),
     (P,"گفتار","I'll write you a prescription.","I'll write you a prescription.","","","prescription","جمله را واضح تلفظ کن.")],

 26:[(R,"چندگزینه‌ای","I enjoy ___ .","reading","","reading | to read | read","enjoy","بعد از enjoy فعل ing می‌گیرد."),
     (R,"تطبیق","enjoy","لذت بردن","","enjoy | avoid | practise","enjoy","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","I decided ___ more. (study)","to study","","","decide to","بعد از decide فعل با to می‌آید."),
     (P,"متن آزاد","درست کنید: I enjoy to cook.","I enjoy cooking.","","","cooking","بعد از enjoy فعل ing می‌گیرد."),
     (P,"متن آزاد","کامل کنید: She's good ___ languages.","at","","","good at","بعد از good حرف اضافه at می‌آید."),
     (P,"مرتب‌سازی","driving / avoid / night / I / at","I avoid driving at night.","","","avoid","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","امیدوارم به‌زودی سفر کنم.","I hope to travel soon.","","","hope to","بعد از hope فعل با to می‌آید."),
     (P,"گفتار","Reading helps me relax.","Reading helps me relax.","","","reading","جمله را واضح تلفظ کن.")],

 27:[(R,"چندگزینه‌ای","He ___ me to wait.","told","","told | said | asked","told","told مخاطب می‌گیرد."),
     (R,"تطبیق","truth","حقیقت","","truth | lie | secret","truth","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","She ___ she was busy.","said","","","said","said مخاطب مستقیم نمی‌گیرد."),
     (P,"متن آزاد","درست کنید: He said me to wait.","He told me to wait.","","","told","با مخاطب، told درست است."),
     (P,"متن آزاد","بازگو کنید: \"I am tired.\" (he)","He said he was tired.","","","said","زمان یک پله عقب می‌رود."),
     (P,"مرتب‌سازی","back / come / told / to / me / she","She told me to come back.","","","told","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","توضیح داد که دیر شده.","She explained that it was late.","He explained that it was late.","","explained that","با فاعل شروع کن."),
     (P,"گفتار","According to Ali, it's closed.","According to Ali, it's closed.","","","according to","جمله را واضح تلفظ کن.")],

 28:[(R,"چندگزینه‌ای","It isn't big ___ .","enough","","enough | too | very","enough","enough بعد از صفت می‌آید."),
     (R,"تطبیق","empty","خالی","","empty | full | extra","empty","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","We don't have ___ time.","enough","","","enough time","enough پیش از اسم می‌آید."),
     (P,"متن آزاد","درست کنید: It isn't enough big.","It isn't big enough.","","","enough","enough بعد از صفت می‌آید."),
     (P,"متن آزاد","با too بنویسید: the soup / hot / drink","The soup is too hot to drink.","","","hot","ساختار too + صفت + to + فعل است."),
     (P,"مرتب‌سازی","tight / shoes / too / these / are","These shoes are too tight.","","","tight","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","بیش از حد گران است.","It's too expensive.","","","too","«بیش از حد» یعنی too."),
     (P,"گفتار","There isn't enough space.","There isn't enough space.","","","space","جمله را واضح تلفظ کن.")],

 29:[(R,"چندگزینه‌ای","I didn't buy ___ .","anything","","anything | nothing | something","anything","در جمله منفی any می‌آید."),
     (R,"تطبیق","nothing","هیچ‌چیز","","nothing | something | everything","nothing","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","___ left this here. (یک نفر)","Someone","","","someone","در جمله مثبت some می‌آید."),
     (P,"متن آزاد","درست کنید: No one didn't answer.","No one answered.","","","no one","no one خودش منفی است."),
     (P,"متن آزاد","درست کنید: Everybody are here.","Everybody is here.","","","everybody","این ضمیرها فعل مفرد می‌گیرند."),
     (P,"مرتب‌سازی","everywhere / looked / I","I looked everywhere.","","","everywhere","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","جایی برای نشستن نیست.","There's nowhere to sit.","There is nowhere to sit.","","nowhere","با There's شروع کن."),
     (P,"گفتار","Did anyone see the owner?","Did anyone see the owner?","","","anyone","جمله را واضح تلفظ کن.")],

 30:[(R,"چندگزینه‌ای","I ___ here for six years.","have lived","","have lived | was living | live","level","برای مدتی که ادامه دارد حال کامل می‌آید."),
     (R,"تطبیق","goal","هدف","","goal | level | challenge","goal","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","It ___ made in Iran.","was","","","achieve","مجهول گذشته با was ساخته می‌شود."),
     (P,"متن آزاد","مؤدبانه بخواهید: help","Could you help me, please?","","","confident","با Could you شروع کن."),
     (P,"متن آزاد","با although بنویسید: it was late / we continued","Although it was late, we continued.","","","challenge","بعد از نیمه اول ویرگول می‌آید."),
     (P,"مرتب‌سازی","step / by / learn / step","Learn step by step.","","","step by step","جمله امری با فعل شروع می‌شود."),
     (P,"ترجمه به انگلیسی","برای سطح بعد آماده‌ای.","You're ready for the next level.","You are ready for the next level.","","next level","با فاعل شروع کن."),
     (P,"گفتار","Mistakes happen — keep going.","Mistakes happen — keep going.","","","keep going","جمله را واضح تلفظ کن.")],
}
