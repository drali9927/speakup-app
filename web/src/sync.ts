import { sync as syncApi, type Streak } from './api'
import {
  commitSync,
  dirtyLeitner,
  dirtyProgress,
  getSince,
  mergeFromServer,
  pendingAnswers,
} from './store'

/**
 * همگام‌سازی با سرور.
 *
 * همان قراردادی که اپ اندروید دارد (data/remote/SyncManager.kt)، پس یک
 * کاربر می‌تواند درسی را روی گوشی نیمه‌کاره بگذارد و روی لپ‌تاپ ادامه
 * دهد. قواعد حل تعارض سمت سرور اعمال می‌شوند؛ این‌جا فقط می‌فرستیم و
 * نتیجه را می‌نشانیم.
 *
 * شکست خوردنش عمداً بی‌صداست: کاربری که وسط درس اینترنتش قطع شده نباید
 * خطای فنی ببیند. داده محلی سرِ جایش می‌ماند و دفعه بعد می‌رود.
 */

let running = false

export async function runSync(): Promise<{ streak: Streak } | null> {
  // دو همگام‌سازی هم‌زمان یعنی پاسخ‌ها دو بار فرستاده می‌شوند
  if (running) return null
  running = true
  try {
    const answers = pendingAnswers()
    const res = await syncApi({
      since: getSince(),
      progress: dirtyProgress(),
      leitner: dirtyLeitner(),
      answers,
    })
    mergeFromServer(res.progress ?? [], res.leitner ?? [])
    commitSync(res.now, answers.length)
    return { streak: res.streak }
  } catch {
    // آفلاین یا خطای سرور — دفعه بعد دوباره تلاش می‌شود
    return null
  } finally {
    running = false
  }
}
