# ViewBindingUtil 中获取 ViewBinding 对象使用了反射，需要不混淆
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
  public static * inflate(android.view.LayoutInflater);
  public static * inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);
  public static * bind(android.view.View);
}

# ISdk 接口名不混淆
-keepnames interface com.hl.arch.web.sdk.ISdk
# 继承 ISdk 接口 的接口名不混淆
-keepclasseswithmembernames interface * extends com.hl.arch.web.sdk.ISdk{
#	所有被使用的方法不混淆
	<methods>;
}

# 不混淆 com.hl.arch.web.bean 该包下的所有内容
-keepclasseswithmembernames class com.hl.arch.web.bean.**{*;}