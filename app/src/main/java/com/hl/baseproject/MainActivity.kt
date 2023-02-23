package com.hl.baseproject

import android.os.Bundle
import com.hl.arch.base.ViewBindingBaseActivity
import com.hl.baseproject.databinding.ActivityMainBinding
import com.hl.utils.navigation.MyNavHostFragment


class MainActivity : ViewBindingBaseActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SDKInitHelper.initSdk(this)
    }

    override fun ActivityMainBinding.onViewCreated(savedInstanceState: Bundle?) {

        this.navHostFragment.getFragment<MyNavHostFragment>().also {
            it.setCommonNavAnimations {
                this.enterAnim = com.hl.res.R.anim.hl_res_slide_in_right
                this.exitAnim = com.hl.res.R.anim.hl_res_slide_out_left
                this.popEnterAnim = com.hl.res.R.anim.hl_res_slide_in_left
                this.popExitAnim = com.hl.res.R.anim.hl_res_slide_out_right
            }
        }
    }
}