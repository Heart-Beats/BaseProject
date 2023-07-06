# JsBridge 的混淆配置，官方库未自带，去除会导致混淆时传输数据解析失败，具体见类： com.github.lzyzsd.jsbridge.Message
-keep class org.json.** { *; }

# ISdk 接口名不混淆
-keepnames interface com.hl.web.sdk.ISdk
# 继承 ISdk 接口 的接口名不混淆
-keepclasseswithmembernames interface * extends com.hl.web.sdk.ISdk{
#	所有被使用的方法不混淆
	<methods>;
}

# 不混淆 com.hl.arch.web.bean 该包下的所有内容
-keepclasseswithmembernames class com.hl.arch.web.bean.**{*;}