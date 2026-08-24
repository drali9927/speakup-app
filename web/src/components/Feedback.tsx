import { Button } from './Button'
import './Feedback.css'

/**
 * نوار بازخورد پس از پاسخ — معادل وبِ CheckCard اپ.
 *
 * از پایین می‌آید و تا وقتی کاربر «ادامه» نزند می‌ماند. دلیلش این است
 * که پاسخ درست باید خوانده شود؛ اگر خودکار رد شود، کاربری که اشتباه
 * کرده هیچ‌وقت نمی‌فهمد درستش چه بود.
 */
export function Feedback({
  correct,
  expected,
  hint,
  onNext,
  last,
}: {
  correct: boolean
  expected?: string
  hint?: string
  onNext: () => void
  last?: boolean
}) {
  return (
    <div className={`feedback ${correct ? 'ok' : 'bad'}`}>
      <div className="feedback-body">
        <strong>{correct ? '✓ درست بود' : '✗ درست نبود'}</strong>
        {!correct && expected && (
          <p>
            پاسخ درست: <span className="ltr strong">{expected}</span>
          </p>
        )}
        {!correct && hint && <p className="feedback-hint">{hint}</p>}
      </div>
      <Button variant={correct ? 'green' : 'red'} onClick={onNext}>
        {last ? 'پایان' : 'ادامه'}
      </Button>
    </div>
  )
}
