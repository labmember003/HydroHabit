plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
}

kotlin {
    // Android target
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    // iOS targets (builds but no code yet — just scaffolding)
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            // Only kotlinx-serialization for now (pure Kotlin, no platform deps)
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
            api("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
            // moko-permissions for cross-platform permission handling
            api("dev.icerock.moko:permissions:0.18.0")
            api("dev.icerock.moko:permissions-compose:0.18.0")
            // Multiplatform DataStore (okio-based)
            api("androidx.datastore:datastore-core-okio:1.1.0")
            implementation("com.squareup.okio:okio:3.9.0")
        }

        androidMain.dependencies {
            // Android-specific shared code will go here later
        }

        iosMain.dependencies {
            // iOS-specific shared code will go here later
        }
    }
}

android {
    namespace = "com.falcon.hydrohabit.shared"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

