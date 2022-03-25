package com.hl.uikit.flowlayout

import android.content.Context
import android.view.View
import android.widget.Checkable
import android.widget.FrameLayout

/**
 * Created by zhy on 15/9/10.
 */
class TagView(context: Context) : FrameLayout(context), Checkable {
    private var isChecked: Boolean = false

    val tagView: View
        get() = getChildAt(0)

    public override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val states = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked()) {
            View.mergeDrawableStates(states, CHECK_STATE)
        }
        return states
    }


    /**
     * Change the checked state of the view
     *
     * @param checked The new checked state
     */
    override fun setChecked(checked: Boolean) {
        if (this.isChecked != checked) {
            this.isChecked = checked
            refreshDrawableState()
        }
    }

    /**
     * @return The current checked state of the view
     */
    override fun isChecked(): Boolean {
        return isChecked
    }

    /**
     * Change the checked state of the view to the inverse of its current state
     */
    override fun toggle() {
        setChecked(!isChecked)
    }

    companion object {
        private val CHECK_STATE = intArrayOf(android.R.attr.state_checked)
    }


}
