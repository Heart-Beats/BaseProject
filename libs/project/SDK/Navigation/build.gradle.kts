plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "com.hl.navigation"
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

dependencies {
	implementation(libs.androidx.activity.ktx)
	implementation(libs.material)


	api(libs.smooth.navigation)
	api(libs.jetpack.navigation.fragment.ktx) {
		//smooth-navigation 重写了 navigation-fragment 中的相关类，会冲突需要排除
		exclude(group = "androidx.navigation", module = "navigation-fragment")
	}
	api(libs.jetpack.navigation.ui.asProvider())
	api(libs.jetpack.navigation.ui.ktx)
}