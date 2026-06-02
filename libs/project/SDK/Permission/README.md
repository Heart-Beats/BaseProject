# Permission — 权限请求模块

## 模块概述

`permission` 基于 PermissionX 封装，提供 Fragment/Activity 的权限请求扩展函数，简化运行时权限处理流程。

**模块坐标**: `com.hl.permission`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
Permission/
├── build.gradle.kts
└── src/main/java/com/hl/permission/
    └── _PermissionXUtil.kt      # 权限请求扩展
```

## 依赖关系

| 外部 | `com.guolindev.permissionx:permissionx` |
| 内部 | 无 |

## 对外接口

```kotlin
fun FragmentActivity.reqPermissions(
    vararg permissions: String,
    needExplainRequestReason: ((List<String>, ExplainScope) -> Unit)? = null,
    deniedAction: ((List<String>) -> Unit)? = null,
    allGrantedAction: (() -> Unit)? = null
)

fun Fragment.reqPermissions(
    vararg permissions: String,
    needExplainRequestReason: ((List<String>, ExplainScope) -> Unit)? = null,
    deniedAction: ((List<String>) -> Unit)? = null,
    allGrantedAction: (() -> Unit)? = null
)
```

## 构建与测试

```bash
./gradlew :permission:assemble
./gradlew :permission:publishToMavenLocal
```

## 使用示例

```kotlin
// 简单请求
reqPermissions(Manifest.permission.CAMERA) {
    // 已授权
}

// 完整请求（含解释和拒绝处理）
reqPermissions(
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO,
    needExplainRequestReason = { deniedList, scope ->
        scope.showRequestReasonDialog(deniedList, "需要使用相机和麦克风", "允许", "拒绝")
    },
    deniedAction = { deniedList ->
        toast("以下权限被拒绝: $deniedList")
    }
) {
    toast("所有权限已授权")
}
```
