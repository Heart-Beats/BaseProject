package com.hl.uikit.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.uikit_dialog_progress_bar.*
import com.hl.uikit.BasicDialogFragment
import com.hl.uikit.R
import com.hl.uikit.onClick


/**
 * @author chibb
 * @description:
 * @date :2020/5/30 11:24
 */
class ProgressBarDialog : BasicDialogFragment() {
    var mProgress: Int = 0
    var mNeedForceUpgrade: Boolean = false
    private var mPositiveButtonListener: ((dialog: DialogFragment, which: Int) -> Unit)? = null
    private var mNegativeButtonListener: ((dialog: DialogFragment, which: Int) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.uikit_dialog_progress_bar, container, false)
    }

    private var count: Int = 0
    private var currTime: Long = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProgress(mProgress)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        //连续点击7次退出 避免 网络异常 卡死在当界面
        tv.setOnClickListener {
            val clickTime = System.currentTimeMillis()
            if (clickTime - currTime < 1000) {

                count ++
                if (count > 6) {
                    dismiss()
                }
            }
            currTime = clickTime

        }
        setNeedForce(mNeedForceUpgrade)

        setPositiveButton(mPositiveButtonListener ?: { _, _ -> })
        setNegativeButton(mNegativeButtonListener ?: { _, _ -> })

    }

    override fun getTheme(): Int {
        return R.style.UiKit_ProgressDialog
    }

    fun setProgress(progress: Int) {
        mProgress = progress
        progressBar?.progress = mProgress
    }

    fun setNeedForce(needForceUpgrade: Boolean) {
        mNeedForceUpgrade = needForceUpgrade
        if (mNeedForceUpgrade) {
            lineBg?.setBackgroundResource(R.drawable.uikit_bg_dialog_progress_bar)
            lineButtons?.visibility = View.GONE
        } else {
            lineButtons?.visibility = View.VISIBLE
            lineBg?.setBackgroundResource(R.drawable.uikit_bg_dialog_progress_normal)
        }


    }

    fun setPositiveButton(listener: (dialog: DialogFragment, which: Int) -> Unit) {

        mPositiveButtonListener = listener
        btnConfirm?.onClick {
            listener(this, DialogInterface.BUTTON_POSITIVE)
        }

    }

    fun setNegativeButton(listener: (dialog: DialogFragment, which: Int) -> Unit) {
        mNegativeButtonListener = listener
        btnClose?.onClick {
            listener.invoke(this, DialogInterface.BUTTON_POSITIVE)
        }
    }
}