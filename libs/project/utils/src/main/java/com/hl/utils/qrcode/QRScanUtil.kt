package com.hl.utils.qrcode

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.FragmentActivity
import com.king.zxing.CameraScan
import com.king.zxing.CaptureActivity

class QRScanUtil(val activity: FragmentActivity) {

    private lateinit var qrCodeScanActivityResult: ActivityResultLauncher<Intent>


    /**
     * 必须在 activity 的 onCreate中调用此方法
     */
    fun registerScanActivityResult(scanResultAction: (String?) -> Unit) {
        qrCodeScanActivityResult = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            scanResultAction(CameraScan.parseScanResult(activityResult.data))
        }
    }

    fun launchDefault(activityOptionsCompat: ActivityOptionsCompat? = null) {
        qrCodeScanActivityResult.launch(Intent(activity, CaptureActivity::class.java), activityOptionsCompat)
    }
}