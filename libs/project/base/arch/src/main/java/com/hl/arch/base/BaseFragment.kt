package com.hl.arch.base

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ktx.immersionBar
import com.hl.arch.R
import com.hl.arch.utils.*

/**
 * @Author  张磊  on  2020/08/28 at 18:35
 * Email: 913305160@qq.com
 */
abstract class BaseFragment : Fragment() {

    companion object {
        private const val TAG = "BaseFragment"
    }

    protected abstract val layoutResId: Int?

    /**
     * 该属性在 ViewBindingBaseFragment 中初始化，因为 ViewBinding 无法获取 layoutResId
     */
    protected var layoutView: View? = null

    @JvmField
    protected var toolbar: Toolbar? = null


    /**
     * Fragment 销毁视图时保存页面数据至  arguments
     */
    protected open fun saveStateToArguments(key: String, saveState: Bundle) {
        requireArguments().putBundle(key, saveState)
    }

    /**
     * Fragment 视图创建时从  arguments 恢复数据
     */
    protected open fun restoreStateFromArguments(key: String): Bundle? {
        return requireArguments().getBundle(key)
    }


    private var saveFragmentInstanceState: SavedState? = null

    /**
     * Fragment 保存状态的方法为空实现，需要子类自己去实现
     *
     * 恢复状态时在 onActivityCreated() 中取出保存的状态数据进行恢复
     *
     * 示例可参考：DialogFragment， 因此 DialogFragment 在旋转屏幕时不会产生状态丢失
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // saveFragmentInstanceState = parentFragmentManager.saveFragmentInstanceState(this)
        Log.d(TAG, "onSaveInstanceState: ${this} 保存的数据 == ${outState}")
    }

    fun setInitialFromSavedState() {
        setInitialSavedState(saveFragmentInstanceState)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach =====> $this")
    }

    /**
     * 该方法在导航回退到当前 Fragment 不会触发，因此可以用来作一次性的数据请求
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate =====> $this")

        // 测试过渡动画
        // val inflater = TransitionInflater.from(requireContext())
        // enterTransition = inflater.inflateTransition(R.transition.fade)
        // exitTransition = inflater.inflateTransition(R.transition.slide_right)
    }

    /**
     * 设置沉浸式状态栏
     *
     * @param statusBarView 作为状态栏填充的 view
     * @param immersionBarBlock 使用 immersionBar 对状态栏的后续设置
     */
    protected fun setStatusBarImmerseFromView(statusBarView: View, immersionBarBlock: ImmersionBar.() -> Unit = {}) {
        initInsetPadding(top = false)
        immersionBar {
            statusBarView(statusBarView)
            statusBarDarkFont(false)
            transparentStatusBar()
            immersionBarBlock()
        }
    }

    protected open fun updateSystemBar() {
        //默认设置根布局上方 padding 为状态栏高度
        initInsetPadding(top = true)

        // 默认状态栏与标题栏同色，深色字体， 导航栏白色深色字体
        immersionBar {
            fitsSystemWindows(false)
            statusBarDarkFont(true)

            statusBarColorInt(getStatusBarColor())
            navigationBarColorInt(getNavigationBarColor())
            navigationBarDarkIcon(true)
            keyboardEnable(true)
        }
    }

