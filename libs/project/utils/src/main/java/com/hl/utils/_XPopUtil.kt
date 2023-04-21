package com.hl.utils

import android.content.Context
import android.widget.ImageView
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.OnImageViewerLongPressListener
import com.lxj.xpopup.util.SmartGlideImageLoader

/**
 * @author  张磊  on  2022/02/22 at 23:59
 * Email: 913305160@qq.com
 */

fun <T : BasePopupView> T.showPop(popOptions: XPopup.Builder.() -> Unit = {}) {
    this.createPop(popOptions)
        .show()
}

fun <T : BasePopupView> T.createPop(popOptions: XPopup.Builder.() -> Unit = {}): T {
    return XPopup.Builder(this.context)
        .isViewMode(true)
        .isDestroyOnDismiss(true)  //对于只使用一次的弹窗，推荐设置这个
        .apply(popOptions)
        .asCustom(this) as T
}


/**
 * 单图浏览
 *
 * @param srcImageView  点击的源 ImageView，弹窗消失的时候需回到该位置。如果实在没有这个View，可以传空，但是动画变的非常僵硬，适用于在 WebView 点击场景
 * @param url           图片链接
 */
fun Context.showImage(srcImageView: ImageView?, url: String) {
    showImage(srcImageView, url, imageShowOption = {})
}

/**
 * 单图浏览
 *
 * @param srcImageView     点击的源 ImageView，弹窗消失的时候需回到该位置。如果实在没有这个View，可以传空，但是动画变的非常僵硬，适用于在 WebView 点击场景
 * @param url              图片资源 id，url 或者文件路径
 * @param imageShowOption  浏览定制项
 * @param onLongPress      长按处理
 */
fun Context.showImage(
    srcImageView: ImageView?,
    url: Any,
    imageShowOption: ImageShowOption.() -> Unit = {},
    onLongPress: OnImageViewerLongPressListener? = null
) {
    val option = ImageShowOption().apply(imageShowOption)
    XPopup.Builder(this)
        .isViewMode(true)
        .asImageViewer(
            srcImageView,
            url,
            option.isInfinite,
            option.placeholderColor,
            option.placeholderStroke,
            option.placeholderRadius,
            option.isShowSaveBtn,
            option.bgColor,
            SmartGlideImageLoader(),
            onLongPress
        )
        .show()
}

data class ImageShowOption(
    /**
     * 是否需要无限滚动
     */
    val isInfinite: Boolean = false,

    /**
     * 是否展示占位图
     */
    val isShowPlaceHolder: Boolean = false,
    /**
     * 占位View的填充色
     */
    val placeholderColor: Int = -1,
    /**
     * 占位View的边框色
     */
    val placeholderStroke: Int = -1,
    /**
     * 占位View的圆角大小
     */
    val placeholderRadius: Int = -1,
    /**
     * 是否显示保存按钮
     */
    val isShowSaveBtn: Boolean = true,
    /**
     * 背景颜色
     */
    val bgColor: Int = -1
)


/**
 * 多图浏览
 *
 * @param srcImageView  点击的源 ImageView，弹窗消失的时候需回到该位置。如果实在没有这个View，可以传空，但是动画变的非常僵硬，适用于在 WebView 点击场景
 * @param index         指定显示图片的位置
 * @param urls          图片链接集合
 */
fun Context.showImages(srcImageView: ImageView?, index: Int, urls: List<String>) {
    showImages(srcImageView, index, urls, imageShowOption = {})
}

/**
 * 多图浏览
 *
 * @param srcImageView     点击的源 ImageView，弹窗消失的时候需回到该位置。如果实在没有这个View，可以传空，但是动画变的非常僵硬，适用于在 WebView 点击场景
 * @param index            指定显示图片的位置
 * @param urls             图片资源 id，url 或者文件路径集合
 * @param imageShowOption  浏览定制项
 * @param onLongPress      长按处理
 */
fun Context.showImages(
    srcImageView: ImageView?,
    index: Int,
    urls: List<Any>,
    imageShowOption: ImageShowOption.() -> Unit = {},
    onLongPress: OnImageViewerLongPressListener? = null
) {
    val option = ImageShowOption().apply(imageShowOption)
    XPopup.Builder(this)
        .isViewMode(true)
        .asImageViewer(
            srcImageView,
            index,
            urls,
            option.isInfinite,
            option.isShowPlaceHolder,
            option.placeholderColor,
            option.placeholderStroke,
            option.placeholderRadius,
            option.isShowSaveBtn,
            option.bgColor,
            { popupView, position ->

                // 注意这里：根据position找到你的itemView。根据你的itemView找到你的ImageView。
                // Demo中RecyclerView里面只有ImageView所以这样写，不要原样COPY。
                // 作用是当Pager切换了图片，需要更新源View，
                // popupView.updateSrcView(recyclerView.getChildAt(position) as ImageView)
            },
            SmartGlideImageLoader(),
            onLongPress
        )
        .show()
}