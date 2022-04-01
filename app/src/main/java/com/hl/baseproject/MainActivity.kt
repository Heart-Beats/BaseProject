package com.hl.baseproject

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hl.uikit.onClick


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun onStart() {
        super.onStart()

        findViewById<TextView>(R.id.test_scan_qrcode).onClick {
            // startActivityForResult(Intent(this, CaptureActivity::class.java), 1)
        }

    }
}