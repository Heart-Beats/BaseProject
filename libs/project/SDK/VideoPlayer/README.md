# VideoPlayer — 视频播放模块

## 模块概述

`video-player` 基于 GSYVideoPlayer 封装视频播放，支持全屏/小窗切换、生命周期感知。

**模块坐标**: `com.hl.videoplayer`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 依赖关系

| 外部 | `com.shuyu:GSYVideoPlayer` |
| 内部 | `image-load` |

## 对外接口

```kotlin
class UIKitMyStandardGSYVideoPlayer(context: Context) : StandardGSYVideoPlayer(context) {
    var orientationUtils: OrientationUtils?
}

// 快速初始化播放器
fun UIKitMyStandardGSYVideoPlayer.initPlayer(
    lifecycleOwner: LifecycleOwner, url: String,
    videoName: String? = null, needTitle: Boolean = false,
    isPrintLog: Boolean = false,
    block: GSYVideoOptionBuilder.() -> Unit = {}
)
```

## 构建与测试

```bash
./gradlew :video-player:assemble
```

## 使用示例

```kotlin
binding.videoPlayer.initPlayer(
    lifecycleOwner = viewLifecycleOwner,
    url = "https://example.com/video.mp4",
    videoName = "示例视频", needTitle = true
    isPrintLog = BuildConfig.DEBUG
) {
    setCacheWithPlay(true)
}
```
