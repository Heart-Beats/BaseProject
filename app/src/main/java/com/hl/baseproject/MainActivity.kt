package com.hl.baseproject

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hl.uikit.onClick
import com.hl.uikit.toast
import com.hl.utils.qrcode.QRScanUtil


class MainActivity : AppCompatActivity() {

    private val qrScanUtil = QRScanUtil(this).apply {
        registerScanActivityResult {
            toast("扫描结果  == $it")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun onStart() {
        super.onStart()

        findViewById<TextView>(R.id.test_scan_qrcode).onClick {
            qrScanUtil.launchDefault()
        }

    }
}