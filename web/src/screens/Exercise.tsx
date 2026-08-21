import { useEffect, useMemo, useState } from 'react'
import { imageUrl, type Item } from '../api'
import { check } from '../answer'
import { Button } from '../components/Button'
import { Feedback } from '../components/Feedback'
import { speak } from '../speech'
import './Exercise.css'

/**
 * یک تمرین — هر ۱۳ نوع فعالیت از همین‌جا رد می‌شوند.
 *
 * انواع بر اساس شکل پاسخ چهار دسته‌اند و بیش از آن لازم نیست:
 *   انتخابی  → MULTIPLE_CHOICE, MATCHING  (گزینه‌ها در options)
 *   تایپی    → FILL_BLANK, FREE_TEXT, TRANSLATE_TO_EN, LISTENING
 *   چیدنی    → REORDER  (واژه‌ها را مرتب کن)
 *   نمایشی   → FLASHCARD, DIALOGUE, TEACHING  (پاسخ ندارند)
 *
 * SPEAKING فعلاً تایپی است — دلیلش پایین‌تر نوشته شده.
 */

export type ItemResult = { itemId: string; correct: boolean; targetWord?: string }

const TYPED = ['FILL_BLANK', 'FREE_TEXT', 'TRANSLATE_TO_EN', 'LISTENING']
const CHOICE = ['MULTIPLE_CHOICE', 'MATCHING']

