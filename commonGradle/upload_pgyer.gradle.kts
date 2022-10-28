apply {
	val commonGradleFile: File by extra

	this.from(File(commonGradleFile, "jdk_http_util.gradle.kts"))
	this.from(File(commonGradleFile, "exec_command.gradle.kts"))
	this.from(File(commonGradleFile, "flavor_channel.gradle"))
}

// 执行命令行函数
val execCommandLine: (String) -> String by extra

// POST 请求
val doPost: (String, Boolean, Map<String, String>) -> String by extra
// 上传文件以及参数的函数
val uploadFile: (String, String, Map<String, String>) -> String by extra

enum class Environment(val desc: String) {
	EnvD("开发环境"),
	EnvT("测试环境"),
	EnvUat("UAT环境"),
	EnvR("正式环境"),
	EnvUnknown("未知环境"),
}

data class UploadPgyerConfig(
	val environment: Environment = Environment.EnvUnknown,
	val apiKey: String = "",
	val uploadApkFile: File = File("")
)

fun buildUploadPgyerConfigList(): MutableList<UploadPgyerConfig> {
	val uploadPgyerConfigList = mutableListOf<UploadPgyerConfig>()
	fileTree(File(project.buildDir, "channel")).forEach {
		val fileName = it.name
		val uploadPgyerConfig = when {
			fileName.contains("EnvD") -> UploadPgyerConfig(Environment.EnvD, "0a585a119c883b3791ebd746fb6b9316", it)
			fileName.contains("EnvT") -> UploadPgyerConfig(Environment.EnvT, "7822b41cbf13e244a5ffdce908d3dcf0", it)
			fileName.contains("EnvUat") -> UploadPgyerConfig(Environment.EnvUat, "0d8b2cff49534bc468e9df45c558a5b7", it)
			fileName.contains("EnvR") -> UploadPgyerConfig(Environment.EnvR, "bc7e7df9388143e15dac1410141520c3", it)
			else -> return@forEach
		}
		uploadPgyerConfigList.add(uploadPgyerConfig)
	}

	return uploadPgyerConfigList
}

/**
 * 创建上传到蒲公英各个环境的相关 Task
 */
fun createAllUploadPgyerTask(filter: (UploadPgyerConfig) -> Boolean): List<Task> {
	return buildUploadPgyerConfigList().filter {
		filter(it)
	}.map {
		createUploadPgyerTask(it)
	}
}

/**
 * 创建上传到蒲公英的 task
 */
fun createUploadPgyerTask(uploadPgyerConfig: UploadPgyerConfig): Task {
	val environment: Environment = uploadPgyerConfig.environment
	return task("upload2${environment.name}") {
		println("创建上传 APK 到${environment.desc}的任务：${this.name}")

		this.group = "蒲公英"
		this.description = "上传${environment.desc}的 APP 到蒲公英"

		this.doFirst {
			println("开始${description}")
		}
		this.doLast {
			println("********** 开始执行上传 APK 到${environment.desc} **********")
			doUploadApp2Pgyer(uploadPgyerConfig, getTodayCommitInfo())
		}
	}
}


/**
 * 获取 git 今天提交的相关信息
 */
fun getTodayCommitInfo(): String {
	val gitCommand = """
		git log  --no-merges --pretty=format:"[%ad] %an: %s"  --date=format:"%Y-%m-%d %H:%M:%S" --after="<date> 00:00" --before="<date> 23:59"
	""".trimIndent()
	return execCommandLine(gitCommand)
}


/**
 * 通过 curl 命令行工具上传 App 至蒲公英
 */
fun doUploadApp2Pgyer(uploadPgyerConfig: UploadPgyerConfig, buildUpdateDescription: String) {
	val environment = uploadPgyerConfig.environment
	val apiKey = uploadPgyerConfig.apiKey
	val apkFile = uploadPgyerConfig.uploadApkFile

	val command = """
		curl -F '_api_key=${apiKey}' -F 'file=@${apkFile.absolutePath}' -F 'buildInstallType=1' -F 'buildUpdateDescription=${buildUpdateDescription}' -g  -# https://www.pgyer.com/apiv2/app/upload
	""".trimIndent()

	// curl 在 windows cmd 中换行不好处理，因此这里使用原始网络请求方式


	val cosTokenResult = getCOSToken(apiKey, environment)

	if (cosTokenResult.isHttpOk()) {
		println("********** 获取  COSToken 成功 **********")
		val cosTokenData = cosTokenResult.getJsonValue("data")

		println("获取到的 cosTokenData ：\n $cosTokenData")

		val uploadFileResult = doUploadApp2PgyerCos(cosTokenData ?: "", apkFile)

		// 上传成功：返回 http 状态码为 204 No Content
		if (uploadFileResult.getJsonValue("code") == "204") {
			println("********** 上传 APK 到${environment.desc} 成功 **********")

			val buildKey = cosTokenData?.getJsonValue("key") ?: ""

			// 3 秒后查询发布信息
			TimeUnit.SECONDS.sleep(5)

			val appBuildInfoResult = getAppBuildInfo(apiKey, buildKey)
			checkAppBuildInfoResult(appBuildInfoResult, environment, apiKey, buildKey)
		} else {
			println("********** 上传 APK 到${environment.desc} 失败 **********")
			println("失败原因：\n ${uploadFileResult.getJsonValue("message")} \n")
		}
	} else {
		println("********** 获取  COSToken 失败 **********")
		println("失败原因：\n ${cosTokenResult.getJsonValue("message")} \n")
	}
}

