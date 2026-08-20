# -*- coding: utf-8 -*-
"""
بررسی درک — یک پرسش کوتاه بعد از هر کارت گرامر.

چرا این بخش وجود دارد: رقیب گرامر را با سه دقیقه روایت صوتی آموزش می‌دهد
و کاربر تا پایان بخش هیچ کاری نمی‌کند. پژوهش خود دولینگو نشان داده که
توضیحِ کوتاهِ **بعد از اشتباهِ مشخص** خطای بعدی را کم می‌کند
(blog.duolingo.com — «Smart Tips»). پس به‌جای شنیدن طولانی، بلافاصله
بعد از هر توضیح یک پرسش می‌آید.

نکته کلیدی طراحی: هر گزینه غلط باید یک **برداشت اشتباه مشخص** را نمایندگی
کند و نکته‌اش هم دقیقاً همان برداشت را نام ببرد. «اشتباه است، دوباره تلاش
کن» بی‌فایده است؛ «is برای he/she/it است» چیزی است که دفعه بعد جلوی خطا
را می‌گیرد.

ساختار: CHECK[(شماره درس, شماره کارت گرامر)] = (سوال, پاسخ, گزینه‌ها, نکته‌ها)
"""

CHECK = {}

# ---------------------------------------------------------------- درس ۱ تا ۱۰

CHECK[(1, 1)] = ("Sara ___ a teacher.", "is", ["am", "is", "are"], {
    "am": "am فقط با I می‌آید. Sara یعنی «او»، پس is لازم است.",
    "are": "are با you، we و they می‌آید. برای یک نفر (he/she/it) is درست است.",
})
# جمله پرسش عمداً با مثال‌های همان کارت یکی نیست.
# پیش‌تر عیناً «Ali and I are friends.» بود که آخرین مثالِ بالای همان
# صفحه هم بود؛ کاربر پاسخ را از دو خط بالاتر می‌خواند و بررسی درک،
# چیزی را بررسی نمی‌کرد.
CHECK[(1, 2)] = ("Reza and I are students. → ___ are students.", "We", ["We", "They", "He"], {
    "They": "They یعنی «آنها» و شامل خودِ گوینده نیست. چون «I» در جمله هست، We می‌شود.",
    "He": "He برای یک مرد است، ولی اینجا دو نفریم و یکی‌شان خود گوینده است.",
})

CHECK[(2, 1)] = ("She has ___ apple.", "an", ["a", "an", "the"], {
    "a": "apple با صدای صدادار شروع می‌شود، پس an می‌گیرد. ملاک صداست نه حرف.",
    "the": "the برای چیز مشخص و شناخته‌شده است. اینجا یک سیب نامعین است.",
})
CHECK[(2, 2)] = ("جمع درست: one watch → two ___", "watches", ["watchs", "watches", "watchies"], {
    "watchs": "اسم مختوم به ch در جمع es می‌گیرد، نه s تنها.",
    "watchies": "قاعده y→ies برای واژه‌های مختوم به y است، نه ch.",
})

CHECK[(3, 1)] = ("___ is that? — It's a pen.", "What", ["Who", "What", "Where"], {
    "Who": "Who برای پرسیدن درباره **افراد** است. پاسخ اینجا یک شیء است.",
    "Where": "Where برای پرسیدن درباره مکان است، نه اینکه چیزی چیست.",
})
CHECK[(3, 2)] = ("سوالی کنید: She is happy.", "Is she happy?", ["Is she happy?", "She is happy?", "Does she happy?"], {
    "She is happy?": "در انگلیسی فقط بالا بردن لحن کافی نیست؛ فعل باید به ابتدای جمله برود.",
    "Does she happy?": "با فعل to be هرگز do/does نمی‌آید. خودِ is به ابتدا می‌رود.",
})

CHECK[(4, 1)] = ("This is ___ book. (مالِ او، یک آقا)", "his", ["his", "her", "he"], {
    "her": "her برای خانم‌هاست. برای یک آقا his می‌آید.",
    "he": "he ضمیر فاعلی است و پیش از اسم نمی‌نشیند. شکل ملکی‌اش his است.",
})
CHECK[(4, 2)] = ("کیفِ سارا:", "Sara's bag", ["Sara's bag", "bag of Sara", "Sara bag"], {
    "bag of Sara": "برای انسان معمولاً 's به‌کار می‌رود، نه ساختار of.",
    "Sara bag": "بدون 's رابطه مالکیت نشان داده نمی‌شود.",
})

