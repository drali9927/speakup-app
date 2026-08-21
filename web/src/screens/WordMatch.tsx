import { useMemo, useState } from 'react'
import type { Item } from '../api'
import { Button } from '../components/Button'
import { speak } from '../speech'
import './WordMatch.css'

/**
 * بازی جفت‌یابی — واژه را به معنی‌اش وصل کن.
 *
 * ترجمه‌ی ui/match/WordMatchScreen.kt، با همان دو تصمیمی که آن‌جا مستند
 * شده و اثر یادگیری دارند:
 *
 * **حریف‌ها از همان دور می‌آیند.** پنج واژه در هر دور یعنی هر انتخاب
 * غلط اطلاعات می‌دهد؛ با بیست کاشی، کاربر فقط شلوغی می‌بیند.
 *
 * **کاشی درست ناپدید می‌شود، نه اینکه فقط سبز شود.** کم شدن تدریجی
 * صفحه همان چیزی است که حس پیشرفت را در چند ثانیه می‌سازد.
 *
 * برخلاف بقیه تمرین‌ها این یکی سطحِ «فعالیت» است نه «آیتم» — یعنی همه
 * آیتم‌های فعالیت با هم روی یک صفحه‌اند.
 */

const ROUND = 5

type Tile = { key: string; text: string; side: 'en' | 'fa'; ltr: boolean }

/**
 * چینش شبه‌تصادفی بدون Math.random.
 *
 * ترتیب باید بین رندرها ثابت بماند، وگرنه هر بار که React دوباره رسم
 * می‌کند کاشی‌ها جابه‌جا می‌شوند و کاربر وسط بازی گم می‌شود.
 */
function shuffle<T>(arr: T[], seed: number): T[] {
  const out = [...arr]
  let s = seed
  for (let i = out.length - 1; i > 0; i--) {
    s = (s * 1103515245 + 12345) & 0x7fffffff
    const j = s % (i + 1)
    ;[out[i], out[j]] = [out[j], out[i]]
  }
  return out
}

export function WordMatch({
  items,
  index,
  total,
  onDone,
}: {
  items: Item[]
  index: number
  total: number
  onDone: (wrongWords: string[]) => void
}) {
  const [roundAt, setRoundAt] = useState(0)
  const [matched, setMatched] = useState<Set<string>>(new Set())
  const [picked, setPicked] = useState<Tile | null>(null)
  /** شناسه کاشی = side+key، چون دو کاشیِ یک جفت key مشترک دارند */
  const [flash, setFlash] = useState<{ ids: string[]; ok: boolean } | null>(null)
  const [wrong, setWrong] = useState<Set<string>>(new Set())

  const rounds = useMemo(() => {
    const out: Item[][] = []
    for (let i = 0; i < items.length; i += ROUND) out.push(items.slice(i, i + ROUND))
    return out
  }, [items])

  const round = rounds[roundAt] ?? []

  const tiles = useMemo(() => {
    const en: Tile[] = round.map((it) => ({
      key: it.id,
      text: it.prompt,
      side: 'en',
      ltr: true,
    }))
    const fa: Tile[] = round.map((it) => ({
      key: it.id,
      text: it.promptFa,
      side: 'fa',
      ltr: false,
    }))
    // هر ستون جدا به‌هم می‌ریزد تا جفت‌ها روبه‌روی هم نیفتند
    return { en: shuffle(en, roundAt + 7), fa: shuffle(fa, roundAt + 13) }
  }, [round, roundAt])

  const roundDone = round.length > 0 && round.every((it) => matched.has(it.id))

  function tap(t: Tile) {
    if (matched.has(t.key)) return

    if (!picked) {
      setPicked(t)
      if (t.side === 'en') speak(t.text)
      return
    }

    // زدن دوباره همان کاشی یعنی انصراف
    if (picked.key === t.key && picked.side === t.side) {
      setPicked(null)
      return
    }

    // دو کاشی از یک ستون جفت نمی‌شوند
    if (picked.side === t.side) {
      setPicked(t)
      return
    }

    const ids = [`${picked.side}-${picked.key}`, `${t.side}-${t.key}`]
    if (picked.key === t.key) {
      setMatched(new Set(matched).add(t.key))
      setFlash({ ids, ok: true })
    } else {
      // واژه‌ای که اشتباه جفت شده برای لایتنر ثبت می‌شود
      const it = round.find((x) => x.id === picked.key)
      if (it?.targetWord) setWrong(new Set(wrong).add(it.targetWord))
      // فقط همان دو کاشی که کاربر زد قرمز می‌شوند — نه کاشیِ هم‌کلیدشان،
      // که کاربر اصلاً لمسش نکرده و قرمز شدنش گیج‌کننده است
      setFlash({ ids, ok: false })
    }
    setPicked(null)
    setTimeout(() => setFlash(null), 350)
  }

  function nextRound() {
    if (roundAt + 1 < rounds.length) {
      setRoundAt(roundAt + 1)
      setMatched(new Set())
      setPicked(null)
    } else {
      onDone([...wrong])
    }
  }

  return (
    <div className="ex">
      <div className="ex-progress">
        <div className="ex-bar" style={{ width: `${((index + 1) / total) * 100}%` }} />
      </div>

      <p className="ex-q">واژه را به معنی‌اش وصل کن</p>

      {/* دو ستون مجزا — انگلیسی یک طرف، معنی طرف دیگر */}
      <div className="match-grid">
        {[tiles.en, tiles.fa].map((col, ci) => (
          <div className="match-col" key={ci}>
            {col.map((t) => {
              const id = `${t.side}-${t.key}`
              const gone = matched.has(t.key)
              const f = flash?.ids.includes(id) ? (flash.ok ? 'ok' : 'bad') : ''
              const sel = picked?.key === t.key && picked.side === t.side ? 'sel' : ''
              return (
                <button
                  key={id}
                  className={`match-tile ${f} ${sel} ${t.ltr ? 'ltr' : ''}`}
                  // ناپدید می‌شود ولی جایش می‌ماند تا بقیه کاشی‌ها نپرند
                  style={{ visibility: gone ? 'hidden' : 'visible' }}
                  onClick={() => tap(t)}
                >
                  {t.text}
                </button>
              )
            })}
          </div>
        ))}
      </div>

      <div className="ex-actions">
        {roundDone && (
          <Button onClick={nextRound}>
            {roundAt + 1 < rounds.length ? 'دور بعد' : 'ادامه'}
          </Button>
        )}
      </div>
    </div>
  )
}
