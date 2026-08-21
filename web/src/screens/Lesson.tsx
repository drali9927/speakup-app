import { useMemo, useState } from 'react'
import type { Bundle } from '../api'
import { Exercise, type ItemResult } from './Exercise'
import { WordMatch } from './WordMatch'
import { Button } from '../components/Button'
import { toPersianDigits } from '../format'
import { addWrongWord, logAnswer, putProgress } from '../store'
import { PRODUCTIVE, SCORED } from '../activity'
import './Lesson.css'

/**
 * اجرای یک درس — آیتم‌ها را پشت سر هم نشان می‌دهد و در پایان خلاصه.
 *
 * ترتیب از خود محتوا می‌آید (بخش‌ها و فعالیت‌ها هرکدام sortOrder دارند)
 * و عمداً دست‌کاری نمی‌شود: چیدمان درس تصمیم آموزشی است، نه فنی.
 */
export function Lesson({
  bundle,
  lessonId,
  onExit,
}: {
  bundle: Bundle
  lessonId: string
  onExit: (r: { correct: number; total: number; wrongWords: string[] }) => void
}) {
  // آیتم‌های درس به ترتیب بخش → فعالیت → آیتم
  const steps = useMemo(() => {
    const sections = bundle.sections
      .filter((s) => s.lessonId === lessonId)
      .sort((a, b) => a.sortOrder - b.sortOrder)

    const out: { items: typeof bundle.items; type: string }[] = []
    for (const s of sections) {
      const acts = bundle.activities
        .filter((a) => a.sectionId === s.id)
        .sort((a, b) => a.sortOrder - b.sortOrder)
      for (const a of acts) {
        const items = bundle.items
          .filter((i) => i.activityId === a.id)
          .sort((x, y) => x.sortOrder - y.sortOrder)
        if (items.length === 0) continue
        // جفت‌یابی سطحِ فعالیت است: همه آیتم‌ها با هم روی یک صفحه‌اند
        if (a.activityType === 'WORD_MATCH') {
          out.push({ items, type: a.activityType })
        } else {
          for (const item of items) out.push({ items: [item], type: a.activityType })
        }
      }
    }
    return out
  }, [bundle, lessonId])

  const [at, setAt] = useState(0)
  const [results, setResults] = useState<ItemResult[]>([])
  const [done, setDone] = useState(false)

  function handleDone(r: ItemResult) {
    const step = steps[at]
    const now = Date.now()

    // هر واژه‌ای که غلط جواب داده شده خودکار وارد لایتنر می‌شود —
    // سند ۰۷ تمایز ۲. کاربر لازم نیست کاری بکند و اصلاً خبردار هم نمی‌شود.
    if (!r.correct && r.targetWord) addWrongWord(r.targetWord)

    // گزارش پاسخ برای متریک «نرخ تولید صحیح» (سند ۰۷ بخش ۷.۷).
    // فقط تمرین‌های نمره‌دار؛ کارت واژه و گفت‌وگو سوالی نمی‌پرسند.
    if (SCORED.has(step.type)) {
      logAnswer({
        itemId: r.itemId,
        activityId: step.items[0].activityId,
        isProductive: PRODUCTIVE.has(step.type),
        userAnswer: r.correct ? 'ok' : 'wrong',
        isCorrect: r.correct,
        targetWord: r.targetWord ?? null,
        answeredAt: now,
      })
    }

    // پیشرفت هر فعالیت وقتی ثبت می‌شود که آخرین آیتمش رد شده باشد
    const lastOfActivity =
      at + 1 >= steps.length ||
      steps[at + 1].items[0].activityId !== step.items[0].activityId
    if (lastOfActivity) {
      putProgress({
        activityId: step.items[0].activityId,
        status: 'COMPLETED',
        completedAt: now,
        updatedAt: now,
      })
    }

    const next = [...results, r]
    setResults(next)
    if (at + 1 >= steps.length) {
      setDone(true)
    } else {
      setAt(at + 1)
      window.scrollTo(0, 0)
    }
  }

  if (steps.length === 0) {
    return (
      <div className="lesson-empty">
        <p>این درس محتوایی ندارد.</p>
        <Button onClick={() => onExit({ correct: 0, total: 0, wrongWords: [] })}>بازگشت</Button>
      </div>
    )
  }

  if (done) {
    // فقط آیتم‌های نمره‌دار شمرده می‌شوند؛ فلش‌کارت و گفت‌وگو پاسخ ندارند
    const graded = results.filter((r) => r.targetWord !== undefined || !r.correct)
    const correct = results.filter((r) => r.correct).length
    const wrongWords = [
      ...new Set(results.filter((r) => !r.correct && r.targetWord).map((r) => r.targetWord!)),
    ]
    return (
      <div className="summary">
        <div className="summary-emoji">{correct === results.length ? '🎉' : '👏'}</div>
        <h1>درس تمام شد</h1>
        <p className="summary-score">
          {toPersianDigits(correct)} از {toPersianDigits(results.length)} درست
        </p>
        {wrongWords.length > 0 && (
          <div className="summary-wrong">
            <p>این واژه‌ها برای مرور برمی‌گردند:</p>
            <div className="wrong-chips">
              {wrongWords.map((w) => (
                <span key={w} className="chip ltr">
                  {w}
                </span>
              ))}
            </div>
          </div>
        )}
        <Button onClick={() => onExit({ correct, total: graded.length, wrongWords })}>
          بازگشت به درس‌ها
        </Button>
      </div>
    )
  }

  const step = steps[at]
  return (
    <div className="lesson">
      <button className="lesson-close" onClick={() => onExit({ correct: 0, total: 0, wrongWords: [] })}>
        ✕
      </button>
      {step.type === 'WORD_MATCH' ? (
        <WordMatch
          key={step.items[0].id}
          items={step.items}
          index={at}
          total={steps.length}
          onDone={(wrongWords) =>
            handleDone({
              itemId: step.items[0].activityId,
              correct: wrongWords.length === 0,
              targetWord: wrongWords[0],
            })
          }
        />
      ) : (
        <Exercise
          key={step.items[0].id}
          item={step.items[0]}
          type={step.type}
          index={at}
          total={steps.length}
          onDone={handleDone}
        />
      )}
    </div>
  )
}
