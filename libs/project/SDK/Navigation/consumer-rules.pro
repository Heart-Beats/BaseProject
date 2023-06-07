# _NavController 中使用到相关属性进行反射，避免混淆
-keepclasseswithmembernames class androidx.navigation.NavDestination{
	private java.util.List deepLinks;
}
-keepclasseswithmembernames class androidx.navigation.NavDeepLink{
	 private  <fields>;
}