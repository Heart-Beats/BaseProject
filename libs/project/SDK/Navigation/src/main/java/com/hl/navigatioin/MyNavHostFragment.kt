package com.hl.navigatioin

import android.view.View
import androidx.annotation.StringRes
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment

/**
 * @Author  张磊  on  2020/12/02 at 14:02
 * Email: 913305160@qq.com
 */
class MyNavHostFragment : NavHostFragment() {

    private var navAnimations: NavAnimations? = null
    private val specialDeepLinks = mutableListOf<String>()

    @Deprecated("Use {@link #onCreateNavController(NavController)}")
    override fun createFragmentNavigator(): Navigator<out FragmentNavigator.Destination> {
        return MyFragmentNavigator(this, childFragmentManager, getContainerId())
    }

    /**
     * 因父类方法私有导致的重写，实现逻辑一致
     */
    private fun getContainerId(): Int {
        val id = id
        return if (id != 0 && id != View.NO_ID) {
            id
        } else com.androidx.navigation.R.id.nav_host_fragment_container
    }

    /**
     * 统一设置导航的跳转动画以及返回动画，
     * 若 fragment 子标签对 action 已设置相关动画属性，则最终生效的是 action 中的动画属性
     */
    fun setCommonNavAnimations(navAnimationsInit: NavAnimations.() -> Unit) {
        navAnimations = NavAnimations().apply(navAnimationsInit)
    }

    fun getCommonNavAnimations() = navAnimations


    /**
     * 统一设置特殊的深链接，对于这些深链接指向的 Fragment 采用 add 方式添加，同时不包含在 NavController 的堆栈中
     *
     * 主要使用场景适用于添加一些透明的 Fragment 处理特殊的业务
     */
    fun setSpecialDeepLinks(deepLinks: List<String>? = null, @StringRes deepLinksRes: List<Int>? = null) {
        deepLinks?.also {
            specialDeepLinks.addAll(it)
        }
        deepLinksRes?.run {
            this.map {
                requireContext().getString(it)
            }.forEach {
                if (it !in specialDeepLinks) {
                    specialDeepLinks.add(it)
                }
            }
        }
    }

    fun getSpecialDeepLinks() = specialDeepLinks
}