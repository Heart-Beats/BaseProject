package com.hl.imageload

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import  com.bumptech.glide.request.target.Target
import kotlin.concurrent.thread

object GlideUtil {

    @JvmOverloads
    @JvmStatic
    fun loadHead(context: Context, url: String?, imageView: ImageView, isGentleman: Boolean?) {
        val headImageHead = when {
            isGentleman == true -> R.drawable.hl_image_load_avatar_gentlemen
            isGentleman == false -> R.drawable.hl_image_load_avatar_lady
            else -> R.drawable.hl_image_load_avatar_genderless
        }
        Glide.with(context).load(url)
            .placeholder(context.resources.getDrawable(headImageHead))
            .error(context.resources.getDrawable(headImageHead))
            .into(imageView)
    }


    /**
     * 加载网络图片
     *
     * @param context
     * @param url                      图片链接
     * @param imageView                显示的 ImageView
     * @param placeholderResId         占位图资源 ID
     * @param roundPx                  圆角大小
     * @param requestOptionsBlock      可定制化选项
     */
    @JvmOverloads
    @JvmStatic
    fun load(
        context: Context,
        url: String?,
        imageView: ImageView,
        @DrawableRes placeholderResId: Int = R.drawable.hl_image_load_loading_img_small,
        roundPx: Int = 0,
        requestOptionsBlock: RequestOptions.() -> Unit = {}
    ) {
        val drawable = ContextCompat.getDrawable(context, placeholderResId)
        load(context, url, imageView, drawable, roundPx, requestOptionsBlock)
    }


    /**
     * 加载网络图片
     *
     * @param context
     * @param url                      图片链接
     * @param imageView                显示的 ImageView
     * @param placeholderResId         占位图
     * @param roundPx                  圆角大小
     * @param requestOptionsBlock      可定制化选项
     */
    @JvmOverloads
    @JvmStatic
    fun load(
        context: Context,
        url: String?,
        imageView: ImageView,
        drawable: Drawable?,
        roundPx: Int = 0,
        requestOptionsBlock: RequestOptions.() -> Unit = {}
    ) {
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
            }.apply(requestOptionsBlock)

        Glide.with(context).load(url).apply(requestOptions).into(imageView)
    }

    /**
     * 加载本地图片
     *
     * @param context
     * @param drawableId               加载图片的 ID
     * @param imageView                显示的 ImageView
     * @param roundPx                  圆角大小
     * @param requestOptionsBlock      可定制化选项
     */
    @JvmOverloads
    @JvmStatic
    fun load(
        context: Context,
        drawableId: Int,
        imageView: ImageView,
        roundPx: Int = 0,
        requestOptionsBlock: RequestOptions.() -> Unit = {}
    ) {
        val drawable = ContextCompat.getDrawable(context, R.drawable.hl_image_load_loading_img_small)

        val requestOptions = RequestOptions()
            .placeholder(drawable)
            .error(drawable)
            .fallback(drawable)
            .apply {
                if (roundPx > 0) {
                    transform(RoundedCorners(roundPx))
                }
            }.apply(requestOptionsBlock)

        Glide.with(context).load(drawableId).apply(requestOptions).into(imageView)
    }

    @JvmOverloads
    @JvmStatic
    fun load(context: Context, url: String, target: Target<Drawable>) {
        val drawable = ContextCompat.getDrawable(context, R.drawable.hl_image_load_loading_img_small)

        val requestOptions = RequestOptions()
            .placeholder(drawable)
            .error(drawable)
            .fallback(drawable)
        Glide.with(context).load(url).apply(requestOptions).into(target)
    }

    /**
     * 在后台线程将 指定 url 加载转换为 Bitmap
     */
    @JvmOverloads
    @JvmStatic
    fun loadUrl2Bitmap(context: Context, url: String, callback: (loadBitmap: Bitmap) -> Unit) {
        thread {
            val bitmap = Glide.with(context)
                .asBitmap()
                .load(url)
                .submit()
                .get()

            runOnUiThread {
                callback(bitmap)
            }
        }
    }

    private fun runOnUiThread(runnable: () -> Unit) {
        Handler(Looper.getMainLooper()).post(runnable)
    }
}