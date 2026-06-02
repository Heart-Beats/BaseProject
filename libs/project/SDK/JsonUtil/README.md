# JsonUtil — JSON 处理模块

## 模块概述

`json-util` 基于 Gson 提供简化的 JSON 序列化/反序列化工具，并扩展 `String` 和 `Any` 类型的便捷转换方法。

**模块坐标**: `com.hl.json`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
JsonUtil/
├── build.gradle.kts
└── src/main/java/com/hl/json/
    ├── GsonUtil.kt            # Gson 工具单例
    └── _JsonUtil.kt           # 扩展函数
```

## 依赖关系

| 外部 | `com.google.code.gson:gson` |

## 对外接口

```kotlin
object GsonUtil {
    val gson: Gson
    inline fun <reified T> fromJson(json: String?): T?
    fun toJson(any: Any?): String
}

// 扩展函数
fun String?.isJson(): Boolean
fun Any?.toJson(): String
inline fun <reified T> String?.fromJson(): T?
```

## 构建与测试

```bash
./gradlew :json-util:assemble
./gradlew :json-util:publishToMavenLocal
```

## 使用示例

```kotlin
// 对象转 JSON
val json = User("张三", 25).toJson()
// {"name":"张三","age":25}

// JSON 转对象
val user = json.fromJson<User>()

// 判断是否为合法 JSON
val isValid = """{"key":"value"}""".isJson()  // true
```
