import com.buildsrc.AndroidVersions
import com.buildsrc.Deps

plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "com.hl.qrcode"
	compileSdk = AndroidVersions.COMPILE_SDK

	this.resourcePrefix = "hl_qrcode_"

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
}

dependencies {
	implementation(project(":SDK:ActivityResult"))

	implementation(Deps.AndroidX.appcompat) // zxing_lite 使用到 AppCompatActivity， 但未自动依赖，添加此依赖解决编译报错

	api(Deps.ZXing.zxing_lite)
}