import './Button.css'

/**
 * دکمه با «کف» رنگی — معادل وبِ DuoButton در اپ اندروید.
 *
 * کف تیره‌تر زیر دکمه و پایین‌رفتنش هنگام فشردن، همان چیزی است که حس
 * فیزیکی بودن را می‌سازد. بدون آن دکمه فقط یک مستطیل رنگی است.
 */

type Variant = 'green' | 'blue' | 'red' | 'ghost'

export function Button({
  children,
  variant = 'green',
  disabled,
  loading,
  onClick,
  type = 'button',
}: {
  children: React.ReactNode
  variant?: Variant
  disabled?: boolean
  loading?: boolean
  onClick?: () => void
  type?: 'button' | 'submit'
}) {
  return (
    <button
      type={type}
      className={`btn btn-${variant}`}
      disabled={disabled || loading}
      onClick={onClick}
    >
      {loading ? '…' : children}
    </button>
  )
}
