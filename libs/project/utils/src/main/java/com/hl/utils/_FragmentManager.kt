package com.hl.utils


import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * @author  张磊  on  2021/11/10 at 18:23
 * Email: 913305160@qq.com
 */

fun FragmentManager.replaceFragment(@IdRes containerId: Int, fragment: Fragment) {
	this.beginTransaction()
		.replace(containerId, fragment)
		.commit()
}