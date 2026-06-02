# UIKit Demo

UIKit 组件库的演示 App，以独立的 Android Application 模块运行，通过菜单-详情两级导航展示 `uikit` 模块提供的全部自定义控件。

## 基本信息

| 属性 | 值 |
|------|-----|
| **模块类型** | Android Application |
| **ApplicationId** | `com.hl.uikit.demo` |
| **包名** | `com.hl.uikit.demo` |
| **依赖** | `:uikit`、`base-ui` |
| **发布** | 不对外发布（仅本地调试用） |

## 运行方式

```shell
# 通过 Gradle 直接运行
./gradlew :uikit:demo:installDebug

# 或在 Android Studio 中
# Run → Edit Configurations → Module: uikit.demo
```

## 源码结构

```
uikit/demo/src/main/java/com/hl/uikit/demo/
├── App.kt                              # Application，初始化 Toast
├── BaseActivity.kt                     # 基础 Activity（透明状态栏、亮色模式）
├── MainActivity.kt                     # 主菜单，RecyclerView 展示分类列表
├── MainMenuAdapter.kt                  # 主菜单 Adapter（一级分类 + 二级子项）
├── FragmentContainerActivity.kt        # 通用 Fragment 容器
├── _ActivityExt.kt                     # Activity 扩展（窗口 insets padding）
├── adapter/
│   ├── ItemInfoAdapter.kt              # 宫格 Adapter（vlayout Grid）
│   └── ViewHolders.kt                  # ViewHolder + ItemBean 数据类
├── dialogs/
│   ├── DialogsFragment.kt              # 对话框演示（AlertDialog/InputDialog）
│   ├── HalfScreenFragment.kt           # 底部弹出菜单演示（ActionSheet）
│   └── PickersFragment.kt              # 选择器演示（单列/多列/联动/日期/时间）
├── fragments/
│   ├── BaseFragment.kt                 # Fragment 基类（layout 属性 + Toolbar 返回）
│   ├── ArticleFragment.kt              # 文章页面
│   ├── BottomTabFragment.kt            # 底部导航栏
│   ├── ButtonsFragment.kt              # 按钮样式演示
│   ├── CommitMsgFragment.kt            # 成功/失败提示页基类
│   ├── CommitSuccessFragment.kt        # 成功提示页
│   ├── CommitFailFragment.kt           # 失败提示页
│   ├── FontFragment.kt                 # 字体大小展示（40pt → 12pt）
│   ├── GalleryListFragment.kt          # 画廊列表（Grid + Glide）
│   ├── GridFragment.kt                 # 宫格布局（vlayout）
│   ├── IconsFragment.kt               # 图标展示
│   ├── LabelTabFragment.kt             # 标签导航
│   ├── NavigationBarFragment.kt        # 导航栏样式演示
│   ├── NoticeFragment.kt               # 通告栏
│   ├── PageFooterFragment.kt           # 页脚
│   ├── PromptPageFragment.kt           # 提示页入口
│   ├── RedBadgeFragment.kt             # 红点徽标演示
│   ├── SearchBarFragment.kt            # 搜索栏演示（字符串搜索/自定义对象搜索）
│   ├── SearchFragment.kt               # 搜索入口页
│   ├── SearchViewFragment.kt           # 固定搜索页
│   ├── SlideViewFragment.kt            # 左滑操作菜单
│   ├── TabFragment.kt                  # 标签页
│   ├── ToastFragment.kt                # 轻提示演示（toast/success/failure）
│   ├── colors/
│   │   ├── ColorsFragment.kt           # 颜色入口
│   │   ├── HLDColorFragment.kt         # 慧徕店主题色
│   │   └── EMSColorFragment.kt         # 邮政主题色
│   └── forms/
│       ├── FormsMainFragment.kt        # 表单入口页
│       ├── FormListFragment.kt         # 列表展示
│       ├── FormStructFragment.kt       # 表单结构（选择器/单选切换）
│       ├── FormTextInputFragment.kt    # 文本域输入（字数统计）
│       ├── FormStepperFragment.kt      # 步进器（整数/浮点数范围，禁用状态）
│       ├── FormImageFragment.kt        # 上传图片
│       ├── FormRegisterFragment.kt     # 验证码输入
│       └── FormToggleButtonFragment.kt # 开关按钮
├── gallery/
│   ├── GalleryImageInfo.kt             # 图片信息数据类
│   ├── GalleryImageLoader.kt           # 图片加载器
│   └── SimpleThumbViewInfo.kt          # 缩略图信息 Parcelable
├── loading/
│   ├── LoadingsFragment.kt             # 加载演示（LoadingDialog + SmartRefreshLayout）
│   └── WebViewFragment.kt              # WebView 加载（标题/进度条/返回拦截）
└── util/
    ├── _DensityUtils.kt                # dp/sp → px 扩展
    ├── StatusbarColorUtils.java        # 魅族状态栏颜色工具
    └── StatusBarUtil.kt                # 通用状态栏适配（MIUI/Flyme/Android 6+）
```

