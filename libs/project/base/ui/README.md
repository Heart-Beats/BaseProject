# base-ui — UI 基础组件模块

## 模块概述

`base-ui` 是 UI 层的基础模块，提供 Activity/Fragment 基类体系、ViewBinding 委托、Compose 集成、系统栏管理（ImmersionBar）、密度转换扩展、Fragment 容器路由等基础设施。

**模块坐标**: `com.hl.ui`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
ui/
├── build.gradle.kts
└── src/main/java/com/hl/ui/
    ├── base/
    │   ├── IPageInflate.kt                    # 页面加载接口
    │   ├── BaseActivity.kt                    # Activity 基类
    │   ├── BaseFragment.kt                    # Fragment 基类
    │   ├── ViewBindingBaseActivity.kt         # ViewBinding Activity 基类
    │   ├── ViewBindingBaseFragment.kt         # ViewBinding Fragment 基类
    │   ├── ComposeBaseActivity.kt             # Compose Activity 基类
    │   ├── ComposeBaseFragment.kt             # Compose Fragment 基类
    │   └── FragmentContainerActivity.kt       # Fragment 容器路由
    ├── bindingDelegate/
    │   ├── ViewBindingDelegate.kt             # ViewBinding 代理接口
    │   ├── ActivityViewBindingDelegate.kt     # Activity ViewBinding 代理
    │   └── FragmentViewBindingDelegate.kt     # Fragment ViewBinding 代理
    └── utils/
        ├── _ActivityExt.kt                    # Activity 扩展函数
        ├── _ActivityJump.kt                   # Activity 跳转扩展
        ├── _DensityUtils.kt                   # 密度转换扩展
        ├── _FindViewUtil.kt                   # 视图查找工具
        ├── _SystemBar.kt                      # 系统栏管理
        └── _View.kt                           # View 扩展函数
```

## 依赖关系

### 外部依赖
| 依赖 | 用途 |
|------|------|
| `androidx.appcompat:appcompat` | AppCompat |
| `androidx.activity:activity-ktx` | Activity KTX |
| `androidx.fragment:fragment-ktx` | Fragment KTX |
| `androidx.compose:compose-bom` (platform) | Compose BOM 版本管理 |
| `androidx.compose.ui:ui` | Compose UI |
| `com.gyf.immersionbar:immersionbar` | 沉浸式状态栏 |
| `com.gyf.immersionbar:immersionbar-ktx` | 沉浸式状态栏 KTX |

### 内部依赖
| 模块 | 关系 |
|------|------|
| `view-binding` | 依赖 —— ViewBinding 工具 |
| `uikit-toast` | 依赖 —— Toast 组件 |

## 对外接口

### Activity 基类

```kotlin
abstract class BaseActivity : AppCompatActivity(), IPageInflate {
    abstract val layoutResId: Int
    protected var toolbar: Toolbar?
    protected var immersionBar: ImmersionBar?
    abstract fun onViewCreated(savedInstanceState: Bundle?)
    protected open fun updateSystemBar()
    protected open fun getStatusBarColor(): Int          // 默认 Color.WHITE
    protected open fun getNavigationBarColor(): Int       // 默认 Color.TRANSPARENT
    protected fun setStatusBarImmerseFromView(statusBarView: View, block: ImmersionBar.() -> Unit = {})
}
```

### Fragment 基类

```kotlin
abstract class BaseFragment : Fragment(), IPageInflate {
    protected abstract val layoutResId: Int
    var toolbar: Toolbar?
    protected var immersionBar: ImmersionBar?
    protected open fun saveStateToArguments(key: String, saveState: Bundle)
    protected open fun restoreStateFromArguments(key: String): Bundle?
    protected open fun updateSystemBar()
    protected open fun isMainPage(): Boolean
    protected open fun onBackPressed()
}
```

### ViewBinding 基类

```kotlin
abstract class ViewBindingBaseActivity<Binding : ViewBinding>(
    private val activityBindingDelegate: ViewBindingDelegate<Binding> = ActivityBindingDelegate()
) : BaseActivity(), ViewBindingDelegate<Binding> by activityBindingDelegate {
    override val layoutResId = 0
    abstract fun Binding.onViewCreated(savedInstanceState: Bundle?)
}

