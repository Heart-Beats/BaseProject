package com.hl.web.sdk

import androidx.fragment.app.Fragment
import com.github.lzyzsd.jsbridge.BridgeWebView

/**
 * @author  张磊  on  2022/06/13 at 15:11
 * Email: 913305160@qq.com
 */
abstract class ISdkImplProvider {

	/**
	 * 浏览器标识， 可用来区分是否在 APP 中打开
	 */
	open fun provideUserAgent() = "JsBridge"

	/**
	 * H5 相关基本通用方法实现
	 */
	open fun provideStandSdkImpl(webViewFragment: Fragment, bridgeWebView: BridgeWebView): ISdk =
		IStandSdkImpl(webViewFragment, bridgeWebView)

	/**
	 * h5 关于项目相关的自定义方法实现
	 */
	abstract fun provideProjectSdkImpl(webViewFragment: Fragment, bridgeWebView: BridgeWebView): ISdk
}