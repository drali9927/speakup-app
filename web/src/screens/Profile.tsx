import { useEffect, useState } from 'react'
import { getStats, type Me, type Streak } from '../api'
import { Button } from '../components/Button'
import { allLeitner, allProgress, isLearned } from '../store'
import { toPersianDigits } from '../format'
import './Profile.css'

/**
 * پروفایل و پیشرفت.
 *
 * سند ۰۷ تمایز ۵: پیشرفت با چیزهای **قابل باور** سنجیده می‌شود، نه با
 * امتیاز. «می‌توانی بنویسی» یعنی واژه‌ای که در تمرین تولیدی درست جواب
 * داده — نه واژه‌ای که فقط دیده. همین صداقت متمایزمان می‌کند.
 */
export function Profile({
  me,
  streak,
  onExit,
  onLogout,
  onPaywall,
}: {
  me: Me
  streak: Streak | null
  onExit: () => void
  onLogout: () => void
  onPaywall: () => void
}) {
  const [accuracy, setAccuracy] = useState<number | null | undefined>(undefined)

  useEffect(() => {
    getStats(0)
      .then((s) => setAccuracy(s.productiveAccuracy))
      .catch(() => setAccuracy(null))
  }, [])

  const leitner = Object.values(allLeitner())
  const learned = leitner.filter((c) => isLearned(c.box)).length
  const reviewing = leitner.length - learned
  const doneActivities = Object.values(allProgress()).filter(
    (p) => p.status === 'COMPLETED',
  ).length

  const sub = me.subscription

  return (
    <div className="profile">
      <header className="profile-head">
        <button className="linklike" onClick={onExit}>
          بستن
        </button>
        <h1>پیشرفت تو</h1>
      </header>

      <div className="cards">
        <div className="pcard">
          <strong>{toPersianDigits(learned)}</strong>
          <span>واژه آموخته‌شده</span>
        </div>
        <div className="pcard">
          <strong>{toPersianDigits(reviewing)}</strong>
          <span>در حال مرور</span>
        </div>
        <div className="pcard">
          <strong>{toPersianDigits(doneActivities)}</strong>
          <span>تمرین تمام‌شده</span>
        </div>
        <div className="pcard">
          <strong>{toPersianDigits(streak?.longestLength ?? 0)}</strong>
          <span>بلندترین زنجیره</span>
        </div>
      </div>

      {/*
        متریک داوری محصول — سند ۰۷ بخش ۷.۷.
        فقط تمرین‌های تولیدی شمرده می‌شوند؛ این عدد قابل بازی کردن نیست.
      */}
      <div className="accuracy">
        <span>دقت در تمرین‌های تولیدی</span>
        {accuracy === undefined ? (
          <strong>…</strong>
        ) : accuracy === null ? (
          <strong className="muted">هنوز داده‌ای نیست</strong>
        ) : (
          <strong>{toPersianDigits(Math.round(accuracy * 100))}٪</strong>
        )}
      </div>

      <div className="profile-rows">
        <div className="prow">
          <span>شماره</span>
          <b className="ltr">{toPersianDigits(me.phone)}</b>
        </div>
        <div className="prow">
          <span>سطح</span>
          <b>{me.currentLevel}</b>
        </div>
        <div className="prow">
          <span>کد معرف</span>
          <b className="ltr">{me.referralCode}</b>
        </div>
        <div className="prow">
          <span>اشتراک</span>
          <b>
            {sub?.isActive
              ? `فعال تا ${new Date(sub.expiresAt * 1000).toLocaleDateString('fa-IR')}`
              : 'ندارد'}
          </b>
        </div>
      </div>

      {!sub?.isActive && (
        <div className="profile-cta">
          <Button onClick={onPaywall}>دیدن اشتراک‌ها</Button>
        </div>
      )}

      <button className="logout" onClick={onLogout}>
        خروج از حساب
      </button>
    </div>
  )
}
