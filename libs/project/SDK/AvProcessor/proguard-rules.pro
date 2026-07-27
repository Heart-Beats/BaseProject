# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# video-compressor 库通过 Class.forName 反射访问，R8 无法感知字符串形式的引用，需显式 keep
# 该库为 compileOnly，由宿主 App 运行时提供，此处规则会随 AAR 合并到宿主 App 的混淆配置中
-keep class com.primaverahq.videocompressor.** { *; }