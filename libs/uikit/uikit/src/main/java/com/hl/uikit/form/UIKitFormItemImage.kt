package com.hl.uikit.form

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import androidx.core.view.updateLayoutParams
import com.hl.uikit.R
import com.hl.uikit.databinding.UikitLayoutFormImageBinding
import com.hl.uikit.utils.dp
import com.hl.uikit.utils.onClick
import com.hl.uikit.utils.sp
import com.hl.viewbinding.bindingMerge

class UIKitFormItemImage : UIKitFormItemView {

    private lateinit var viewBinding: UikitLayoutFormImageBinding

    var holderImageId: Int = -1
        private set

    private var mImageClickListener: (view: View) -> Unit = {}

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @SuppressLint("CustomViewStyleable")
    private fun init(context: Context, attrs: AttributeSet? = null) {
        orientation = VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL

        viewBinding = bindingMerge()

        val ta = context.obtainStyledAttributes(attrs, R.styleable.UIKitFormItemImage)
        holderImageId = ta.getResourceId(R.styleable.UIKitFormItemImage_uikit_formImage, -1)
        val drawable = ta.getDrawable(R.styleable.UIKitFormItemImage_uikit_formImage)
        setImageDrawable(drawable)
        viewBinding.image.onClick {
            if (it.isShown) {
                mImageClickListener(it)
            }
        }
        val imageText = ta.getString(R.styleable.UIKitFormItemImage_uikit_formImageText)
        val textSize = ta.getDimension(
            R.styleable.UIKitFormItemImage_uikit_formImageTextSize,
            12f.sp
        )
        val marginVertical = ta.getDimension(
            R.styleable.UIKitFormItemImage_uikit_formImageMarginVertical,
            10f.dp
        ).toInt()
        setImageText(imageText)
        viewBinding.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        viewBinding.text.updateLayoutParams<MarginLayoutParams> {
            topMargin = marginVertical
        }
        val imageTextColor = ta.getColorStateList(R.styleable.UIKitFormItemImage_uikit_formImageTextColor)
            ?: ColorStateList.valueOf(Color.BLACK)
        setImageTextColor(imageTextColor)
        ta.recycle()
    }

    fun setImageClickListener(listener: (view: View) -> Unit) {
        mImageClickListener = listener
    }

    fun setImageDrawable(drawable: Drawable?) {
        val imageView = viewBinding.image
        if (drawable != null) {
            imageView.visibility = View.VISIBLE
            imageView.setImageDrawable(drawable)
        } else {
            imageView.visibility = View.INVISIBLE
        }
    }

    fun setImageText(txt: CharSequence?) {
        val textView = viewBinding.text
        if (txt.isNullOrEmpty()) {
            textView.visibility = View.GONE
        } else {
            textView.visibility = View.VISIBLE
            textView.text = txt
        }
    }

    fun setImageTextColor(colors: ColorStateList) {
        viewBinding.text.setTextColor(colors)
    }

    fun setImageTextColor(color: Int) {
        viewBinding.text.setTextColor(color)
    }

    fun getImageText(): String {
        return viewBinding.text.text?.toString() ?: ""
    }
}