export function Exercise({
  item,
  type,
  index,
  total,
  onDone,
}: {
  item: Item
  type: string
  index: number
  total: number
  onDone: (r: ItemResult) => void
}) {
  const [answer, setAnswer] = useState('')
  const [verdict, setVerdict] = useState<ReturnType<typeof check> | null>(null)
  /** اندیس کاشی‌های انتخاب‌شده در REORDER — نه متنشان (دلیلش پایین‌تر) */
  const [picked, setPicked] = useState<number[]>([])

  // با عوض شدن آیتم همه‌چیز پاک می‌شود، وگرنه پاسخ قبلی می‌ماند
  useEffect(() => {
    setAnswer('')
    setVerdict(null)
    setPicked([])
  }, [item.id])

  const options = useMemo(
    () =>
      (item.options ?? '')
        .split('|')
        .map((x) => x.trim())
        .filter(Boolean),
    [item.options],
  )

  // واژه‌های REORDER در prompt با « / » جدا شده‌اند
  const tiles = useMemo(
    () =>
      type === 'REORDER'
        ? item.prompt
            .split('/')
            .map((x) => x.trim())
            .filter(Boolean)
        : [],
    [item.prompt, type],
  )

  // شنیداری: کاربر باید بشنود، پس متن سوال را نشان نمی‌دهیم
  const isListening = type === 'LISTENING'
  useEffect(() => {
    if (isListening && item.ttsText) speak(item.ttsText)
  }, [isListening, item.id, item.ttsText])

  function submit(given?: string) {
    const raw = given ?? (type === 'REORDER' ? picked.map((i) => tiles[i]).join(' ') : answer)
    setVerdict(check(item.correctAnswer, item.alternatives, raw))
  }

  function next() {
    onDone({
      itemId: item.id,
      correct: verdict?.correct ?? true,
      targetWord: item.targetWord ?? undefined,
    })
  }

  const graded =
    TYPED.includes(type) || CHOICE.includes(type) || type === 'REORDER' || type === 'SPEAKING'

  return (
    <div className="ex">
      <div className="ex-progress">
        <div className="ex-bar" style={{ width: `${((index + 1) / total) * 100}%` }} />
      </div>

      {/* --- نمایشی: فلش‌کارت */}
      {type === 'FLASHCARD' && (
        <div className="card-view">
          {item.imageFile && <img src={imageUrl(item.imageFile)} alt="" className="ex-image" />}
          <h2 className="ltr">{item.prompt}</h2>
          {item.hintFa && <p className="ex-ipa ltr">{item.hintFa}</p>}
          <p className="ex-fa">{item.promptFa}</p>
          {item.exampleEn && (
            <div className="ex-example">
              <p className="ltr">{item.exampleEn}</p>
              <p className="muted">{item.exampleFa}</p>
            </div>
          )}
          <button className="ex-speak" onClick={() => speak(item.ttsText || item.prompt)}>
            🔊 شنیدن
          </button>
        </div>
      )}

      {/* --- نمایشی: گفت‌وگو */}
      {type === 'DIALOGUE' && (
        <div className={`bubble ${item.voice === 'F' ? 'her' : 'him'}`}>
          {item.hintFa && <span className="bubble-who">{item.hintFa}</span>}
          <p className="ltr">{item.prompt}</p>
          <p className="muted">{item.promptFa}</p>
          <button className="ex-speak" onClick={() => speak(item.ttsText || item.prompt)}>
            🔊
          </button>
        </div>
      )}

      {/* --- نمایشی: آموزش گرامر */}
      {type === 'TEACHING' && (
        <div className="teach">
          <h2>{item.prompt}</h2>
          {item.promptFa.split('\n').map((line, i) => (
            <p key={i}>{line}</p>
          ))}
          {item.correctAnswer && (
            <div className="teach-examples">
              {item.correctAnswer.split('\n').map((line, i) => (
                <p key={i} className="ltr">
                  {line}
                </p>
              ))}
            </div>
          )}
        </div>
      )}

      {/*
        داستان — ورودی قابل فهم (سند ۰۷، تمایز ۴).
        متن انگلیسی و ترجمه‌اش کنار هم؛ پاسخی ندارد و فقط خوانده می‌شود.
      */}
      {type === 'STORY' && (
        <div className="story">
          <p className="story-en ltr">{item.prompt}</p>
          <p className="story-fa">{item.promptFa}</p>
          <button className="ex-speak" onClick={() => speak(item.ttsText || item.prompt)}>
            🔊 گوش کن
          </button>
        </div>
      )}

      {/* --- انتخابی */}
      {CHOICE.includes(type) && (
        <>
          <p className="ex-q ltr">{item.prompt}</p>
          <div className="choices">
            {options.map((o) => (
              <button
                key={o}
                className={`choice ${
                  verdict && o === answer ? (verdict.correct ? 'ok' : 'bad') : ''
                }`}
                disabled={!!verdict}
                onClick={() => {
                  setAnswer(o)
                  submit(o)
                }}
              >
                {o}
              </button>
            ))}
          </div>
        </>
      )}

      {/* --- تایپی */}
      {TYPED.includes(type) && (
        <>
          {isListening ? (
            <button className="ex-listen" onClick={() => speak(item.ttsText)}>
              🔊 دوباره پخش کن
            </button>
          ) : (
            <p className={`ex-q ${type === 'TRANSLATE_TO_EN' ? '' : 'ltr'}`}>{item.prompt}</p>
          )}
          <input
            className="ex-input ltr"
            value={answer}
            disabled={!!verdict}
            autoFocus
            placeholder="پاسخ را بنویس"
            onChange={(e) => setAnswer(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && answer.trim() && !verdict && submit()}
          />
        </>
      )}

      {/* --- چیدنی */}
      {type === 'REORDER' && (
        <>
          <p className="ex-q">جمله را مرتب کن</p>
          <div className="reorder-slot ltr">
            {picked.length === 0 ? (
              <span className="muted">…</span>
            ) : (
              picked.map((i) => tiles[i]).join(' ')
            )}
          </div>
          {/*
            کاشی‌ها با اندیس ردیابی می‌شوند نه با متن. اگر با متن بود،
            جمله‌ای که واژه تکراری دارد خراب می‌شد: با زدن یکی، هر دو
            کاشیِ هم‌متن ناپدید می‌شدند.
          */}
          <div className="tiles">
            {tiles.map((w, i) => (
              <button
                key={i}
                className="tile"
                hidden={picked.includes(i)}
                disabled={!!verdict}
                onClick={() => setPicked([...picked, i])}
              >
                {w}
              </button>
            ))}
          </div>
          {picked.length > 0 && !verdict && (
            <button className="linklike" onClick={() => setPicked(picked.slice(0, -1))}>
              آخری را پس بگیر
            </button>
          )}
        </>
      )}

      {/*
        گفتار: در وب فعلاً تایپی است.
        Web Speech API در همه مرورگرها نیست و کیفیتش با نسخه اندروید
        یکی نیست. تمرینی که نیمی از کاربران نتوانند انجامش دهند بدتر از
        تمرینی است که شکل دیگری دارد — پس کاربر عبارت را می‌شنود و
        همان را می‌نویسد.
      */}
      {type === 'SPEAKING' && (
        <>
          <p className="ex-q">این عبارت را بگو و بنویس</p>
          <p className="ex-target ltr">{item.prompt}</p>
          <button className="ex-speak" onClick={() => speak(item.ttsText || item.prompt)}>
            🔊 شنیدن تلفظ
          </button>
          <input
            className="ex-input ltr"
            value={answer}
            disabled={!!verdict}
            placeholder="همان عبارت را بنویس"
            onChange={(e) => setAnswer(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && answer.trim() && !verdict && submit()}
          />
        </>
      )}

      {/* --- کنش پایین صفحه */}
      {verdict ? (
        <Feedback
          correct={verdict.correct}
          expected={verdict.expected}
          hint={item.hintFa}
          onNext={next}
          last={index + 1 === total}
        />
      ) : (
        <div className="ex-actions">
          {!graded ? (
            <Button onClick={next}>{index + 1 === total ? 'پایان' : 'ادامه'}</Button>
          ) : CHOICE.includes(type) ? null : (
            <Button
              disabled={type === 'REORDER' ? picked.length === 0 : !answer.trim()}
              onClick={() => submit()}
            >
              بررسی کن
            </Button>
          )}
        </div>
      )}
    </div>
  )
}
