package com.hl.utils

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.getFragmentById(@IdRes id: Int): Fragment? {
    return supportFragmentManager.findFragmentById(id)
}


fun FragmentActivity.getCurrentNavigationFragment(): Fragment? {
    return supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.firstOrNull()
}

fun FragmentActivity.replaceFragment(
    @IdRes containerId: Int,
    fragment: Fragment,
    tag: String? = fragment.javaClass.simpleName
) {
    supportFragmentManager.beginTransaction()
        .replace(containerId, fragment, tag)
        .commitAllowingStateLoss()
}
