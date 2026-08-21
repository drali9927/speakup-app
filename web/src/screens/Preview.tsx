import { useEffect, useState } from 'react'
import { getBundle, type Bundle, type Item } from '../api'
import { Exercise } from './Exercise'
import { WordMatch } from './WordMatch'

/**
 * گالری تمرین‌ها — فقط برای توسعه (‏?preview=1).
 *
 * یک نمونه از هر نوع فعالیت را پشت سر هم نشان می‌دهد تا بشود همه را در
 * یک نگاه دید. بدون این، برای دیدن یک نوع تمرین باید نصف درس را بازی
 * کرد و اشکال ظاهری دیر پیدا می‌شود.
 *
 * در ساخت تولید هم بارگذاری نمی‌شود مگر با همان پارامتر آدرس.
 */
export function Preview() {
  const [bundle, setBundle] = useState<Bundle | null>(null)

  useEffect(() => {
    getBundle('A1').then(setBundle).catch(() => {})
  }, [])

  if (!bundle) return <div className="center">…</div>

  // یک نمونه از هر نوع
  const samples: { type: string; item: Item }[] = []
  const byAct = new Map(bundle.activities.map((a) => [a.id, a]))
  for (const it of bundle.items) {
    const a = byAct.get(it.activityId)
    if (!a) continue
    if (samples.some((s) => s.type === a.activityType)) continue
    samples.push({ type: a.activityType, item: it })
  }

  return (
    <div>
      {samples.map((s) => (
        <section key={s.type} style={{ borderBottom: '4px solid var(--border)' }}>
          <p
            style={{
              margin: 0,
              padding: '8px 24px',
              background: 'var(--gold)',
              color: '#4b3b00',
              fontWeight: 700,
            }}
          >
            {s.type}
          </p>
          {s.type === 'WORD_MATCH' ? (
            <WordMatch
              items={bundle.items.filter((i) => i.activityId === s.item.activityId)}
              index={0}
              total={10}
              onDone={() => {}}
            />
          ) : (
            <Exercise item={s.item} type={s.type} index={0} total={10} onDone={() => {}} />
          )}
        </section>
      ))}
    </div>
  )
}
