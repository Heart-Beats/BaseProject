package com.hl.health.wxapi

import android.app.Activity
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA

object UMLoginUtils {

    /**
     * 友盟第三方登录并获取用户信息
     * SHARE_MEDIA.QQ [qq]
     * SHARE_MEDIA.SINA [新浪微博]
     * SHARE_MEDIA.WEIXIN [微信]
     * SHARE_MEDIA.WXWORK [企业微信]
     * SHARE_MEDIA.FACEBOOK [脸书]
     * SHARE_MEDIA.TWITTER [推特]
     * SHARE_MEDIA.LINKEDIN [linkedin]
     * SHARE_MEDIA.DOUBAN [豆瓣]
     * SHARE_MEDIA.RENREN [人人网]
     * SHARE_MEDIA.KAKAO [KakaoTalk]
     * SHARE_MEDIA.VKONTAKTE [VKontakte]
     * SHARE_MEDIA.DROPBOX [DropBox]
     */
    @JvmStatic
    fun toShareAppLoginGetInfo(
        activity: Activity,
        action: SHARE_MEDIA,
        listener: MyUMAuthListener
    ) {
        UMShareAPI.get(activity.application).getPlatformInfo(activity, action, listener)
    }

    /**
     * 取消授权
     */
    @JvmStatic
    fun deleteOauth(
        activity: Activity,
        action: SHARE_MEDIA,
        listener: MyUMAuthListener
    ) {
        UMShareAPI.get(activity.application).deleteOauth(activity, action, listener)
    }


    /**
     * 判断应用是否安装
     */
    fun isInstall(activity: Activity, action: SHARE_MEDIA): Boolean {
        return UMShareAPI.get(activity.application).isInstall(activity, action)
    }

}

abstract class MyUMAuthListener : UMAuthListener {

    /**
     * @desc 授权开始的回调
     * @param platform 平台名称
     */
    override fun onStart(platform: SHARE_MEDIA?) {

    }


    /**
     * @desc 授权成功的回调
     * @param platform 平台名称
     * @param action 行为序号，开发者用不上
     * @param data 用户资料返回
     */
    abstract override fun onComplete(platform: SHARE_MEDIA?, action: Int, data: MutableMap<String, String>?)

    /**
     * @desc 授权失败的回调
     * @param platform 平台名称
     * @param action 行为序号，开发者用不上
     * @param t 错误原因
     */
    override fun onError(platform: SHARE_MEDIA?, action: Int, t: Throwable?) {
        //拒绝登录
    }

    /**
     * @desc 授权取消的回调
     * @param platform 平台名称
     * @param action 行为序号，开发者用不上
     */
    override fun onCancel(platform: SHARE_MEDIA?, action: Int) {
        //取消登录
    }
}



