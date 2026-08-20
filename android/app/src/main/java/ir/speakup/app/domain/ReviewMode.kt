package ir.speakup.app.domain

import ir.speakup.app.data.local.glossKey

/**
 * شکل پرسیدن در جعبه لایتنر.
 *
 * پیش از این، مرور یک شکل بیشتر نداشت: واژه نشان داده می‌شد، کاربر
 * «نمایش معنی» را می‌زد و بعد **خودش** می‌گفت بلد بودم یا نبودم.
 *
 * دو ایراد داشت:
 *
 * **۱. خودارزیابی، نمره را باد می‌کند.** دیدنِ پاسخ و گفتن «آره بلد
 * بودم» با به‌یاد آوردنش فرق دارد. آدم وقتی جواب را می‌بیند مطمئن
 * می‌شود که بلد بوده، حتی وقتی نبوده. نتیجه: کارت‌ها زودتر از آنچه
 * باید به جعبه‌های بالاتر می‌روند و واژه فراموش می‌شود.
 *
 * **۲. یک شکلِ ثابت، خسته‌کننده است** — و مرور روزانه باید هر روز
 * انجام شود.
 *
 * حالا شکل پرسش با **جعبه** کارت سخت‌تر می‌شود. این تصادفی نیست: کارتی
 * که تازه وارد شده باید بازشناسی شود، و کارتی که پنج بار درست جواب
 * داده شده باید بدون کمک به یاد آورده شود.
 */
enum class ReviewMode {
    /** واژه انگلیسی → انتخاب معنی فارسی. آسان‌ترین: بازشناسی */
    MEANING,

    /** معنی فارسی → انتخاب واژه انگلیسی. سخت‌تر: یادآوری در جهت تولید */
    WORD,

    /** شنیدن واژه → تایپ کردنش. سخت‌ترین: تولید کامل */
    TYPING,
    ;

    companion object {
        /**
         * شکل پرسش برای یک کارت.
         *
         * @param box جعبه لایتنر، از ۱ تا [ir.speakup.app.data.model.LeitnerSchedule.MAX_BOX]
         * @param canType آیا این کارت قابل تایپ کردن است — واژه‌های
         *   چندکلمه‌ای یا دارای نویسه ویژه، تایپشان آزاردهنده است و
         *   کاربر را به‌خاطر یک فاصله اشتباه جریمه می‌کند.
         */
        fun forBox(box: Int, canType: Boolean): ReviewMode = when {
            box <= 2 -> MEANING
            box <= 4 -> WORD
            canType -> TYPING
            // جعبه پنج ولی واژه تایپ‌نشدنی: سخت‌ترین حالتِ ممکن را بده
            else -> WORD
        }

        /** واژه‌ای که تایپ کردنش منصفانه است */
        fun typable(word: String): Boolean =
            word.length in 2..14 && word.all { it.isLetter() || it == '\'' }
    }
}

/**
 * گزینه‌های یک پرسش مرور.
 *
 * حواس‌پرت‌کن‌ها از کارت‌های دیگرِ همان جلسه می‌آیند و نه از کل
 * دیکشنری: معنی‌های بی‌ربط، پرسش را بی‌اهمیت می‌کنند.
 *
 * یکتایی روی **معنی پایه** است، پس «سلام» و «سلام (خودمانی)» هرگز با
 * هم گزینه نمی‌شوند — همان اشکالی که در آزمون واژه پیدا شد.
 */
object ReviewOptions {

    fun build(answer: String, pool: List<String>, at: Int, count: Int = 3): List<String> {
        val clean = answer.trim()
        if (clean.isEmpty()) return emptyList()

        val seen = mutableSetOf(clean.glossKey())
        val picked = ArrayList<String>(count - 1)
        for (step in 1..pool.size) {
            val cand = pool[(at + step) % pool.size].trim()
            if (cand.isEmpty() || !seen.add(cand.glossKey())) continue
            picked += cand
            if (picked.size == count - 1) break
        }
        if (picked.isEmpty()) return emptyList()

        // جای پاسخ درست می‌چرخد، وگرنه ترتیب ثابت خودش پاسخ را لو می‌دهد
        return ArrayList(picked).apply { add(slotFor(clean, at, size + 1), clean) }
    }

    /**
     * جای پاسخ درست.
     *
     * همان درمانی که در آزمون واژه لازم شد: هش ساده، پاسخ را در
     * بیشتر پرسش‌ها به یک جایگاه می‌برد و کاربر بدون دانستن چیزی
     * نمره می‌گیرد. این آمیزه، بیت‌ها را پخش می‌کند.
     */
    internal fun slotFor(answer: String, index: Int, slots: Int): Int {
        var h = answer.hashCode() xor (index * 0x9E3779B9L.toInt())
        h = h xor (h ushr 16); h *= 0x85EBCA6BL.toInt()
        h = h xor (h ushr 13); h *= 0xC2B2AE35L.toInt()
        h = h xor (h ushr 16)
        return (h ushr 1) % slots
    }
}
