# -*- coding: utf-8 -*-
"""
داستانک‌های سطح A1 — شش داستان روی دروس ۵، ۱۰، ۱۵، ۲۰، ۲۵ و ۳۰.

این سطح تنگ‌ترین قید را دارد: تا درس ۷ هیچ فعلی جز to be تدریس نشده و
تا درس ۲۴ هیچ زمان گذشته‌ای. پس داستان درس ۵ فقط با «بودن»، صفت، عدد و
مالکیت ساخته می‌شود — بدون فعل عادی، بدون حرف اضافه مکان، بدون گذشته.

هر داستان دقیقاً همان چیزی را به کار می‌برد که تا آن درس تدریس شده:

  درس ۵  ← to be، a/an، جمع، مالکیت و 's، عدد و سن، this/that/here
  درس ۱۰ ← + حال ساده (هر دو شکل)، پرسش با where، at/on/in زمان، مشاغل
  درس ۱۵ ← + like/love/hate + ing، some/any، there is/are، حرف اضافه مکان
  درس ۲۰ ← + have got، لباس و رنگ، قیمت، آب‌وهوا، can/can't، should
  درس ۲۵ ← + امری، حال استمراری، was/were، گذشته ساده باقاعده
  درس ۳۰ ← + گذشته بی‌قاعده، پرسش با did، be going to، تفضیلی و عالی

قید سخت‌گیرانه‌تر از سطوح دیگر است، اما همین است که داستان را خواندنی
می‌کند: کاربر یک بند را می‌خواند و هیچ چیز تازه‌ای جلویش سبز نمی‌شود.

ساختار: (عنوان, [(بند, ترجمه)], [(پرسش, ترجمه, پاسخ, [گزینه‌ها], راهنما)])
"""

STORIES = {}

# ---------------------------------------------------------------- درس ۵
# فقط to be. هیچ فعل دیگری در کار نیست، چون حال ساده از درس ۸ می‌آید.
STORIES[5] = (
    "Sara's Birthday",
    [
        ("Hello! This is my family. My father is a doctor and my mother is a teacher. "
         "I am a student.",
         "سلام! این خانواده من است. پدرم پزشک است و مادرم معلم. من دانش‌آموزم."),

        ("This is my sister. Her name is Sara. She is nine years old. "
         "It is her birthday.",
         "این خواهر من است. نامش سارا است. نه سال دارد. تولد اوست."),

        ("Our house is nice. Many people are here. My grandmother and my grandfather "
         "are here. Sara's friends are here too.",
         "خانه ما قشنگ است. آدم‌های زیادی اینجا هستند. مادربزرگ و پدربزرگم اینجا هستند. "
         "دوستان سارا هم اینجا هستند."),

        ("It is a happy party. Nine candles are here. What is Sara's gift? "
         "It is a new book!",
         "جشن شادی است. نه شمع اینجاست. هدیه سارا چیست؟ یک کتاب نو است!"),
    ],
    [
        ("How old is Sara?", "سارا چند سال دارد؟",
         "Nine", ["Nine", "Seven", "Ten", "Three"],
         "بند دوم — عدد سن."),

        ("What is Sara's gift?", "هدیه سارا چیست؟",
         "A book", ["A book", "A phone", "A ball", "A watch"],
         "بند چهارم."),

        ("Who is a doctor?", "چه کسی پزشک است؟",
         "The father", ["The father", "The mother", "Sara", "The grandfather"],
         "بند اول."),

        ("Is Sara a teacher?", "آیا سارا معلم است؟",
         "No, she is a sister", ["No, she is a sister", "Yes, she is",
                                 "No, she is a doctor", "Yes, she is a student"],
         "بند اول و دوم — معلم مادر است."),

        ("Whose birthday is it?", "تولد چه کسی است؟",
         "Sara's", ["Sara's", "My mother's", "My father's", "My brother's"],
         "بند دوم — مالکیت با 's."),
    ],
)

