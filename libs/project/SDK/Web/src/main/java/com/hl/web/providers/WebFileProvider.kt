package com.hl.web.providers

import android.content.Context
import android.content.pm.ProviderInfo
import androidx.core.content.FileProvider
import com.hl.web.pool.WebViewPoolManager

/**
 * @author  张磊  on  2023/08/11 at 17:21
 * Email: 913305160@qq.com
 */
class WebFileProvider : FileProvider(){

	override fun attachInfo(context: Context, info: ProviderInfo) {
		super.attachInfo(context, info)

		WebViewPoolManager.prepare(context)
	}
}