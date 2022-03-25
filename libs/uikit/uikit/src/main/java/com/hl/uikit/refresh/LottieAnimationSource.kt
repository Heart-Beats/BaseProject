package com.hl.uikit.refresh

import com.airbnb.lottie.LottieAnimationView

/**
 * @Author  张磊  on  2021/01/12 at 18:37
 * Email: 913305160@qq.com
 */
data class LottieAnimationSource(
        val rawRes: Int? = null,
        val url: String? = null
) {
    fun isCanUse(): Boolean {
        return rawRes != null || url != null
    }
}

fun LottieAnimationView.setAnimation(lottieAnimationSource: LottieAnimationSource) {
    lottieAnimationSource.rawRes?.also {
        this.setAnimation(it)
    }
    lottieAnimationSource.url?.also {
        this.setAnimationFromUrl(it)
    }
}