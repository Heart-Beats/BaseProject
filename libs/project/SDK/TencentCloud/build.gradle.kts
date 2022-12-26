import com.buildsrc.AndroidVersions
import com.buildsrc.Deps

plugins {
	id("com.android.library")
	id("org.jetbrains.kotlin.android")
}

android {
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

repositories {
	mavenCentral()
	google()
}

dependencies {
	implementation(fileTree("libs") {
		include("*.jar", "*.aar")
	})

	implementation(Deps.AndroidX.core_ktx)
	implementation(Deps.AndroidX.appcompat)
	api(Deps.Tencent.cos_android_lite)
}