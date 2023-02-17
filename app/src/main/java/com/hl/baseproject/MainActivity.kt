package com.hl.baseproject

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.hl.arch.base.ViewBindingBaseActivity
import com.hl.baseproject.databinding.ActivityMainBinding
import com.hl.res.R
import com.hl.utils.navigation.MyNavHostFragment


class MainActivity : ViewBindingBaseActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SDKInitHelper.initSdk(this)
    }

    override fun ActivityMainBinding.onViewCreated(savedInstanceState: Bundle?) {

        val myNavHostFragment = this.navHostFragment.getFragment<MyNavHostFragment>().also {
            it.setCommonNavAnimations {
                this.enterAnim = R.anim.hl_res_slide_in_right
                this.exitAnim = R.anim.hl_res_slide_out_left
                this.popEnterAnim = R.anim.hl_res_slide_in_left
                this.popExitAnim = R.anim.hl_res_slide_out_right
            }
        }

        this.bottomNavigationView.setupWithNavController(myNavHostFragment.findNavController())
    }

}