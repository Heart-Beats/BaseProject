package com.hl.web.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.hl.web.R

class ProgressBridgeWebView : BridgeWebView {

    private var isDisplay: Boolean = true
        set(value) {
            field = value
            if (value) {
                if (progressBar?.parent == null) {
                    addView(progressBar)
                }
            } else {
                removeView(progressBar)
            }
        }

    private var progressBar: ProgressBar? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)
        progressBar?.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 6)
        progressBar?.progressDrawable = ContextCompat.getDrawable(context, R.drawable.hl_web_progress_webview_top)

        setProgressDisplay(isDisplay)
        webChromeClient = WebChromeClient()
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        val lp = progressBar?.layoutParams as LayoutParams?
        lp?.x = l
        lp?.y = t
        progressBar?.layoutParams = lp
        super.onScrollChanged(l, t, oldl, oldt)
    }

    fun updateProgressBar(newProgress: Int) {
        if (!isDisplay) return

        if (newProgress == 100) {
            progressBar?.visibility = View.GONE
        } else {
            progressBar?.visibility = View.VISIBLE
            progressBar?.progress = newProgress
        }
    }

    fun setProgressDisplay(isDisplay: Boolean) {
        this.isDisplay = isDisplay
    }

    inner class WebChromeClient : android.webkit.WebChromeClient() {

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            updateProgressBar(newProgress)
            super.onProgressChanged(view, newProgress)
        }
    }
}