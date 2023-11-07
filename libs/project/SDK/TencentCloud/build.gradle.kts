plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "com.hl.tencentcloud"

	compileSdk = libs.versions.compileSdk.get().toInt()
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
		jvmTarget = "1.8"
	}
}

repositories {
	// cos_android_lite 从阿里仓库同步有问题需要单独配置
	mavenCentral()
	google()
}

dependencies {
	implementation(fileTree("libs") {
		include("*.jar", "*.aar")
	})

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	api(libs.tencentCos)
}