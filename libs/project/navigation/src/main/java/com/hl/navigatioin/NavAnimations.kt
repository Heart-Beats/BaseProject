package com.hl.navigatioin

import androidx.annotation.AnimRes
import androidx.annotation.StringDef

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


@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@StringDef(
    NavAnimateScene.ENTER_ANIM,
    NavAnimateScene.EXIT_ANIM,
    NavAnimateScene.POP_ENTER_ANIM,
    NavAnimateScene.POP_EXIT_ANIM
)
annotation class NavAnimateScene {
    companion object {
        const val ENTER_ANIM = "enterAnim"
        const val EXIT_ANIM = "exitAnim"
        const val POP_ENTER_ANIM = "popEnterAnim"
        const val POP_EXIT_ANIM = "popExitAnim"
    }
}