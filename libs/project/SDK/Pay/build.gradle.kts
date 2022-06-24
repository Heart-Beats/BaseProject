import com.buildsrc.AndroidVersions
import com.buildsrc.Deps


plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = AndroidVersions.COMPILE_SDK

    defaultConfig {
        minSdk = 21
        targetSdk = 31

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
    implementation(project(mapOf("path" to ":libs-aar")))

    implementation(Deps.AndroidX.core_ktx)
    implementation(Deps.AndroidX.appcompat)
    implementation(Deps.Tencent.wechat_sdk_android_without_mta)
    implementation(Deps.Json.gson)
}