CHECK[(5, 1)] = ("___ old are you? — I'm thirty.", "How", ["How", "What", "Who"], {
    "What": "برای پرسیدن سن، ساختار ثابت How old است — نه What old.",
    "Who": "Who درباره هویت شخص می‌پرسد، نه سنش.",
})
CHECK[(5, 2)] = ("پاسخ درست به «How old are you?»", "I'm twenty.", ["I'm twenty.", "I have twenty.", "I'm twenty years."], {
    "I have twenty.": "در فارسی «بیست سال دارم» می‌گوییم، اما انگلیسی از فعل to be استفاده می‌کند.",
    "I'm twenty years.": "یا I'm twenty یا I'm twenty years old — نصفه‌اش درست نیست.",
})

CHECK[(6, 1)] = ("___ are you from? — I'm from Iran.", "Where", ["Where", "What", "How"], {
    "What": "What درباره چیزهاست. برای مکان و کشور Where می‌آید.",
    "How": "How درباره چگونگی است، نه اینکه اهل کجایی.",
})
CHECK[(6, 2)] = ("I live ___ Tehran.", "in", ["in", "on", "at"], {
    "on": "on برای سطح است (on the table). برای شهر in می‌آید.",
    "at": "at برای نقطه دقیق است (at the door). برای شهر in درست است.",
})

CHECK[(7, 1)] = ("She is ___ engineer.", "an", ["a", "an", "—"], {
    "a": "engineer با صدای صدادار شروع می‌شود، پس an می‌گیرد.",
    "—": "برخلاف فارسی، پیش از نام شغل در انگلیسی حتماً a یا an می‌آید.",
})
CHECK[(7, 2)] = ("They ___ doctors.", "are", ["is", "are", "am"], {
    "is": "is برای یک نفر است. They جمع است، پس are می‌گیرد.",
    "am": "am فقط با I می‌آید.",
})

CHECK[(8, 1)] = ("I ___ to school every day.", "go", ["go", "goes", "going"], {
    "goes": "s سوم‌شخص فقط با he، she و it می‌آید. با I فعل ساده می‌ماند.",
    "going": "going به‌تنهایی فعل کامل نیست؛ به am/is/are نیاز دارد.",
})
CHECK[(8, 2)] = ("منفی: I ___ like coffee.", "don't", ["don't", "doesn't", "am not"], {
    "doesn't": "doesn't برای he، she و it است. با I از don't استفاده می‌شود.",
    "am not": "am not با صفت و اسم می‌آید، نه با فعل اصلی مثل like.",
})

CHECK[(9, 1)] = ("He ___ every day.", "works", ["work", "works", "working"], {
    "work": "با he، she و it فعل s می‌گیرد. این پرکاربردترین خطای این سطح است.",
    "working": "working بدون is فعل کامل نیست.",
})
CHECK[(9, 2)] = ("She ___ like tea.", "doesn't", ["don't", "doesn't", "isn't"], {
    "don't": "با she از doesn't استفاده می‌شود.",
    "isn't": "isn't با صفت و اسم می‌آید، نه با فعل اصلی مثل like.",
})

CHECK[(10, 1)] = ("The class is ___ Monday.", "on", ["at", "on", "in"], {
    "at": "at برای ساعت است (at five). برای روزهای هفته on می‌آید.",
    "in": "in برای ماه، سال و بخش‌های روز است (in May, in the morning).",
})
CHECK[(10, 2)] = ("I get up ___ seven o'clock.", "at", ["at", "on", "in"], {
    "on": "on برای روز و تاریخ است، نه ساعت.",
    "in": "in برای بازه‌های بلند است. برای ساعت دقیق at می‌آید.",
})

# ---------------------------------------------------------------- درس ۱۱ تا ۲۰

