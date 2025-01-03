package com.hl.ui.base

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
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ktx.immersionBar
import com.hl.ui.utils.initInsetPadding
import com.hl.ui.utils.traverseFindFirstViewByType

/**
 * @Author  张磊  on  2020/08/28 at 18:35
 * Email: 913305160@qq.com
 */
abstract class BaseFragment : Fragment(), IPageInflate {

    companion object {
        private const val TAG = "BaseFragment"
    }

    /**
     *  页面对应的视图 layout Id, 需子类实现
     */
    @get:LayoutRes
    protected abstract val layoutResId: Int

    var toolbar: Toolbar? = null
        private set

    protected var immersionBar: ImmersionBar? = null

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
        Log.d(TAG, "onSaveInstanceState: $this 保存的数据 == $outState")
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
        Log.d(TAG, "setStatusBarImmerseFromView =====> 开始设置沉浸式状态栏， statusBarView == $statusBarView")

        initInsetPadding(top = false)

        immersionBar?.apply {
            statusBarView(statusBarView)
            statusBarDarkFont(false)
            transparentStatusBar()
            immersionBarBlock()
        }?.init()
    }

    protected open fun updateSystemBar() {
        Log.d(TAG, "updateSystemBar =====> $this, 更新状态栏为默认配置")

        //默认设置根布局上方 padding 为状态栏高度
        initInsetPadding(top = true)

        // 默认状态栏与标题栏同色，深色字体， 导航栏白色深色字体
        immersionBar {
            immersionBar = this

            fitsSystemWindows(false)
            statusBarDarkFont(true)

            statusBarColorInt(getStatusBarColor())
            navigationBarColorInt(getNavigationBarColor())
            navigationBarDarkIcon(true)
            keyboardEnable(true)
        }
    }

    override fun getPageInflateView(layoutInflater: LayoutInflater): View {
        return layoutInflater.inflate(layoutResId, null, false)
    }

    /**
     * 该方法在导航回退到当前 Fragment 时也会触发，因此可用作页面切换时更新数据保持数据最新
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView =====> $this")

        val inflateView = getPageInflateView(inflater)

        //需要注意，这里的 toolbar 必须为 androidx.appcompat.widget.Toolbar
        toolbar = inflateView.traverseFindFirstViewByType(Toolbar::class.java)?.apply {
            // xml 中通过 style 可统一配置，这里设置会导致 xml 中设置失效
            // this.setTitleTextColor(getColorByRes(R.color.colorTitleText))
            // this.setBackgroundColor(getColorByRes(R.color.colorTitlePrimary))
        }

        if (isMainPage()) {
            // 为主页面时更新状态栏的设置
            updateSystemBar()
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
    }

    /**
     * 判断当前 Fragment 是否为主页面
     */
    protected open fun isMainPage(): Boolean {
        val isActivityMainPage = isActivityMainPage()

        if (isActivityMainPage) {
            Log.d(TAG, "isMainPage =====>  ${this.javaClass.simpleName} 为 Activity 主页面")
        } else {
            Log.d(TAG, "isMainPage =====>  ${this.javaClass.simpleName} 非 Activity 主页面")
        }

        return isActivityMainPage
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

        val appCompatActivity = requireActivity as? AppCompatActivity

        /**
         * Activity 创建之后设置 toolbar
         *
         * 注意：关联 ActionBar 时， toolbar 在 xml 中设置属性 app:title 为 null 或 "" 设置不生效，
         *      页面的标题会默认 app 名称， 可在 setSupportActionBar 之前调用 toolbar.setTitle("") 方法解决
         */
        appCompatActivity?.setSupportActionBar(toolbar)
    }

    /**
     *   在未使用 Navigation 的 Activity 中， 指示当前 Fragment 是否为 Activity 整个页面
     *      当 Fragment 页面中存在 androidx.appcompat.widget.Toolbar 时可认为正常页面，此时返回 true
     *
     *  @return true: 当作页面处理，添加返回事件、标题栏以及系统状态栏的设置
     *          false：当作页面中的子视图， 不添加以上处理
     *
     *         注意：fragment 为 页面子视图时，必须重写返回 false , 否则会导致无法返回页面
     */
    open fun isActivityMainPage() = toolbar != null


    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated =====> $this")
    }

    /**
     * 该方法可重写更改状态栏颜色, 默认与标题栏保持同色
     */
    protected open fun getStatusBarColor(): Int {
        return Color.WHITE
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

    /**
     * 采用 add hide 方式对 fragment 进行显示控制时， 该方法会回调
     *
     * @param hidden  true: 隐藏   false:  显示
     */
    override fun onHiddenChanged(hidden: Boolean) {
        if (hidden) {
            onFragmentHide()
        } else {
            onFragmentShow()
        }
    }

    /**
     *  fragment Show 时， 该方法会回调
     */
    protected open fun onFragmentShow() {
        Log.d(TAG, "onFragmentShow  =========> $this")
    }

    /**
     * fragment Hide 时， 该方法会回调
     */
    protected open fun onFragmentHide() {
        Log.d(TAG, "onFragmentHide  =========> $this")
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
            if (isMainPage()) {
                requireActivity().finish()
            }
        } catch (e: Exception) {
            requireActivity().finish()
        }
    }
}