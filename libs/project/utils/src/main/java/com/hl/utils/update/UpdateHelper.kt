package com.hl.utils.update

import android.content.Context
import constant.UiType.PLENTIFUL
import listener.Md5CheckResultListener
import listener.OnBtnClickListener
import listener.UpdateDownloadListener
import model.UiConfig
import model.UpdateConfig
import update.UpdateAppUtils

object UpdateHelper {
    /**
     * 版本更新下载apk
     * 一行代码帮你搞定Android版本更新
     *
     * https://github.com/teprinciple/UpdateAppUtils
     *
     */
    @JvmOverloads
    fun downloadApk(
        context:Context,
        apkUrl: String,
        title: String,
        content: String,
        uiConfig: UiConfig.() -> Unit,
        updateConfig: UpdateConfig.() -> Unit,
        cancelClickListener: () -> Unit = {},
        updateClickListener: () -> Unit = {},
        observer: DownloadApkObserver? = null
    ) {
        //版本更新初始化
        UpdateAppUtils.init(context)
        UpdateAppUtils.getInstance()
            .apkUrl(apkUrl)
            .updateTitle(title)
            .updateContent(content)
            .uiConfig(UiConfig().apply(uiConfig))
            .updateConfig(getDefaultUpdateConfig().apply(updateConfig))
            .setMd5CheckResultListener(object : Md5CheckResultListener {
                override fun onResult(result: Boolean) {
                    observer?.onMd5CheckResult(result)
                }
            })
            .setUpdateDownloadListener(object : UpdateDownloadListener {
                override fun onStart() {
                    observer?.onStart()
                }

                override fun onDownload(progress: Int) {
                    observer?.onDownload(progress)
                }

                override fun onFinish() {
                    observer?.onFinish()
                }


                override fun onError(e: Throwable) {
                    observer?.onError(e)
                }
            })
            .setCancelBtnClickListener(object : OnBtnClickListener {
                override fun onClick(): Boolean {
                    cancelClickListener()
                    return false
                }
            })
            .setUpdateBtnClickListener(object : OnBtnClickListener {
                override fun onClick(): Boolean {
                    updateClickListener()
                    return false
                }

            })
            .update()
    }

    /**
     * 默认更新配置
     */
    private fun getDefaultUpdateConfig(): UpdateConfig {
        val updateConfig = UpdateConfig()
        updateConfig.checkWifi = true
        updateConfig.needCheckMd5 = true
        return updateConfig
    }

    /**
     * 默认UI配置
     */
    private val uiConfig: UiConfig
        get() {
            val uiConfig = UiConfig()
            uiConfig.uiType = PLENTIFUL
            return uiConfig
        }
}