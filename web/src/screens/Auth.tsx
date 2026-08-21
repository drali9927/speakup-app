import { useState } from 'react'
import { Button } from '../components/Button'
import { ApiError, imageUrl, requestCode, setToken, verifyCode } from '../api'
import { toPersianDigits } from '../format'
import './Auth.css'

/**
 * ورود با شماره موبایل — همان دو گام اپ اندروید (ui/auth/AuthScreen.kt).
 *
 * متن‌ها عیناً از اپ برداشته شده‌اند. دو نسخه از یک محصول که حرف متفاوت
 * می‌زنند، حس دو محصول متفاوت می‌دهند.
 */

const PROMISES = [
  { icon: '📚', title: 'بیش از ۲۰۰۰ واژه', body: 'از مقدماتی تا متوسط، با تصویر و صدا' },
  { icon: '🗣', title: 'تمرین تولیدی', body: 'بیشترِ تمرین‌ها تایپ و گفتار است، نه تست' },
  { icon: '🔁', title: 'مرور خودکار', body: 'واژه‌هایی که اشتباه می‌زنی خودشان برمی‌گردند' },
]

const phoneValid = (p: string) => /^09\d{9}$/.test(p)

export function Auth({ onDone }: { onDone: () => void }) {
  const [step, setStep] = useState<'phone' | 'code'>('phone')
  const [phone, setPhone] = useState('')
  const [code, setCode] = useState('')
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [codeLength, setCodeLength] = useState(5)
  // فقط در توسعه پر می‌شود — سرور در تولید این را نمی‌فرستد
  const [devCode, setDevCode] = useState<string | null>(null)

  async function submitPhone() {
    setBusy(true)
    setError(null)
    try {
      const r = await requestCode(phone)
      setCodeLength(r.codeLength)
      setDevCode(r.devCode ?? null)
      setStep('code')
    } catch (e) {
      setError(e instanceof ApiError ? e.message : 'مشکلی پیش آمد')
    } finally {
      setBusy(false)
    }
  }

  async function submitCode() {
    setBusy(true)
    setError(null)
    try {
      const r = await verifyCode(phone, code)
      setToken(r.token)
      onDone()
    } catch (e) {
      setError(e instanceof ApiError ? e.message : 'مشکلی پیش آمد')
    } finally {
      setBusy(false)
    }
  }

  const canSubmit = step === 'phone' ? phoneValid(phone) : code.length === codeLength

  return (
    <div className="auth">
      {/*
        تصویر خوش‌آمد از محتوای درس اول می‌آید. بدون آن، اولین چیزی که
        کاربر از محصول می‌بیند دو سوم صفحه فضای خالی است.
      */}
      <div className="auth-hero">
        <img src={imageUrl('a1_l01_w20_meet.png')} alt="" />
      </div>

      {step === 'phone' ? (
        <>
          <h1>شماره موبایلت را وارد کن</h1>
          <p className="auth-sub">
            یک کد {toPersianDigits(String(codeLength))} رقمی برایت می‌فرستیم تا وارد شوی و
            پیشرفتت ذخیره بماند.
          </p>
          <input
            className="auth-input ltr"
            type="tel"
            inputMode="numeric"
            value={phone}
            disabled={busy}
            placeholder="09123456789"
            onChange={(e) => setPhone(e.target.value.replace(/\D/g, '').slice(0, 11))}
            onKeyDown={(e) => e.key === 'Enter' && canSubmit && submitPhone()}
          />
          {phone.length > 0 && !phoneValid(phone) && (
            <p className="auth-hint">شماره باید با ۰۹ شروع شود و ۱۱ رقم باشد</p>
          )}

          <div className="promises">
            {PROMISES.map((p) => (
              <div className="promise" key={p.title}>
                <span className="promise-icon">{p.icon}</span>
                <div>
                  <strong>{p.title}</strong>
                  <span>{p.body}</span>
                </div>
              </div>
            ))}
          </div>
        </>
      ) : (
        <>
          <h1>کد ورود را وارد کن</h1>
          <p className="auth-sub">
            کد به شماره <span className="ltr">{toPersianDigits(phone)}</span> فرستاده شد.{' '}
            <button className="linklike" onClick={() => setStep('phone')}>
              تغییر شماره
            </button>
          </p>
          <input
            className="auth-input auth-code ltr"
            type="text"
            inputMode="numeric"
            autoFocus
            value={code}
            disabled={busy}
            onChange={(e) => setCode(e.target.value.replace(/\D/g, '').slice(0, codeLength))}
            onKeyDown={(e) => e.key === 'Enter' && canSubmit && submitCode()}
          />
          {devCode && (
            <p className="auth-devcode">
              حالت توسعه — کد: <span className="ltr">{devCode}</span>
            </p>
          )}
        </>
      )}

      {error && <p className="auth-error">{error}</p>}

      <div className="auth-actions">
        <Button
          loading={busy}
          disabled={!canSubmit}
          onClick={step === 'phone' ? submitPhone : submitCode}
        >
          {step === 'phone' ? 'دریافت کد ورود' : 'تأیید و ورود'}
        </Button>
      </div>

      <p className="auth-terms">
        با ورود، <a href="/terms">قوانین</a> را می‌پذیری.
      </p>
    </div>
  )
}
