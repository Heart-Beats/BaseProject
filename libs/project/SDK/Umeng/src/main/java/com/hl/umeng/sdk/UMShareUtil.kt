package com.youma.umeng.sdk

import android.app.Activity
import android.util.Log
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA

/**
 * @author  张磊  on  2022/01/23 at 7:44
 * Email: 913305160@qq.com
 */

object UMShareUtil {

    private const val TAG = "UMShareUtil"

    fun share2PlatformsWithBoard(
        activity: Activity,
        vararg sharePlatforms: SHARE_MEDIA = arrayOf(SHARE_MEDIA.WEIXIN),
        shareListener: MyUMShareListener = object: MyUMShareListener() {
            override fun onResult(platform: SHARE_MEDIA) {
                Log.d(TAG, "onShareResult: ${platform.getName()} 分享成功")
            }
        },
        shareContentAction: ShareAction.() -> Unit
    ) {
        shareWithBoard(
            activity, *sharePlatforms,
            shareListener = shareListener,
            shareContentAction = shareContentAction
        )
    }

    /**
     * 通过分享面板分享
     */
    private fun shareWithBoard(
        activity: Activity,
        vararg sharePlatforms: SHARE_MEDIA,
        shareListener: MyUMShareListener,
        shareContentAction: ShareAction.() -> Unit,
    ) {
        ShareAction(activity)
            .setDisplayList(*sharePlatforms)
            .setCallback(shareListener)
            .apply(shareContentAction)
            .open()
    }

    /**
     * 单个 App 分享
     */
    fun shareWithsetPlatform(
        activity: Activity,
        platform: SHARE_MEDIA,
        shareListener: MyUMShareListener,
        shareContentAction: ShareAction.() -> Unit,
    ) {
        ShareAction(activity)
            .setPlatform(platform)
            .setCallback(shareListener)
            .apply(shareContentAction)
            .share()
    }

}


abstract class MyUMShareListener : UMShareListener {
    private val TAG = "MyUMShareListener"

    /**
     * @descrption 分享开始的回调
     * @param platform 平台类型
     */
    override fun onStart(platform: SHARE_MEDIA) {
        Log.d(TAG, "${platform}分享开始")
    }

    /**
     * @descrption 分享成功的回调
     * @param platform 平台类型
     */
    abstract override fun onResult(platform: SHARE_MEDIA)

    /**
     * @descrption 分享失败的回调
     * @param platform 平台类型
     * @param t 错误原因
     */
    override fun onError(platform: SHARE_MEDIA, t: Throwable) {
        Log.e(TAG, "${platform}分享出错", t)
    }

    /**
     * @descrption 分享取消的回调
     * @param platform 平台类型
     */
    override fun onCancel(platform: SHARE_MEDIA) {
        Log.d(TAG, "${platform}取消分享")
    }
}

