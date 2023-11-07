plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "com.hl.videoplayer"
	compileSdk = libs.versions.compileSdk.get().toInt()

	this.resourcePrefix = "hl_video_player_"

	defaultConfig {
		minSdk = libs.versions.minSdk.get().toInt()

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		consumerProguardFiles("consumer-rules.pro")

		ndk {
			/**
			 * armeabi-v7a 和 arm64-v8a 架构的设备会向下兼容：
			 * 1. 只适配 armeabi 的 APP 可以跑在 armeabi,x86,x86_64,armeabi-v7a,arm64-v8 设备上
			 * 2. 只适配 armeabi-v7a 可以运行在 armeabi-v7a 和 arm64-v8a 设备上
			 * 3. 只适配 arm64-v8a 的 App 只可以运行在 arm64-v8a 设备上
			 *
			 */
			//设置支持的SO库架构
			abiFilters += setOf("armeabi-v7a", "arm64-v8a")
		}
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
	implementation(libs.androidx.activity.ktx)
	implementation(libs.androidx.fragment.ktx)

	implementation(project(":SDK:ImageLoad"))

	api(libs.gsyVideoPlayer)
}