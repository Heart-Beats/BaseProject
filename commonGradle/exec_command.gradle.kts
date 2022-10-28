val execCommandLine by extra {
    this::execCommandLineFun
}

/**
 *   执行命令行并返回结果
 *
 *   @param command 执行命令行
 *   @return  执行的输出结果
 */
fun execCommandLineFun(command: String): String {
    val byteArrayOutputStream = java.io.ByteArrayOutputStream()
    project.exec {
        // 获取不同平台的命令参数
        val args = if (isWindows()) arrayOf("cmd", "/c") else arrayOf("bash", "-c")

        // 给命令行的执行结果指定输出流接管
        commandLine(*args, command).standardOutput = byteArrayOutputStream
    }

    byteArrayOutputStream.use {
        return String(byteArrayOutputStream.toByteArray())
    }
}

fun isWindows(): Boolean {
    return org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem().isWindows
}