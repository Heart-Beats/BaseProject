package com.hl.utils

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.getFragmentById(@IdRes id: Int): Fragment? {
    return supportFragmentManager.findFragmentById(id)
}


fun FragmentActivity.getCurrentNavigationFragment(): Fragment? {
    return supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.firstOrNull()
}


/**
 *  Activity 中替换指定 view 为 Fragment
 */
fun FragmentActivity.replaceFragment(
    @IdRes containerId: Int,
    fragmentClass: Class<out Fragment>,
    tag: String? = fragmentClass.simpleName,
    bundle: Bundle? = null
) {
    supportFragmentManager.beginTransaction()
        .replace(containerId, fragmentClass, bundle, tag)
        .commitAllowingStateLoss()
}

/**
 *  Activity 中替换指定 view 为 Fragment
 */
fun FragmentActivity.replaceFragment(
    @IdRes containerId: Int,
    fragment: Fragment,
    tag: String? = fragment.javaClass.simpleName
) {
    replaceFragment(containerId, fragment::class.java, tag, fragment.arguments)
}

/**
 *  Activity 中替换指定 view 为 Fragment
 */
inline fun <reified T : Fragment> FragmentActivity.replaceFragment(
    @IdRes containerId: Int,
    tag: String? = T::class.java.simpleName,
    argumentsBlock: Bundle.() -> Unit = {},
) {
    val bundle = bundleOf().apply(argumentsBlock)
    replaceFragment(containerId, T::class.java, tag, bundle)
}
