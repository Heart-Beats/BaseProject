# video-compressor 库通过 Class.forName 反射访问，R8 无法感知字符串形式的引用，需显式 keep
# 该库为 compileOnly，由宿主 App 运行时提供，此处规则会随 AAR 合并到宿主 App 的混淆配置中
-keep class com.primaverahq.videocompressor.** { *; }
