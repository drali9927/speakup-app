# نسخه وب اسپیک‌آپ — راهنمای استقرار

این پوشه رابط کاربری وب است. **سرور جداگانه لازم ندارد** — روی همان
سروری می‌نشیند که بک‌اند رویش است، فقط روی یک زیردامنه دیگر.

---

## ۱. ساخت

```bash
cd web
npm install
npm run build
```

خروجی در `web/dist/` ساخته می‌شود: چند فایل ثابت، حدود ۶۰۰ کیلوبایت
(حدود ۱۸۰ کیلوبایت فشرده روی شبکه). همین پوشه است که سرو می‌شود.

## ۲. نکته‌ای که اگر رعایت نشود اپ کار نمی‌کند

کد **هیچ‌جا نشانی سرور را داخل خودش ندارد** و همه‌چیز را نسبی صدا می‌زند
(`/v1/...` و `/images/...`). این عمدی است: نسخه‌ای که ساخته می‌شود روی هر
دامنه‌ای کار می‌کند و امکان ندارد کسی نسخه‌ای منتشر کند که هنوز به لپ‌تاپ
برنامه‌نویس وصل است.

در عوض، **وب‌سرور باید این دو مسیر را به بک‌اند پاس بدهد.** بدون این،
صفحه بالا می‌آید ولی ورود و محتوا کار نمی‌کند.

## ۳. نمونه پیکربندی nginx

```nginx
server {
    listen 443 ssl http2;
    server_name app.spkupacademy.com;

    ssl_certificate     /etc/letsencrypt/live/app.spkupacademy.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/app.spkupacademy.com/privkey.pem;

    root /var/www/speakup-web;   # همان محتوای dist/
    index index.html;

    # اپ تک‌صفحه‌ای است: هر مسیری که فایل نباشد به index.html می‌رود
    location / {
        try_files $uri $uri/ /index.html;
    }

    # فایل‌های ساخته‌شده نام یکتا (hash) دارند، پس کش طولانی بی‌خطر است
    location /assets/ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # ⚠️ این دو بلوک اجباری‌اند
    location /v1/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host              $host;
        proxy_set_header X-Real-IP         $remote_addr;
        proxy_set_header X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /images/ {
        proxy_pass http://127.0.0.1:8080;
        expires 30d;
    }
}

# http → https
server {
    listen 80;
    server_name app.spkupacademy.com;
    return 301 https://$host$request_uri;
}
```

پورت `8080` را با پورتی که بک‌اند واقعاً رویش گوش می‌دهد عوض کنید.

## ۴. استقرار

```bash
# روی سرور
rsync -a --delete web/dist/ /var/www/speakup-web/
nginx -t && systemctl reload nginx
```

`--delete` مهم است: فایل‌های نسخه قبلی نام متفاوتی دارند و اگر پاک نشوند
روی هم انباشته می‌شوند.

## ۵. تست پس از استقرار

```bash
curl -I https://app.spkupacademy.com/            # باید 200 بدهد
curl -s https://app.spkupacademy.com/v1/plans    # باید JSON پلن‌ها بیاید
```

اگر دستور دوم HTML برگرداند، یعنی بلوک `location /v1/` کار نمی‌کند و اپ
هم کار نخواهد کرد.

بعد در مرورگر: ورود با شماره واقعی → یک درس → بستن تب → ورود دوباره.
پیشرفت باید سرِ جایش باشد.

---

## آنچه در نسخه وب عمداً نیست

| مورد | چرا |
|---|---|
| **دکمه خرید** | پرداخت از راه کافه‌بازار است که فقط در اپ اندروید وجود دارد. صفحه اشتراک قیمت‌ها را نشان می‌دهد و می‌گوید خرید کجا انجام می‌شود. اشتراکِ خریداری‌شده در اپ، در وب هم فعال است — سرور نگهدارش است نه دستگاه. برای فروش از خودِ وب، درگاه پرداخت ایرانی لازم است. |
| **تمرین گفتاری با میکروفن** | Web Speech API در همه مرورگرها نیست. تمرین SPEAKING در وب تایپی است: کاربر تلفظ را می‌شنود و همان عبارت را می‌نویسد. |
| **یادآور روزانه** | نوتیفیکیشن وب به‌مراتب ضعیف‌تر از اپ است؛ زنجیره در وب فقط نمایش داده می‌شود. |

## نکته‌ای برای توسعه

`?preview=1` گالری همه انواع تمرین را نشان می‌دهد — برای دیدن یک نوع
تمرین لازم نیست نصف درس بازی شود.
