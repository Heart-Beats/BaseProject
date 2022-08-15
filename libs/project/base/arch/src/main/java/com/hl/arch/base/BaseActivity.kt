package com.hl.arch.base

import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.MutableLiveData
import androidx.palette.graphics.Palette
import com.blankj.utilcode.util.ImageUtils
import com.elvishew.xlog.XLog
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ktx.immersionBar
import com.hl.arch.R
import com.hl.arch.utils.getColorByRes
import com.hl.arch.utils.initInsetPadding
import com.hl.arch.utils.setSafeValue
import com.hl.arch.utils.traverseFindFirstViewByType


abstract class BaseActivity : AppCompatActivity() {

    private val TAG = "BaseActivity"

    val touchEvent by lazy {
        MutableLiveData<MotionEvent>()
    }

    abstract val layoutResId: Int?

    @JvmField
    protected var toolbar: Toolbar? = null

    protected lateinit var immersionBar: ImmersionBar

    abstract fun onViewCreated(savedInstanceState: Bundle?)

    /**
     * 该方法在 ViewBindingBaseActivity 中使用，确保 super.onCreate(savedInstanceState) 之后填充 ViewBinding 的视图
     */
    protected open fun getViewBindingLayoutView(): View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate:  =====> $this")

        layoutResId?.let {
            val inflateView = View.inflate(this, it, null)
            toolbar = inflateView?.traverseFindFirstViewByType(Toolbar::class.java)?.apply {
                // xml 中通过 style 可统一配置，这里设置会导致 xml 中设置失效
                // this.setTitleTextColor(getColorByRes(R.color.colorTitleText))
                // this.setBackgroundColor(getColorByRes(R.color.colorTitlePrimary))
            }
            setContentView(inflateView)
        }
        getViewBindingLayoutView()?.run {
            setContentView(this)
        }
        updateSystemBar()

        //Activity 创建之后设置toolbar
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

    /**
     * @param percent   透明度
     * @param rgb   RGB值
     * @return 最终设置过透明度的颜色值
     */
    protected open fun getTranslucentColor(percent: Float, rgb: Int): Int {
        val blue = Color.blue(rgb)
        val green = Color.green(rgb)
        val red = Color.red(rgb)
        var alpha = Color.alpha(rgb)
        alpha = Math.round(alpha * percent)
        return Color.argb(alpha, red, green, blue)
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
        initInsetPadding(top = false)
        immersionBar {
            statusBarView(statusBarView)
            statusBarDarkFont(false)
            transparentStatusBar()
            immersionBarBlock()
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