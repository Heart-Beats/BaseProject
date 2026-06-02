# MMKVSharedPreferences — 高性能存储模块

## 模块概述

`mmkv-sp` 基于腾讯 MMKV 提供高性能键值存储，完全兼容 SharedPreferences API，支持多进程、加密存储和对象序列化。

**模块坐标**: `com.hl.mmkvsharedpreferences`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
MMKVSharedPreferences/
├── build.gradle.kts
└── src/main/java/com/hl/mmkvsharedpreferences/
    ├── SharedPreferencesInitializer.kt   # Startup 自动初始化
    ├── MMKVIniter.kt                     # 内部初始化
    ├── _MMKVUtil.kt                      # MMKV 工具
    └── _SharePreferenceUtil.kt           # SharedPreferences 创建和扩展
```

## 依赖关系

| 外部 | `com.tencent:mmkv`, `androidx.security:security-crypto` |
| 内部 | `json-util`（依赖，用于对象序列化） |

## 对外接口

```kotlin
fun sharedPreferences(
    name: String = "default",
    isUseMMKV: Boolean = true,
    isMultiProcess: Boolean = false,
    isEncrypted: Boolean = false
): SharedPreferences

// 扩展：存储/读取任意对象
fun SharedPreferences.Editor.putObject(key: String, obj: Any)
inline fun <reified T> SharedPreferences.getObject(key: String): T?
```

## 构建与测试

```bash
./gradlew :mmkv-sp:assemble
./gradlew :mmkv-sp:publishToMavenLocal
```

## 使用示例

```kotlin
// 使用 MMKV 替代 SharedPreferences
val sp = sharedPreferences("user", isUseMMKV = true)
sp.edit().putString("token", "xxx").apply()
val token = sp.getString("token", "")

// 存储/读取对象
data class User(val name: String, val age: Int)
sp.edit().putObject("user", User("张三", 25)).apply()
val user = sp.getObject<User>("user")
```
