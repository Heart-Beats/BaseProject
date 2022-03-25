package com.hl.uikit.demo.fragments

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_slide_view.*
import com.hl.uikit.demo.FragmentContainerActivity
import com.hl.uikit.demo.R
import com.hl.uikit.dialog.LongPressMenuDialog

class SlideViewFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.activity_slide_view
    private var mTouchEvent: MotionEvent? = null
    private var mLongPressMenu: LongPressMenuDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mLongPressMenu = LongPressMenuDialog().apply {
            data = listOf("修改", "删除")
        }
        val parentActivity = requireActivity()
        if (parentActivity is FragmentContainerActivity) {
            parentActivity.touchEvent.observe(viewLifecycleOwner,
                Observer {
                    mTouchEvent = it
                })
        }
        item1?.setOnLongClickListener {
            mLongPressMenu?.show(
                childFragmentManager,
                it,
                mTouchEvent?.rawX?.toInt() ?: -1,
                mTouchEvent?.rawY?.toInt() ?: -1
            )
            true
        }
    }
}