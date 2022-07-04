package com.hl.utils.qrcode

import android.content.Intent
import androidx.activity.result.ActivityResultCaller
import androidx.core.app.ActivityOptionsCompat
import com.hl.utils.activityResult.ActivityResultHelper
import com.hl.utils.activityResult.OnActivityResult
import com.king.zxing.CameraScan
import com.king.zxing.CaptureActivity

/**
 * 扫码工具类
 *
 * 注意：  初始化必须在 onCreate() 中或之前
 */
class QRScanUtil(activityResultCaller: ActivityResultCaller) {

    private val activityResultHelper = ActivityResultHelper(activityResultCaller)

    /**
     * 启动默认扫码页面
     *
     * @param  scanCancelAction： 扫码取消时的回调
     * @param  scanResultAction： 扫码识别成功时的回调
     */
    fun launchDefault(
        activityOptionsCompat: ActivityOptionsCompat? = null,
        scanCancelAction: () -> Unit = {},
        scanResultAction: (String?) -> Unit
    ) {
        activityResultHelper.launchActivity(CaptureActivity::class.java, activityOptionsCompat,
            callback = object : OnActivityResult {
                override fun onResultOk(data: Intent?) {
                    scanResultAction(CameraScan.parseScanResult(data))
                }

                override fun onResultCanceled(data: Intent?) {
                    scanCancelAction()
                }
            })
    }
}