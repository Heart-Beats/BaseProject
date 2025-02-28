plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
	id(libs.plugins.kotlin.parcelize.get().pluginId)
}

android {
	namespace = "com.hl.web"
	compileSdk = libs.versions.compileSdk.get().toInt()

	this.resourcePrefix = "hl_web_"

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
		buildConfig = true
	}
}

dependencies {
	api(libs.jsBridge)

	api(project(":base-ui"))
	api(project(":activity-result"))
	api(project(":xlog-init"))

	api(project(":umeng"))
	api(project(":permission"))
	api(project(":picture-selector"))
	api(project(":date-util"))
	api(project(":bitmap-util"))
	api(project(":download"))
	api(project(":qrcode"))
	api(project(":popup"))
	api(project(":sms-util"))
	api(project(":preview-file"))

	api(libs.utilCodeX)
	api(libs.jetpack.navigation.runtime.ktx)
}