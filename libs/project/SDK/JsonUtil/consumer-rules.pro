##---------------开始：Gson 的 proguard 配置  ----------
# Gson 在处理字段时使用存储在类文件中的泛型。混淆默认情况下会删除此类信息，因此将其配置为保留所有信息.
-keepattributes Signature

# 用于使用 GSON @Expose 注释
-keepattributes *Annotation*

# Gson 特定类
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# 防止 proguard 从TypeAdapter、TypeAdapterFactory 中剥离接口信息， JsonSerializer、JsonDeserializer 实例（因此它们可以在 @JsonAdapter 中使用）
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# 防止 R8 让数据对象成员始终为空
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# 使用 R8 版本 3.0 及更高版本保留 TypeToken 及其子类的通用签名
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
##---------------结束：Gson的proguard配置  ----------