    /**
     * 该方法在导航回退到当前 Fragment 时也会触发，因此可用作页面切换时更新数据保持数据最新
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView =====> $this")
        if (isMainPage()) {
            // 为主页面时更新状态栏的设置
            updateSystemBar()
        }

        val inflateView = layoutResId?.let {
            inflater.inflate(it, container, false)
        } ?: layoutView

        toolbar = inflateView?.traverseFindFirstViewByType(Toolbar::class.java)?.apply {
            // xml 中通过 style 可统一配置，这里设置会导致 xml 中设置失效
            // this.setTitleTextColor(getColorByRes(R.color.colorTitleText))
            // this.setBackgroundColor(getColorByRes(R.color.colorTitlePrimary))
        }
        return inflateView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated =====> $this")

        if (isMainPage()) {
            // 仅为主页面时才添加返回事件处理、标题栏的设置
            pageInit()
        }

        val currentDestination = getCurrentDestination()

        Log.d(TAG, "当前 fragment == ${this.javaClass.name},  当前标题栏 ======= ${toolbar?.title}")
        Log.d(TAG, "导航当前所在地 label =========== ${currentDestination?.label}")

        if (currentDestination is FragmentNavigator.Destination) {

            if (this.javaClass.name == currentDestination.className) {
                // 判断当前 fragment 是否为导航当前所在页面

                if (toolbar?.title.isNullOrEmpty()) {
                    //当前页面 无 title 时的处理

                    currentDestination.label = when (currentDestination.className) {
                        // MainHomeFragment::class.java.name -> "首页"
                        // MainMrchFragment::class.java.name -> "商户中心"
                        // MainMeFragment::class.java.name -> "我的"
                        else -> {
                            ""
                        }
                    }
                } else {
                    //当前页面有 title
                    currentDestination.label = toolbar?.title
                }
            }
        }
    }

    /**
     * 判断当前 Fragment 是否为主页面
     */
    protected fun isMainPage(): Boolean {
        return if (isNavigationPage()) {
            Log.d(TAG, "isMainPage =====> ${this.javaClass.simpleName} 为 Navigation 页面")
            // 使用 Navigation 时
            true
        } else {
            // 未使用 Navigation 且为 activity 主页面
            val isActivityMainPage = getNavController() == null && isActivityMainPage()
            if (isActivityMainPage) {
                Log.d(TAG, "isMainPage =====> ${this.javaClass.simpleName} 为未使用 Navigation 的 Activity 页面")
            }

            isActivityMainPage
        }
    }

    private fun pageInit() {
        val requireActivity = requireActivity()
        // 目前状态栏以及导航栏的背景颜色通过 immersionBar 来进行设置
        // requireActivity.window.statusBarColor = getStatusBarColor()
        // requireActivity.window.navigationBarColor = getNavigationBarColor()
        requireActivity.onBackPressedDispatcher.addCallback(
            owner = viewLifecycleOwner,
            onBackPressed = backPressed()
        )

        //Activity 创建之后设置toolbar
        val appCompatActivity = requireActivity as? AppCompatActivity
        appCompatActivity?.setSupportActionBar(toolbar)
    }

    /**
     *   在未使用 Navigation 的 Activity 中， 指示当前 Fragment 是否为 Activity 整个页面 , 默认： true
     *
     *  @return true: 当作页面处理，添加返回事件、标题栏以及系统状态栏的设置
     *          false：当作页面中的子视图， 不添加以上处理
     *
     *         注意：fragment 为 页面子视图时，必须重写返回 false , 否则会导致无法返回页面
     */
    protected open fun isActivityMainPage() = true


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated =====> $this")
    }

    /**
     * 该方法可重写更改状态栏颜色, 默认与标题栏保持同色
     */
    protected open fun getStatusBarColor(): Int {
        return getColorByRes(R.color.colorTitlePrimary)
    }

    /**
     * 该方法可重写更改导航条/导航栏颜色
     */
    protected open fun getNavigationBarColor(): Int {
        return Color.TRANSPARENT
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart =====> $this")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume =====> $this")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause =====> $this")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop =====> $this")
    }


    /**
     * Fragment中的布局被移除时调用
     *
     *  当使用 ViewPager + Fragment 时， 滑动的位置超过 ViewPager 的缓存时会触发 onDestroyView() 但不会触发 onDestroy()
     *  因此此种场景可以在这里保存状态数据， 在 onCreateView() 及之后恢复状态
     */
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView =====> $this")

        // 页面销毁时关闭软键盘
        hideSoftInput(requireView())
    }

    override fun onDestroy() {

        // dismissLoading()
        super.onDestroy()
        Log.d(TAG, "onDestroy =====> $this")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach =====> $this")
    }

    private fun hideSoftInput(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun backPressed(): OnBackPressedCallback.() -> Unit = {
        onBackPressed()
    }

    protected open fun onBackPressed() {
        try {
            if (isNavigationPage()) {
                findNavController().popBackStack()
            } else {
                // 此时为为使用 Navigation 的 Activity 的主页面
                if (isMainPage()) {
                    requireActivity().finish()
                }
            }
        } catch (e: Exception) {
            requireActivity().finish()
        }
    }


    /**
     * 判断当前 Fragment 是否为 Navigation 导航中的页面
     */
    protected fun isNavigationPage(): Boolean {
        val currentDestination = getCurrentDestination()
        // 判断目的地为 fragment  且当前 fragment 为导航当前所在页面
        return currentDestination is FragmentNavigator.Destination && this.javaClass.name == currentDestination.className
    }
}