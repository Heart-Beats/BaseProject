package com.hl.arch.mvp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hl.arch.R
import com.hl.arch.ToastUtils
import com.hl.arch.base.BaseActivity

/**
 * 功能：activity基类
 *
 * @author txwang
 * @Version : 1.0
 * @date : 2019.3.5
 */
abstract class MvpBaseActivity<Presenter : MvpBasePresenter<out MvpBaseView>> : BaseActivity(), MvpBaseView {

    protected val TAG = this.javaClass.simpleName
    protected lateinit var menuIV: ImageView

    private var mFraLayoutContent: FrameLayout? = null
    private var mFraLayoutHeadView: View? = null
    private var mRelLayoutBase: RelativeLayout? = null
    private var errorLayout: RelativeLayout? = null

    private var emptyLayout: RelativeLayout? = null
    private var emptyTV: TextView? = null
    private var titleTV: TextView? = null
    private var menuTV: TextView? = null
    private var errorTV: TextView? = null
    private var ivBack: ImageView? = null

    protected var presenter: Presenter? = null

    private var backView: View? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        if (!getData(intent)) {
            ToastUtils.showShort(this, "无数据")
            finish()
            return
        }

        super.onCreate(savedInstanceState)

        presenter = createPresenter()?.also {
            it.detachView(this)
        }

        requestData()
    }

    protected fun setTitleStyle() {}

    protected abstract fun createPresenter(): Presenter?

    protected open fun getData(intent: Intent?): Boolean {
        return true
    }

    override fun dealNetError(code: Int, e: Throwable) {
        showError("错误信息（" + e.message + "），点击刷新")
    }

    protected open fun requestData() {}

    override fun showEmpty(vararg str: String) {
        emptyLayout!!.visibility = View.VISIBLE
        if (str.isNotEmpty()) {
            emptyTV?.text = str[0]
        } else {
            emptyTV!!.text = "空空如也"
        }
    }

    //隐藏空页面
    fun hideEmptyLayout() {
        emptyLayout!!.visibility = View.GONE
    }

    //显示错误布局
    override fun showError(vararg str: String) {
        errorLayout!!.visibility = View.VISIBLE
        if (str.isNotEmpty()) {
            errorTV?.text = str[0]
        }
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(R.layout.activity_base_mvp)
        mFraLayoutContent = findViewById<View>(R.id.fraLayoutContent) as FrameLayout
        LayoutInflater.from(this).inflate(layoutResID, mFraLayoutContent, true)
        initView()
    }

    private fun initView() {
        mRelLayoutBase = findViewById<View>(R.id.relLayoutBase) as RelativeLayout
        mFraLayoutHeadView = findViewById(R.id.fraLayoutHeadView)
        //        mFraLayoutHeadView.setPadding(0,StatusBarUtil.getStatusBarHeight(this),0,StatusBarUtil.getStatusBarHeight(this));
        backView = findViewById(R.id.tv_base_back)
        backView?.setOnClickListener { finish() }
        titleTV = findViewById<View>(R.id.tv_tab_title) as TextView
        titleTV!!.isSelected = true
        menuTV = findViewById<View>(R.id.tv_menu) as TextView
        menuIV = findViewById<View>(R.id.iv_menu) as ImageView
        ivBack = findViewById<View>(R.id.iv_back) as ImageView
        errorLayout = findViewById<View>(R.id.errorLayout) as RelativeLayout
        errorTV = errorLayout!!.findViewById(R.id.reset_textView)
        emptyLayout = findViewById<View>(R.id.emptyLayout) as RelativeLayout
        emptyTV = emptyLayout!!.findViewById(R.id.tvEmtyHit)
        menuTV!!.setOnClickListener { onTvMenuClick() }
        menuIV!!.setOnClickListener { onIvMenuClick() }
        errorLayout!!.setOnClickListener {
            errorLayout!!.visibility = View.GONE
            requestData()
        }
    }

    override fun setContentView(view: View) {
        super.setContentView(R.layout.activity_base_mvp)
        mFraLayoutContent = findViewById<View>(R.id.fraLayoutContent) as FrameLayout
        mFraLayoutContent?.addView(view)
        initView()
    }

    protected open fun onTvMenuClick() {}

    protected open fun onIvMenuClick() {}

    override fun setTitleColor(@ColorInt color: Int) {
        titleTV!!.setTextColor(color)
    }

    //增加title
    fun setTitleString(title: String) {

        mFraLayoutHeadView!!.visibility = View.VISIBLE
        titleTV!!.text = title
    }

    fun setTitleString(title: String, @ColorInt titleColor: Int) {
        setTitleString(title)
        titleTV!!.setTextColor(titleColor)
    }

    //设置 title 背景资源
    fun setTitleBackground(@DrawableRes resId: Int) {
        mFraLayoutHeadView!!.visibility = View.VISIBLE
        mFraLayoutHeadView!!.setBackgroundResource(resId)
    }

    fun setTitleColor(color: String) {
        titleTV!!.setTextColor(Color.parseColor(color))
    }

    fun setTitleSize(size: Float) {
        titleTV!!.textSize = size
    }

    fun setTitleBgResId(@DrawableRes resId: Int) {
        mFraLayoutHeadView!!.visibility = View.VISIBLE
        mFraLayoutHeadView!!.setBackgroundResource(resId)
    }

    fun setMenuColor(@ColorInt color: Int) {
        menuTV!!.setTextColor(color)
    }

    fun setTitleBgColor(color: String) {
        mFraLayoutHeadView!!.visibility = View.VISIBLE
        mFraLayoutHeadView!!.setBackgroundColor(Color.parseColor(color))
    }

    //设置标题栏的颜色，因为base 设置状态栏透明且在标题栏上，标题栏向上填充状态栏的高度，所以也是状态栏的颜色
    fun setTitleBgColor(@ColorRes colorId: Int) {
        val color = ContextCompat.getColor(this, colorId)
        mFraLayoutHeadView!!.visibility = View.VISIBLE
        mFraLayoutHeadView!!.setBackgroundColor(color)
    }

    fun setTitleBgImg(url: String) {
        val customTarget = object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                mFraLayoutHeadView!!.background = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }
        }
        Glide.with(this).load(url).into(customTarget)
    }

    fun setBackResource(resId: Int) {
        ivBack!!.setImageResource(resId)
    }

    //增加title
    fun setTitleVisible(visible: Int) {
        mFraLayoutHeadView!!.visibility = visible
    }

    //隐藏返回按钮
    fun hideBack() {
        backView!!.visibility = View.GONE
    }

    fun setBackClick(click: View.OnClickListener?) {
        backView!!.setOnClickListener(click)
    }

    //增加title
    fun setMenuString(menu: String) {
        mFraLayoutHeadView!!.visibility = View.VISIBLE
        setMenuVisible(true)
        menuTV!!.text = menu
    }

    fun setMenuSrc(id: Int) {
        menuIV!!.setImageResource(id)
        menuIV!!.visibility = View.VISIBLE
    }

    //增加title
    fun setMenuVisible(visible: Boolean) {
        menuTV!!.visibility = if (visible) View.VISIBLE else View.GONE
        menuIV!!.visibility = if (visible) View.VISIBLE else View.GONE
    }

    //隐藏无网络布局
    protected fun hideErrorLayout() {
        errorLayout!!.visibility = View.GONE
    }

    fun getmFraLayoutContent(): View? {
        return mFraLayoutContent
    }

    override fun showMsg(msg: String) {
        ToastUtils.showShort(this, msg)
    }


    override fun getAttachContext() = this
}