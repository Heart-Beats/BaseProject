import com.buildsrc.AndroidVersions
import com.buildsrc.Deps

plugins {
	id("com.android.library")
	id("org.jetbrains.kotlin.android")
}

android {
	namespace = "com.hl.unimp"
	compileSdk = AndroidVersions.COMPILE_SDK

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

	//此处配置必须添加 否则无法正确运行
	androidResources {
		this.additionalParameters("--auto-add-overlay")
		//noCompress 'foo', 'bar'
		this.ignoreAssetsPattern = "!.svn:!.git:.*:!CVS:!thumbs.db:!picasa.ini:!*.scc:*~"
	}
}

dependencies {
	api(fileTree("libs") { include("*.jar") })

	api(project(":SDK:uniapp-base-oaid-sdk"))
	api(project(":SDK:uniapp-breakpad-build-release"))
	api(project(":SDK:uniapp-v8-release"))
	api(project(":SDK:uniMPSDK-V2-release"))

	implementation(Deps.AndroidX.recyclerview)
	implementation("androidx.legacy:legacy-support-v4:1.0.0")
	implementation(Deps.AndroidX.appcompat)
	implementation(Deps.Json.fast_json)

	val fresco_version = "2.5.0"
	implementation("com.facebook.fresco:fresco:${fresco_version}")
	implementation("com.facebook.fresco:animated-gif:${fresco_version}")

	implementation(Deps.Glide.glide)
	api(Deps.Gif.android_gif_drawable)
	implementation("androidx.webkit:webkit:1.3.0")
}