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
	implementation(project(":SDK:XLogInit"))
	implementation(project(":SDK:MimeType"))
	implementation(project(":SDK:ImageLoad"))
	implementation(project(":SDK:Permission"))
	implementation(project(":SDK:Download"))
	implementation(project(":SDK:Popup"))
	implementation(project(":SDK:Share"))
	implementation(project(":SDK:VideoPlayer"))

	api(libs.material)
	api(libs.tbsSdk)
}