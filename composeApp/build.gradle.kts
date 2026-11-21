plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.android.application")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            // Koin
            implementation("io.insert-koin:koin-core:3.5.4")
            implementation("io.insert-koin:koin-compose:1.1.2")
            // Compottie (Lottie) — multiplatform
            implementation("io.github.alexzhirkevich:compottie:2.0.0-rc01")
            implementation("io.github.alexzhirkevich:compottie-resources:2.0.0-rc01")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
        }

        // Compose UI tests run on the iOS simulator (./gradlew :composeApp:iosSimulatorArm64Test).
        iosTest.dependencies {
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        androidMain.dependencies {
            implementation("androidx.activity:activity-compose:1.9.0")
            implementation("io.insert-koin:koin-android:3.5.4-RC1")
            // 24.8.0 is the newest Ads SDK whose Kotlin metadata (2.1) Kotlin 2.0.21 can read
            implementation("com.google.android.gms:play-services-ads:24.8.0")
            implementation("com.google.android.ump:user-messaging-platform:4.0.0")
        }
    }
}

// UI tests boot this simulator (a 6.9" iPhone, the size App Store screenshots use).
tasks.withType<org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest>().configureEach {
    device.set("iPhone 18 Pro Max")
}

configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
        force("org.jetbrains.kotlinx:kotlinx-datetime-jvm:0.6.1")
    }
}

android {
    namespace = "com.falcon.hydrohabit"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.falcon.hydrohabit"
        minSdk = 26
        targetSdk = 36
        versionCode = 3
        versionName = "1.1.0"
        // AdMob (account pub-7683902216769416). Debug builds use Google's test banner so we never click our own ads.
        manifestPlaceholders["admobAppId"] = "ca-app-pub-7683902216769416~5364157563"
        resValue("string", "admob_banner_id", "ca-app-pub-7683902216769416/4366982699")
    }

    // Release signing is configured from environment variables so the keystore and
    // passwords never live in the repo. CI (.github/workflows/release.yml) sets these;
    // locally, leave them unset and sign from Android Studio as usual.
    val releaseKeystorePath: String? = System.getenv("ANDROID_KEYSTORE_PATH")
    signingConfigs {
        if (releaseKeystorePath != null) {
            create("release") {
                storeFile = file(releaseKeystorePath)
                storePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("ANDROID_KEY_ALIAS")
                keyPassword = System.getenv("ANDROID_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            resValue("string", "admob_banner_id", "ca-app-pub-3940256099942544/9214589741")
        }
        release {
            if (releaseKeystorePath != null) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

