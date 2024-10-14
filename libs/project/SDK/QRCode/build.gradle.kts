plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "com.hl.qrcode"
	compileSdk = libs.versions.compileSdk.get().toInt()

	this.resourcePrefix = "hl_qrcode_"

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
	implementation(project(":SDK:ActivityResult"))

	// zxing_lite 使用到 AppCompatActivity， 但未自动依赖，添加此依赖解决编译报错
	implementation(libs.androidx.appcompat)

	api(libs.zxing.lite)
}