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
	api(project(":SDK:ActivityResult"))
	api(project(":SDK:XLogInit"))

	api(project(":SDK:Umeng"))
	api(project(":SDK:Permission"))
	api(project(":SDK:PictureSelector"))
	api(project(":SDK:DateUtil"))
	api(project(":SDK:BitmapUtil"))
	api(project(":SDK:Download"))
	api(project(":SDK:QRCode"))
	api(project(":SDK:Popup"))
	api(project(":SDK:SmsUtil"))
	api(project(":SDK:PreviewFile"))

	api(libs.utilCodeX)
	api(libs.jetpack.navigation.runtime.ktx)
}