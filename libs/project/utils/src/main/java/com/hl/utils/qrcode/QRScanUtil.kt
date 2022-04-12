package com.hl.utils.qrcode

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.king.zxing.CameraScan
import com.king.zxing.CaptureActivity

class QRScanUtil(private val activityResultCaller: ActivityResultCaller) {

    private lateinit var qrCodeScanActivityResult: ActivityResultLauncher<Intent>


    /**
     * 必须在 activity 的 onCreate中调用此方法
     */
    fun registerScanActivityResult(scanResultAction: (String?) -> Unit) {
        qrCodeScanActivityResult = activityResultCaller.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            scanResultAction(CameraScan.parseScanResult(activityResult.data))
        }
    }

    fun launchDefault(activityOptionsCompat: ActivityOptionsCompat? = null) {
        val context: Context = when (activityResultCaller) {
            is FragmentActivity -> activityResultCaller
            is Fragment -> activityResultCaller.requireContext()
            else -> throw IllegalStateException("仅可在 Activity 或 Fragment 中支持使用")
        }

        qrCodeScanActivityResult.launch(Intent(context, CaptureActivity::class.java), activityOptionsCompat)
    }
}