# --------------------------------------------------------------- درس ۱۰
# حال ساده در هر دو شکل، قیدهای تکرار، و ساعت. هنوز گذشته‌ای نیست.
STORIES[10] = (
    "My Uncle the Driver",
    [
        ("My uncle is a driver. He lives in a small village. The village is near a big city. "
         "His house is old but beautiful.",
         "عموی من راننده است. در روستای کوچکی زندگی می‌کند. روستا نزدیک شهر بزرگی است. "
         "خانه‌اش قدیمی اما زیباست."),

        ("He wakes up early every day. He eats breakfast at six o'clock. "
         "Then he goes to work.",
         "هر روز زود بیدار می‌شود. ساعت شش صبحانه می‌خورد. بعد سر کار می‌رود."),

        ("His work is in the city. He always helps people. "
         "He usually finishes at four o'clock, but on Thursday he finishes late.",
         "کارش در شهر است. همیشه به مردم کمک می‌کند. معمولاً ساعت چهار تمام می‌کند، "
         "اما پنجشنبه دیر تمام می‌کند."),

        ("At the weekend he never works. He sits with his family and reads a book. "
         "Where is my uncle now? He is at home!",
         "آخر هفته هرگز کار نمی‌کند. با خانواده‌اش می‌نشیند و کتاب می‌خواند. "
         "عمویم الان کجاست؟ خانه است!"),
    ],
    [
        ("What is the uncle's job?", "شغل عمو چیست؟",
         "A driver", ["A driver", "A teacher", "A cook", "A farmer"],
         "بند اول."),

        ("When does he eat breakfast?", "کِی صبحانه می‌خورد؟",
         "At six o'clock", ["At six o'clock", "At four o'clock",
                            "At the weekend", "Late"],
         "بند دوم."),

        ("Where does he live?", "کجا زندگی می‌کند؟",
         "In a village", ["In a village", "In the city", "In a shop", "In an office"],
         "بند اول."),

        ("Does he work at the weekend?", "آخر هفته کار می‌کند؟",
         "No, never", ["No, never", "Yes, always", "Yes, usually", "Yes, sometimes"],
         "بند چهارم — قید تکرار."),

        ("What does he do on Thursday?", "پنجشنبه چه می‌کند؟",
         "He finishes late", ["He finishes late", "He finishes early",
                              "He is at home", "He reads a book"],
         "بند سوم."),
    ],
)

# --------------------------------------------------------------- درس ۱۵
# there is / there are، حرف اضافه مکان، و like/love + ing.
STORIES[15] = (
    "The New Flat",
    [
        ("My friend Ali has a new flat. There are three rooms: a kitchen, a bedroom "
         "and a living room. There is a small garden too.",
         "دوستم علی آپارتمان تازه‌ای دارد. سه اتاق دارد: آشپزخانه، اتاق خواب و نشیمن. "
         "یک باغچه کوچک هم هست."),

        ("In the living room there is a big sofa. The television is on a desk "
         "next to the window. A picture is on the wall behind the sofa.",
         "در نشیمن یک مبل بزرگ هست. تلویزیون روی میزی کنار پنجره است. "
         "تابلویی روی دیوار پشت مبل است."),

        ("Ali loves cooking. There is some rice and some bread in the kitchen, "
         "but there isn't any meat. Is there any cheese? Yes, there is.",
         "علی عاشق آشپزی است. در آشپزخانه کمی برنج و نان هست، اما گوشتی نیست. "
         "پنیر هست؟ بله، هست."),

        ("Ali likes sitting in the garden. There is a chair under the tree. "
         "«Would you like some tea?» «Yes, please!»",
         "علی دوست دارد در باغچه بنشیند. زیر درخت یک صندلی هست. "
         "«کمی چای می‌خواهی؟» «بله، لطفاً!»"),
    ],
    [
        ("How many rooms are there?", "چند اتاق هست؟",
         "Three", ["Three", "Two", "Four", "One"],
         "بند اول."),

        ("Where is the picture?", "تابلو کجاست؟",
         "On the wall", ["On the wall", "Under the table",
                         "Next to the window", "In the garden"],
         "بند دوم."),

        ("What isn't in the kitchen?", "چه چیزی در آشپزخانه نیست؟",
         "Meat", ["Meat", "Rice", "Bread", "Cheese"],
         "بند سوم — جمله منفی با any."),

        ("What does Ali love?", "علی عاشق چیست؟",
         "Cooking", ["Cooking", "Painting", "Running", "Dancing"],
         "بند سوم — بعد از love فعل ing می‌آید."),

        ("Where is the chair?", "صندلی کجاست؟",
         "Under the tree", ["Under the tree", "On the sofa",
                            "Behind the door", "Next to the window"],
         "بند چهارم."),
    ],
)

