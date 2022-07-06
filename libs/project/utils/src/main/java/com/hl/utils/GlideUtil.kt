package com.hl.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

object GlideUtil {

    @JvmOverloads
    @JvmStatic
    fun loadHead(context: Context, url: String?, imageView: ImageView, isGentleman: Boolean?) {
        val headImageHead = when {
            isGentleman == true -> R.drawable.gentlemen_head
            isGentleman == false -> R.drawable.lady_head
            else -> R.drawable.genderless_head
        }
        Glide.with(context).load(url)
            .placeholder(context.resources.getDrawable(headImageHead))
            .error(context.resources.getDrawable(headImageHead))
            .into(imageView)
    }

    @JvmOverloads
    @JvmStatic
    fun loadTeam(context: Context, url: String?, imageView: ImageView) {
        Glide.with(context).load(url)
            .placeholder(context.resources.getDrawable(R.drawable.icon_team_logo))
            .error(context.resources.getDrawable(R.drawable.icon_team_logo))
            .into(imageView)
    }

    private fun isBase64Img(imgUrl: String): Boolean {
        return !TextUtils.isEmpty(imgUrl) && imgUrl.matches(Regex("^data:image/(png|jpg|\\*);base64,.*$"))
    }

    @JvmOverloads
    @JvmStatic
    fun load(
        context: Context,
        url: String?,
        imageView: ImageView,
        placeholderResId: Int = R.drawable.loading_img_small,
        roundPx: Int = 0
    ) {
        val drawable = ContextCompat.getDrawable(context, placeholderResId)

        val requestOptions = RequestOptions()
            .placeholder(drawable)
            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)   //关键代码，加载原始大小
            .format(DecodeFormat.PREFER_RGB_565)  //设置为这种格式去掉透明度通道，可以减少内存占有
            .error(drawable)
            .fallback(drawable)
            .apply {
                if (roundPx > 0) {
                    transform(RoundedCorners(roundPx))
                }
            }

        Glide.with(context).load(url).apply(requestOptions).into(imageView)
    }

    @JvmOverloads
    @JvmStatic
    fun load(context: Context, url: String, target: Target<Drawable>) {
        val drawable = ContextCompat.getDrawable(context, R.drawable.loading_img_small)

        val requestOptions = RequestOptions()
            .placeholder(drawable)
            .error(drawable)
            .fallback(drawable)
        Glide.with(context).load(url).apply(requestOptions).into(target)
    }

    @JvmOverloads
    @JvmStatic
    fun load(context: Context, drawableId: Int, imageView: ImageView, roundPx: Int = 0) {
        val drawable = ContextCompat.getDrawable(context, R.drawable.loading_img_small)

        val requestOptions = RequestOptions()
            .placeholder(drawable)
            .error(drawable)
            .fallback(drawable)
            .apply {
                if (roundPx > 0) {
                    transform(RoundedCorners(roundPx))
                }
            }

        Glide.with(context).load(drawableId).apply(requestOptions).into(imageView)
    }
}