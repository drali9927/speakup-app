# قواعد نگهداری برای نسخه منتشرشده.
#
# این فایل **وجود نداشت** در حالی که build.gradle.kts به آن ارجاع
# می‌داد. Gradle نبودنش را خطا نمی‌گیرد و بی‌صدا رد می‌شود، پس نسخه
# release فقط با قواعد پیش‌فرض ساخته می‌شد.
#
# در عمل کار می‌کرد، چون کتابخانه‌ها قواعد خودشان را همراه دارند
# (consumer rules). اما تکیه بر آن شکننده است: کافی است یک کتابخانه در
# نسخه بعدی قاعده‌اش را عوض کند تا نسخه منتشرشده — و فقط نسخه
# منتشرشده — بشکند. آن‌وقت روی گوشی کاربر می‌شکند، نه روی میز ما.
#
# صحت این فایل با بررسی خودِ DEX نسخه release سنجیده شد: نام فیلدهای
# JSON و جدول‌های Room در خروجی هستند.

# --- kotlinx.serialization ---
# سریالایزرها با بازتاب پیدا می‌شوند؛ اگر نام‌ها عوض شوند، خواندن بسته
# محتوا و پاسخ‌های سرور در زمان اجرا می‌شکند.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class ir.speakup.app.** {
    *** Companion;
}
-keepclasseswithmembers class ir.speakup.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class ir.speakup.app.**$$serializer { *; }

# --- Retrofit ---
# امضای عمومی متدها برای ساخت پویا لازم است.
-keepattributes Signature, Exceptions
-keep,allowobfuscation interface ir.speakup.app.data.remote.Api

# --- Room ---
# کدِ تولیدشده خودش امن است؛ این فقط برای موجودیت‌هاست.
-keep class ir.speakup.app.data.local.** { *; }

# --- گزارش خطا ---
# بدون این، ردِ خطا در گزارش‌های کافه‌بازار بی‌معنا می‌شود.
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile
