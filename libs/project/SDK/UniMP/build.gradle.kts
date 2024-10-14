plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "com.hl.unimp"

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

	implementation(libs.androidx.recyclerview)
	implementation("androidx.legacy:legacy-support-v4:1.0.0")
	implementation(libs.androidx.appcompat)
	implementation(libs.fastJson)

	val fresco_version = "2.5.0"
	implementation("com.facebook.fresco:fresco:${fresco_version}")
	implementation("com.facebook.fresco:animated-gif:${fresco_version}")

	implementation(libs.glide.asProvider())
	api(libs.android.gif.drawable)
	implementation(libs.androidx.webkit)
}