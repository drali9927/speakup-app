import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { App } from './App'
import './theme.css'
import './app.css'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)

// فقط در ساخت نهایی — در حالت توسعه سرویس‌ورکر باعث می‌شود تغییرات کد
// با کش کهنه اشتباه گرفته شوند و دیباگ را گمراه‌کننده کند
if ('serviceWorker' in navigator && import.meta.env.PROD) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/sw.js')
  })
}
