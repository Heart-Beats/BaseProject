package com.hl.utils

import android.os.Bundle
import androidx.annotation.IdRes
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
    fragment: Fragment,
    tag: String? = fragment.javaClass.simpleName
) {
    supportFragmentManager.beginTransaction()
        .replace(containerId, fragment, tag)
        .commitAllowingStateLoss()
}

/**
 *  Activity 中替换指定 view 为 Fragment
 */
fun FragmentActivity.replaceFragment(
    @IdRes containerId: Int,
    fragmentClass: Class<out Fragment>,
    bundle: Bundle? = null,
    tag: String? = fragmentClass.simpleName
) {
    supportFragmentManager.beginTransaction()
        .replace(containerId, fragmentClass, bundle, tag)
        .commitAllowingStateLoss()
}
