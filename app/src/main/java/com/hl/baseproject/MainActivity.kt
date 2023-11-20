package com.hl.baseproject

import android.os.Bundle
import com.hl.arch.mvvm.vmDelegate.BaseViewModelDelegate
import com.hl.arch.mvvm.vmDelegate.ViewModelDelegate
import com.hl.baseproject.databinding.ActivityMainBinding
import com.hl.ui.base.ViewBindingBaseActivity


class MainActivity : ViewBindingBaseActivity<ActivityMainBinding>(), ViewModelDelegate by BaseViewModelDelegate() {

    override fun onCreate(savedInstanceState: Bundle?) {
        registerOnViewModelCreated(this)
        super.onCreate(savedInstanceState)
        SDKInitHelper.initSdk(this)
    }

    override fun ActivityMainBinding.onViewCreated(savedInstanceState: Bundle?) {

        this.navHostFragment.getFragment<com.hl.navigatioin.MyNavHostFragment>().also {
            it.setCommonNavAnimations {
                this.enterAnim = com.hl.res.R.anim.hl_res_slide_in_right
                this.exitAnim = com.hl.res.R.anim.hl_res_slide_out_left
                this.popEnterAnim = com.hl.res.R.anim.hl_res_slide_in_left
                this.popExitAnim = com.hl.res.R.anim.hl_res_slide_out_right
            }
        }
    }
}