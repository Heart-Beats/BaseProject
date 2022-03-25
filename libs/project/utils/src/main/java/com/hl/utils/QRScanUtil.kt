package com.hl.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.yzq.zxinglibrary.android.CaptureActivity
import com.yzq.zxinglibrary.bean.ZxingConfig
import com.yzq.zxinglibrary.common.Constant

object QRScanUtil {

    /**
     * 调用扫面功能的来源
     */
    enum class ScanSource(val code: Int, val desc: String) {
        /**
         * H5 插件调用扫描
         */
        PLUGIN(600, "插件"),

        /**
         * 原生 app 调用扫描
         */
        NATIVE(601, "原生"),
    }

    @JvmStatic
    fun startScan(activity: FragmentActivity, scanSource: ScanSource) {
        startScan(scanSource.code, activity)
    }

    @JvmStatic
    fun startScan(fragment: Fragment, scanSource: ScanSource) {
        startScan(fragment, scanSource.code)
    }

    private fun startScan(fragment: Fragment, reqCode: Int) {
        fragment.reqPermissions(Manifest.permission.CAMERA, deniedAction = {
            fragment.showShortToast("权限拒绝，无法使用手机摄像头扫码~")
        })
        val intent = getZXCaptureIntent(fragment.requireContext())
        fragment.startActivityForResult(intent, reqCode)
    }


    private fun startScan(reqCode: Int, activity: FragmentActivity) {
        activity.reqPermissions(Manifest.permission.CAMERA, deniedAction = {
            activity.showShortToast("权限拒绝，无法使用手机摄像头扫码~")
        }) {

            val intent = getZXCaptureIntent(activity)
            activity.startActivityForResult(intent, reqCode)
        }
    }

    private fun getZXCaptureIntent(context: Context): Intent {
        val intent = Intent(context, CaptureActivity::class.java)
        /*ZxingConfig是配置类
                 *可以设置是否显示底部布局，闪光灯，相册，
                 * 是否播放提示音  震动
                 * 设置扫描框颜色等
                 * 也可以不传这个参数
                 * */
        val config = ZxingConfig()
        config.isPlayBeep = true //是否播放扫描声音 默认为true
        config.isShake = true //是否震动  默认为true
        config.isDecodeBarCode = true //是否扫描条形码 默认为true
        config.reactColor = R.color.scanMainColor //设置扫描框四个角的颜色 默认为白色
        config.frameLineColor = R.color.scanMainColor //设置扫描框边框颜色 默认无色
        config.scanLineColor = R.color.scanMainColor //设置扫描线的颜色 默认白色
        config.isFullScreenScan = false //是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config)
        return intent
    }

    private fun parseScanInfo(context: Context, scanState: Int, intent: Intent?): String {
        if (null == intent) {
            context.showShortToast("请扫描正确的二维码~")
        } else {
            val info = intent.getStringExtra(Constant.CODED_CONTENT)
            if (info.isNullOrEmpty()) {
                context.showShortToast("请扫描正确的二维码~")
            } else if (info.contains("http")) {
                context.showShortToast("请扫描正确的二维码~")
            } else {
                val split = info.split("\\.".toRegex()).toTypedArray()
                if (null != split && split.size == 2) {
                    if (split[0] == scanState.toString()) {
                        return split[1]
                    }
                } else {
                    context.showShortToast("请扫描正确的二维码~")
                }
            }
        }
        return ""
    }

    @JvmStatic
    fun getScanInfo(context: Context, intent: Intent?): String {
        return intent?.getStringExtra(Constant.CODED_CONTENT).run {
            if (this == null) {
                context.showShortToast("未识别到有效信息~")
                ""
            } else {
                this
            }
        }
    }
}