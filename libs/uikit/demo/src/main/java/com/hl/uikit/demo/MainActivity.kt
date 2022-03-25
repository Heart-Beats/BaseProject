package com.hl.uikit.demo

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.hl.uikit.demo.dialogs.DialogsFragment
import com.hl.uikit.demo.dialogs.HalfScreenFragment
import com.hl.uikit.demo.dialogs.PickersFragment
import com.hl.uikit.demo.fragments.*
import com.hl.uikit.demo.fragments.colors.ColorsFragment
import com.hl.uikit.demo.fragments.forms.FormListFragment
import com.hl.uikit.demo.fragments.forms.FormsMainFragment
import com.hl.uikit.demo.loading.LoadingsFragment
import com.hl.uikit.demo.loading.WebViewFragment
import com.hl.uikit.filter.FilterDialogFragment
import com.hl.uikit.filter.FilterOption
import com.hl.uikit.filter.FilterOptions
import com.hl.uikit.filter.FiltraceKindBean
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    private lateinit var mAdapter: MainMenuAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar?.setNavigationOnClickListener {
            Toast.makeText(this, "返回", Toast.LENGTH_SHORT).show()
        }
        recyclerView?.layoutManager = LinearLayoutManager(this)
        val formMenus = listOf(
            SecondMenu("按钮 Button") {
                startFragment(ButtonsFragment::class.java)
            },
            SecondMenu("表单页 Form") {
                startFragment(FormsMainFragment::class.java)
            },
            SecondMenu("列表 List") {
                startFragment(FormListFragment::class.java)
            },
            SecondMenu("左滑操作 SlideView") {
                startFragment(SlideViewFragment::class.java)
            },
            SecondMenu("选择器 Picker") {
                startFragment(PickersFragment::class.java)
            }
        )
        val basicMenus = listOf(
            SecondMenu("颜色 Color") {
                startFragment(ColorsFragment::class.java)
            },
            SecondMenu("字体 Font") {
                startFragment(FontFragment::class.java)
            },
            SecondMenu("图标 Icon") {
                startFragment(IconsFragment::class.java)
            },
            SecondMenu("文章 Article") {
                startFragment(WebViewFragment::class.java, Bundle().apply {
                    putString(
                        WebViewFragment.KEY_URL,
                        "https://shanhao-app-1259195890.cos.ap-shanghai.myqcloud.com/app-resources/uikit/article.html"
                    )
                })
            },
            SecondMenu("徽标 Badge") {
                startFragment(RedBadgeFragment::class.java)
            },
            SecondMenu("页脚 Footer") {
                startFragment(PageFooterFragment::class.java)
            },
            SecondMenu("画廊 Gallery") {
                startFragment(GalleryListFragment::class.java)
//                val rect = Rect()
//                it.getGlobalVisibleRect(rect)
//                GPreviewBuilder.from(this)
//                    .setData(
//                        listOf(
//                            "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2977879458,598022109&fm=26&gp=0.jpg".toGalleryImage(rect),
//                            "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2033652965,2723161418&fm=26&gp=0.jpg".toGalleryImage(rect),
//                            "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1175572731,3969597227&fm=26&gp=0.jpg".toGalleryImage(rect),
//                            "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=160379341,1357943093&fm=26&gp=0.jpg".toGalleryImage(rect)
//                        )
//                    )
//                    .setCurrentIndex(0)
//                    .setSingleFling(true)
//                    .setDrag(false)
//                    .setType(GPreviewBuilder.IndicatorType.Number)
//                    .start()
            },
            SecondMenu("宫格 Grid") {
                startFragment(GridFragment::class.java)
            }
        )
        val optionMenus = listOf(
            SecondMenu("对话框 Dialog") {
                startFragment(DialogsFragment::class.java)
            },
            SecondMenu("弹出式菜单 ActionSheet") {
                startFragment(HalfScreenFragment::class.java)
            },
            SecondMenu("选项对话框") {

                val filterOptions = FilterOptions().apply {
                    this.options = mutableListOf(FilterOption().apply {
                        this.titleStr = "测试选项"
                        this.optionType = 0
                        this.filterBeans = mutableListOf(
                            FiltraceKindBean().apply {
                                this.text = " 测试选项1"
                                this.filterType = 0
                            },
                            FiltraceKindBean().apply {
                                this.text = " 测试选项2"
                                this.filterType = 0
                            },
                            FiltraceKindBean().apply {
                                this.text = " 测试选项3"
                                this.filterType = 0
                                this.isCheck = true
                            },
                            FiltraceKindBean().apply {
                                this.text = " 测试选项4"
                                this.filterType = 0
                            },
                        )
                    })
                }
                FilterDialogFragment(filterOptions)
                    .show(supportFragmentManager, "")
            },
            SecondMenu("提示页 Msg") {
                startFragment(PromptPageFragment::class.java)
            },
            SecondMenu("轻提示 Toast") {
                startFragment(ToastFragment::class.java)
            },
            SecondMenu("加载 Loading") {
                startFragment(LoadingsFragment::class.java)
            }
        )
        val navigationMenus = listOf(
            SecondMenu("导航栏 NavigationBar") {
                startFragment(NavigationBarFragment::class.java)
            },
            SecondMenu("底部标签栏 TabBar") {
                startFragment(BottomTabFragment::class.java)
            },
            SecondMenu("标签页 Tabs") {
                startFragment(TabFragment::class.java)
            }
        )
        val searchMenus = listOf(
            SecondMenu("搜索栏 SearchBar") {
                startFragment(SearchFragment::class.java)
            }
        )
        val guideMenus = listOf(
            SecondMenu("通告栏 NoticeBar") {
                startFragment(NoticeFragment::class.java)
            },
            SecondMenu("标签 Tag") {
                startFragment(LabelTabFragment::class.java)
            }
        )
        val formMenu = FirstMenu(
            "表单", R.drawable.icon_menu_form, nextMenus = formMenus
        )
        val basicMenu = FirstMenu(
            "基础组件", R.drawable.icon_menu_basic, nextMenus = basicMenus
        )
        val options = FirstMenu(
            "操作反馈", R.drawable.icon_menu_options, nextMenus = optionMenus
        )
        val navigationMenu = FirstMenu(
            "导航组件", R.drawable.icon_menu_navigations, nextMenus = navigationMenus
        )
        val searchMenu = FirstMenu(
            "搜索相关", R.drawable.icon_menu_search, nextMenus = searchMenus
        )
        val guideMenu = FirstMenu(
            "引导提示", R.drawable.icon_menu_guide, nextMenus = guideMenus
        )
        mAdapter = MainMenuAdapter(
            listOf(formMenu, basicMenu, options, navigationMenu, searchMenu, guideMenu)
        )
        recyclerView?.adapter = mAdapter
    }
}
