import { useEffect, useState } from 'react'
import { getPlans, type Me, type Plan } from '../api'
import { Button } from '../components/Button'
import { toPersianDigits, toToman } from '../format'
import './Paywall.css'

/**
 * صفحه اشتراک.
 *
 * ⚠️ خرید در وب هنوز ممکن نیست و این صفحه عمداً وانمود نمی‌کند که هست.
 * پرداخت از راه خرید درون‌برنامه‌ای کافه‌بازار انجام می‌شود که فقط در اپ
 * اندروید وجود دارد؛ وب تا وقتی درگاه پرداخت ایرانی وصل نشود راهی ندارد.
 *
 * دکمه‌ای که کاری نکند بدتر از نبودنش است: کاربر فکر می‌کند خرید خراب
 * شده و دیگر برنمی‌گردد. پس این‌جا قیمت‌ها را نشان می‌دهیم و صادقانه
 * می‌گوییم خرید کجا انجام می‌شود — با این نکته که اشتراکِ خریداری‌شده در
 * اپ، همین‌جا هم فعال است (سرور نگهدارش است، نه دستگاه).
 */

/** چهار ارزش اصلی — همان‌هایی که در پی‌وال اپ است */
const PERKS = [
  { icon: '📚', text: 'همه درس‌های هر پنج سطح' },
  { icon: '🔁', text: 'مرور نامحدود واژه‌ها' },
  { icon: '📖', text: 'کتابخانه داستان' },
  { icon: '📊', text: 'گزارش پیشرفت کامل' },
]

export function Paywall({ me, onExit }: { me: Me; onExit: () => void }) {
  const [plans, setPlans] = useState<Plan[] | null>(null)
  const [showAll, setShowAll] = useState(false)
  const [error, setError] = useState(false)

  useEffect(() => {
    getPlans()
      .then((r) => setPlans(r.plans))
      .catch(() => setError(true))
  }, [])

  const active = me.subscription?.isActive ?? false

  // پی‌وال شلوغ کاربر را فراری می‌دهد (سند ۰۷، اصلاح ۶):
  // دو پلن اصلی، بقیه پشت «گزینه‌های بیشتر»
  const shown = plans && !showAll ? plans.filter((p) => p.days >= 180) : plans

  return (
    <div className="paywall">
      <header className="pw-head">
        <button className="linklike" onClick={onExit}>
          بستن
        </button>
      </header>

      {active ? (
        <div className="pw-active">
          <div className="big-emoji">✅</div>
          <h1>اشتراکت فعال است</h1>
          <p className="muted">
            تا {new Date(me.subscription!.expiresAt * 1000).toLocaleDateString('fa-IR')}
          </p>
        </div>
      ) : (
        <>
          <h1 className="pw-title">همه درس‌ها را باز کن</h1>

          <ul className="perks">
            {PERKS.map((p) => (
              <li key={p.text}>
                <span>{p.icon}</span>
                {p.text}
              </li>
            ))}
          </ul>

          {error && <p className="pw-error">قیمت‌ها بارگذاری نشد. اتصالت را بررسی کن.</p>}
          {!plans && !error && <p className="muted">…</p>}

          {/*
            فقط یک پلن برجسته می‌شود. وقتی هر دو کادر سبزِ ضخیم داشتند،
            عملاً هیچ‌کدام برجسته نبود و چشم جایی برای نشستن نداشت.
          */}
          <div className="plans">
            {shown?.map((p, i) => (
              <div className={`plan ${i === 0 ? 'featured' : ''}`} key={p.sku}>
                {p.badge && <span className="plan-badge">{p.badge}</span>}
                <div className="plan-main">
                  <strong>{p.title}</strong>
                  {p.note && <span className="plan-note">{p.note}</span>}
                </div>
                <div className="plan-price">
                  <b>{toToman(p.priceRial)}</b>
                  <span>تومان</span>
                  {/* قیمت ماهانه معادل — تنها راه مقایسه منصفانه پلن‌ها */}
                  {p.days >= 30 && (
                    <small>
                      ماهی {toToman(Math.round(p.priceRial / (p.days / 30)))} تومان
                    </small>
                  )}
                </div>
              </div>
            ))}
          </div>

          {plans && !showAll && (
            <button className="linklike more" onClick={() => setShowAll(true)}>
              گزینه‌های بیشتر
            </button>
          )}

          {/*
            صادقانه‌ترین چیزی که می‌شود این‌جا گفت. جایگزینش — دکمه خریدی
            که به جایی وصل نیست — کاربر را از محصول ناامید می‌کند.
          */}
          <div className="pw-where">
            <strong>خرید فعلاً از اپ اندروید انجام می‌شود</strong>
            <p>
              اشتراکی که در اپ می‌خری همین‌جا هم فعال می‌شود — حسابت یکی است و
              پیشرفتت بین گوشی و مرورگر مشترک است.
            </p>
          </div>
        </>
      )}

      <div className="pw-actions">
        <Button variant="ghost" onClick={onExit}>
          بازگشت
        </Button>
      </div>

      <p className="pw-terms">
        قیمت‌ها به تومان و شامل مالیات است. اشتراک {toPersianDigits(7)} روز ضمانت بازگشت دارد.
      </p>
    </div>
  )
}
