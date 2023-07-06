import com.buildsrc.AndroidVersions
import com.buildsrc.Deps

plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
	id(libs.plugins.kotlin.parcelize.get().pluginId)
}

android {
	namespace = "com.hl.web"
	compileSdk = AndroidVersions.COMPILE_SDK

	this.resourcePrefix = "hl_web_"

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
		buildConfig = true
	}
}

dependencies {
	api(Deps.JsBridge.jsbridge)

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

	api(Deps.UtilCodeX.utilcodex)
	api(Deps.Jetpack.Navigation.navigation_runtime_ktx)
}