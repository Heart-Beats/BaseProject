package com.example.zhanglei.myapplication.activities.base

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ktx.immersionBar
import com.hl.arch.R
import com.hl.arch.utils.initInsetPadding
import com.hl.arch.utils.setSafeValue

abstract class BaseActivity : AppCompatActivity() {

    private val TAG = "BaseActivity"

    val touchEvent by lazy {
        MutableLiveData<MotionEvent>()
    }

    abstract val layoutResId: Int?

    abstract fun onViewCreated(savedInstanceState: Bundle?)

    /**
     * 该方法在 ViewBindingBaseActivity 中使用，确保 super.onCreate(savedInstanceState) 之后填充 ViewBinding 的视图
     */
    protected open fun getViewBindingLayoutView(): View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutResId?.run {
            setContentView(this)
        }
        getViewBindingLayoutView()?.run {
            setContentView(this)
        }
        updateSystemBar()

        onViewCreated(savedInstanceState)
    }


    /**
     * 旋屏之后，随着 onPause 的执行，onSaveInstanceState 会被执行， Activity 会自动保存当前页面的状态数据
     */
    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        Log.d(TAG, "onSaveInstanceState: ${this} 保存的数据 == ${outState}")
    }

    /**
     * 当由于旋屏触发 Activity 再次初始化时，onRestoreInstanceState 会被执行， Activity 会恢复当前页面的状态数据
     *
     * 整个生命周期：onCreate -> onStart -> onResume -> Running 转屏 -> onPause ->
     *                  onSaveInstanceState -> onStop -> onDestroy -> onCreate ->
     *                          onStart -> onRestoreInstanceState -> onResume;
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.d(TAG, "onRestoreInstanceState: ${this} 恢复时保存的数据 == ${savedInstanceState}")
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun updateSystemBar() {
        //默认设置根布局上方 padding 为状态栏高度
        initInsetPadding(top = true)

        // 默认状态栏透明，深色字体， 导航栏白色深色字体
        immersionBar {
            fitsSystemWindows(false)
            statusBarDarkFont(true)
            navigationBarColor(R.color.white)
            navigationBarDarkIcon(true)
        }
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


    protected fun Context.startActivity(clazz: Class<*>) {
        startActivity(Intent(this, clazz))
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
    private fun handleHideSoftKeyBoard(ev: MotionEvent) {
        val v = currentFocus
        if (v != null && v is EditText) {
            val outRect = Rect()
            v.getGlobalVisibleRect(outRect)
            if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                v.clearFocus()
                val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
    }
}