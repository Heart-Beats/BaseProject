import com.buildsrc.AndroidVersions
import com.buildsrc.Deps


plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.hl.pay"

    compileSdk = AndroidVersions.COMPILE_SDK
    defaultConfig {
        minSdk = AndroidVersions.MIN_SDK
        targetSdk = AndroidVersions.TARGET_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(fileTree("libs", { include("*.jar", "*.aar") }))

    implementation(Deps.AndroidX.core_ktx)
    implementation(Deps.AndroidX.appcompat)

    implementation(Deps.Ali.pay)
    implementation(Deps.Tencent.wechat_sdk_android_without_mta)
    implementation(Deps.Json.gson)
}