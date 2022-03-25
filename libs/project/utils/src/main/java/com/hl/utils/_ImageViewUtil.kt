package com.hl.utils

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/**
 * @author  张磊  on  2021/12/17 at 16:27
 * Email: 913305160@qq.com
 */

/**
 * 使用着色器给  drawable 着色
 */
fun ImageView.setColorFromRes(@ColorRes colorResId: Int) {
    //不调用 mutate() 不会在内存中创建新的 Drawable， 着色时就会全局更改
    val modeDrawable: Drawable = this.getDrawable().mutate()
    val temp: Drawable = DrawableCompat.wrap(modeDrawable)
    val colorStateList: ColorStateList = ColorStateList.valueOf(ContextCompat.getColor(this.context, colorResId))
    DrawableCompat.setTintList(temp, colorStateList)
    this.setImageDrawable(temp)
}

/**
 * 使用着色器给  drawable 着色
 */
fun ImageView.setColor(@ColorInt color: Int) {
    //不调用 mutate() 不会在内存中创建新的 Drawable， 着色时就会全局更改
    val modeDrawable: Drawable = this.getDrawable().mutate()
    val temp: Drawable = DrawableCompat.wrap(modeDrawable)
    val colorStateList: ColorStateList = ColorStateList.valueOf(color)
    DrawableCompat.setTintList(temp, colorStateList)
    this.setImageDrawable(temp)
}

/**
 * 获取视频的首帧图
 * @param url
 */
fun ImageView.loadFirstFrameCover(url: String?, errorResIs: Int? = null, placeholderResId: Int? = null) {
    //可以参考Glide，内部也是封装了MediaMetadataRetriever
    Glide.with(this.context)
        .setDefaultRequestOptions(
            RequestOptions().apply {
                frame(1000000)
                // centerCrop()
                // error(errorResIs)
                // placeholder(placeholderResId)
            }
        )
        .load(url)
        .into(this)
}