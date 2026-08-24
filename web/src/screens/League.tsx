import { useEffect, useState } from 'react'
import { getLeague, type LeagueStandings } from '../api'
import { Button } from '../components/Button'
import { toPersianDigits } from '../format'
import './League.css'

/**
 * جدول لیگ هفتگی.
 *
 * امتیاز را سرور می‌دهد، نه دستگاه (`/v1/sync` آن را از شمار فعالیت‌های
 * تازه حساب می‌کند). اگر به عددی که کلاینت می‌فرستد اعتماد می‌شد، هرکسی
 * با دستکاری داده محلی صدر جدول را می‌خرید.
 */
export function League({ onExit }: { onExit: () => void }) {
  const [data, setData] = useState<LeagueStandings | null>(null)
  const [error, setError] = useState(false)

  useEffect(() => {
    getLeague().then(setData).catch(() => setError(true))
  }, [])

  if (error) return <div className="center">جدول بارگذاری نشد.</div>
  if (!data) return <div className="center">…</div>

  const daysLeft = Math.max(0, Math.ceil((data.endsAt * 1000 - Date.now()) / 86_400_000))

  return (
    <div className="league">
      <header className="lg-head">
        <button className="linklike" onClick={onExit}>
          بستن
        </button>
        <h1>لیگ {data.tierName}</h1>
      </header>

      <p className="lg-sub">
        {daysLeft > 0
          ? `${toPersianDigits(daysLeft)} روز تا پایان هفته`
          : 'هفته امروز تمام می‌شود'}
        {data.nextTierName && ` · ${toPersianDigits(data.promoteCount)} نفر اول به لیگ ${data.nextTierName} می‌روند`}
      </p>

      <ol className="lg-rows">
        {data.rows.map((r) => {
          // مرز صعود و سقوط باید دیده شود، وگرنه رتبه عدد بی‌معنایی است
          const zone =
            r.rank <= data.promoteCount
              ? 'up'
              : r.rank > data.rows.length - data.relegateCount
                ? 'down'
                : ''
          return (
            <li key={r.userId} className={`lg-row ${zone} ${r.isMe ? 'me' : ''}`}>
              <span className="lg-rank">{toPersianDigits(r.rank)}</span>
              <span className="lg-name">
                {r.name}
                {r.isMe && <b> (تو)</b>}
              </span>
              <span className="lg-xp">{toPersianDigits(r.xp)}</span>
            </li>
          )
        })}
      </ol>

      <div className="lg-legend">
        <span className="dot up" /> صعود
        <span className="dot down" /> سقوط
      </div>

      <div className="lg-actions">
        <Button variant="ghost" onClick={onExit}>
          بازگشت
        </Button>
      </div>
    </div>
  )
}
