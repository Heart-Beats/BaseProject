package com.hl.baseproject.web

import android.os.Bundle
import android.view.View
import com.hl.arch.base.BaseNavigationFragmentDelegate
import com.hl.arch.base.NavigationFragmentDelegate
import com.hl.web.WebViewFragment

/**
 * @author  张磊  on  2023/07/07 at 16:38
 * Email: 913305160@qq.com
 */

class WebViewNavigationFragment : WebViewFragment(), NavigationFragmentDelegate by BaseNavigationFragmentDelegate() {

	init {
		setDelegateFragment(this)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		onNavigationFragmentViewCreated()
	}
}