# --------------------------------------------------------------- درس ۲۰
# آب‌وهوا، لباس و رنگ، can/can't، و should برای توصیه.
STORIES[20] = (
    "A Cold Morning",
    [
        ("It is winter. Today it is very cold and it is snowing. "
         "There are many clouds and the wind is cold.",
         "زمستان است. امروز خیلی سرد است و برف می‌آید. ابرهای زیادی هست و باد سرد است."),

        ("Maryam has got a long black coat and a red scarf. "
         "She has got warm shoes too. She can walk to school.",
         "مریم پالتوی مشکی بلند و شال قرمزی دارد. کفش گرم هم دارد. "
         "می‌تواند پیاده به مدرسه برود."),

        ("Her brother can't walk today. He is ill. He has got a headache and a fever. "
         "He should rest at home.",
         "برادرش امروز نمی‌تواند راه برود. مریض است. سردرد و تب دارد. "
         "باید خانه استراحت کند."),

        ("In the shop the scarf is cheap. How much is it? It is two hundred. "
         "The coat is expensive, but Maryam's coat isn't new. It is her sister's coat.",
         "در مغازه شال ارزان است. چند است؟ دویست تومان. پالتو گران است، "
         "اما پالتوی مریم نو نیست. پالتوی خواهرش است."),
    ],
    [
        ("What is the weather like?", "هوا چطور است؟",
         "Cold", ["Cold", "Hot", "Warm", "Sunny"],
         "بند اول."),

        ("What colour is Maryam's scarf?", "شال مریم چه رنگی است؟",
         "Red", ["Red", "Black", "Blue", "Green"],
         "بند دوم."),

        ("Why can't her brother walk to school?", "چرا برادرش نمی‌تواند پیاده به مدرسه برود؟",
         "He is ill", ["He is ill", "It is too cold",
                       "He has got no shoes", "He is late"],
         "بند سوم."),

        ("What should the brother do?", "برادر باید چه کند؟",
         "Rest at home", ["Rest at home", "Walk to school",
                          "Buy a scarf", "Go to the shop"],
         "بند سوم — توصیه با should."),

        ("Is Maryam's coat new?", "پالتوی مریم نو است؟",
         "No, it is her sister's", ["No, it is her sister's", "Yes, it is new",
                                    "No, it is her mother's", "Yes, it is cheap"],
         "بند چهارم."),
    ],
)

