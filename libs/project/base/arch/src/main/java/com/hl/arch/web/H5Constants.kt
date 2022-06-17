package com.hl.arch.web

import com.hl.arch.BuildConfig

/**
 * @author  张磊  on  2022/06/17 at 19:20
 * Email: 913305160@qq.com
 */
object H5Constants {

	/**
	 * 操作完成的 Action
	 */
	const val ACTION_CALL_BACK = "${BuildConfig.LIBRARY_PACKAGE_NAME}.ACTION_CALL_BACK"

	/**
	 * 操作对应的  CallBackFunction 的 key
	 */
	const val ACTION_CALL_BACK_FUNCTION_KEY="${BuildConfig.LIBRARY_PACKAGE_NAME}.ACTION_CALL_BACK_FUNCTION_KEY"

	/**
	 * 操作返回的数据 对应的 KEY
	 */
	const val ACTION_CALL_BACK_DATA_KEY="${BuildConfig.LIBRARY_PACKAGE_NAME}.ACTION_CALL_BACK_DATA_KEY"

	/**
	 * 分享 的 Action
	 */
	const val ACTION_SHARE_TO_MORE = "${BuildConfig.LIBRARY_PACKAGE_NAME}.ACTION_SHARE_TO_MORE"

	/**
	 * 分享数据的 key
	 */
	const val ACTION_SHARE_MORE_DATA = "${BuildConfig.LIBRARY_PACKAGE_NAME}.ACTION_SHARE_TO_MORE"
}