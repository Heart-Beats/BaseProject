plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "com.hl.mmkvsharedpreferences"
	compileSdk = libs.versions.compileSdk.get().toInt()

	this.resourcePrefix = "hl_mmkv_sharedpreferences_"

	defaultConfig {
		minSdk = libs.versions.minSdk.get().toInt()

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
		jvmTarget = JavaVersion.VERSION_1_8.toString()
	}
}

dependencies {
	api(libs.androidx.startup)
	implementation(libs.androidx.security.crypto)
	api(libs.androidx.core.ktx)
	api(libs.mmkv)
	api(project(":json-util"))
}