# base-rv-adapter — RecyclerView 适配器模块

## 模块概述

`base-rv-adapter` 提供了两套完整的 RecyclerView 适配器框架：传统的 ViewHolder 模式和基于 ViewBinding 的现代模式。支持单类型/多类型列表、空视图、Header/Footer、DiffUtil 增量更新、拖拽排序等高级特性。

**模块坐标**: `com.hl.rvadapter`
**发布 GroupId**: `io.github.heart-beats.baseproject`

## 目录结构

```
rv-adapter/
├── build.gradle.kts
└── src/main/java/com/hl/rvadapter/
    ├── IDataOperate.kt                    # 数据操作接口（增删改）
    ├── IDataType.kt                       # 数据类型标识接口
    ├── ItemViewType.kt                    # 视图类型枚举
    ├── binding/                           # ViewBinding 适配器体系
    │   ├── BaseBindingMultiAdapter.kt     # 多类型 ViewBinding 适配器
    │   ├── BaseBindingSingleAdapter.kt    # 单类型 ViewBinding 适配器
    │   ├── itemprovider/
    │   │   ├── BaseBindingItemProvider.kt  # ViewBinding ItemProvider 基类
    │   │   └── impl/
    │   │       ├── DataItemProvider.kt     # 数据项提供者
    │   │       ├── EmptyItemProvider.kt    # 空数据提供者
    │   │       ├── HeaderItemProvider.kt   # 头部提供者
    │   │       └── FooterItemProvider.kt   # 底部提供者
    │   └── viewholder/
    │       ├── BaseBindingViewHolder.kt    # ViewBinding ViewHolder 基类
    │       └── MultiBindingViewHolder.kt   # 多类型 ViewBinding ViewHolder
    ├── normal/                            # 传统 ViewHolder 体系
    │   ├── BaseMultiAdapter.kt            # 多类型适配器
    │   ├── BaseSingleAdapter.kt           # 单类型适配器
    │   ├── itemprovider/
    │   │   ├── BaseItemProvider.kt
    │   │   └── impl/
    │   └── viewholder/
    │       ├── BaseViewHolder.kt
    │       └── MultiViewHolder.kt
    ├── diffcallback/
    │   └── MyDiffCallback.kt              # DiffUtil 回调
    ├── drag/
    │   └── ItemDragCallBack.kt            # 拖拽排序回调
    └── utils/
        ├── _ClickUtil.kt                  # 点击工具
        └── _Color.kt                      # 颜色工具
```

## 依赖关系

### 外部依赖
| 依赖 | 用途 |
|------|------|
| `androidx.recyclerview:recyclerview` | RecyclerView |
| `androidx.core:core-ktx` | Core KTX |
| `androidx.databinding:viewbinding` | ViewBinding 支持 |

### 内部依赖
| 模块 | 关系 |
|------|------|
| `view-binding` | 依赖 —— ViewBinding 工具 |

## 对外接口

### 核心接口

```kotlin
interface IDataType {
    var itemViewType: Int       // 视图类型标识
}
interface IDataOperate<T : IDataType> {
    fun insertData(vararg addData: T)
    fun removeData(vararg removeData: T)
    fun updateData(newData: List<T>)
    fun getData(): List<T>
}
enum class ItemViewType { EMPTY, HEADER, FOOTER, DATA }
```

### ViewBinding 适配器体系

```kotlin
// 多类型适配器
abstract class BaseBindingMultiAdapter<T : IDataType>(adapterData: MutableList<T>)
    : RecyclerView.Adapter<BaseBindingViewHolder<T, ViewBinding>>(), IDataOperate<T> {
    open var emptyView: View?
    abstract fun registerItemProvider(position: Int, itemData: T): BaseBindingItemProvider<out T, out ViewBinding>
    protected fun isDisplayEmpty(): Boolean
    protected fun isNoData(): Boolean
    inline fun modifyDataByCondition(crossinline condition: (T) -> Boolean, crossinline modifyAction: T.() -> Unit)
}

// 单类型适配器
abstract class BaseBindingSingleAdapter<T : IDataType, VB : ViewBinding>(adapterData: MutableList<T>)
    : BaseBindingMultiAdapter<T>(adapterData) {
    open var headerView: View?
    open var footerView: View?
    abstract fun onItemBind(viewHolder: BaseBindingViewHolder<T, VB>, itemData: T)
    open fun onItemBind(helper: BaseBindingViewHolder<T, VB>, itemData: T, payloads: List<Any?>)
    open fun onItemInit(viewHolder: BaseBindingViewHolder<T, VB>)
    open fun onItemClick(itemView: View, position: Int, itemData: T)
    open fun onItemLongClick(itemView: View, position: Int, itemData: T)
}
```

