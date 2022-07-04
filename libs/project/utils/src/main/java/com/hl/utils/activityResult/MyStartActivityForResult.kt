package com.hl.utils.activityResult

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract

/**
 * @author  张磊  on  2022/07/04 at 13:38
 * Email: 913305160@qq.com
 */
class MyStartActivityForResult : ActivityResultContract<Intent, ActivityResult>() {

	private var inputComponent: ComponentName? = null

	override fun createIntent(context: Context, input: Intent): Intent {

		inputComponent = input.component

		return input
	}

	override fun parseResult(
		resultCode: Int,
		intent: Intent?
	): ActivityResult {

		// 这里保证 intent 不为空，因为在取消返回的情况下 intent 会为 null, 此时就会无法取得 intent 对应的 component
		val data = intent ?: Intent()
		val activityResult = ActivityResult(resultCode, data.apply {
			// 设置返回数据时的 component， 该 component 为启动时对应的Activity
			this.component = inputComponent
		})
		return activityResult
	}
}