package com.hl.uikit

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.GravityInt
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.TintTypedArray
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.updatePadding
import com.hl.uikit.search.UIKitSearchBar
import kotlinx.android.synthetic.main.uikit_toolbar_search.view.*
import kotlin.reflect.KProperty1

class UIKitToolbar : Toolbar {
    private var mNavigationIcon: Drawable? = null

    //仅仅在展开
    var searchView: UIKitSearchBar? = null

    /**
     * 居中标题的父级 Layout
     */
    private var centerTitleLayout: LinearLayout? = null

    /**
     * 右边操作的父级 Layout
     */
    private var rightActionLayout: LinearLayout? = null

    /**
     * 右边当前的操作 View 集合
     */
    private var rightActionTextViewMap = hashMapOf<String, TextView>()

    /**
     * 右边的 Text 和 Icon 之间的距离
     */
    private var mRightSpacing: Int = 0
    private var mRightActionIconRes: Drawable? = null
    private var mRightTextSize: Int? = null
    private var mRightText: String? = null

    /**
     * Text 或者 Icon 距离右边的距离
     */
    private var mRightPaddingEnd: Int = 0
    private var mBackgroundColor: Int? = null
    private var mRightActionTextColor: ColorStateList? = null
    private var mRightActionImageColor: ColorStateList? = null

    private var mTitleGravity: Int = Gravity.START

    private var mTitle: CharSequence? = null
    private var tvCenterTitle: TextView? = null
    private var mTitleTextAppearance: Int = 0
    private var mTitleTextSize: Int? = null
    private var mTitleTextColor: ColorStateList? = null
    private var mTitleIsBold: Boolean = false
    private var titleMargin: Int = 15.dpInt
    private var titleEllipsize: TextUtils.TruncateAt = TextUtils.TruncateAt.MIDDLE

    private var mSubTitle: CharSequence? = null
    private var tvCenterSubTitle: TextView? = null
    private var mSubTitleTextAppearance: Int = 0
    private var mSubTitleTextSize: Int? = null
    private var mSubTitleTextColor: ColorStateList? = null
    private var mSubTitleIsBold: Boolean = false

