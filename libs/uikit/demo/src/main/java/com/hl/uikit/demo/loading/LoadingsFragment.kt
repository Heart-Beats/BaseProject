package com.hl.uikit.demo.loading

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_loadings.*
import com.hl.uikit.demo.R
import com.hl.uikit.demo.fragments.BaseFragment
import com.hl.uikit.demo.startFragment
import com.hl.uikit.dialog.LoadingDialogFragment
import com.hl.ui.utils.onClick
import com.hl.uikit.refresh.UIKitCommonRefreshFooter
import com.hl.uikit.refresh.UIKitCommonRefreshHeader
import com.hl.uikit.toast
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader

class LoadingsFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.activity_loadings

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        item1?.onClick {
            LoadingDialogFragment()
                .apply {
                    allowIntercept = false
                }
                .show(childFragmentManager, "loading")
        }
        item2?.onClick {
            val mLoadingDialog = LoadingDialogFragment()
                .apply {
                    allowIntercept = true
                }
            mLoadingDialog.show(childFragmentManager, "loading")
            it.postDelayed({
                mLoadingDialog.dismiss()
            }, 5000)
        }
        item3?.onClick {
            toast("当前屏幕下拉")
        }
        item4?.onClick {
            toast("当前屏幕上拉")
        }
        item5?.onClick {
            startFragment(WebViewFragment::class.java)
        }

        val header = ClassicsHeader(requireContext())
        smartRefresh?.setRefreshHeader(header)
        val footer = ClassicsFooter(requireContext())
        smartRefresh?.setRefreshFooter(footer)
        smartRefresh?.setEnableFooterFollowWhenNoMoreData(true)
        smartRefresh?.setOnRefreshListener {
            smartRefresh?.postDelayed({
                smartRefresh?.finishRefresh()
            }, 3000)
        }
        smartRefresh?.setOnLoadMoreListener {
            smartRefresh?.postDelayed({
                smartRefresh?.finishLoadMoreWithNoMoreData()
            }, 3000)
        }
    }
}