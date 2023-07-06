import com.buildsrc.AndroidVersions
import com.buildsrc.Deps

plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "com.hl.previewfile"
	compileSdk = AndroidVersions.COMPILE_SDK

	this.resourcePrefix = "hl_preview_file_"

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
	}
}

dependencies {
	implementation("com.hl:uikit-toast")

	implementation(project(":base-ui"))
	implementation(project(":base-app-res"))
	implementation(project(":SDK:XLogInit"))
	implementation(project(":SDK:MimeType"))
	implementation(project(":SDK:ImageLoad"))
	implementation(project(":SDK:Permission"))
	implementation(project(":SDK:Download"))
	implementation(project(":SDK:Popup"))
	implementation(project(":SDK:Share"))
	implementation(project(":SDK:VideoPlayer"))

	implementation(Deps.Okhttp.okhttp)
	api(Deps.Tencent.tbs_sdk)
}