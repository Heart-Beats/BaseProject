import com.buildsrc.AndroidVersions
import com.buildsrc.Deps

plugins {
	id("com.android.library")
	id("org.jetbrains.kotlin.android")
}

android {
	namespace = "com.hl.mmkvsharedpreferences"
	compileSdk = AndroidVersions.COMPILE_SDK

	this.resourcePrefix = "hl_mmkv_sharedpreferences_"

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
	api(Deps.AndroidX.startup)
	implementation(Deps.AndroidX.security_crypto)
	api(Deps.AndroidX.core_ktx)
	api(project(":SDK:JsonUtil"))
	api(Deps.Tencent.mmkv)
}