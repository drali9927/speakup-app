# -*- coding: utf-8 -*-
"""مکالمه، گرامر و تمرین دروس ۱ تا ۱۰ سطح A2."""

P, R = "تولیدی", "تشخیصی"

# (گوینده, انگلیسی, فارسی)
DLG = {
 1: [("A","What time do you usually wake up?","معمولاً ساعت چند بیدار می‌شوی؟"),
     ("B","At seven. But today I woke up late.","ساعت هفت. اما امروز دیر بیدار شدم."),
     ("A","Do you have breakfast at home?","صبحانه را خانه می‌خوری؟"),
     ("B","Usually, yes. I hardly ever skip it.","معمولاً بله. به‌ندرت جا می‌اندازمش."),
     ("A","And when do you leave?","و کِی از خانه می‌روی؟"),
     ("B","At eight, but today I had to hurry.","ساعت هشت، اما امروز باید عجله می‌کردم.")],
 2: [("A","What are you doing at the moment?","الان داری چه کار می‌کنی؟"),
     ("B","I'm washing the dishes. And you?","دارم ظرف می‌شویم. تو چطور؟"),
     ("A","I'm tidying the living room.","دارم اتاق نشیمن را مرتب می‌کنم."),
     ("B","Is Ali still upstairs?","علی هنوز طبقه بالاست؟"),
     ("A","No, he's downstairs. He's fixing the door.","نه، پایین است. دارد در را تعمیر می‌کند."),
     ("B","Good. Then we can finish the housework together.","خوب است. پس می‌توانیم کارهای خانه را با هم تمام کنیم.")],
 3: [("A","Where did you meet your best friend?","با بهترین دوستت کجا آشنا شدی؟"),
     ("B","We met at university, ten years ago.","دانشگاه، ده سال پیش."),
     ("A","That's a long time!","خیلی وقت است!"),
     ("B","Yes. It's a great memory.","بله. خاطره عالی‌ای است."),
     ("A","Did you keep in touch?","ارتباطتان را حفظ کردید؟"),
     ("B","Of course. I called her last week.","البته. هفته گذشته بهش زنگ زدم.")],
}

# (عنوان, توضیح, مثال‌ها, پرسش بررسی, پاسخ, گزینه‌ها, نکته هر گزینه غلط)
GR = {
 1: [("مرور حال ساده",
      "برای کارهای همیشگی و روال روزانه.\n"
      "با he، she و it فعل s می‌گیرد؛ با بقیه فاعل‌ها بدون تغییر می‌ماند.\n"
      "منفی و سوالی با do و does ساخته می‌شود و فعل به شکل ساده برمی‌گردد:\n"
      "She works → She doesn't work → Does she work?\n"
      "دقت کنید: بعد از doesn't و does، دیگر s نمی‌آید.",
      "I wake up at seven.\nShe wakes up at six.\nHe doesn't work on Friday.\nDo you leave at eight?\nDoes she call every day?",
      "کدام درست است؟", "She doesn't work here.",
      "She doesn't work here. | She doesn't works here. | She don't work here.",
      "She doesn't works here. ← بعد از doesn't فعل ساده می‌آید، بدون s.\n"
      "She don't work here. ← با she باید doesn't بیاید نه don't."),
     ("جای قیدهای تکرار",
      "always · usually · often · sometimes · hardly ever · never\n"
      "**پیش از** فعل اصلی می‌آیند: I usually walk.\n"
      "اما **بعد از** فعل to be: She is usually late.\n"
      "hardly ever و never خودشان منفی‌اند؛ جمله دیگر منفی نمی‌شود:\n"
      "درست: I never eat out — نادرست: I don't never eat out",
      "I usually take the bus.\nShe hardly ever eats out.\nHe is always busy.\nWe never arrive late.\nThey sometimes call me.",
      "قید را در جای درست بگذارید: She is ___ late. (usually)",
      "She is usually late.",
      "She is usually late. | She usually is late.",
      "She usually is late. ← قید بعد از فعل to be می‌آید، نه پیش از آن.")],

 2: [("حال استمراری در برابر حال ساده",
      "حال استمراری برای کاری که **همین حالا** در جریان است:\n"
      "I'm washing the dishes. · She's cooking at the moment.\n"
      "حال ساده برای کاری که **همیشه** انجام می‌شود:\n"
      "I wash the dishes every evening.\n"
      "نشانه‌ها: now، at the moment، still → استمراری\n"
      "usually، every day، always → ساده",
      "I'm cooking now.\nI cook every evening.\nShe's still working.\nShe works every day.\nWhat are you doing? — I'm tidying the room.",
      "کدام درست است؟", "She is cooking at the moment.",
      "She is cooking at the moment. | She cooks at the moment.",
      "She cooks at the moment. ← at the moment یعنی همین حالا، پس حال استمراری لازم است."),
     ("افعالی که استمراری نمی‌شوند",
      "بعضی فعل‌ها حالت یا احساس را نشان می‌دهند و شکل ing نمی‌گیرند:\n"
      "know · like · love · hate · want · need · understand · hear\n"
      "نادرست: I am knowing the answer\n"
      "درست: I know the answer\n"
      "این‌ها حتی وقتی «همین حالا» هم باشد، ساده می‌مانند.",
      "I know the answer.\nShe likes this song.\nWe need more time.\nI hear a noise.\nHe wants to leave.",
      "کدام درست است؟", "I know the answer.",
      "I know the answer. | I am knowing the answer.",
      "I am knowing the answer. ← know فعل حالت است و شکل ing نمی‌گیرد.")],

 3: [("گذشته ساده: مرور و افعال بی‌قاعده بیشتر",
      "فعل باقاعده ed می‌گیرد: work → worked\n"
      "فعل بی‌قاعده شکل خودش را دارد و باید حفظ شود:\n"
      "meet → met · build → built · win → won · lose → lost\n"
      "bring → brought · become → became · leave → left · hear → heard\n"
      "این شکل برای همه فاعل‌ها یکی است.",
      "We met at university.\nThey built this house.\nShe won the competition.\nI lost my phone.\nHe brought a gift.\nShe became a teacher.",
      "گذشته «meet» چیست؟", "met", "met | meeted | meted",
      "meeted ← meet بی‌قاعده است و ed نمی‌گیرد.\nmeted ← این شکل وجود ندارد."),
     ("منفی و سوالی گذشته",
      "با didn't و did ساخته می‌شود و فعل به **شکل ساده** برمی‌گردد:\n"
      "I met → I didn't meet (نه I didn't met)\n"
      "Did you meet her? (نه Did you met)\n"
      "دلیلش ساده است: خودِ did گذشته بودن را نشان داده، پس فعل دیگر\n"
      "لازم نیست گذشته باشد.",
      "I didn't meet him.\nShe didn't win.\nDid you call her?\nDid they build it?\nWhat did you do yesterday?",
      "کدام درست است؟", "I didn't meet him.",
      "I didn't meet him. | I didn't met him.",
      "I didn't met him. ← بعد از didn't فعل ساده می‌آید، نه شکل گذشته.")],
}

