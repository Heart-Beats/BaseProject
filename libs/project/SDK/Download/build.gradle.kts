plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "com.hl.download"
	compileSdk = libs.versions.compileSdk.get().toInt()

	this.resourcePrefix = "hl_download_"

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
	implementation(libs.androidx.activity.asProvider())
	implementation(libs.androidx.fragment.asProvider())

	implementation("com.hl:uikit-toast")
	implementation(project(":permission"))
	implementation(project(":xlog-init"))

	api(libs.okHttp.okhttp)
	api(libs.okHttp.easy.http)
}