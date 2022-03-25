package com.hl.uikit.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import kotlinx.android.synthetic.main.uikit_loading_dialog_fragment.lottieView
import kotlinx.android.synthetic.main.uikit_loading_dialog_intercept_fragment.*
import com.hl.uikit.BasicDialogFragment
import com.hl.uikit.R

class LoadingDialogFragment : BasicDialogFragment() {
    var lottieAssetName: String? = null
        set(value) {
            field = value
            if (!value.isNullOrEmpty()) {
                lottieView?.setAnimation(value)
            }
        }
    var lottieUrl: String? = null
        set(value) {
            field = value
            if (!value.isNullOrEmpty()) {
                lottieView?.setAnimationFromUrl(value)
            }
        }
    var allowIntercept: Boolean = false
    var message: CharSequence? = null
        set(value) {
            field = value
            if (!value.isNullOrEmpty()) {
                tvMessage?.text = value
            }
        }

    companion object {
        const val DEFAULT_LOTTIE_FILE_NAME = "lottie/uikit_dialog_loading.json"
        const val DEFAULT_LOTTIE_INTERCEPT_FILE_NAME = "lottie/uikit_dialog_intercept_loading.json"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            when (allowIntercept) {
                true -> R.layout.uikit_loading_dialog_intercept_fragment
                false -> R.layout.uikit_loading_dialog_fragment
            }, container, false
        )
    }

    override fun getTheme(): Int {
        return R.style.UiKit_LoadingDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (lottieAssetName.isNullOrEmpty() && lottieUrl.isNullOrEmpty()) {
            lottieView?.setAnimation(
                when (allowIntercept) {
                    true -> DEFAULT_LOTTIE_INTERCEPT_FILE_NAME
                    false -> DEFAULT_LOTTIE_FILE_NAME
                }
            )
            lottieView?.playAnimation()
        } else {
            apply {
                lottieAssetName?.let {
                    lottieView?.setAnimation(it)
                    lottieView?.playAnimation()
                    return@apply
                }
                lottieUrl?.let {
                    lottieView?.setAnimationFromUrl(lottieUrl)
                    lottieView?.playAnimation()
                    return@apply
                }
            }
        }

        if (allowIntercept) {
            val window = dialog?.window
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            isCancelable = false
        } else {
            val window = dialog?.window
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val pm = window?.attributes
            pm?.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            window?.attributes = pm
            isCancelable = true
        }

        message = message
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            attributes = attributes.apply {
                dimAmount = 0.0f
            }
        }
    }
}