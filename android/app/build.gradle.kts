plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

/**
 * نشانی سرور نسخه منتشرشده.
 *
 * از gradle.properties خوانده می‌شود تا در کد ثابت نباشد. محافظ زیر یک
 * اشتباه مشخص را می‌گیرد: انتشار نسخه‌ای که هنوز به لپ‌تاپ برنامه‌نویس
 * وصل است. چنین نسخه‌ای روی گوشی کاربر بی‌صدا کار نمی‌کند و علتش هم
 * از بیرون پیدا نیست.
 *
 * پیش‌فرض قبلی `api.speakup.ir` بود — دامنه‌ای که هیچ‌وقت واقعاً بالا
 * نیامد (سند ۰۸). Backend واقعی روی `spkupacademy.com` است.
 */
val releaseApiBaseUrl: String =
    (project.findProperty("speakup.apiBaseUrl") as String?
        ?: "https://spkup-app-back.spkupacademy.com/").also {
        if (!it.startsWith("https://") || it.contains("localhost") || it.contains("127.0.0.1")) {
            throw GradleException("speakup.apiBaseUrl باید یک نشانی https واقعی باشد — مقدار فعلی: $it")
        }
    }

/**
 * کلید امضای انتشار — از gradle.properties خارج از مخزن.
 *
 * اگر تنظیم نشده باشد، release بدون امضا ساخته می‌شود تا ساخت محلی و CI
 * نشکند. نسخه بدون امضا را کافه‌بازار نمی‌پذیرد؛ همین هم عمدی است، چون
 * تنها راه امضا داشتن کلید واقعی است.
 */
val releaseKeystore: File? =
    (project.findProperty("speakup.storeFile") as String?)?.let(::File)?.takeIf { it.exists() }

android {
    namespace = "ir.speakup.app"
    compileSdk = 35

    signingConfigs {
        if (releaseKeystore != null) {
            create("release") {
                storeFile = releaseKeystore
                storePassword = project.property("speakup.storePassword") as String
                keyAlias = project.property("speakup.keyAlias") as String
                keyPassword = project.property("speakup.keyPassword") as String
            }
        }
    }

    defaultConfig {
        applicationId = "ir.speakup.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // اسکیمای Room برای بررسی نسخه‌بندی مهاجرت‌ها ذخیره می‌شود
        ksp { arg("room.schemaLocation", "$projectDir/schemas") }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            // 127.0.0.1 و نه IP شبکه محلی: با `adb reverse tcp:8099 tcp:8099`
            // گوشی به لپ‌تاپ می‌رسد و تمدید DHCP دیگر build را نمی‌شکند.
            buildConfigField("String", "API_BASE_URL", "\"http://127.0.0.1:8099/\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "API_BASE_URL", "\"$releaseApiBaseUrl\"")
            if (releaseKeystore != null) signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true; buildConfig = true }
    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.navigation.compose)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization)
    implementation(libs.okhttp.logging)
    implementation(libs.datastore.preferences)
    implementation(libs.work.runtime)
    implementation(libs.coil.compose)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.room.testing)
}
