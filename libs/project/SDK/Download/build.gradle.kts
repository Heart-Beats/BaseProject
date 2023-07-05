import com.buildsrc.AndroidVersions
import com.buildsrc.Deps

plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "com.hl.download"
	compileSdk = AndroidVersions.COMPILE_SDK

	this.resourcePrefix = "hl_download_"

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
	implementation(Deps.AndroidX.activity)
	implementation(Deps.AndroidX.fragment)

	implementation("com.hl:uikit-toast")
	implementation(project(":SDK:Permission"))
	implementation(project(":SDK:XLogInit"))

	implementation(Deps.Okhttp.okhttp)
	api(Deps.Okhttp.easy_http)
}