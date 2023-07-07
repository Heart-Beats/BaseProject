package com.hl.arch.base

import android.os.Bundle
import android.view.View
import com.hl.ui.base.ComposeBaseFragment

/**
 * @author  张磊  on  2023/07/06 at 20:58
 * Email: 913305160@qq.com
 */
abstract class ComposeBaseNavigationFragment : ComposeBaseFragment(), NavigationFragmentDelegate by BaseNavigationFragmentDelegate() {

	init {
		// 必须执行此方法给 NavigationFragmentDelegate 设置代理的 Fragment
		setDelegateFragment(this)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		// 重写 onViewCreated 方法再执行下述方法才可完成 Fragment 针对 navigation 的初始化
		onNavigationFragmentViewCreated()
	}
}