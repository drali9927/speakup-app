import { useEffect, useState } from 'react'
import { Auth } from './screens/Auth'
import { Lessons } from './screens/Lessons'
import { Lesson } from './screens/Lesson'
import { Preview } from './screens/Preview'
import {
  clearToken,
  getBundle,
  getMe,
  getStreak,
  getToken,
  type Bundle,
  type Me,
  type Streak,
} from './api'

/**
 * پوسته اپ.
 *
 * درس‌های تمام‌شده فعلاً محلی نگه داشته می‌شوند. همگام‌سازی با سرور
 * (‏/v1/sync) در گام بعد وصل می‌شود؛ تا آن‌موقع پیشرفت روی همین مرورگر
 * می‌ماند و بین دستگاه‌ها جابه‌جا نمی‌شود.
 */

const DONE_KEY = 'speakup.done'

const loadDone = (): Set<string> => {
  try {
    return new Set(JSON.parse(localStorage.getItem(DONE_KEY) ?? '[]'))
  } catch {
    return new Set()
  }
}

export function App() {
  const [me, setMe] = useState<Me | null>(null)
  const [bundle, setBundle] = useState<Bundle | null>(null)
  const [streak, setStreak] = useState<Streak | null>(null)
  const [checking, setChecking] = useState(true)
  const [lessonId, setLessonId] = useState<string | null>(null)
  const [done, setDone] = useState<Set<string>>(loadDone)
  const [error, setError] = useState<string | null>(null)

  // توکن ذخیره‌شده ممکن است منقضی شده باشد؛ تنها راه فهمیدنش پرسیدن از سرور است
  useEffect(() => {
    if (!getToken()) {
      setChecking(false)
      return
    }
    getMe()
      .then(setMe)
      .catch(() => clearToken())
      .finally(() => setChecking(false))
  }, [])

  // محتوای سطح کاربر پس از ورود بارگذاری می‌شود
  useEffect(() => {
    if (!me) return
    getBundle(me.currentLevel)
      .then(setBundle)
      .catch(() => setError('محتوا بارگذاری نشد. اتصالت را بررسی کن.'))
    getStreak()
      .then(setStreak)
      .catch(() => {
        /* زنجیره حیاتی نیست؛ نبودش نباید صفحه را خراب کند */
      })
  }, [me])

  function afterAuth() {
    getMe().then(setMe).catch(() => clearToken())
  }

  function finishLesson(id: string, r: { total: number }) {
    // درسی که کاربر وسطش بیرون آمده تمام‌شده حساب نمی‌شود
    if (r.total > 0) {
      const next = new Set(done).add(id)
      setDone(next)
      localStorage.setItem(DONE_KEY, JSON.stringify([...next]))
    }
    setLessonId(null)
  }

  // گالری تمرین‌ها — فقط ابزار توسعه، با ?preview=1
  if (new URLSearchParams(location.search).has('preview')) return <Preview />

  if (checking) return <div className="center">…</div>
  if (!me) return <Auth onDone={afterAuth} />
  if (error) return <div className="center">{error}</div>
  if (!bundle) return <div className="center">…</div>

  if (lessonId) {
    return (
      <Lesson
        bundle={bundle}
        lessonId={lessonId}
        onExit={(r) => finishLesson(lessonId, r)}
      />
    )
  }

  return (
    <Lessons
      bundle={bundle}
      me={me}
      streak={streak}
      doneIds={done}
      onOpen={setLessonId}
      onPaywall={() => alert('صفحه خرید در گام بعد ساخته می‌شود')}
    />
  )
}
