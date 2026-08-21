import { useCallback, useEffect, useState } from 'react'
import { Auth } from './screens/Auth'
import { Lessons } from './screens/Lessons'
import { Lesson } from './screens/Lesson'
import { Leitner } from './screens/Leitner'
import { Profile } from './screens/Profile'
import { Paywall } from './screens/Paywall'
import { League } from './screens/League'
import { Preview } from './screens/Preview'
import {
  checkIn,
  clearToken,
  getBundle,
  getMe,
  getStreak,
  getToken,
  type Bundle,
  type Me,
  type Streak,
} from './api'
import { runSync } from './sync'
import { allProgress, clearAll, dueCards } from './store'

type View = 'lessons' | 'lesson' | 'leitner' | 'profile' | 'paywall' | 'league'

export function App() {
  const [me, setMe] = useState<Me | null>(null)
  const [bundle, setBundle] = useState<Bundle | null>(null)
  const [streak, setStreak] = useState<Streak | null>(null)
  const [checking, setChecking] = useState(true)
  const [view, setView] = useState<View>('lessons')
  const [lessonId, setLessonId] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)
  /** فقط برای وادار کردن رابط به خواندن دوباره انبار محلی */
  const [tick, setTick] = useState(0)

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

  // پس از ورود: محتوا، زنجیره، و یک همگام‌سازی برای آوردن پیشرفت دستگاه‌های دیگر
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
    runSync().then((r) => {
      if (r) setStreak(r.streak)
      setTick((t) => t + 1)
    })
  }, [me])

  // همگام‌سازی هنگام بستن/پنهان شدن صفحه — کاربر تبش را می‌بندد و می‌رود
  useEffect(() => {
    const onHide = () => {
      if (document.visibilityState === 'hidden') runSync()
    }
    document.addEventListener('visibilitychange', onHide)
    return () => document.removeEventListener('visibilitychange', onHide)
  }, [])

  const afterAuth = useCallback(() => {
    getMe().then(setMe).catch(() => clearToken())
  }, [])

  async function finishLesson(r: { total: number }) {
    setLessonId(null)
    setView('lessons')
    // درسی که کاربر وسطش بیرون آمده «انجام‌شده» نیست
    if (r.total > 0) {
      // زنجیره را سرور تصمیم می‌گیرد، نه دستگاه — با ساعت گوشی نمی‌شود ساختش
      try {
        const res = await checkIn()
        setStreak(res.streak)
      } catch {
        /* آفلاین — دفعه بعد ثبت می‌شود */
      }
    }
    const s = await runSync()
    if (s) setStreak(s.streak)
    setTick((t) => t + 1)
  }

  function logout() {
    clearToken()
    clearAll()
    setMe(null)
    setBundle(null)
    setStreak(null)
    setView('lessons')
  }

  // گالری تمرین‌ها — فقط ابزار توسعه، با ?preview=1
  if (new URLSearchParams(location.search).has('preview')) return <Preview />

  if (checking) return <div className="center">…</div>
  if (!me) return <Auth onDone={afterAuth} />
  if (error) return <div className="center">{error}</div>
  if (!bundle) return <div className="center">…</div>

  if (view === 'lesson' && lessonId) {
    return <Lesson bundle={bundle} lessonId={lessonId} onExit={finishLesson} />
  }

  if (view === 'leitner') {
    return (
      <Leitner
        bundle={bundle}
        onExit={() => {
          setView('lessons')
          runSync()
          setTick((t) => t + 1)
        }}
      />
    )
  }

  if (view === 'profile') {
    return (
      <Profile
        me={me}
        streak={streak}
        onExit={() => setView('lessons')}
        onLogout={logout}
        onPaywall={() => setView('paywall')}
      />
    )
  }

  if (view === 'paywall') {
    return <Paywall me={me} onExit={() => setView('lessons')} />
  }

  if (view === 'league') {
    return <League onExit={() => setView('lessons')} />
  }

  // درس‌های تمام‌شده از انبار محلی می‌آیند — که خودش با سرور همگام است
  const progress = allProgress()
  const doneIds = new Set(
    bundle.lessons
      .filter((l) =>
        bundle.sections
          .filter((s) => s.lessonId === l.id)
          .flatMap((s) => bundle.activities.filter((a) => a.sectionId === s.id))
          .every((a) => progress[a.id]?.status === 'COMPLETED'),
      )
      .map((l) => l.id),
  )

  return (
    <Lessons
      key={tick}
      bundle={bundle}
      me={me}
      streak={streak}
      doneIds={doneIds}
      dueCount={dueCards().length}
      onOpen={(id) => {
        setLessonId(id)
        setView('lesson')
      }}
      onReview={() => setView('leitner')}
      onProfile={() => setView('profile')}
      onLeague={() => setView('league')}
      onPaywall={() => setView('paywall')}
    />
  )
}
