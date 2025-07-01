plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "com.hl.camera"
	compileSdk = libs.versions.compileSdk.get().toInt()

	this.resourcePrefix = "hl_camera_"

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
	buildFeatures {
		viewBinding = true
	}
}

dependencies {
	implementation("com.hl:uikit-toast")
	implementation(project(":base-ui"))
	implementation(project(":permission"))

	api(libs.camera.likeWX)
}