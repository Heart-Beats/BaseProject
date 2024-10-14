plugins {
	id("com.android.test")
	id("org.jetbrains.kotlin.android")
}

android {
	namespace = "com.hl.benchmark"
	compileSdk = 33

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}

	kotlinOptions {
		jvmTarget = JavaVersion.VERSION_17.toString()
	}

	defaultConfig {
		minSdk = 24
		targetSdk = 33

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		// This benchmark buildType is used for benchmarking, and should function like your
		// release build (for example, with minification on). It"s signed with a debug key
		// for easy local/CI testing.
		create("benchmark") {
			isDebuggable = true
			signingConfig = getByName("debug").signingConfig
			matchingFallbacks += listOf("release")
		}
	}

	// Note that your module name may be different
	targetProjectPath = ":app"
	// Enable the benchmark to run separately from the app process
	experimentalProperties["android.experimental.self-instrumenting"] = true
}

dependencies {
	implementation("androidx.test.ext:junit:1.1.5")
	implementation("androidx.test.espresso:espresso-core:3.5.1")
	implementation("androidx.test.uiautomator:uiautomator:2.2.0")
	implementation("androidx.benchmark:benchmark-macro-junit4:1.1.0-beta05")
}

androidComponents {
	beforeVariants(selector().all()) {
		it.enabled = it.buildType == "benchmark"
	}
}