package com.hl.activityresult

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract

/**
 * @author  张磊  on  2022/07/04 at 13:38
 * Email: 913305160@qq.com
 */
internal class MyStartActivityForResult : ActivityResultContract<Intent, ActivityResult>() {

	companion object {
		const val INTENT_IDENTIFIER_KEY = "intentIdentifierKey"
	}

	// intent 的标识符
	private var intentIdentifier: String? = null

	override fun createIntent(context: Context, input: Intent): Intent {
		intentIdentifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			// Android 10 以上可直接设置获取 intent 的标识符， 以下通过 bundle 进行传输
			input.identifier
		} else {
			input.getStringExtra(INTENT_IDENTIFIER_KEY)
		}

		return input
	}

	override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult {

		// 这里保证 intent 不为空，因为在取消返回的情况下 intent 会为 null, 此时就会无法取得 intent 对应的 component
		val data = intent ?: Intent()

		val activityResult = ActivityResult(resultCode, data.apply {
			// 设置返回数据 intent 的 identifier 唯一标识符， 该 identifier 等于启动的 Intent 的 identifier
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				// Android 10 以上可直接设置获取 intent 的标识符， 以下通过 bundle 进行传输
				this.identifier = intentIdentifier
			} else {
				this.putExtra(INTENT_IDENTIFIER_KEY, intentIdentifier)
			}

		})
		return activityResult
	}
}