abstract class ViewBindingBaseFragment<Binding : ViewBinding>(
    private val fragmentBindingDelegate: ViewBindingDelegate<Binding> = FragmentBindingDelegate()
) : BaseFragment(), ViewBindingDelegate<Binding> by fragmentBindingDelegate {
    override val layoutResId = 0
    abstract fun Binding.onViewCreated(savedInstanceState: Bundle?)
}
```

### Compose 基类

```kotlin
abstract class ComposeBaseActivity : ViewBindingBaseActivity<ActivityComposeBaseBinding>() {
    @Composable abstract fun Content(savedInstanceState: Bundle?)
}

abstract class ComposeBaseFragment : ViewBindingBaseFragment<FragmentComposeBaseBinding>() {
    @Composable abstract fun Content(savedInstanceState: Bundle?)
}
```

### Fragment 容器路由

```kotlin
// 启动一个 Fragment 在独立的容器 Activity 中
fun Context.startFragment(fragmentClass: Class<out Fragment>, extras: Bundle? = null)

// reified 泛型版本
inline fun <reified T : Fragment> Context.startFragment(argumentsBlock: Bundle.() -> Unit = {})
inline fun <reified T : Fragment> Fragment.startFragment(argumentsBlock: Bundle.() -> Unit = {})
inline fun <reified T : Fragment> View.startFragment(argumentsBlock: Bundle.() -> Unit = {})
```

### Activity 跳转扩展

```kotlin
inline fun <reified T> Context.startAct(block: Intent.() -> Unit = {})
inline fun <reified T> Fragment.startAct(block: Intent.() -> Unit = {})
inline fun <reified T> Activity.startActForResult(reqCode: Int, block: Intent.() -> Unit = {})
inline fun <reified T> Fragment.startActForResult(reqCode: Int, block: Intent.() -> Unit = {})
```

### Fragment 管理扩展

```kotlin
fun FragmentActivity.getFragmentById(id: Int): Fragment?
fun FragmentActivity.replaceFragment(containerId: Int, fragmentClass: Class<out Fragment>, ...)
inline fun <reified T : Fragment> FragmentActivity.replaceFragment(containerId: Int, ...)
```

### 密度转换扩展

```kotlin
val Int.dpInt: Int    // dp → px（Int）
val Int.dp: Int       // dp → px
val Int.sp: Float     // sp → px
val Float.dp: Float
val Float.sp: Float
```

### 系统栏管理

```kotlin
fun Activity.initInsetPadding(top: Boolean = true, bottom: Boolean = true)
fun Fragment.initInsetPadding(top: Boolean = true, bottom: Boolean = true)
fun LifecycleOwner.initInsetPaddingSmart(top: Boolean = true, bottom: Boolean = true)
fun Activity.setImmersiveSystemBar(isImmersive: Boolean)
```

### View 扩展

```kotlin
fun View.onClick(interval: Long, listener: (View) -> Unit)  // 防抖点击
fun View.onClick(listener: (View) -> Unit)                   // 默认 500ms 防抖
fun View.getStatusBarHeight(): Int
fun View.getNavigationBarHeight(): Int
fun View.visible()
fun View.invisible()
fun View.gone()
fun View.visibleOrGone(show: Boolean)
```

## 构建与测试

```bash
# 构建模块
./gradlew :base-ui:assemble

# 发布到本地 Maven
./gradlew :base-ui:publishToMavenLocal
```

## 使用示例

### 创建 ViewBinding Activity

```kotlin
class HomeActivity : ViewBindingBaseActivity<ActivityHomeBinding>() {
    override fun Binding.onViewCreated(savedInstanceState: Bundle?) {
        textView.text = "Hello World"
        button.onClick {
            toast("点击了按钮")
        }
    }
}
```

### 创建 Compose Fragment

```kotlin
class ProfileFragment : ComposeBaseFragment() {
    @Composable
    override fun Content(savedInstanceState: Bundle?) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("个人中心", fontSize = 18.sp)
        }
    }
}
```

### Fragment 容器路由

```kotlin
// 启动独立 Fragment 页面
startFragment<DetailFragment> {
    putString("itemId", "123")
}
```

### 密度转换

```kotlin
val margin = 16.dp         // dp → px
val textSize = 14.sp       // sp → px
val width = 300.dpInt      // dp → Int px
```

### 沉浸式状态栏

```kotlin
class MyActivity : ViewBindingBaseActivity<ActivityMyBinding>() {
    override fun Binding.onViewCreated(savedInstanceState: Bundle?) {
        // 自动处理安全区域
        initInsetPadding()
        
        // 设置沉浸式
        setImmersiveSystemBar(true)
    }
    
    override fun getStatusBarColor() = Color.TRANSPARENT
}
```
