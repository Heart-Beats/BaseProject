package com.hl.utils.location

import android.content.Context
import com.hl.uikit.onClick
import com.hl.utils.R
import com.lxj.xpopup.core.CenterPopupView
import com.lxj.xpopup.util.XPopupUtils
import kotlinx.android.synthetic.main.hl_utils_pop_request_open_gps.view.*

/**
 * @author  张磊  on  2023/01/11 at 17:15
 * Email: 913305160@qq.com
 */
class RequestOpenGPSPop(context: Context) : CenterPopupView(context) {

	var onSureAction: () -> Unit = {}

	override fun getImplLayoutId() = R.layout.hl_utils_pop_request_open_gps

	override fun getPopupWidth() = (XPopupUtils.getAppWidth(context) * 0.75F).toInt()

	override fun onCreate() {
		action_sure.onClick {
			dismiss()
			onSureAction()
		}
	}
}