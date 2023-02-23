package com.hl.utils.navigation

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

/**
 * @author  张磊  on  2023/02/22 at 17:03
 * Email: 913305160@qq.com
 */
/**
 * Find a [NavController] given a [Fragment]
 *
 * Calling this on a Fragment that is not a [NavHostFragment] or within a [NavHostFragment]
 * will result in an [IllegalStateException]
 */
fun Fragment.findNavController(): NavController =
	NavHostFragment.findNavController(this)