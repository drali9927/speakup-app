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
  onOpen,
  onPaywall,
}: {
  bundle: Bundle
  me: Me
  streak: Streak | null
  doneIds: Set<string>
  onOpen: (lessonId: string) => void
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
          <strong>{toPersianDigits(streak?.current ?? 0)}</strong>
          <span className="stat-label">روز</span>
        </div>
        <div className="stat">
          <span className="stat-icon">📘</span>
          <strong>{me.currentLevel}</strong>
          <span className="stat-label">سطح</span>
        </div>
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
