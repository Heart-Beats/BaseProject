package com.hl.uikit.form

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import kotlinx.android.synthetic.main.uikit_layout_form_image.view.*
import com.hl.uikit.R
import com.hl.uikit.dp
import com.hl.uikit.onClick
import com.hl.uikit.sp

class UIKitFormItemImage : UIKitFormItemView {
    var holderImageId: Int = -1
        private set
    lateinit var imageView: ImageView
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
        LayoutInflater.from(context).inflate(R.layout.uikit_layout_form_image, this, true)
        val ta = context.obtainStyledAttributes(attrs, R.styleable.UIKitFormItemImage)
        holderImageId = ta.getResourceId(R.styleable.UIKitFormItemImage_uikit_formImage, -1)
        val drawable = ta.getDrawable(R.styleable.UIKitFormItemImage_uikit_formImage)
        imageView = image
        setImageDrawable(drawable)
        image.onClick {
            if (image.isShown) {
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
        text?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        text?.updateLayoutParams<MarginLayoutParams> {
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
        if (drawable != null) {
            image.visibility = View.VISIBLE
            image.setImageDrawable(drawable)
        } else {
            image.visibility = View.INVISIBLE
        }
    }

    fun setImageText(txt: CharSequence?) {
        if (txt.isNullOrEmpty()) {
            text.visibility = View.GONE
        } else {
            text.visibility = View.VISIBLE
            text.text = txt
        }
    }

    fun setImageTextColor(colors: ColorStateList) {
        text?.setTextColor(colors)
    }

    fun setImageTextColor(color: Int) {
        text?.setTextColor(color)
    }

    fun getImageText(): String {
        return text?.text?.toString() ?: ""
    }
}