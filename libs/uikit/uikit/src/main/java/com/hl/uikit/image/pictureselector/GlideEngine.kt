package com.hl.uikit.image.pictureselector

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.interfaces.OnCallbackListener

/**
 * @author：luck
 * @date：2019-11-13 17:02
 * @describe：Glide加载引擎
 */
class GlideEngine private constructor() : ImageEngine {
    /**
     * 加载图片
     *
     * @param context   上下文
     * @param url       资源url
     * @param imageView 图片承载控件
     */
    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        if (!assertValidRequest(context)) {
            return
        }
        Glide.with(context)
            .load(url)
            .into(imageView)
    }

    /**
     * 加载指定url并返回bitmap
     *
     * @param context   上下文
     * @param url       资源url
     * @param maxWidth  资源最大加载尺寸
     * @param maxHeight 资源最大加载尺寸
     * @param call      回调接口
     */
    override fun loadImageBitmap(
        context: Context,
        url: String,
        maxWidth: Int,
        maxHeight: Int,
        call: OnCallbackListener<Bitmap>
    ) {
        if (!assertValidRequest(context)) {
            return
        }
        Glide.with(context)
            .asBitmap()
            .override(maxWidth, maxHeight)
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    call.onCall(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    call.onCall(null)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    /**
     * 加载相册目录封面
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadAlbumCover(context: Context, url: String, imageView: ImageView) {
        if (!assertValidRequest(context)) {
            return
        }
        Glide.with(context)
            .asBitmap()
            .load(url)
            .override(180, 180)
            .centerCrop()
            .sizeMultiplier(0.5f)
            .placeholder(com.luck.picture.lib.R.drawable.ps_image_placeholder)
            .into(object : BitmapImageViewTarget(imageView) {
                override fun setResource(resource: Bitmap?) {
                    val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, resource)
                    circularBitmapDrawable.cornerRadius = 8f
                    imageView.setImageDrawable(circularBitmapDrawable)
                }
            })
    }

    /**
     * 加载图片列表图片
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        if (!assertValidRequest(context)) {
            return
        }
        Glide.with(context)
            .load(url)
            .override(200, 200)
            .centerCrop()
            .placeholder(com.luck.picture.lib.R.drawable.ps_image_placeholder)
            .into(imageView)
    }

    override fun pauseRequests(context: Context) {
        Glide.with(context).pauseRequests()
    }

    override fun resumeRequests(context: Context) {
        Glide.with(context).resumeRequests()
    }

    companion object {
        fun assertValidRequest(context: Context?): Boolean {
            if (context is Activity) {
                return !isDestroy(context)
            } else if (context is ContextWrapper) {
                if (context.baseContext is Activity) {
                    val activity = context.baseContext as Activity
                    return !isDestroy(activity)
                }
            }
            return true
        }

        private fun isDestroy(activity: Activity?): Boolean {
            return if (activity == null) {
                true
            } else activity.isFinishing || activity.isDestroyed
        }

        private var instance: GlideEngine? = null
        fun createGlideEngine(): GlideEngine? {
            if (null == instance) {
                synchronized(GlideEngine::class.java) {
                    if (null == instance) {
                        instance = GlideEngine()
                    }
                }
            }
            return instance
        }
    }
}
