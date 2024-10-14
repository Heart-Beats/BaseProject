plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "com.hl.pictureselector"
	compileSdk = libs.versions.compileSdk.get().toInt()

	this.resourcePrefix = "hl_picture_selector_"

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
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	kotlinOptions {
		jvmTarget = JavaVersion.VERSION_17.toString()
	}
}

dependencies {
	api(project(":SDK:ImageLoad"))

	api(libs.pickSelector.picture.selector.asProvider())
	api(libs.pickSelector.picture.selector.compress)
	api(libs.pickSelector.picture.selector.ucrop)
}