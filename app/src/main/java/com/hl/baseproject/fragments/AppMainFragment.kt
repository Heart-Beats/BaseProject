package com.hl.baseproject.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hl.baseproject.base.BaseFragment
import com.hl.baseproject.databinding.FragmentAppMainBinding
import com.hl.baseproject.fragments.home.HomeFragment
import com.hl.utils.views.setupWithViewPager2


class AppMainFragment : BaseFragment<FragmentAppMainBinding>() {

	companion object {
		const val SHOW_FRAGMENT_ACTION = "show_fragment_action"
		const val SHOW_FRAGMENT_NAME_KEY = "show_fragment_name_key"
	}

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

	override fun onFragmentShow() {
		super.onFragmentShow()

		val currentItem = viewBinding.mainViewPager.currentItem
		val currentFragment = fragments[currentItem]

		requireContext().sendBroadcast(Intent(SHOW_FRAGMENT_ACTION).apply {
			this.putExtra(SHOW_FRAGMENT_NAME_KEY, currentFragment.javaClass.name)
		})
	}
}