plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "com.hl.ui"
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
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	kotlinOptions {
		jvmTarget = JavaVersion.VERSION_17.toString()
	}

	buildFeatures {
		viewBinding = true
		compose = true

		// 默认不生成 BuildConfig 类
		buildConfig = false
	}

	composeOptions {
		// 定义 Kotlin 编译器扩展版本与 Kotlin 版本相关联，需与项目的 Kotlin 版本匹配，详见：https://developer.android.com/jetpack/androidx/releases/compose-kotlin?hl=zh-cn
		kotlinCompilerExtensionVersion = "1.5.5"
	}
}

dependencies {
	api(libs.androidx.appcompat)
	api(libs.androidx.activity.asProvider())
	api(libs.androidx.fragment.asProvider())
	api(libs.androidx.activity.ktx)
	api(libs.androidx.fragment.ktx)

	api(project(":view-binding"))
	api("com.hl:uikit-toast")

	api(libs.statusbar.immersionbar.asProvider())
	api(libs.statusbar.immersionbar.ktx)

	api(platform(libs.jetpack.compose.bom))
	api(libs.jetpack.compose.ui.asProvider())
}