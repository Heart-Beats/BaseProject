plugins {
	id("version-catalog")
	id("maven-publish")
}

catalog {
	// 定义目录
	versionCatalog {
		val files = files(
			"/tomls/versionLibs.toml", "/tomls/pluginLibs.toml",
			"/tomls/dependendLibs.toml", "/tomls/bundleLibs.toml",
			"/tomls/compilerLibs.toml"
		)

		from(files)
	}
}

publishing {
	publications {
		this.register("versionCatalog", MavenPublication::class.java) {
			this.groupId = "com.hl.catalog"
			this.artifactId = "catalog"
			this.version = "1.0.0"

			this.from(components["versionCatalog"])
		}
	}

	this.repositories {
		// 配置 MavenLocal 仓库
		mavenLocal()
	}
}


task("publishingToMavenLocal") {
	this.description = "打包至 MavenLocal"
	this.group = "my-publishing"

	this.doLast {
		println("打包完成 ------>  MavenLocal")
	}
}