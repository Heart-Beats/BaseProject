import  com.buildsrc.AndroidVersions
import  com.buildsrc.Deps

plugins {
	id("com.android.library")
	id("org.jetbrains.kotlin.android")
}

android {
	namespace = "com.hl.ui"
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

	buildFeatures {
		viewBinding = true
		compose = true

		// 默认不生成 BuildConfig 类
		buildConfig = false
	}

	composeOptions {
		// 定义 Kotlin 编译器扩展版本与 Kotlin 版本相关联，需与项目的 Kotlin 版本匹配，详见：https://developer.android.com/jetpack/androidx/releases/compose-kotlin?hl=zh-cn
		kotlinCompilerExtensionVersion = "1.4.5"
	}
}

dependencies {
	api(Deps.AndroidX.appcompat)
	api(Deps.AndroidX.activity)
	api(Deps.AndroidX.fragment)
	api(Deps.AndroidX.activity_ktx)
	api(Deps.AndroidX.fragment_ktx)

	api(Deps.AndroidX.view_binding)

	api(Deps.Status_Bar.immersionbar)
	api(Deps.Status_Bar.immersionbar_ktx)

	api(platform(libs.jetpack.compose.bom))
	api(libs.jetpack.compose.ui)
}