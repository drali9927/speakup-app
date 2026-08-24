import { useMemo, useState } from 'react'
import type { Bundle } from '../api'
import { Button } from '../components/Button'
import { Feedback } from '../components/Feedback'
import { check } from '../answer'
import { dueCards, reviewCard, type LeitnerRow } from '../store'
import { speak } from '../speech'
import { toPersianDigits } from '../format'
import './Leitner.css'

/**
 * مرور لایتنر — کارت‌های سررسیدشده.
 *
 * مرور **تولیدی** است نه شناختی: کاربر باید واژه را بنویسد، نه اینکه از
 * میان چند گزینه بشناسد. سند ۰۷ تمایز ۱ می‌گوید شناختن با توانستن یکی
 * نیست، و همین‌جا مهم‌ترین جایش است — کارتی که «به‌نظر آشنا می‌آید» هنوز
 * یاد گرفته نشده.
 */
export function Leitner({ bundle, onExit }: { bundle: Bundle; onExit: () => void }) {
  const [cards] = useState<LeitnerRow[]>(() => dueCards())
  const [at, setAt] = useState(0)
  const [answer, setAnswer] = useState('')
  const [verdict, setVerdict] = useState<ReturnType<typeof check> | null>(null)
  const [done, setDone] = useState({ correct: 0, total: 0 })

  // معنی فارسی واژه از واژه‌نامه همان سطح می‌آید
  const meaning = useMemo(() => {
    const m: Record<string, string> = {}
    for (const d of bundle.dictionary) m[d.word.toLowerCase()] = d.translationFa
    return m
  }, [bundle])

  if (cards.length === 0) {
    return (
      <div className="leitner-empty">
        <div className="big-emoji">✨</div>
        <h1>چیزی برای مرور نیست</h1>
        <p className="muted">
          واژه‌هایی که در تمرین‌ها اشتباه جواب بدهی خودشان این‌جا برمی‌گردند.
        </p>
        <Button onClick={onExit}>بازگشت</Button>
      </div>
    )
  }

  if (at >= cards.length) {
    return (
      <div className="leitner-empty">
        <div className="big-emoji">🎯</div>
        <h1>مرور تمام شد</h1>
        <p className="leitner-score">
          {toPersianDigits(done.correct)} از {toPersianDigits(done.total)} درست
        </p>
        <Button onClick={onExit}>بازگشت</Button>
      </div>
    )
  }

  const card = cards[at]
  const fa = meaning[card.word.toLowerCase()]

  function submit() {
    const v = check(card.word, null, answer)
    setVerdict(v)
    reviewCard(card.entryId, v.correct)
    setDone({ correct: done.correct + (v.correct ? 1 : 0), total: done.total + 1 })
  }

  function next() {
    setAt(at + 1)
    setAnswer('')
    setVerdict(null)
  }

  return (
    <div className="ex leitner">
      <div className="ex-progress">
        <div className="ex-bar" style={{ width: `${((at + 1) / cards.length) * 100}%` }} />
      </div>

      <div className="leitner-head">
        <span className="box-chip">جعبه {toPersianDigits(card.box)}</span>
        <button className="linklike" onClick={onExit}>
          بستن
        </button>
      </div>

      <p className="ex-q">این واژه را به انگلیسی بنویس</p>
      <p className="leitner-word">{fa ?? '—'}</p>

      <input
        className="ex-input ltr"
        value={answer}
        disabled={!!verdict}
        autoFocus
        placeholder="واژه انگلیسی"
        onChange={(e) => setAnswer(e.target.value)}
        onKeyDown={(e) => e.key === 'Enter' && answer.trim() && !verdict && submit()}
      />

      {verdict ? (
        <>
          <button className="ex-speak" onClick={() => speak(card.word)}>
            🔊 {card.word}
          </button>
          <Feedback
            correct={verdict.correct}
            expected={card.word}
            onNext={next}
            last={at + 1 === cards.length}
          />
        </>
      ) : (
        <div className="ex-actions">
          <Button disabled={!answer.trim()} onClick={submit}>
            بررسی کن
          </Button>
        </div>
      )}
    </div>
  )
}
