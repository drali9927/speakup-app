import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

/**
 * نشانی سرور در زمان توسعه از پروکسی می‌آید تا مرورگر مسئله CORS نداشته
 * باشد و کد اپ در هر دو حالت یک نشانی نسبی صدا بزند.
 *
 * در ساخت نهایی، همان nginx که وب‌اپ را سرو می‌کند `/v1` و `/images` را
 * به بک‌اند پاس می‌دهد — پس کد هیچ‌جا نشانی مطلق ندارد.
 */
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/v1': 'http://localhost:8090',
      '/images': 'http://localhost:8090',
    },
  },
  // `vite preview` نسخه ساخته‌شده را سرو می‌کند — همان چیزی که روی سرور
  // می‌رود. پروکسی این‌جا نقش nginx را بازی می‌کند تا بشود پیش از انتشار،
  // خروجی واقعی را تست کرد و نه فقط حالت توسعه را.
  preview: {
    port: 4173,
    proxy: {
      '/v1': 'http://localhost:8090',
      '/images': 'http://localhost:8090',
    },
  },
})
