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
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.GravityInt
import androidx.appcompat.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.updatePaddingRelative
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
    private var rightActionIcon: ImageButton? = null
    private var rightActionTextView: TextView? = null

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
        setRightActionIcon(icon = mRightActionIconRes)
        setRightActionText(mRightText)

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

    fun setRightActionIcon(resId: Int, listener: (view: View) -> Unit = {}) {
        val drawable = if (resId != 0) {
            ContextCompat.getDrawable(context, resId)
        } else {
            null
        }
        setRightActionIcon(drawable, listener)
    }

    fun setRightActionIcon(icon: Drawable?, listener: (view: View) -> Unit = {}) {
        if (icon != null) {
            if (rightActionIcon == null) {
                val lp = generateDefaultLayoutParams()
                lp.gravity = GravityCompat.END or Gravity.CENTER_VERTICAL
                rightActionIcon = AppCompatImageButton(context, null, 0)
                rightActionIcon?.updatePaddingRelative(end = mRightPaddingEnd)
                val color = mRightActionImageColor
                color?.let { imageColor ->
                    rightActionIcon?.setImageTintList(imageColor)
                }
                rightActionIcon?.layoutParams = lp
                addView(rightActionIcon)
            } else if (rightActionIcon?.parent == null) {
                addView(rightActionIcon)
            }
            rightActionIcon?.setImageDrawable(icon)
            rightActionIcon?.onClick(listener)
        } else if (rightActionIcon != null) {
            removeView(rightActionIcon)
        }
        updatePaddingEnd()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        // rightActionIcon 和 rightActionTextView 是有可能创建出来后没显示（此时 isShown == false），
        // 这时更新 PaddingEnd 会无效, 因此布局时更新 PaddingEnd
        updatePaddingEnd()
    }

    private fun updatePaddingEnd() {
        when {
            rightActionIcon?.isShown == true && rightActionTextView?.isShown == true -> {
                rightActionTextView?.updatePaddingRelative(end = mRightSpacing)
                rightActionIcon?.updatePaddingRelative(end = mRightPaddingEnd)
            }
            rightActionIcon?.isShown == true -> {
                rightActionIcon?.updatePaddingRelative(end = mRightPaddingEnd)
            }
            rightActionTextView?.isShown == true -> {
                rightActionTextView?.updatePaddingRelative(end = mRightPaddingEnd)
            }
        }

        // 加入 isInEditMode 用来控制编辑模式时实时更新 mRightPaddingEnd
        if (isInEditMode) {
            when {
                rightActionIcon != null && rightActionTextView != null -> {
                    rightActionTextView?.updatePaddingRelative(end = mRightSpacing)
                    rightActionIcon?.updatePaddingRelative(end = mRightPaddingEnd)
                }
                rightActionIcon != null -> {
                    rightActionIcon?.updatePaddingRelative(end = mRightPaddingEnd)
                }
                rightActionTextView != null -> {
                    rightActionTextView?.updatePaddingRelative(end = mRightPaddingEnd)
                }
            }
        }
    }

    fun setRightActionListener(listener: (view: View) -> Unit) {
        rightActionTextView?.onClick(listener)
        rightActionIcon?.onClick(listener)
    }

    fun setRightActionText(text: CharSequence?, listener: ((view: View) -> Unit)? = null) {
        if (!text.isNullOrEmpty()) {
            if (rightActionTextView == null) {
                val lp = generateDefaultLayoutParams()
                lp.gravity = GravityCompat.END or Gravity.CENTER_VERTICAL
                rightActionTextView = getTextViewWithParams(lp, 0)
                mRightTextSize?.toFloat()?.let { textSize ->
                    rightActionTextView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                }
                mRightActionTextColor?.let { textColor ->
                    rightActionTextView?.setTextColor(textColor)
                }
                addView(rightActionTextView)
            } else if (rightActionTextView?.parent == null) {
                addView(rightActionTextView)
            }
        } else if (rightActionTextView != null) {
            removeView(rightActionTextView)
        }
        rightActionTextView?.text = text
        listener?.run {
            rightActionTextView?.onClick(this)
        }
        updatePaddingEnd()
    }

    fun setRightActionTextColor(color: Int) {
        mRightActionTextColor = ColorStateList.valueOf(color)
        rightActionTextView?.setTextColor(color)
    }

    fun setRightActionTextColor(color: ColorStateList) {
        mRightActionTextColor = color
        rightActionTextView?.setTextColor(color)
    }

    fun getRightActionTextView(): TextView? {
        return rightActionTextView
    }

    fun getRightActionText(): String {
        return rightActionTextView?.text?.toString() ?: ""
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
                textView.isFocusableInTouchMode = true
                // 重复循环
                textView.marqueeRepeatLimit = -1
                textView?.post {
                    textView.requestFocus()
                }
            }
        }

        textView.layoutParams = params
        return textView
    }

    private fun getTextViewWithParams(
        params: LayoutParams,
        defStyleAttr: Int = android.R.attr.textViewStyle
    ): TextView {
        val textView = AppCompatTextView(context, null, defStyleAttr)
        textView.setSingleLine()
        textView.ellipsize = TextUtils.TruncateAt.MIDDLE
        textView.layoutParams = params
        return textView
    }
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