# --------------------------------------------------------------- درس ۲۵
# was/were و گذشته ساده باقاعده، کنار حال استمراری برای پس‌زمینه.
STORIES[25] = (
    "The Football Match",
    [
        ("Last week my friends and I played football in the park. "
         "The weather was warm and the park was full of people.",
         "هفته گذشته من و دوستانم در پارک فوتبال بازی کردیم. "
         "هوا گرم بود و پارک پر از آدم بود."),

        ("We started at four o'clock. My brother watched us. "
         "He was excited and he shouted my name.",
         "ساعت چهار شروع کردیم. برادرم ما را تماشا کرد. هیجان‌زده بود و نامم را فریاد زد."),

        ("After twenty minutes I was tired. I wanted some water. "
         "My friend Reza helped me and we walked slowly to a chair.",
         "بعد از بیست دقیقه خسته بودم. کمی آب می‌خواستم. دوستم رضا کمکم کرد "
         "و آرام تا یک صندلی راه رفتیم."),

        ("Look at this picture! Now we are laughing and my brother is smiling. "
         "It was a long day, but we liked it. We finished at six o'clock.",
         "به این تصویر نگاه کن! حالا داریم می‌خندیم و برادرم لبخند می‌زند. "
         "روز طولانی‌ای بود، اما دوستش داشتیم. ساعت شش تمام کردیم."),
    ],
    [
        ("Where was the football match?", "بازی فوتبال کجا بود؟",
         "In the park", ["In the park", "At school", "At home", "In the street"],
         "بند اول."),

        ("How was the weather?", "هوا چطور بود؟",
         "Warm", ["Warm", "Cold", "Wet", "Sunny"],
         "بند اول — was برای گذشته."),

        ("Who helped the narrator?", "چه کسی به راوی کمک کرد؟",
         "Reza", ["Reza", "His brother", "His mother", "The teacher"],
         "بند سوم."),

        ("How was the narrator after twenty minutes?", "راوی بعد از بیست دقیقه چطور بود؟",
         "Tired", ["Tired", "Happy", "Angry", "Ready"],
         "بند سوم — was برای حالت گذشته."),

        ("What is the brother doing in the picture?", "برادر در تصویر چه می‌کند؟",
         "Smiling", ["Smiling", "Shouting", "Playing", "Walking"],
         "بند چهارم — حال استمراری."),
    ],
)

# --------------------------------------------------------------- درس ۳۰
# جمع‌بندی سطح: گذشته بی‌قاعده، پرسش با did، going to، تفضیلی و عالی.
STORIES[30] = (
    "The Best Trip",
    [
        ("Last summer my family went to the sea. We took a train and it was a long trip. "
         "My father drove us to the station early in the morning.",
         "تابستان گذشته خانواده‌ام به دریا رفتیم. با قطار رفتیم و سفر طولانی‌ای بود. "
         "پدرم صبح زود ما را تا ایستگاه رساند."),

        ("We arrived at a small hotel near the beach. The hotel was cheaper than "
         "the hotel in the city, but our room was bigger and cleaner.",
         "به هتل کوچکی نزدیک ساحل رسیدیم. آن هتل از هتلِ داخل شهر ارزان‌تر بود، "
         "اما اتاق ما بزرگ‌تر و تمیزتر بود."),

        ("On the first day we saw a boat and we ate fish. I took many photos with my "
         "new camera. My sister slept on the beach.",
         "روز اول قایقی دیدیم و ماهی خوردیم. با دوربین نوَم عکس‌های زیادی گرفتم. "
         "خواهرم روی ساحل خوابید."),

        ("Did you enjoy the trip? Yes! It was the best week of the year. "
         "Next summer we are going to visit the mountains. I hope it is as good as the sea.",
         "از سفر لذت بردی؟ بله! بهترین هفته سال بود. تابستان بعد قرار است به کوه برویم. "
         "امیدوارم به خوبیِ دریا باشد."),
    ],
    [
        ("How did the family travel?", "خانواده چطور سفر کردند؟",
         "By train", ["By train", "By car", "By boat", "By bus"],
         "بند اول."),

        ("Was the hotel expensive?", "هتل گران بود؟",
         "No, it was cheaper", ["No, it was cheaper", "Yes, it was expensive",
                                "It was the same", "We don't know"],
         "بند دوم — صفت تفضیلی."),

        ("What did the sister do on the beach?", "خواهر روی ساحل چه کرد؟",
         "She slept", ["She slept", "She took photos", "She found a boat", "She ate fish"],
         "بند سوم — گذشته بی‌قاعده."),

        ("What are they going to do next summer?", "تابستان بعد قرار است چه کنند؟",
         "Visit the mountains", ["Visit the mountains", "Go to the sea",
                                 "Stay at home", "Buy a camera"],
         "بند چهارم — be going to برای آینده."),

        ("How was the week?", "آن هفته چطور بود؟",
         "The best of the year", ["The best of the year", "The worst of the year",
                                  "Shorter than a day", "Very bad"],
         "بند چهارم — صفت عالی."),
    ],
)
