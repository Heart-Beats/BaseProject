package com.hl.baseproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hl.arch.mvvm.activity.ViewBindingBaseActivity
import com.hl.baseproject.databinding.ActivityMainBinding
import com.hl.baseproject.fragments.MainFragment
import com.hl.baseproject.fragments.TestFragment
import com.hl.uikit.onClick


class MainActivity : ViewBindingBaseActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SDKInitHelper.initSdk(this)
    }

    override fun ActivityMainBinding.onViewCreated(savedInstanceState: Bundle?) {

        this.viewPager.adapter = object : FragmentStateAdapter(this@MainActivity) {
            override fun getItemCount() = 2

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> MainFragment()
                    else -> TestFragment()
                }
            }
        }

        this.main.onClick {
            viewPager.setCurrentItem(0)
        }

        this.test.onClick {
            viewPager.setCurrentItem(1)
        }

    }

}