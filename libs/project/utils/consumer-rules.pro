#  x5  混淆开始
-dontwarn dalvik.**
-dontwarn com.tencent.smtt.**

-keep class com.tencent.smtt.** {
    *;
}

-keep class com.tencent.tbs.** {
    *;
}
#  x5  混淆结束


# _NavController 中使用到相关属性进行反射，避免混淆
-keepclasseswithmembernames class androidx.navigation.NavDestination{
	private java.util.List deepLinks;
}
-keepclasseswithmembernames class androidx.navigation.NavDeepLink{
	 private  <fields>;
}