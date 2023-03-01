package com.hl.navigatioin

import androidx.annotation.AnimRes

data class NavAnimations(
        @AnimRes var enterAnim: Int? = null,
        @AnimRes var exitAnim: Int? = null,
        @AnimRes var popEnterAnim: Int? = null,
        @AnimRes var popExitAnim: Int? = null,
) {
    companion object {
        const val NO_ANIM = 0
    }
}