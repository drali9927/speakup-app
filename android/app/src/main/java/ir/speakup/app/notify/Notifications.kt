package ir.speakup.app.notify

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import ir.speakup.app.MainActivity
import ir.speakup.app.R

/**
 * ساخت و نمایش یادآور.
 *
 * یک کانال جدا دارد و نه کانال پیش‌فرض، چون کاربر باید بتواند فقط
 * یادآورها را خاموش کند بدون اینکه کل اعلان‌های اپ را ببندد — کسی که
 * راه خاموش کردن نداشته باشد، کل اپ را بی‌صدا می‌کند.
 */
object Notifications {

    const val CHANNEL_REMINDER = "speakup_reminder"
    private const val ID_REMINDER = 1001

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_REMINDER,
            "یادآور تمرین",
            // اهمیت پیش‌فرض و نه بالا: یادآور روزانه نباید روی صفحه بپرد
            // و صدای هشدار بدهد؛ آن لحن برای پیام فوری است نه دعوت.
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "یادآوری روزانه تمرین و مرور واژه‌ها"
        }
        context.getSystemService(NotificationManager::class.java)
            ?.createNotificationChannel(channel)
    }

    /** آیا اجازه نمایش اعلان را داریم — از اندروید ۱۳ مجوز صریح لازم است */
    fun allowed(context: Context): Boolean {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return false
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun show(context: Context, reminder: Reminder) {
        if (!allowed(context)) return
        ensureChannel(context)

        val open = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val n = NotificationCompat.Builder(context, CHANNEL_REMINDER)
            .setSmallIcon(R.drawable.ic_stat_speakup)
            // آیکن تک‌رنگ در سایه اعلان با همین رنگ ته‌رنگ می‌گیرد؛ بدون
            // آن، اعلان ما میان بقیه خاکستری و بی‌هویت دیده می‌شود.
            .setColor(0xFF58CC02.toInt())
            .setContentTitle(reminder.title)
            .setContentText(reminder.body)
            // متن بلند بریده نشود: عنوان و متن ما فارسی‌اند و در یک خط جا نمی‌شوند
            .setStyle(NotificationCompat.BigTextStyle().bigText(reminder.body))
            .setContentIntent(open)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        runCatching { NotificationManagerCompat.from(context).notify(ID_REMINDER, n) }
    }
}
