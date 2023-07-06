package com.hl.web.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity


/**
 * 获取 navigation 当前使用的 Fragment
 */
fun FragmentActivity.getCurrentNavigationFragment(): Fragment? {
	return supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.firstOrNull()
}