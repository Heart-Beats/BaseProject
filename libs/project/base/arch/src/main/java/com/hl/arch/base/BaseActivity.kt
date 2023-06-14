package com.hl.arch.base

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ktx.immersionBar
import com.hl.arch.R
import com.hl.utils.getColorByRes
import com.hl.utils.*


abstract class BaseActivity : AppCompatActivity(), IPageInflate {

    private val TAG = "BaseActivity"

    protected val touchEvent by lazy {
        MutableLiveData<MotionEvent>()
    }

    /**
     *  页面对应的视图 layout Id, 需子类实现
     */
    @get:LayoutRes
    abstract val layoutResId: Int

    @JvmField
    protected var toolbar: Toolbar? = null

    protected var immersionBar: ImmersionBar? = null

    /**
     * 页面视图创建完成
     */
    abstract fun onViewCreated(savedInstanceState: Bundle?)

    override fun getPageInflateView(layoutInflater: LayoutInflater): View {
        return layoutInflater.inflate(layoutResId, null, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate:  =====> $this")

        getPageInflateView(layoutInflater).run {
            //需要注意，这里的 toolbar 必须为 androidx.appcompat.widget.Toolbar
            toolbar = this.traverseFindFirstViewByType(Toolbar::class.java)?.apply {
                // xml 中通过 style 可统一配置，这里设置会导致 xml 中设置失效
                // this.setTitleTextColor(getColorByRes(R.color.colorTitleText))
                // this.setBackgroundColor(getColorByRes(R.color.colorTitlePrimary))
            }
            setContentView(this)
        }

        updateSystemBar()

        /**
         * Activity 创建之后设置toolbar
         *
         * 注意：关联 ActionBar 时， toolbar 在 xml 中设置属性 app:title 为 null 或 "" 设置不生效，
         *      页面的标题会默认 app 名称， 可在 setSupportActionBar 之前调用 toolbar.setTitle("") 方法解决
         */
        setSupportActionBar(toolbar)

        onViewCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: =====> $this ")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart:  =====> $this")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume:  =====> $this")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause:  =====> $this")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop:  =====> $this")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy:  =====> $this")
    }

    /**
     * 旋屏之后，随着 onPause 的执行，onSaveInstanceState 会被执行， Activity 会自动保存当前页面的状态数据
     */
    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        Log.d(TAG, "onSaveInstanceState: $this 保存的数据 == $outState")
    }

    /**
     * 当由于旋屏触发 Activity 再次初始化时，onRestoreInstanceState 会被执行， Activity 会恢复当前页面的状态数据
     *
     * 整个生命周期：onCreate -> onStart -> onResume -> Running 转屏 -> onPause ->
     *                  onSaveInstanceState -> onStop -> onDestroy -> onCreate ->
     *                          onStart -> onRestoreInstanceState -> onResume;
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.d(TAG, "onRestoreInstanceState: $this 恢复时保存的数据 == $savedInstanceState")
        super.onRestoreInstanceState(savedInstanceState)
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

    /**
     * 从 bitmap 中分析修改状态栏对应的颜色以及字体颜色
     *
     * @param bitmap              需要分析的 bitmap
     * @param onPaletteColorParse 分析结果的回调
     */
    protected fun changeStatusBarStyleFromBitmap(bitmap: Bitmap, onPaletteColorParse: OnPaletteColorParse? = null) {
        PaletteUtil.getColorFromBitmap(bitmap) { rgb, bodyTextColor, titleTextColor, isLight ->
            onPaletteColorParse?.invoke(rgb, bodyTextColor, titleTextColor, isLight)
            immersionBar?.apply {
                statusBarColorInt(rgb)
                statusBarDarkFont(isLight)
            }?.init()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            return onNavigationClicked()
        }
        return super.onOptionsItemSelected(item)
    }

    protected open fun onNavigationClicked(): Boolean {
        onBackPressed()
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        touchEvent.setSafeValue(ev)
        handleHideSoftKeyBoard(ev)
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 点击除 EditText 外区域，当前焦点在 ET 上，EditText 取消焦点
     */
    protected open fun handleHideSoftKeyBoard(ev: MotionEvent) {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideKeyboard(v, ev)) {
                v?.clearFocus()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v!!.windowToken, 0)
            }
        }
    }

    private fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val outLocation = IntArray(2)
            v.getLocationInWindow(outLocation)
            val left = outLocation[0]
            val top = outLocation[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }
}