    private var mConfigBuilder: ConfigBuilder<Any>? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        R.attr.uikit_toolbarStyle
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val barTypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.UIKitToolbar, defStyleAttr, 0)
        initNormal(context, attrs, defStyleAttr, barTypedArray)
        barTypedArray.recycle()
    }

    private fun <T> initSearch(inputBuilder: ConfigBuilder<T>? = null) {
        val searchItem = menu.add("搜索")
        navigationIcon = null
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        searchItem.setActionView(R.layout.uikit_toolbar_search)
        searchItem.icon = mRightActionIconRes
        val searchGroup = searchItem.actionView
        searchView = searchGroup.searchView
        val searchAutoComplete =
            searchView?.findViewById<SearchView.SearchAutoComplete>(R.id.search_src_text)
        val tvCancel = searchGroup.tvCancel
        inputBuilder?.let { builder ->
            val imeOptions = builder.imeOptions
            if (imeOptions != null) {
                searchAutoComplete?.imeOptions = imeOptions
            }
            searchView?.queryHint = builder.queryHint
            val queryData = builder.queryData
            if (queryData?.isNotEmpty() == true) {
                searchView?.initSearchBar(
                    queryData,
                    builder.queryProperty,
                    builder.queryCompleteListener
                )
            }
            val closeListener = builder.onQueryCloseListener
            tvCancel.onClick {
                val interceptor = closeListener()
                if (!interceptor) {
                    collapseSearchView()
                }
            }
        }
    }

    /**
     * @param builder SearchView 参数，为 null 时不更新
     */
    fun <T : Any> expandSearchView(builder: ConfigBuilder<T>? = null) {
        if (mConfigBuilder !== builder) {
            mConfigBuilder = builder as ConfigBuilder<Any>?
        }
        mNavigationIcon = navigationIcon
        initSearch(mConfigBuilder)
    }

    fun expandSearchView() {
        expandSearchView<String>()
    }

    fun collapseSearchView() {
        menu.clear()
        navigationIcon = mNavigationIcon
    }

    @SuppressLint("PrivateResource", "RestrictedApi", "CustomViewStyleable")
    private fun initNormal(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int,
        barTypedArray: TypedArray
    ) {
        mBackgroundColor = barTypedArray.getColor(R.styleable.UIKitToolbar_uikit_backgroundColor, Color.WHITE).also {
            setBackgroundColor(it)
        }

        val gravity = barTypedArray.getInt(R.styleable.UIKitToolbar_uikit_titleGravity, 0)

        mTitleGravity = when (gravity) {
            0 -> Gravity.CENTER
            else -> Gravity.START
        }

        val ellipsize = barTypedArray.getInt(R.styleable.UIKitToolbar_uikit_titleEllipsize, 1)
        titleEllipsize = when (ellipsize) {
            0 -> TextUtils.TruncateAt.START
            1 -> TextUtils.TruncateAt.MIDDLE
            2 -> TextUtils.TruncateAt.END
            else -> TextUtils.TruncateAt.MARQUEE
        }

        titleMargin = barTypedArray.getDimensionPixelSize(R.styleable.UIKitToolbar_uikit_titleMargin, titleMargin)

        /******************** 初始化标题  *************/
        val titleSize =
            barTypedArray.getDimensionPixelSize(R.styleable.UIKitToolbar_uikit_titleSize, -1)
        mTitleTextSize = if (titleSize > 0) titleSize else null

        val title = barTypedArray.getString(R.styleable.UIKitToolbar_uikit_title)
        setTitle(title)

        barTypedArray.getColorStateList(R.styleable.UIKitToolbar_uikit_titleColor)?.let {
            setTitleTextColor(it)
        }

        setTitleBold(barTypedArray.getBoolean(R.styleable.UIKitToolbar_uikit_titleIsBold, mTitleIsBold))

        /******************** 初始化副标题  *************/
        val subtitleSize =
            barTypedArray.getDimensionPixelSize(R.styleable.UIKitToolbar_uikit_subtitleSize, -1)
        mSubTitleTextSize = if (subtitleSize > 0) subtitleSize else null

        val subtitle = barTypedArray.getString(R.styleable.UIKitToolbar_uikit_subtitle)
        setSubtitle(subtitle)

        barTypedArray.getColorStateList(R.styleable.UIKitToolbar_uikit_subtitleColor)?.let {
            setSubtitleTextColor(it)
        }

        setSubTitleBold(barTypedArray.getBoolean(R.styleable.UIKitToolbar_uikit_subtitleIsBold, mSubTitleIsBold))

        mRightPaddingEnd =
            barTypedArray.getDimensionPixelSize(R.styleable.UIKitToolbar_uikit_rightPaddingEnd, 0)
        mRightSpacing =
            barTypedArray.getDimensionPixelSize(R.styleable.UIKitToolbar_uikit_rightSpacing, 0)
        mRightText = barTypedArray.getString(R.styleable.UIKitToolbar_uikit_rightText)
        val rightTextSize =
            barTypedArray.getDimensionPixelSize(R.styleable.UIKitToolbar_uikit_rightTextSize, -1)
        mRightTextSize = if (rightTextSize > 0) {
            rightTextSize
        } else {
            null
        }
        mRightActionTextColor =
            barTypedArray.getColorStateList(R.styleable.UIKitToolbar_uikit_rightTextColor)
        mRightActionImageColor =
            barTypedArray.getColorStateList(R.styleable.UIKitToolbar_uikit_rightImageColor)
        mRightActionIconRes = barTypedArray.getDrawable(R.styleable.UIKitToolbar_uikit_rightImage)
        // setRightActionIcon(icon = mRightActionIconRes)
        // setRightActionText(mRightText)
        setRightAction(MenuAction(menuIcon = mRightActionIconRes, menuText = mRightText))

        //  从主题中获取 TextAppearance 相关属性设置
        val ta = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.Toolbar, defStyleAttr, 0)
        mTitleTextAppearance = ta.getResourceId(R.styleable.Toolbar_titleTextAppearance, 0)
        mSubTitleTextAppearance = ta.getResourceId(R.styleable.Toolbar_subtitleTextAppearance, 0)
        if (ta.hasValue(R.styleable.Toolbar_titleTextColor)) {
            setTitleTextColor(ta.getColorStateList(R.styleable.Toolbar_titleTextColor))
        }
        if (ta.hasValue(R.styleable.Toolbar_subtitleTextColor)) {
            setSubtitleTextColor(ta.getColorStateList(R.styleable.Toolbar_subtitleTextColor))
        }
        ta.recycle()
    }

    fun getBackgroundColor(): Int? {
        return mBackgroundColor
    }

    override fun setBackgroundColor(color: Int) {
        super.setBackgroundColor(color)
        mBackgroundColor = color
    }

    override fun setTitle(title: CharSequence?) {
        if (mTitle != null && title == context.getString(context.applicationInfo.labelRes)) {
            // 当标题不为空且设置的标题为 app label 时，不更改标题，
            // 忽略 Fragment 中设置 setSupportActionBar  后 Activity 的 onPostCreate ---> onTitleChanged 对 title 更改的影响
            return
        }

        mTitle = title
        if (isCenterGravity()) {
            setCenterTitle(title)
        } else {
            super.setTitle(title)
        }
    }

    override fun setSubtitle(subtitle: CharSequence?) {
        mSubTitle = subtitle
        if (isCenterGravity()) {
            setCenterSubtitle(subtitle)
        } else {
            super.setSubtitle(subtitle)
        }
    }

    override fun getTitle(): CharSequence? {
        return mTitle
    }

    override fun getSubtitle(): CharSequence? {
        return mSubTitle
    }

    private fun isCenterGravity(): Boolean {
        return mTitleGravity == Gravity.CENTER || mTitleGravity == Gravity.CENTER_HORIZONTAL
    }

    fun setTitleGravity(@GravityInt gravity: Int) {
        mTitleGravity = gravity
    }

    fun setTitleBold(isBold: Boolean) {
        mTitleIsBold = isBold
        if (isCenterGravity() && isBold) {
            tvCenterTitle?.typeface = Typeface.DEFAULT_BOLD
        }
    }

    fun setSubTitleBold(isBold: Boolean) {
        mSubTitleIsBold = isBold
        if (isCenterGravity() && isBold) {
            tvCenterSubTitle?.typeface = Typeface.DEFAULT_BOLD
        }
    }

    override fun setTitleTextColor(color: ColorStateList) {
        mTitleTextColor = color
        if (isCenterGravity()) {
            tvCenterTitle?.setTextColor(color)
        } else {
            super.setTitleTextColor(color)
        }
    }

    override fun setSubtitleTextColor(color: ColorStateList) {
        mSubTitleTextColor = color
        if (isCenterGravity()) {
            tvCenterSubTitle?.setTextColor(color)
        } else {
            super.setSubtitleTextColor(color)
        }
    }

    override fun setTitleTextColor(color: Int) {
        mTitleTextColor = ColorStateList.valueOf(color)
        if (isCenterGravity()) {
            tvCenterTitle?.setTextColor(color)
        } else {
            super.setTitleTextColor(color)
        }
    }

    override fun setSubtitleTextColor(color: Int) {
        mSubTitleTextColor = ColorStateList.valueOf(color)
        if (isCenterGravity()) {
            tvCenterSubTitle?.setTextColor(color)
        } else {
            super.setSubtitleTextColor(color)
        }
    }

    override fun setTitleTextAppearance(context: Context?, resId: Int) {
        mTitleTextAppearance = resId
        if (isCenterGravity()) {
            tvCenterTitle?.setTextAppearance(context, resId)
        } else {
            super.setTitleTextAppearance(context, resId)
        }
    }

    override fun setSubtitleTextAppearance(context: Context?, resId: Int) {
        mSubTitleTextAppearance = resId
        if (isCenterGravity()) {
            tvCenterSubTitle?.setTextAppearance(context, resId)
        } else {
            super.setSubtitleTextAppearance(context, resId)
        }
    }

    /**
     * 设置右边操作按钮的点击时间
     *
     * 注意： 该操作会将通过 addRightAction 添加的操作按钮都移除， 将布局文件中设置的操作按钮重新添加
     */
    fun setRightActionListener(listener: (view: View) -> Unit) {
        setRightAction(MenuAction(mRightActionIconRes, mRightText, listener))
    }

    /**
     * 设置图标形式的操作按钮
     */
    fun setRightActionIcon(resId: Int, listener: (view: View) -> Unit = {}) {
        val drawable = if (resId != 0) {
            ContextCompat.getDrawable(context, resId)
        } else {
            null
        }
        setRightActionIcon(drawable, listener)
    }

    /**
     * 设置图标形式的操作按钮
     */
    fun setRightActionIcon(icon: Drawable?, listener: (view: View) -> Unit = {}) {
        setRightAction(MenuAction(icon, null, listener))
    }

    /**
     * 添加图标形式的操作按钮
     */
    fun addRightActionIcon(resId: Int, listener: (view: View) -> Unit = {}) {
        val drawable = if (resId != 0) {
            ContextCompat.getDrawable(context, resId)
        } else {
            null
        }
        addRightActionIcon(drawable, listener)
    }

    /**
     * 添加图标形式的操作按钮
     */
    fun addRightActionIcon(icon: Drawable?, listener: (view: View) -> Unit = {}) {
        addRightAction(MenuAction(icon, null, listener))
    }


    /**
     * 确保右边操作按钮的父布局存在
     */
    private fun ensureInitRightActionLayout() {
        //view未进行绘制之前手动测量大小
        val width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        val height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        measure(width, height)

        if (rightActionLayout == null) {
            val lp = LayoutParams(LayoutParams.WRAP_CONTENT, measuredHeight).apply {
                // 设置 rightActionLayout 的总高度为 toolbar 高度
                this.gravity = Gravity.END
                this.marginEnd = mRightPaddingEnd
            }
            rightActionLayout = LinearLayout(context).apply {
                this.orientation = LinearLayout.HORIZONTAL
                this.gravity = Gravity.CENTER_VERTICAL
            }
            addView(rightActionLayout, lp)
        } else if (rightActionLayout?.parent == null) {
            addView(rightActionLayout)
        }
    }


    /**
     * 添加右边的操作按钮
     */
    fun addRightAction(menuAction: MenuAction, space: Int = mRightSpacing) {
        ensureInitRightActionLayout()

        val menuText = menuAction.menuText
        val menuIcon = menuAction.menuIcon
        val menuAction = menuAction.menuAction

        val key = menuText?.toString() ?: menuIcon?.hashCode()?.toString() ?: ""

        var rightActionTextView: TextView? = null
        if (!menuText.isNullOrEmpty() || menuIcon != null) {
            if (rightActionTextView == null) {
                val lp = generateDefaultLayoutParams().apply {
                    this.gravity = GravityCompat.END or Gravity.CENTER_VERTICAL

                    if (rightActionLayout?.childCount != 0) {
                        // 已有操作按钮时添加间距
                        this.marginStart = space
                    }
                }
                rightActionTextView = getTextViewWithParams(lp, 0, menuIcon)
                mRightTextSize?.toFloat()?.let { textSize ->
                    rightActionTextView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                }
                mRightActionTextColor?.let { textColor ->
                    rightActionTextView?.setTextColor(textColor)
                }
                rightActionLayout?.addView(rightActionTextView)

                rightActionTextViewMap[key] = rightActionTextView
            } else if (rightActionTextView?.parent == null) {
                rightActionLayout?.addView(rightActionTextView)

                rightActionTextViewMap[key] = rightActionTextView
            }
        } else {
            rightActionLayout?.removeView(rightActionTextView)

            rightActionTextViewMap.remove(key)
        }
        menuText?.run {
            rightActionTextView?.text = this
        }

        menuAction?.run {
            rightActionTextView?.onClick(this)
        }

        rightActionTextView?.post {
            if (menuText != null) {
                val verticalPadding = ((rightActionLayout?.height ?: 0) - rightActionTextView.height) / 2
                rightActionTextView.updatePadding(top = verticalPadding, bottom = verticalPadding)
            } else {
                // 仅有图标时更新 padding 以及显示的大小
                menuIcon?.intrinsicHeight?.run {
                    val verticalPadding = ((rightActionLayout?.height ?: 0) - this) / 2
                    rightActionTextView.height = this + verticalPadding * 2
                    rightActionTextView?.updatePadding(top = verticalPadding, bottom = verticalPadding)
                }
            }
        }

        if (isInEditMode) {
            // 预览模式下当仅有图标，设置 rightActionTextView 与图标等高即居中
            if (menuText == null) {
                menuIcon?.intrinsicHeight?.run {
                    rightActionTextView?.height = this
                }
            }
        }
    }

    /**
     * 设置右边的操作按钮（唯一按钮）
     */
    fun setRightAction(menuAction: MenuAction) {
        ensureInitRightActionLayout()
        rightActionLayout?.removeAllViews()
        rightActionTextViewMap.clear()
        addRightAction(menuAction)
    }

    /**
     * 添加文字描述的操作按钮
     */
    fun addRightActionText(text: CharSequence?, listener: (view: View) -> Unit = {}) {
        addRightAction(MenuAction(null, text, listener))
    }

    /**
     * 设置文字描述的操作按钮
     */
    fun setRightActionText(text: CharSequence?, listener: (view: View) -> Unit = {}) {
        setRightAction(MenuAction(null, text, listener))
    }


    /**
     * 设置操作按钮的文字颜色
     *
     * @param color 颜色
     * @param changeText   需要更改的按钮的文字, 为 null 时更改所有按钮文字颜色
     */
    @JvmOverloads
    fun setRightActionTextColor(color: Int, changeText: String? = null) {
        mRightActionTextColor = ColorStateList.valueOf(color)

        rightActionTextViewMap.forEach { key, rightActionTextView ->
            changeText?.also {
                if (key == changeText) {
                    rightActionTextView?.setTextColor(color)
                }
            } ?: rightActionTextView?.setTextColor(color)
        }
    }

    /**
     * 设置操作按钮的文字颜色
     *
     * @param color 颜色
     * @param changeText   需要更改的按钮的文字, 为 null 时更改所有按钮文字颜色
     */
    @JvmOverloads
    fun setRightActionTextColor(color: ColorStateList, changeText: String? = null) {
        mRightActionTextColor = color

        rightActionTextViewMap.forEach { key, rightActionTextView ->
            changeText?.also {
                if (key == changeText) {
                    rightActionTextView?.setTextColor(color)
                }
            } ?: rightActionTextView?.setTextColor(color)
        }
    }


    /**
     * 获取最右边的操作按钮
     */
    fun getRightActionTextView(): TextView? {
        return getAllRightActionTextViews().lastOrNull()
    }


    /**
     * 获取所有操作按钮的集合
     */
    fun getAllRightActionTextViews(): List<TextView> {
        return rightActionTextViewMap.values.toList()
    }

    /**
     * 获取最右边操作按钮的文字描述
     */
    fun getRightActionText(): String {
        return getAllRightActionTextViews().lastOrNull()?.text?.toString() ?: ""
    }

    /**
     * 获取操作按钮的文字描述集合
     */
    fun getAllRightActionTexts(): List<String> {
        val rightActionTextViews = getAllRightActionTextViews()
        return rightActionTextViews.map { it.text.toString() }
    }

    private fun setCenterTitle(title: CharSequence?) {
        if (!title.isNullOrEmpty()) {
            ensureInitCenterTitleLayout()

            if (tvCenterTitle == null) {
                val lp = generateDefaultLayoutParams().apply {
                    this.marginStart = titleMargin
                    this.marginEnd = titleMargin
                    this.gravity = Gravity.CENTER_HORIZONTAL
                }
                tvCenterTitle = getTitleTextViewWithParams(lp)
                if (mTitleTextAppearance != 0) {
                    tvCenterTitle?.setTextAppearance(context, mTitleTextAppearance)
                }
                mTitleTextColor?.let {
                    setTitleTextColor(it)
                }
                mTitleTextSize?.toFloat()?.let { titleSize ->
                    tvCenterTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize)
                }
                if (mTitleIsBold) {
                    tvCenterTitle?.typeface = Typeface.DEFAULT_BOLD
                }
                centerTitleLayout?.addView(tvCenterTitle, 0)
            } else if (tvCenterTitle?.parent == null) {
                centerTitleLayout?.addView(tvCenterTitle, 0)
            }
        } else if (tvCenterTitle != null) {
            centerTitleLayout?.removeView(tvCenterTitle)
        }
        tvCenterTitle?.text = title
    }

    /**
     * 确保居中标题的父布局存在
     */
    private fun ensureInitCenterTitleLayout() {
        if (centerTitleLayout == null) {
            val lp = generateDefaultLayoutParams().apply {
                this.gravity = Gravity.CENTER
            }
            centerTitleLayout = LinearLayout(context).apply {
                this.orientation = LinearLayout.VERTICAL
                this.gravity = Gravity.CENTER_HORIZONTAL
            }
            addView(centerTitleLayout, lp)
        } else if (centerTitleLayout?.parent == null) {
            addView(centerTitleLayout)
        }
    }

    private fun setCenterSubtitle(subtitle: CharSequence?) {
        if (!subtitle.isNullOrEmpty()) {
            ensureInitCenterTitleLayout()

            if (tvCenterSubTitle == null) {
                val lp = generateDefaultLayoutParams().apply {
                    this.gravity = Gravity.CENTER_HORIZONTAL
                }
                tvCenterSubTitle = getTitleTextViewWithParams(lp, isSubTitle = true)
                if (mSubTitleTextAppearance != 0) {
                    tvCenterSubTitle?.setTextAppearance(context, mSubTitleTextAppearance)
                }
                mSubTitleTextColor?.let {
                    setTitleTextColor(it)
                }
                mSubTitleTextSize?.toFloat()?.let { titleSize ->
                    tvCenterSubTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize)
                }
                if (mSubTitleIsBold) {
                    tvCenterSubTitle?.typeface = Typeface.DEFAULT_BOLD
                }
                centerTitleLayout?.addView(tvCenterSubTitle)
            } else if (tvCenterTitle?.parent == null) {
                centerTitleLayout?.addView(tvCenterSubTitle)
            }
        } else if (tvCenterTitle != null) {
            centerTitleLayout?.removeView(tvCenterSubTitle)
        }
        tvCenterSubTitle?.text = subtitle
    }

    private fun getTitleTextViewWithParams(
        params: LayoutParams,
        defStyleAttr: Int = android.R.attr.textViewStyle,
        isSubTitle: Boolean = false
    ): TextView {
        val textView = AppCompatTextView(context, null, defStyleAttr)
        textView.setSingleLine()

        if (!isSubTitle) {
            textView.ellipsize = titleEllipsize
            if (titleEllipsize == TextUtils.TruncateAt.MARQUEE) {
                textView.isFocusable = true
                // 重复循环
                textView.marqueeRepeatLimit = -1
                // 必须设置这个才有跑马灯效果
                textView.isSelected = true

                // textView.isFocusableInTouchMode = true
                // textView?.post {
                //     textView.requestFocus()
                // }
            }
        }

        textView.layoutParams = params
        return textView
    }

    private fun getTextViewWithParams(
        params: LayoutParams,
        defStyleAttr: Int = android.R.attr.textViewStyle,
        menuIcon: Drawable?
    ): TextView {
        val textView = AppCompatTextView(context, null, defStyleAttr).apply {
            this.gravity = Gravity.CENTER
        }
        textView.setSingleLine()
        textView.ellipsize = TextUtils.TruncateAt.MIDDLE
        textView.layoutParams = params

        if (menuIcon != null) {

            menuIcon?.setBounds(0, 0, menuIcon?.intrinsicWidth ?: 0, menuIcon?.intrinsicHeight ?: 0)
            textView.setCompoundDrawablesRelative(null, menuIcon, null, null)
            textView.compoundDrawableTintList = mRightActionImageColor
            // textView.compoundDrawablePadding = 2.dpInt
        }
        return textView
    }

    data class MenuAction(
        /**
         * 图标
         */
        val menuIcon: Drawable? = null,

        /**
         * 文字
         */
        val menuText: CharSequence? = null,

        /**
         * 点击回调
         */
        val menuAction: (view: View) -> Unit = {}
    )
}

class ConfigBuilder<T> {
    var queryHint: CharSequence? = null
    var queryData: List<T>? = null
    var queryProperty:List<KProperty1<T, String?>?> ? = null
    var queryCompleteListener: ((List<T>) -> Unit) = {}
    var onQueryCloseListener: () -> Boolean = { false }
    /**
     * {@link EditorInfo#imeOptions}
     */
    var imeOptions: Int? = null
}