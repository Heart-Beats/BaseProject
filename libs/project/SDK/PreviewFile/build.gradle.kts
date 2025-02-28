plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "com.hl.previewfile"
	compileSdk = libs.versions.compileSdk.get().toInt()

	this.resourcePrefix = "hl_preview_file_"

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

	buildFeatures {
		viewBinding = true
	}
}

dependencies {
	implementation("com.hl:uikit-toast")

	implementation(project(":base-ui"))
	implementation(project(":base-app-res"))
	implementation(project(":xlog-init"))
	implementation(project(":mime-type"))
	implementation(project(":image-load"))
	implementation(project(":permission"))
	implementation(project(":download"))
	implementation(project(":popup"))
	implementation(project(":share"))
	implementation(project(":video-player"))

	api(libs.material)
	api(libs.tbsSdk)
}