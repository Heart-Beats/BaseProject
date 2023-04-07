package com.hl.baseproject

import android.content.Intent
import android.os.Bundle
import com.hl.arch.base.ViewBindingBaseActivity
import com.hl.baseproject.databinding.ActivityTest2Binding
import com.hl.uikit.onClick
import com.hl.uikit.toast
import com.hl.utils.PaletteUtil
import com.hl.utils.toBitmap

class TestActivity2 : ViewBindingBaseActivity<ActivityTest2Binding>() {

	@Deprecated("Deprecated in Java")
	override fun onBackPressed() {
		setResult(RESULT_OK, Intent().apply {
			this.putExtra("data", "我是测试2页面数据")
		})
		super.onBackPressed()
	}

	override fun ActivityTest2Binding.onViewCreated(savedInstanceState: Bundle?) {
		uikitToolbar.title = "测试长度测试长度测试长度测试长度测试长度测试长度测试长度"
		uikitToolbar.addRightActionText("按钮1")

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