CHECK[(11, 1)] = ("I like ___ .", "swimming", ["swim", "swimming", "to swimming"], {
    "swim": "بعد از like فعل با ing می‌آید.",
    "to swimming": "یا to swim یا swimming — این دو با هم نمی‌آیند.",
})
CHECK[(11, 2)] = ("شکل ing درست: run →", "running", ["runing", "running", "runnning"], {
    "runing": "فعل کوتاهی که به یک بی‌صدا ختم می‌شود، آن حرف را دو بار می‌گیرد.",
    "runnning": "فقط یک n اضافه می‌شود، نه دو تا.",
})

CHECK[(12, 1)] = ("There isn't ___ milk.", "any", ["some", "any", "a"], {
    "some": "some در جمله مثبت می‌آید. در منفی و سوالی any درست است.",
    "a": "milk ناشمارا است و a نمی‌گیرد.",
})
CHECK[(12, 2)] = ("کدام درست است؟", "two pieces of bread", ["two breads", "two pieces of bread", "two bread"], {
    "two breads": "bread ناشمارا است و جمع بسته نمی‌شود. در فارسی «دو نان» می‌گوییم، اما انگلیسی واحد می‌خواهد.",
    "two bread": "عدد پیش از اسم ناشمارا نمی‌آید؛ باید واحد بیاید.",
})

CHECK[(13, 1)] = ("مؤدبانه‌ترین شکل در رستوران:", "I'd like a coffee.", ["I want a coffee.", "I'd like a coffee.", "Give me a coffee."], {
    "I want a coffee.": "want درست است اما خشک شنیده می‌شود. در رستوران I'd like رایج‌تر و مؤدبانه‌تر است.",
    "Give me a coffee.": "این شکل دستوری و بی‌ادبانه شنیده می‌شود.",
})
CHECK[(13, 2)] = ("Can I ___ the bill, please?", "have", ["have", "having", "to have"], {
    "having": "بعد از can فعل ساده می‌آید.",
    "to have": "بعد از can هرگز to نمی‌آید.",
})

CHECK[(14, 1)] = ("___ three rooms in my flat.", "There are", ["There is", "There are", "It is"], {
    "There is": "برای جمع there are می‌آید. rooms جمع است.",
    "It is": "It is یعنی «آن است». برای گفتن «وجود دارد» از there استفاده می‌شود.",
})
CHECK[(14, 2)] = ("جواب کوتاه: Is there a garden? — No, ___ .", "there isn't", ["there isn't", "it isn't", "there aren't"], {
    "it isn't": "در جواب کوتاه همان there تکرار می‌شود، نه it.",
    "there aren't": "سوال مفرد بود (a garden)، پس جواب هم مفرد است.",
})

CHECK[(15, 1)] = ("The key is ___ the bag.", "in", ["in", "on", "under"], {
    "on": "on یعنی روی سطح. کلید داخل کیف است، پس in می‌آید.",
    "under": "under یعنی زیر. کلید داخل است، نه زیر.",
})
CHECK[(15, 2)] = ("The table is ___ two chairs.", "between", ["between", "next to", "behind"], {
    "next to": "next to یعنی کنارِ یک چیز. برای وسطِ **دو** چیز between می‌آید.",
    "behind": "behind یعنی پشت، که جای میز را نسبت به دو صندلی نشان نمی‌دهد.",
})

CHECK[(16, 1)] = ("She ___ got a blue dress.", "has", ["have", "has", "is"], {
    "have": "با he، she و it شکل has می‌آید.",
    "is": "ساختار have got است، نه is got.",
})
CHECK[(16, 2)] = ("ترتیب درست:", "a red coat", ["a red coat", "a coat red", "red a coat"], {
    "a coat red": "برخلاف فارسی، صفت در انگلیسی **پیش از** اسم می‌آید.",
    "red a coat": "حرف تعریف a پیش از صفت می‌آید، نه بعدش.",
})

CHECK[(17, 1)] = ("How much ___ these shoes?", "are", ["is", "are", "do"], {
    "is": "shoes جمع است، پس are می‌گیرد.",
    "do": "برای پرسیدن قیمت از فعل to be استفاده می‌شود، نه do.",
})
CHECK[(17, 2)] = ("کدام درست است؟", "these shirts", ["these shirt", "these shirts", "this shirts"], {
    "these shirt": "بعد از these اسم باید جمع باشد.",
    "this shirts": "this مفرد است و با اسم جمع نمی‌آید.",
})

