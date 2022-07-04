package com.hl.baseproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class TestActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_test)
	}

	override fun onBackPressed() {
		setResult(RESULT_OK, Intent().apply {
			this.putExtra("data", "我是测试1页面数据")
		})
		super.onBackPressed()
	}
}