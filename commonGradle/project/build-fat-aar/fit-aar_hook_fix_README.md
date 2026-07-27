# fat-aar 架构缺陷全自动修复与混淆防护指南

本指南配合修复脚本（`fit-aar_hook_fix.gradle`）使用，专门解决 Android 组件化/重构打包中使用 `fat-aar`（或类似大融合打包插件）时带来的三大原生**致命缺陷**（DataBinding 丢失、SPI 覆盖、Kotlin 元数据丢失），同时提供了配套的**防混淆/防裁剪白名单**，确保 Release 包上线后的绝对安全。

------

## 🔥 解决的核心痛点

在默认的 `fat-aar` 机制下，被 `embed` 依赖的本地子模块往往会遭遇以下组件化灾难：

1. **DataBinding 元数据丢失**：子模块的 `-br.bin` 和 `binding_classes.json` 被无脑覆盖，导致引用该 AAR 的主工程在编译时**凭空缺失 `BR` 类**而引发闪退。
2. **SPI 服务覆盖失效**：多个子模块中同名的 `META-INF/services/` 配置文件发生覆盖，导致运行期通过 `ServiceLoader` 动态注入组件时**只有最后一个生效/全部丢失**。
3. **Kotlin 元数据丢失**：`.kotlin_module` 文件被丢弃，导致依赖 Kotlin 特性的跨模块反射或内联调用发生异常。
4. **Release 混淆裁剪**：Release 打包时，由于 SPI 是反射调用，实现类会被 ProGuard 误认为“未引用”而**直接被移除（Shrink）**，或类名被混淆导致反射找不到类。

------

## 🛠️ 核心工作原理

本机制不破坏原本的 AAR 打包流程，而是采用“两路并进、临门一脚拦截 + 自带免疫体质”的组合拳策略：

```shell
                           ┌──► 1. 扫描子模块中间产物
[embed 子模块编译完成] ────┤
                           └──► 2. 扫描纯预编译 AAR/JAR
                                       │
                                       ▼
                     【全量去重、智能缝合中间目录】
                                       │
          ┌────────────────────────────┴────────────────────────────┐
          ▼                                                         ▼
【Hook: processJavaResTask】                                   【Hook: bundleAarTask】
  └─► 智能排除旧文件，防止大一统覆盖                             └─► 强行把 db 元数据
  └─► 精准把全量 SPI & .kotlin_module                                送入 AAR 根目录
      塞进 classes.jar 内部                                           (彻底修复 BR 丢失)
          │
          ▼
【自带 consumerProguardFiles】 ──► 自动向宿主注入白名单 ──► 保护实现类不被混淆/删除
```

------

## 📦 集成三步走

### 第一步：放置并引入修复脚本

1. 将 `fit-aar_hook_fix.gradle` 脚本放入项目的统一管理目录下（例如 `CommonGradle/combine-build-aar/`）。
2. 在**负责最终打包输出大 AAR** 的那个主控 Module 的 `build.gradle` 最底部引入它：

```Groovy
// ====== 在文件最底部引入本修复脚本 ======
apply from: "${rootProject.projectDir}/CommonGradle/build-fat-aar/fit-aar_hook_fix.gradle"
```

### 第二步：配置 AAR 自带混淆规则（让 AAR 自带免疫体质）

为了不给上层引用该 AAR 的主 App 增加维护负担，必须配置 `consumerProguardFiles`。当 App 依赖你的 AAR 时，这些规则会自动合并到 App 的混淆流程中。

在主控打包模块的 `build.gradle` 中开启配置：

```Groovy
android {
    defaultConfig {
        // ... 其他配置 ...
        
        // 关键配置：指定消费者的混淆规则文件
        consumerProguardFiles 'consumer-rules.pro'
    }
}
```

### 第三步：编写 `consumer-rules.pro` 规则

在主控打包模块的根目录下，创建 `consumer-rules.pro` 文件，并写入以下防线代码：

```groovy
# =========================================================================
# 1. SPI 服务接口与实现类混淆白名单
# =========================================================================
# 保护 META-INF/services 目录结构不被优化或破坏
-keepdirectories META-INF/services

# 【核心规则】保持所有通过 SPI 声明的接口和实现类不被混淆和移除
# 注意：假设你的公共组件或实现类都在 com.xxx 包名下，请根据项目实际情况调整包名匹配
-keep class * implements com.xxx.** { *; }
-keep class com.xxx.**.impl.** { *; }

# =========================================================================
# 2. DataBinding / ViewBinding 混淆白名单
# =========================================================================
# 保持所有的 BR 类及其内部的成员变量不被混淆（极重要！否则布局 XML 找不到字段）
-keep class *..BR { *; }

# 保持所有生成的 ViewDataBinding / ViewBinding 实现类
-keep class * extends androidx.databinding.ViewDataBinding { *; }
-keep class * implements androidx.viewbinding.ViewBinding { *; }

# 保持 DataBinding 自动生成的注解器和一些内建工具类
-dontwarn androidx.databinding.**
-keep class androidx.databinding.** { *; }
```

------

## ⚠️ 极其关键的注意事项（避坑指南）

1. 必须养成 Build -> Clean 的好习惯

   Gradle 拥有强大的增量编译缓存机制（`UP-TO-DATE`）。当你**修改了子模块的代码、布局、或者新增了 SPI 接口实现**并准备重新打 AAR 包时，**务必先执行 `Clean Project`**！否则，Gradle 可能会直接复用上一次未合并前的缓存，导致新生产的 AAR 依旧残缺。

   

2. 为什么不能随便在脚本里写字符串 `exclude`？

   脚本内部对 `processJavaResTask` 采用了**智能闭包过滤器**（`processJavaResTask.exclude { details -> ... }`）。这是为了防止 Gradle 判定“大一统拦截”。**后续维护者千万不要**无脑在脚本中加入 `processJavaResTask.exclude 'META-INF/services/**'` 这样的硬编码字符串，否则会导致我们亲手合并出来的新服务文件再次被 Gradle 全局拉黑抹杀。

   

3. 混淆白名单是最终防线（不可或缺）

   修复脚本只负责**“产物封装阶段”**的文件拼装，而混淆规则负责**“代码优化阶段”**的防腐。如果不配置第二步和第三步的混淆白名单，打出来的 Release APK 会因为找不到真实的类（被类重命名或被 Shrink 裁剪）而引发大面积运行时 `ClassNotFoundException`。