CHECK[(18, 1)] = ("کدام جمله درست است؟", "It is cold today.", ["Is cold today.", "It is cold today.", "There is cold today."], {
    "Is cold today.": "جمله انگلیسی بدون فاعل نمی‌شود. برای هوا همیشه it می‌آید.",
    "There is cold today.": "there is برای «وجود داشتن» است. برای وضعیت هوا it is درست است.",
})
CHECK[(18, 2)] = ("همین حالا باران می‌بارد:", "It is raining.", ["It is rainy.", "It is raining.", "It rains."], {
    "It is rainy.": "rainy وضعیت کلی هواست. برای «همین حالا در حال باریدن» شکل ing می‌آید.",
    "It rains.": "این یعنی «معمولاً می‌بارد»، نه همین حالا.",
})

CHECK[(19, 1)] = ("She ___ swim.", "can", ["can", "cans", "can to"], {
    "cans": "can با هیچ فاعلی s نمی‌گیرد — حتی با he و she.",
    "can to": "بعد از can هرگز to نمی‌آید.",
})
CHECK[(19, 2)] = ("منفی: I ___ drive.", "can't", ["can't", "don't can", "not can"], {
    "don't can": "can خودش منفی می‌شود و به don't نیاز ندارد.",
    "not can": "not بعد از can می‌آید، نه پیش از آن.",
})

CHECK[(20, 1)] = ("You ___ rest at home.", "should", ["should", "should to", "shoulds"], {
    "should to": "بعد از should فعل ساده می‌آید، بدون to.",
    "shoulds": "should با هیچ فاعلی s نمی‌گیرد.",
})
CHECK[(20, 2)] = ("کدام درست است؟", "I have a headache.", ["I have a headache.", "I am headache.", "I have headache."], {
    "I am headache.": "شما «سردرد» نیستید؛ سردرد **دارید**. پس فعل have می‌آید.",
    "I have headache.": "headache شمارا است و a می‌خواهد.",
})

# ---------------------------------------------------------------- درس ۲۱ تا ۳۰

CHECK[(21, 1)] = ("جمله امری درست:", "Turn left.", ["Turn left.", "You turn left.", "To turn left."], {
    "You turn left.": "در جمله امری فاعل حذف می‌شود.",
    "To turn left.": "جمله امری با فعل ساده شروع می‌شود، بدون to.",
})
CHECK[(21, 2)] = ("منفی امری: ___ turn right.", "Don't", ["Don't", "Not", "No"], {
    "Not": "برای منفی کردن جمله امری Don't می‌آید، نه not تنها.",
    "No": "No برای پاسخ منفی است، نه ساختن جمله امری منفی.",
})

CHECK[(22, 1)] = ("She ___ now.", "is reading", ["reading", "is reading", "reads"], {
    "reading": "فعل to be جا افتاده. حال استمراری همیشه am/is/are می‌خواهد.",
    "reads": "reads یعنی «معمولاً می‌خواند». برای همین حالا شکل استمراری لازم است.",
})
CHECK[(22, 2)] = ("سوالی: ___ you working?", "Are", ["Are", "Do", "Is"], {
    "Do": "در حال استمراری فعل to be به ابتدا می‌رود، نه do.",
    "Is": "با you شکل are می‌آید.",
})

CHECK[(23, 1)] = ("I ___ walk to work, but today I'm taking the bus.", "usually", ["usually", "now", "am"], {
    "now": "now با حال استمراری می‌آید. اینجا کارِ همیشگی توصیف می‌شود.",
    "am": "بعد از am باید فعل ing بیاید؛ walk ساده است.",
})
CHECK[(23, 2)] = ("جای درست قید: She ___ busy.", "is always", ["is always", "always is", "always"], {
    "always is": "قید تکرار **بعد از** فعل to be می‌آید، نه پیش از آن.",
    "always": "فعل to be جا افتاده است.",
})

