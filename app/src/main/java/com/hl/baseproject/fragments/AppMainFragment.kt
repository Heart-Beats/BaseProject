package com.hl.baseproject.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blankj.utilcode.util.ActivityUtils
import com.hl.baseproject.base.BaseFragment
import com.hl.baseproject.databinding.FragmentAppMainBinding
import com.hl.baseproject.fragments.home.HomeFragment
import com.hl.utils.navigation.ui.setupWithViewPager2


class AppMainFragment : BaseFragment<FragmentAppMainBinding>() {

	private val fragments = listOf<Fragment>(
		HomeFragment(),
		MainFragment(),
		TestFragment()
	)

	override fun FragmentAppMainBinding.onViewCreated(savedInstanceState: Bundle?) {
		this.mainViewPager.adapter = object : FragmentStateAdapter(this@AppMainFragment) {
			override fun getItemCount(): Int = fragments.size

			override fun createFragment(position: Int): Fragment = fragments[position]
		}
		// 禁止首页滑动
		mainViewPager.isUserInputEnabled = false

		this.bottomNavigationView.setupWithViewPager2(this.mainViewPager, false)
	}
}