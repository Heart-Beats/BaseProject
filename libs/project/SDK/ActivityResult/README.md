# ActivityResult — ActivityResult 封装模块

## 模块概述

`activity-result` 封装了 Android Activity Result API，提供简化的 `startActivityForResult` 替代方案。

**模块坐标**: `com.hl.activityresult`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 对外接口

```kotlin
class ActivityResultHelper(private val caller: ActivityResultCaller) {
    fun launchActivity(targetClass: Class<out Activity>, options: ActivityOptionsCompat? = null,
                       callback: OnActivityResult)
    fun launchIntent(intent: Intent, options: ActivityOptionsCompat? = null, callback: OnActivityResult)
}

interface OnActivityResult {
    fun onResultOk(data: Intent?)
    fun onResultCanceled(data: Intent?) {}
    fun onResultOther(resultCode: Int, data: Intent?) {}
}
```

## 构建与测试

```bash
./gradlew :activity-result:assemble
```

## 使用示例

```kotlin
val helper = ActivityResultHelper(this)
helper.launchActivity(DetailActivity::class.java, object : OnActivityResult {
    override fun onResultOk(data: Intent?) {
        val result = data?.getStringExtra("key")
    }
})
```
