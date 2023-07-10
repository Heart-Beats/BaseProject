package com.hl.uikit.flowlayout

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.hl.uikit.R
import com.hl.uikit.utils.onClick
import java.util.*

/**
 * Created by zhy on 15/9/10.
 */
class TagFlowLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    FlowLayout(context, attrs, defStyle), TagAdapter.OnDataChangedListener {

    var adapter: TagAdapter<Any>? = null
        set(adapter) {
            field = adapter
            this.adapter?.setOnDataChangedListener(this)
            mSelectedView.clear()
            changeAdapter()
        }
    private var mSelectedMax = -1//-1为不限制数量

    private val mSelectedView = HashSet<Int>()

    private var mOnSelectListener: OnSelectListener? = null
    private var mOnTagClickListener: OnTagClickListener? = null

    val selectedList: Set<Int>
        get() = HashSet(mSelectedView)

    interface OnSelectListener {
        fun onSelected(parent: FlowLayout, selectPosSet: Set<Int>)

    }

    interface OnTagClickListener {
        fun onTagClick(view: View, position: Int, parent: FlowLayout): Boolean
    }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.UIKitTagFlowLayout)
        mSelectedMax = ta.getInt(R.styleable.UIKitTagFlowLayout_uikit_max_select, -1)
        ta.recycle()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val cCount = childCount
        for (i in 0 until cCount) {
            val tagView = getChildAt(i) as TagView
            if (tagView.visibility == View.GONE) {
                continue
            }
            if (tagView.tagView.visibility == View.GONE) {
                tagView.visibility = View.GONE
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }


    fun setOnSelectListener(onSelectListener: OnSelectListener) {
        mOnSelectListener = onSelectListener
    }


    fun setOnTagClickListener(onTagClickListener: OnTagClickListener) {
        mOnTagClickListener = onTagClickListener
    }

    private fun changeAdapter() {
        removeAllViews()
        val adapter = this.adapter
        var tagViewContainer: TagView? = null
        val preCheckedList = this.adapter!!.preCheckedList
        for (i in 0 until adapter!!.count) {
            val tagView = adapter!!.getView(this, i, adapter.getItem(i))

            tagViewContainer = TagView(context)
            tagView.isDuplicateParentStateEnabled = true
            if (tagView.layoutParams != null) {
                tagViewContainer.layoutParams = tagView.layoutParams


            } else {
                val lp = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(
                    dip2px(context, 5f),
                    dip2px(context, 10f),
                    dip2px(context, 5f),
                    dip2px(context, 0f)
                )
                tagViewContainer.layoutParams = lp
            }
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            tagView.layoutParams = lp
            tagViewContainer.addView(tagView)
            addView(tagViewContainer)

            if (preCheckedList.contains(i)) {
                setChildChecked(i, tagViewContainer)
            }

            if (this.adapter!!.setSelected(i, adapter.getItem(i))) {
                setChildChecked(i, tagViewContainer)
            }
            tagView.isClickable = false
            val finalTagViewContainer = tagViewContainer
            tagViewContainer.onClick {
                doSelect(finalTagViewContainer, i)
                if (mOnTagClickListener != null) {
                    mOnTagClickListener!!.onTagClick(
                        finalTagViewContainer, i,
                        this@TagFlowLayout
                    )
                }
            }
        }
        mSelectedView.addAll(preCheckedList)
        Log.d("TagFlowLayout", "changeAdapterFlowChild count： $childCount")
    }

    fun setMaxSelectCount(count: Int) {
        if (mSelectedView.size > count) {
            Log.w(TAG, "you has already select more than $count views , so it will be clear .")
            mSelectedView.clear()
        }
        mSelectedMax = count
    }

    private fun setChildChecked(position: Int, view: TagView) {
        view.isChecked = true
        adapter!!.onSelected(position, view.tagView)
    }

    private fun setChildUnChecked(position: Int, view: TagView) {
        view.isChecked = false
        adapter!!.unSelected(position, view.tagView)
    }

    private fun doSelect(child: TagView, position: Int) {
        if (!child.isChecked) {
            //处理max_select=1的情况
            if (mSelectedMax == 1 && mSelectedView.size == 1) {
                val iterator = mSelectedView.iterator()
                val preIndex = iterator.next()
                val pre = getChildAt(preIndex) as TagView
                setChildUnChecked(preIndex, pre)
                setChildChecked(position, child)

                mSelectedView.remove(preIndex)
                mSelectedView.add(position)
            } else {
                if (mSelectedMax > 0 && mSelectedView.size >= mSelectedMax) {
                    return
                }
                setChildChecked(position, child)
                mSelectedView.add(position)
            }
        } else {
            setChildUnChecked(position, child)
            mSelectedView.remove(position)
        }
        if (mOnSelectListener != null) {
            mOnSelectListener!!.onSelected(this@TagFlowLayout,HashSet(mSelectedView))
        }
    }


    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(KEY_DEFAULT, super.onSaveInstanceState())

        var selectPos = ""
        if (mSelectedView.size > 0) {
            for (key in mSelectedView) {
                selectPos += "$key|"
            }
            selectPos = selectPos.substring(0, selectPos.length - 1)
        }
        bundle.putString(KEY_CHOOSE_POS, selectPos)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            val mSelectPos = state.getString(KEY_CHOOSE_POS)
            if (!TextUtils.isEmpty(mSelectPos)) {
                val split = mSelectPos!!.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (pos in split) {
                    val index = Integer.parseInt(pos)
                    mSelectedView.add(index)

                    val tagView = getChildAt(index) as TagView
                    if (tagView != null) {
                        setChildChecked(index, tagView)
                    }
                }

            }
            super.onRestoreInstanceState(state.getParcelable(KEY_DEFAULT))
            return
        }
        super.onRestoreInstanceState(state)
    }


    override fun onChanged() {
        mSelectedView.clear()
        changeAdapter()
    }

    companion object {
        private val TAG = "TagFlowLayout"


        private val KEY_CHOOSE_POS = "key_choose_pos"
        private val KEY_DEFAULT = "key_default"

        fun dip2px(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
    }
}
