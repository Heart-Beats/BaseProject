package com.hl.arch.web.client

import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebView
import com.elvishew.xlog.XLog
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

/**
 * @author  张磊  on  2021/12/21 at 14:41
 * Email: 913305160@qq.com
 *
 * BridgeWebView 设置的 webViewClient 必须继承自 BridgeWebViewClient
 */
open class MyBridgeWebViewClient(val webView: BridgeWebView) : BridgeWebViewClient(webView) {

	private val TAG = "MyBridgeWebViewClient"

	override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
		var decodeUrl = url
		try {
			decodeUrl = URLDecoder.decode(decodeUrl, "UTF-8")
		} catch (e: UnsupportedEncodingException) {
			 XLog.e(e)
		}
		if (decodeUrl.startsWith("tel:")) {
			val intent = Intent(Intent.ACTION_VIEW, Uri.parse(decodeUrl))
			webView.context.startActivity(intent)
			return true
		}
		return super.shouldOverrideUrlLoading(view, url)
	}

	override fun onReceivedSslError(webView: WebView?, handler: SslErrorHandler, sslError: SslError?) {
		handler.proceed()
	}
}