### 传统体系（无 ViewBinding）

```kotlin
abstract class BaseSingleAdapter<T : IDataType>(adapterData: MutableList<T>)
    : BaseMultiAdapter<T>(adapterData) {
    abstract val itemLayout: Int
    abstract fun onItemBind(viewHolder: BaseViewHolder<T>, itemData: T)
    open fun onItemInit(viewHolder: BaseViewHolder<T>)
    open fun onItemClick(itemView: View, position: Int, itemData: T)
    open fun onItemLongClick(itemView: View, position: Int, itemData: T)
}
```

### DiffUtil

```kotlin
class MyDiffCallback<T>(oldList: List<T>, newList: List<T>) : DiffUtil.Callback()
```

### 拖拽排序

```kotlin
class ItemDragCallBack(
    adapter: RecyclerView.Adapter<*>,
    dataList: MutableList<*>
) : ItemTouchHelper.Callback()
```

## 构建与测试

```bash
# 构建模块
./gradlew :base-rv-adapter:assemble

# 发布到本地 Maven
./gradlew :base-rv-adapter:publishToMavenLocal
```

## 使用示例

### 单类型列表（ViewBinding）

```kotlin
// 1. 数据类实现 IDataType
data class User(val name: String, val age: Int) : IDataType {
    override var itemViewType = 0
}

// 2. 创建适配器
class UserAdapter : BaseBindingSingleAdapter<User, ItemUserBinding>(
    adapterData = mutableListOf()
) {
    override fun onItemBind(viewHolder: BaseBindingViewHolder<User, ItemUserBinding>, itemData: User) {
        viewHolder.binding.apply {
            tvName.text = itemData.name
            tvAge.text = "${itemData.age}岁"
        }
    }
    
    override fun onItemClick(itemView: View, position: Int, itemData: User) {
        toast("点击了 ${itemData.name}")
    }
}

// 3. 使用
val adapter = UserAdapter()
recyclerView.adapter = adapter
adapter.updateData(userList)  // 刷新数据
```

### 多类型列表

```kotlin
class MixedAdapter : BaseBindingMultiAdapter<Any>(mutableListOf()) {
    
    override fun registerItemProvider(
        position: Int, itemData: Any
    ): BaseBindingItemProvider<out Any, out ViewBinding> {
        return when (itemData) {
            is HeaderData -> object : BaseBindingItemProvider<HeaderData, ItemHeaderBinding>() {
                override fun createBinding(parent: ViewGroup) = 
                    ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                override fun onBindViewHolder(viewHolder: BaseBindingViewHolder<HeaderData, ItemHeaderBinding>, itemData: HeaderData) {
                    viewHolder.binding.tvTitle.text = itemData.title
                }
            }
            else -> // ...
        }
    }
}
```

### 传统 ViewHolder 模式

```kotlin
class SimpleAdapter(dataList: MutableList<User>) : BaseSingleAdapter<User>(dataList) {
    override val itemLayout = R.layout.item_user
    
    override fun onItemBind(viewHolder: BaseViewHolder<User>, itemData: User) {
        viewHolder.setText(R.id.tv_name, itemData.name)
        viewHolder.setText(R.id.tv_age, "${itemData.age}")
    }
}
```

### DiffUtil 增量更新

```kotlin
// 使用 DiffUtil 更新数据（带动画）
val diffCallback = MyDiffCallback(adapter.getData(), newDataList)
val diffResult = DiffUtil.calculateDiff(diffCallback)
adapter.updateData(newDataList)
diffResult.dispatchUpdatesTo(adapter)
```

### 拖拽排序

```kotlin
val itemTouchHelper = ItemTouchHelper(
    ItemDragCallBack(adapter, adapter.getData() as MutableList<*>)
)
itemTouchHelper.attachToRecyclerView(recyclerView)
```