# (دسته, نوع, صورت سوال, پاسخ, جایگزین‌ها, گزینه‌ها, واژه هدف, راهنما)
EX = {
 1: [(R,"چندگزینه‌ای","She ___ work on Friday.","doesn't","","doesn't | don't | isn't","","با she فعل کمکی doesn't می‌آید."),
     (R,"تطبیق","hurry","عجله کردن","","hurry | leave | arrive","hurry","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","I ___ home at eight. (leave)","leave","","","leave","با I فعل بدون s می‌آید."),
     (P,"متن آزاد","منفی کنید: She wakes up early.","She doesn't wake up early.","She does not wake up early.","","wake up","بعد از doesn't فعل ساده می‌آید."),
     (P,"متن آزاد","قید را در جای درست بگذارید: I take the bus. (usually)","I usually take the bus.","","","usually","قید پیش از فعل اصلی می‌آید."),
     (P,"مرتب‌سازی","ever / she / out / hardly / eats","She hardly ever eats out.","","","hardly ever","قید پیش از فعل اصلی می‌آید."),
     (P,"ترجمه به انگلیسی","ساعت هفت بیدار می‌شوم.","I wake up at seven.","","","wake up","با I فعل بدون s می‌آید."),
     (P,"گفتار","I usually leave home at eight.","I usually leave home at eight.","","","leave","جمله را واضح تلفظ کنید.")],

 2: [(R,"چندگزینه‌ای","She ___ dinner at the moment.","is cooking","","is cooking | cooks | cook","cook","at the moment یعنی همین حالا."),
     (R,"تطبیق","neighbour","همسایه","","neighbour | noise | laundry","neighbour","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","I ___ the dishes now. (wash)","am washing","'m washing","","wash","برای «همین حالا» حال استمراری می‌آید."),
     (P,"متن آزاد","درست کنید: I am knowing the answer.","I know the answer.","","","still","know فعل حالت است و ing نمی‌گیرد."),
     (P,"متن آزاد","سوالی کنید: You are tidying the room.","Are you tidying the room?","","","tidy","فعل to be به ابتدای جمله می‌رود."),
     (P,"مرتب‌سازی","still / is / working / he","He is still working.","He's still working.","","still","قید still پیش از فعل اصلی می‌آید."),
     (P,"ترجمه به انگلیسی","خانه الان ساکت است.","The house is quiet now.","","","quiet","با فعل to be شروع کن."),
     (P,"گفتار","We are cooking together at the moment.","We are cooking together at the moment.","","","together","جمله را واضح تلفظ کنید.")],

 3: [(R,"چندگزینه‌ای","We ___ at university ten years ago.","met","","met | meeted | meet","met","گذشته meet همان met است."),
     (R,"تطبیق","memory","خاطره","","memory | childhood | event","memory","واژه را به معنی درست وصل کن."),
     (P,"جای خالی","She ___ the competition last year. (win)","won","","","won","گذشته win همان won است."),
     (P,"متن آزاد","گذشته بنویسید: bring","brought","","","brought","این فعل بی‌قاعده است."),
     (P,"متن آزاد","منفی کنید: I met him yesterday.","I didn't meet him yesterday.","I did not meet him yesterday.","","met","بعد از didn't فعل ساده می‌آید."),
     (P,"مرتب‌سازی","house / they / this / built","They built this house.","","","built","با فاعل شروع کن."),
     (P,"ترجمه به انگلیسی","گوشی‌ام را گم کردم.","I lost my phone.","","","lost","گذشته lose همان lost است."),
     (P,"گفتار","It was a great day and a good memory.","It was a great day and a good memory.","","","great","جمله را واضح تلفظ کنید.")],
}