/**
 * 第一步： 获取上传的 token
 *
 * */
fun getCOSToken(apiKey: String, environment: Environment): String {
	val url = "http://www.pgyer.com/apiv2/app/getCOSToken"

	val paramMap = hashMapOf(
		"_api_key" to apiKey,
		"buildType" to "android",
		"buildUpdateDescription" to "${environment.desc}：\n ${getTodayCommitInfo()}",
	)
	return doPost(url, true, paramMap)
}

/**
 * 第二步： 通过获取到的 CosToken 上传 app 到服务器后台队列， 不代表 app 完成发布， 一般来说，一般1分钟以内就能完成发布
 *
 * */
fun doUploadApp2PgyerCos(cosTokenData: String, apkFile: File): String {
	// 该接口使用代理时请求会有问题
	val endpoint = cosTokenData.getJsonValue("endpoint")/*?.replace("https", "http")*/ ?: ""

	val key = cosTokenData.getJsonValue("key") ?: ""

	val params = cosTokenData.getJsonValue("params") ?: ""
	val signature = params.getJsonValue("signature") ?: ""
	val xCosSecurityToken = params.getJsonValue("x-cos-security-token") ?: ""

	val paramMap = mapOf(
		"key" to key,
		"signature" to signature,
		"x-cos-security-token" to xCosSecurityToken
	)

	return uploadFile(endpoint, apkFile.absolutePath, paramMap)
}


/**
 * 第三步： 检测应用是否发布完成，并获取发布应用的信息
 *
 * */
fun getAppBuildInfo(apiKey: String, buildKey: String): String {
	val url = "https://www.pgyer.com/apiv2/app/buildInfo"
	val paramMap = mapOf(
		"_api_key" to apiKey,
		"buildKey" to buildKey,
	)
	return doPost(url, true, paramMap)
}


fun String.getJsonValue(key: String): String? {
	// JsonSlurper 转换时，数组会被转换为 ArrayList 对象，而 object 会被转换为 Map 对象
	val parse = groovy.json.JsonSlurper().parseText(this)

	if (parse is Map<*, *>) {
		val value = parse[key]

		//  取到的仍是 map 或数组对象需要转换为 json 字符串
		if (value is Map<*, *> || value is List<*>) {
			return groovy.json.JsonOutput.toJson(value)
		}

		if (value !is String?) {
			return value.toString()
		}
		return value
	}

	return null
}

fun String.isHttpOk(): Boolean {
	return this.getJsonValue("code") == "0"
}

fun checkAppBuildInfoResult(appBuildInfoResult: String, environment: Environment, apiKey: String, buildKey: String) {
	println("checkAppBuildInfoResult -------------> appBuildInfoResult == ${appBuildInfoResult}\n")

	if (appBuildInfoResult.isHttpOk()) {
		println("********** 发布 APK 到${environment.desc} 成功 **********")
		println("返回结果：\n ${appBuildInfoResult.getJsonValue("data")} \n")
	} else {
		val appBuildInfoResultCode = appBuildInfoResult.getJsonValue("code") ?: ""
		if (appBuildInfoResultCode == "1216") {
			println("********** 发布 APK 到${environment.desc} 失败 **********")
			println("失败原因：\n ${appBuildInfoResult.getJsonValue("message")} \n")
		} else if (appBuildInfoResultCode == "1246") {
			println("********** 正在发布 APK 到${environment.desc}  **********")

			// 三秒后继续查询
			TimeUnit.SECONDS.sleep(3)

			val nextAppBuildInfoResult = getAppBuildInfo(apiKey, buildKey)
			checkAppBuildInfoResult(nextAppBuildInfoResult, environment, apiKey, buildKey)
		}
	}
}

task("UploadAllApp2Pgyer") {
	this.group = "蒲公英"
	this.description = "编译上传当前项目所有环境的 APP 到蒲公英"

	this.dependsOn("channelRelease")

	createAllUploadPgyerTask {
		it.environment == Environment.EnvD
				|| it.environment == Environment.EnvT
				// || it.environment == Environment.EnvR
	}.forEach {
		this.dependsOn(it)

		// 首先执行生成相关环境 apk 的任务
		it.mustRunAfter("channelRelease")
	}
}