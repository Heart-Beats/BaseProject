# Benchmark — 基准测试模块

## 模块概述

`benchmark` 是 Android Macrobenchmark 模块，用于生成 Baseline Profile（基准配置文件），以优化应用启动性能和关键用户旅程的流畅度。

**模块类型**: `com.android.test`（测试模块）
**目标模块**: `:app`

## 目录结构

```
benchmark/
├── build.gradle.kts
└── src/main/
    ├── AndroidManifest.xml
    └── java/com/hl/benchmark/
        └── BaselineProfileGenerator.kt   # 基准配置文件生成器
```

## 依赖关系

| 依赖 | 用途 |
|------|------|
| `androidx.benchmark:benchmark-macro-junit4` | Macrobenchmark 测试框架 |
| `androidx.test.ext:junit` | JUnit 测试扩展 |
| `androidx.test.espresso:espresso-core` | Espresso 测试框架 |
| `androidx.test.uiautomator:uiautomator` | UI Automator 测试框架 |
| `:app` (targetProjectPath) | 测试目标 |

## 构建配置特点

- `compileSdk = 33`, `minSdk = 24`, `targetSdk = 33`
- 启用 `experimentalProperties["android.experimental.self-instrumenting"]`
- 仅 `benchmark` 构建类型被激活
- 目标项目路径：`":app"`

## 构建与测试

```bash
# 生成基准配置文件
./gradlew :benchmark:connectedBenchmarkAndroidTest

# 构建 benchmark 变体
./gradlew :app:assembleBenchmark
```

## 使用说明

Baseline Profile 可在应用安装时由 ART 预编译，显著减少首次启动和关键路径的编译开销。

**流程**：
1. 在 `BaselineProfileGenerator.kt` 中定义需要追踪的关键用户旅程
2. 执行 `connectedBenchmarkAndroidTest` 生成配置文件
3. 将生成的 `baseline-prof.txt` 放入 `app/src/main` 目录
4. ProfileInstaller 会在应用安装时自动应用
