# ViewBinding — ViewBinding 工具模块

## 模块概述

`view-binding` 提供 ViewBinding 的便捷创建与获取工具，支持泛型自动推导绑定类型，简化 Activity/Fragment/ViewHolder/ViewGroup 中的 ViewBinding 使用。

**模块坐标**: `com.hl.viewbinding`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
ViewBinding/
├── build.gradle.kts
└── src/main/java/com/hl/viewbinding/
    ├── ViewBindingUtil.kt       # 核心工具
    ├── _Activity.kt             # Activity 绑定扩展
    ├── _Fragment.kt             # Fragment 绑定扩展
    ├── _View.kt                 # View 绑定扩展
    ├── _ViewGroup.kt            # ViewGroup 绑定扩展
    └── _ViewHolder.kt           # ViewHolder 绑定扩展
```

## 依赖关系

| 外部 | `androidx.databinding:viewbinding`, `androidx.fragment:fragment-ktx` |

## 对外接口

```kotlin
// ViewBindingUtil 核心方法
object ViewBindingUtil {
    @JvmStatic inline fun <reified VB : ViewBinding> inflateWithGeneric(
        lifecycleOwner: LifecycleOwner, inflater: LayoutInflater
    ): VB
    @JvmStatic inline fun <reified VB : ViewBinding> bindWithGeneric(
        lifecycleOwner: LifecycleOwner, view: View
    ): VB
}

// Activity 扩展（自动管理 setContentView）
inline fun <reified VB : ViewBinding> ComponentActivity.binding(setContentView: Boolean = true): Lazy<VB>

// Fragment 扩展
inline fun <reified VB : ViewBinding> Fragment.inflate(method: Method = Method.BIND): ReadOnlyProperty<Fragment, VB>
inline fun <reified VB : ViewBinding> Fragment.binding(): ReadOnlyProperty<Fragment, VB>

// View/ViewGroup 扩展
inline fun <reified VB : ViewBinding> View.getBinding(): VB
inline fun <reified VB : ViewBinding> ViewGroup.binding(): VB  // attachToParent = true
inline fun <reified VB : ViewBinding> ViewGroup.inflate(): Lazy<VB>

// ViewHolder 扩展
inline fun <reified VB : ViewBinding> RecyclerView.ViewHolder.withBinding(block: VB.(ViewHolder) -> Unit)

// 辅助类
class BindingViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)
fun <VB : ViewBinding> BindingViewHolder(parent: ViewGroup): BindingViewHolder<VB>
```

## 构建与测试

```bash
./gradlew :view-binding:assemble
./gradlew :view-binding:publishToMavenLocal
```

## 使用示例

```kotlin
// Activity 中使用
class MyActivity : ComponentActivity() {
    private val binding by binding<ActivityMyBinding>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.tvTitle.text = "Hello"
    }
}

// Fragment 中使用
class MyFragment : Fragment() {
    private val binding by inflate<FragmentMyBinding>()
}

// ViewHolder 中使用
class VH(parent: ViewGroup) : BindingViewHolder<ItemUserBinding>(parent) {
    fun bind(user: User) {
        binding.tvName.text = user.name
    }
}
```
