plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "com.hl.permission"
	compileSdk = libs.versions.compileSdk.get().toInt()

	this.resourcePrefix = "hl_permission_"

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
		jvmTarget = "1.8"
	}
}

dependencies {
	api(libs.androidx.activity.asProvider())
    api(libs.androidx.fragment.asProvider())
	implementation(libs.permissionX)
}