## 导航架构

```
MainActivity (RecyclerView 一级+二级菜单)
    │
    ├── 表单
    │   ├── FormStructFragment      → FragmentContainerActivity
    │   ├── FormListFragment        → FragmentContainerActivity
    │   ├── FormRegisterFragment    → FragmentContainerActivity
    │   ├── FormTextInputFragment   → FragmentContainerActivity
    │   ├── FormToggleButtonFragment → FragmentContainerActivity
    │   ├── FormImageFragment       → FragmentContainerActivity
    │   └── FormStepperFragment     → FragmentContainerActivity
    │
    ├── 基础组件
    │   ├── FontFragment            → FragmentContainerActivity
    │   ├── ColorsFragment          → FragmentContainerActivity
    │   ├── IconsFragment           → FragmentContainerActivity
    │   └── ButtonsFragment         → FragmentContainerActivity
    │
    ├── 操作反馈
    │   ├── DialogsFragment         → FragmentContainerActivity
    │   ├── HalfScreenFragment      → FragmentContainerActivity
    │   ├── PickersFragment         → FragmentContainerActivity
    │   ├── ToastFragment           → FragmentContainerActivity
    │   ├── LoadingsFragment        → FragmentContainerActivity
    │   ├── CommitSuccessFragment   → FragmentContainerActivity
    │   └── CommitFailFragment      → FragmentContainerActivity
    │
    ├── 导航组件
    │   ├── NavigationBarFragment   → FragmentContainerActivity
    │   ├── TabFragment             → FragmentContainerActivity
    │   ├── LabelTabFragment        → FragmentContainerActivity
    │   ├── BottomTabFragment       → FragmentContainerActivity
    │   ├── PageFooterFragment      → FragmentContainerActivity
    │   └── GridFragment            → FragmentContainerActivity
    │
    ├── 搜索相关
    │   ├── SearchBarFragment       → FragmentContainerActivity
    │   └── SearchViewFragment      → FragmentContainerActivity
    │
    └── 引导提示
        ├── NoticeFragment          → FragmentContainerActivity
        └── PromptPageFragment      → FragmentContainerActivity
```

## MainMenuAdapter 数据模型

```kotlin
// 一级菜单（分类）
data class FirstMenu(val id: Int, val name: String, val subMenus: List<SecondMenu>)

// 二级菜单（具体组件项）
data class SecondMenu(val id: Int, val name: String, val targetFragmentCls: Class<out Fragment>)
```

## FragmentContainerActivity 跳转机制

```kotlin
// 通过扩展函数启动任意 Fragment
fun Context.startFragment(fragmentCls: Class<out Fragment>, title: String = "")

// FragmentContainerActivity 通过 Intent 接收类名，反射实例化 Fragment
val fragment = javaClass.classLoader!!
    .loadClass(intent.getStringExtra("fragmentClassName"))
    .newInstance() as Fragment
```

## BaseActivity 配置

```kotlin
open class BaseActivity : AppCompatActivity() {
    // 透明状态栏 + 亮色状态栏图标 + 白色状态栏背景
    init {
        StatusBarUtil.setTransparentStatusBar(this)
        setStatusBarLightMode(this)
        StatusBarUtil.setStatusBarColor(this, Color.WHITE)
    }
}
```

## 主要依赖

| 依赖 | 说明 |
|------|------|
| `:uikit` | UIKit 核心组件库（project 依赖） |
| `base-ui` | 基础 UI 架构（Composite Build 依赖） |
| `vlayout` | 阿里 vlayout 复杂布局框架（宫格展示） |
| `Glide` | 图片加载（画廊列表） |
| `SmartRefreshLayout` | 下拉刷新/上拉加载 |
| `material` | Material Design 组件 |
| `appcompat` | AndroidX AppCompat |
| `lifecycle-*` | LiveData / ViewModel / Lifecycle |
| `kotlinx-coroutines` | 协程支持 |
| `systembartint` | 状态栏着色 |

## 构建配置要点

- 使用 `com.kanyun.kace` Gradle 插件
- 签名使用 `../keystore.jks`（密码 `123456`），仅用于本地调试
- `compileOptions` / `kotlinOptions` 均为 Java 8
- 所有 Fragment 布局文件约 35 个，放置在 `res/layout/`
