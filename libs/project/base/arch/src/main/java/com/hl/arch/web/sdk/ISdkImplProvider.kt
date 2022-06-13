package com.hl.arch.web.sdk

import androidx.fragment.app.Fragment
import com.github.lzyzsd.jsbridge.BridgeHandler
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.hl.utils.ProxyHandler

/**
 * @author  张磊  on  2022/06/13 at 15:11
 * Email: 913305160@qq.com
 */
abstract class ISdkImplProvider {

	/**
	 * H5 相关基本通用方法实现
	 */
	open fun provideStandSdkImpl(
		currentFragment: Fragment,
		bridgeWebView: BridgeWebView,
		bridgeHandlerProxy: ProxyHandler<BridgeHandler>
	): ISdk {
		return IStandSdkImpl(currentFragment, bridgeWebView, bridgeHandlerProxy)
	}

	/**
	 * h5 关于项目相关的自定义方法实现
	 */
	abstract fun provideProjectSdkImpl(
		currentFragment: Fragment,
		bridgeWebView: BridgeWebView,
		bridgeHandlerProxy: ProxyHandler<BridgeHandler>
	): ISdk
}