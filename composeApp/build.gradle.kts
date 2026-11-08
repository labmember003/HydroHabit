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

        androidMain.dependencies {
            implementation("androidx.activity:activity-compose:1.9.0")
            implementation("io.insert-koin:koin-android:3.5.4-RC1")
        }
    }
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
        versionCode = 2
        versionName = "1.0"
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

