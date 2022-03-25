package com.hl.uikit.demo.loading

import android.os.Bundle
import android.view.View
import android.webkit.*
import com.hl.uikit.demo.FragmentContainerActivity
import com.hl.uikit.demo.R
import com.hl.uikit.demo.fragments.BaseFragment
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewFragment : BaseFragment() {
    var url: String = "https://www.baidu.com"
    var title: String? = null

    companion object {
        const val KEY_TITLE = "WebView_Title"
        const val KEY_URL = "WebView_URL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = arguments ?: return
        val argumentUrl = arguments.getString(KEY_URL)
        val argumentTitle = arguments.getString(KEY_TITLE)
        if (!argumentUrl.isNullOrEmpty()) {
            url = argumentUrl
        }
        if (!argumentTitle.isNullOrEmpty()) {
            title = argumentTitle
        }
    }

    override val layout: Int
        get() = R.layout.activity_web_view

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (title?.isNotEmpty() == true) {
            toolbar?.title = title
        }
        webView.settings.apply {
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            javaScriptEnabled = true
            setSupportZoom(true)
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = true
            setSupportZoom(false)
            displayZoomControls = false
        }
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.loadUrl(url)
        webView?.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                if (title?.isNotEmpty() == true && this@WebViewFragment.title.isNullOrEmpty()) {
                    toolbar?.title = title
                }
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                webView?.updateProgressBar(newProgress)
                super.onProgressChanged(view, newProgress)
            }
        }
        webView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return request?.url?.toString()?.also {
                    view?.loadUrl(it)
                } == null
            }
        }

        val activity = requireActivity()
        if (activity is FragmentContainerActivity) {
            activity.onBackPressedListener = {
                if (webView?.canGoBack() == true) {
                    webView?.goBack()
                    true
                } else {
                    false
                }
            }
        }
    }
}