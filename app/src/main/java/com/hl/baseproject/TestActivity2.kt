package com.hl.baseproject

import android.content.Intent
import android.os.Bundle
import com.hl.baseproject.databinding.ActivityTest2Binding
import com.hl.bitmaputil.toBitmap
import com.hl.ui.utils.onClick
import com.hl.uikit.toast
import com.hl.utils.PaletteUtil
import com.hl.utils.registerReceiver

class TestActivity2 : com.hl.ui.base.ViewBindingBaseActivity<ActivityTest2Binding>() {

	companion object {
		const val TEST_ACTION = "com.hl.action.test"
	}

	@Deprecated("Deprecated in Java")
	override fun onBackPressed() {
		setResult(RESULT_OK, Intent().apply {
			this.putExtra("data", "我是测试2页面数据")
		})
		super.onBackPressed()
	}

	override fun ActivityTest2Binding.onViewCreated(savedInstanceState: Bundle?) {
		this@TestActivity2.registerReceiver(TEST_ACTION) { _, intent ->
			if (intent.action == TEST_ACTION) {
				toast("我收到测试广播啦")
			}
		}

		uikitToolbar.title = "测试长度测试长度测试长度测试长度测试长度测试长度测试长度"
		uikitToolbar.addRightActionText("按钮1") {
			sendBroadcast(Intent(TEST_ACTION))
		}

		radioGroup.setOnCheckedChangeListener { _, checkedId ->
			if (checkedId == radioButtonDark.id) {
				contentLayout.setBackgroundResource(R.drawable.dark_image)
			} else {
				contentLayout.setBackgroundResource(R.drawable.light_image)
			}
		}
		radioGroup.check(radioButtonDark.id)

		testPalette.onClick {
			contentLayout.toBitmap()?.run {
				PaletteUtil.getColorFromBitmap(this) { rgb, _, _, isLight ->
					toast("是否为深色的图片 == ${!isLight}")

					immersionBar?.apply {
						statusBarColorInt(rgb)
						statusBarDarkFont(isLight)
					}?.init()
				}
			}
		}
	}
}