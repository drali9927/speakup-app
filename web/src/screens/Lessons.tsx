import type { Bundle, Lesson, Me, Streak } from '../api'
import { toPersianDigits } from '../format'
import './Lessons.css'

/**
 * فهرست درس‌ها — صفحه اصلی.
 *
 * وضعیت هر درس: تمام‌شده، فعلی، باز، یا قفل. قفل دو دلیل دارد و در
 * رابط از هم جدا می‌شوند: یا نوبتش نرسیده، یا اشتراک لازم دارد. کاربری
 * که نمی‌داند چرا نمی‌تواند وارد شود، به‌جای خرید، اپ را می‌بندد.
 */
export function Lessons({
  bundle,
  me,
  streak,
  doneIds,
  dueCount,
  onOpen,
  onReview,
  onProfile,
  onPaywall,
}: {
  bundle: Bundle
  me: Me
  streak: Streak | null
  doneIds: Set<string>
  dueCount: number
  onOpen: (lessonId: string) => void
  onReview: () => void
  onProfile: () => void
  onPaywall: () => void
}) {
  const lessons = [...bundle.lessons].sort((a, b) => a.number - b.number)
  const hasSub = me.subscription?.isActive ?? false
  // اولین درسی که تمام نشده — «ادامه بده» به همین می‌رود
  const currentIndex = lessons.findIndex((l) => !doneIds.has(l.id))

  function stateOf(l: Lesson, i: number) {
    if (doneIds.has(l.id)) return 'done'
    if (!l.isFree && !hasSub) return 'paid'
    if (i === currentIndex) return 'current'
    if (i < currentIndex) return 'open'
    return 'locked'
  }

  return (
    <div className="lessons">
      <header className="lessons-head">
        <div className="stat">
          <span className="stat-icon">🔥</span>
          <strong>{toPersianDigits(streak?.currentLength ?? 0)}</strong>
          <span className="stat-label">روز</span>
        </div>
        <div className="stat">
          <span className="stat-icon">📘</span>
          <strong>{me.currentLevel}</strong>
        </div>
        <button className="avatar" onClick={onProfile} aria-label="پروفایل">
          👤
        </button>
        {!hasSub && (
          <button className="upgrade" onClick={onPaywall}>
            ارتقا
          </button>
        )}
      </header>

      {currentIndex >= 0 && (
        <button className="continue" onClick={() => onOpen(lessons[currentIndex].id)}>
          <span>ادامه بده</span>
          <strong>
            درس {toPersianDigits(lessons[currentIndex].number)} — {lessons[currentIndex].themeFa}
          </strong>
        </button>
      )}

      {/*
        ایستگاه مرور — سند ۰۷، تمایز ۳.
        شکلش عمداً با ردیف درس فرق دارد تا با درس اشتباه گرفته نشود. سرِ
        راه است چون تا وقتی کاربر باید خودش سراغ لایتنر برود، عملاً
        نمی‌رود — در حساب تست رقیب هر سه شمارنده روی صفر مانده بود.
      */}
      <button
        className={`review-station ${dueCount > 0 ? 'active' : ''}`}
        onClick={onReview}
      >
        <span className="rs-icon">🔁</span>
        <span className="rs-text">
          <strong>مرور واژه‌ها</strong>
          <span>
            {dueCount > 0
              ? `${toPersianDigits(dueCount)} کارت آماده مرور`
              : 'فعلاً کارتی سررسید نشده'}
          </span>
        </span>
      </button>

      <ol className="lesson-list">
        {lessons.map((l, i) => {
          const st = stateOf(l, i)
          const locked = st === 'locked' || st === 'paid'
          return (
            <li key={l.id}>
              <button
                className={`node node-${st}`}
                disabled={st === 'locked'}
                onClick={() => (st === 'paid' ? onPaywall() : onOpen(l.id))}
                style={!locked ? { ['--node' as string]: l.colorHex } : undefined}
              >
                <span className="node-num">
                  {st === 'done' ? '✓' : st === 'paid' ? '🔒' : toPersianDigits(l.number)}
                </span>
                <span className="node-text">
                  <strong>{l.themeFa}</strong>
                  <span className="node-sub">{l.grammarTopicFa}</span>
                </span>
                <span className="node-min">{toPersianDigits(l.estimatedMinutes)}′</span>
              </button>
            </li>
          )
        })}
      </ol>
    </div>
  )
}