CHECK[(24, 1)] = ("They ___ at the park.", "were", ["was", "were", "are"], {
    "was": "was با I، he، she و it می‌آید. با they شکل were است.",
    "are": "are زمان حال است؛ جمله درباره گذشته است.",
})
CHECK[(24, 2)] = ("سوالی: ___ you free yesterday?", "Were", ["Were", "Did", "Was"], {
    "Did": "با فعل to be هرگز did نمی‌آید؛ خودِ were به ابتدا می‌رود.",
    "Was": "با you شکل were می‌آید.",
})

CHECK[(25, 1)] = ("گذشته درست: study →", "studied", ["studyed", "studied", "studyied"], {
    "studyed": "فعل مختوم به بی‌صدا + y، حرف y را به i تبدیل می‌کند.",
    "studyied": "فقط y به i تبدیل می‌شود و ed اضافه می‌شود، نه هر دو.",
})
CHECK[(25, 2)] = ("گذشته درست: stop →", "stopped", ["stoped", "stopped", "stopeed"], {
    "stoped": "فعل کوتاهی که به یک بی‌صدا ختم می‌شود، آن حرف را دو بار می‌گیرد.",
    "stopeed": "e اضافه‌ای در کار نیست؛ فقط p دو بار می‌آید.",
})

CHECK[(26, 1)] = ("گذشته درست: go →", "went", ["goed", "went", "gone"], {
    "goed": "go بی‌قاعده است و ed نمی‌گیرد.",
    "gone": "gone شکل سوم فعل است و به‌تنهایی برای گذشته ساده به‌کار نمی‌رود.",
})
CHECK[(26, 2)] = ("منفی: I ___ to Tehran.", "didn't go", ["didn't go", "didn't went", "not went"], {
    "didn't went": "بعد از didn't فعل به شکل **ساده** برمی‌گردد؛ didn't خودش گذشته را نشان داده.",
    "not went": "برای منفی کردن گذشته ساده به didn't نیاز است.",
})

CHECK[(27, 1)] = ("___ you like the holiday?", "Did", ["Do", "Did", "Was"], {
    "Do": "Do زمان حال است؛ جمله درباره گذشته است.",
    "Was": "was با صفت و اسم می‌آید، نه با فعل اصلی مثل like.",
})
CHECK[(27, 2)] = ("کدام درست است؟", "Did you like it?", ["Did you liked it?", "Did you like it?", "Did you liking it?"], {
    "Did you liked it?": "بعد از did فعل ساده می‌ماند؛ خودِ did گذشته بودن را نشان داده.",
    "Did you liking it?": "شکل ing بعد از did نمی‌آید.",
})

CHECK[(28, 1)] = ("I'm going ___ study tomorrow.", "to", ["to", "for", "—"], {
    "for": "ساختار ثابت be going **to** است.",
    "—": "بدون to، ساختار آینده کامل نیست.",
})
CHECK[(28, 2)] = ("کدام درست است؟", "I'm going to study.", ["I'm going to studying.", "I'm going to study.", "I going to study."], {
    "I'm going to studying.": "بعد از going to فعل ساده می‌آید، نه ing.",
    "I going to study.": "فعل to be جا افتاده است.",
})

CHECK[(29, 1)] = ("Tehran is ___ Yazd.", "bigger than", ["bigger than", "bigger that", "more big than"], {
    "bigger that": "برای مقایسه than می‌آید، نه that.",
    "more big than": "big صفت کوتاه است و er می‌گیرد، نه more.",
})
CHECK[(29, 2)] = ("تفضیلی درست: good →", "better", ["gooder", "better", "more good"], {
    "gooder": "good بی‌قاعده است و er نمی‌گیرد.",
    "more good": "شکل تفضیلی good همان better است؛ more لازم نیست.",
})

CHECK[(30, 1)] = ("It was ___ day of the year.", "the best", ["the best", "best", "the better"], {
    "best": "صفت عالی همیشه با the می‌آید.",
    "the better": "better برای مقایسه دو چیز است. برای «ترین» از میان همه، best می‌آید.",
})
CHECK[(30, 2)] = ("عالی درست: expensive →", "the most expensive", ["the expensivest", "the most expensive", "the more expensive"], {
    "the expensivest": "صفت بلند est نمی‌گیرد؛ the most پیش از آن می‌آید.",
    "the more expensive": "more برای مقایسه دو چیز است، نه صفت عالی.",
})
