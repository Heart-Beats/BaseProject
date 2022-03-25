################# 友盟 混淆开始 #################
-keep class com.umeng.** { *; }

-keep class com.uc.** { *; }

-keep class com.efs.** { *; }

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# com.youma.health 为应用包名，需要注意
-keep public class [com.youma.health].R$*{
public static final int *;
}
################# 友盟 混淆结束 #################