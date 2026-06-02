# Navigation — 导航组件优化模块

## 模块概述

`navigation` 是对 Android Jetpack Navigation 组件的优化改造。核心改进：**使用 `add()` / `hide()` 替代原生的 `replace()` 策略**，保留 Fragment 状态；同时支持全局统一过渡动画。

**模块坐标**: `com.hl.navigation`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
Navigation/
├── build.gradle.kts
├── README.md
└── src/main/java/com/hl/navigatioin/
    ├── MyNavHostFragment.kt           # 自定义 NavHostFragment
    ├── MyFragmentNavigator.kt         # 自定义 FragmentNavigator（add/hide 策略）
    ├── NavAnimations.kt               # 导航动画数据类
    ├── _ActivityExt.kt                # Activity 扩展
    ├── _Fragment.kt                   # Fragment 扩展（_NavController.kt）
    ├── _NavController.kt              # NavController 扩展
    ├── ui/
    │   └── _BottomNavigationView.kt   # BottomNavigationView 集成
    └── utils/
        └── ReflectHelper.kt           # 反射工具类
```

## 依赖关系

### 外部依赖
| 依赖 | 用途 |
|------|------|
| `androidx.navigation:navigation-fragment-ktx` | Jetpack Navigation |
| `androidx.navigation:navigation-ui-ktx` | Navigation UI |
| `com.google.android.material:material` | Material 组件 |
| `com.github.Poppingal:smooth-navigation` (Fork) | 自定义 Fork 版本 |

### 内部依赖
无内部模块依赖。

## 对外接口

### MyNavHostFragment

```kotlin
class MyNavHostFragment : NavHostFragment() {
    // 设置全局默认导航动画
    fun setCommonNavAnimations(navAnimationsInit: NavAnimations.() -> Unit)

    // 获取全局动画配置
    fun getCommonNavAnimations(): NavAnimations?

    // 设置特殊深度链接
    fun setSpecialDeepLinks(deepLinks: List<String>? = null, @StringRes deepLinksRes: List<Int>? = null)
    fun getSpecialDeepLinks(): List<String>
}
```

### MyFragmentNavigator

```kotlin
@Navigator.Name("fragment")
class MyFragmentNavigator(
    myNavHostFragment: MyNavHostFragment,
    manager: FragmentManager,
    containerId: Int
) : FragmentNavigator
```

**核心改进**: `navigate()` 方法内部使用 `add()` + `hide()` + `show()` 替代原生 `replace()`，避免 Fragment 重建丢失状态。

### NavAnimations

```kotlin
data class NavAnimations(
    @AnimRes var enterAnim: Int? = null,      // 入场动画
    @AnimRes var exitAnim: Int? = null,       // 出场动画
    @AnimRes var popEnterAnim: Int? = null,   // 返回入场动画
    @AnimRes var popExitAnim: Int? = null     // 返回出场动画
) {
    companion object { const val NO_ANIM = 0 }
}
```

### Navigation 扩展函数

```kotlin
// 获取当前展示的 Navigation Fragment
fun FragmentActivity.getCurrentNavigationFragment(): Fragment?

// 查找 NavController
fun Fragment.findNavController(): NavController

// 获上一页标题（从返回栈中）
fun Fragment.getLastPage(): CharSequence

// 获取当前 NavDestination
fun Fragment.getCurrentDestination(): NavDestination?

// 单顶模式导航（同一目标只保留一个实例）
fun NavController.navigateSingleTopTo(route: String)

// 从 URL 导航
fun NavController.navigateFromUrl(url: String, navOptions: NavOptions? = ...)
```

### BottomNavigationView 集成

```kotlin
// 简化 BottomNavigationView 与 NavController 的绑定
fun BottomNavigationView.setupWithNavController(navController: NavController)
```

## 构建与测试

```bash
# 构建模块
./gradlew :navigation:assemble

# 发布到本地 Maven
./gradlew :navigation:publishToMavenLocal
```

## 使用示例

### 布局中使用 MyNavHostFragment

```xml
<!-- activity_main.xml -->
<androidx.fragment.app.FragmentContainerView
    android:id="@+id/nav_host_fragment"
    android:name="com.hl.navigatioin.MyNavHostFragment"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    app:defaultNavHost="true"
    app:navGraph="@navigation/nav_graph" />
```

### 设置全局动画

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as MyNavHostFragment
        navHost.setCommonNavAnimations {
            enterAnim = R.anim.slide_in_right
            exitAnim = R.anim.slide_out_left
            popEnterAnim = R.anim.slide_in_left
            popExitAnim = R.anim.slide_out_right
        }
    }
}
```

### BottomNavigationView 集成

```kotlin
val navHostFragment = supportFragmentManager
    .findFragmentById(R.id.nav_host_fragment) as MyNavHostFragment
val navController = navHostFragment.navController

val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
bottomNav.setupWithNavController(navController)
```

### 页面导航

```kotlin
// 直接导航
findNavController().navigate(R.id.detail_fragment)

// 单顶模式（避免重复创建）
findNavController().navigateSingleTopTo(R.id.profile_fragment)

// URL 导航
findNavController().navigateFromUrl("https://example.com/app/detail?id=123")
```
