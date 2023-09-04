package com.hl.uikit.dialog

import android.app.Dialog
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hl.uikit.BasicDialogFragment
import com.hl.uikit.R
import com.hl.uikit.databinding.UikitLongPressMenuDialogFragmentBinding
import com.hl.uikit.utils.getStatusBarHeight
import com.hl.viewbinding.inflate

/**
 * @Author  张磊  on  2020/10/10 at 11:33
 * Email: 913305160@qq.com
 */
class LongPressMenuDialog : BasicDialogFragment() {

    private val viewBind by inflate<UikitLongPressMenuDialogFragmentBinding>()

    var data: List<String> = listOf()
        set(value) {
            adapter = Adapter(value)
            field = value
        }

    var touchUpListener: (String) -> Unit = { }

    private lateinit var anchorView: View

    private var adapter: Adapter = Adapter(data)
    private val point = Point()

    private object AnimationPivot {
        var left = true
        var top = true
    }

    override fun getTheme(): Int {
        return R.style.UiKit_LongPressMenuDialogStyle
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return viewBind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBind.menuRv.layoutManager = LinearLayoutManager(requireContext())
        viewBind.menuRv.adapter = adapter

        //view未进行绘制之前手动测量大小
        val width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(width, height)

        if (!::anchorView.isInitialized) {
            throw Exception("未设置依附的view, 请调用本类的show方法")
        }
        updateDialog(anchorView, view, point)
    }

    fun show(fragmentManager: FragmentManager, anchorView: View, x: Int = -1, y: Int = -1) {
        this.show(fragmentManager, "")
        this.anchorView = anchorView
        point.x = x
        point.y = y
    }

    private fun updateDialog(anchorView: View, selfView: View, point: Point) {
        if (point.x < 0 || point.y < 0) {
            updateDefaultDialogPosition(anchorView, selfView)
        } else {
            updateDialogPosition(selfView, point)
        }

        updateDialogAnimationStyle()
    }

    private fun updateDefaultDialogPosition(anchorView: View, selfView: View) {
        val dialog = requireDialog()

        val anchorViewPosition = IntArray(2)
        anchorView.getLocationOnScreen(anchorViewPosition)

        val screenSize = Point()
        dialog.window?.windowManager?.defaultDisplay?.getSize(screenSize)

        val positionX: Int
        var positionY = anchorViewPosition[1] + anchorView.height / 2

        //弹出的对话框最下方超出屏幕
        if (positionY + selfView.measuredHeight > screenSize.y) {
            positionY -= selfView.measuredHeight
            AnimationPivot.top = false
        }

        //锚点view比弹出view 宽
        positionX = if (anchorView.width > selfView.measuredWidth) {
            anchorViewPosition[0] + (anchorView.width - selfView.measuredWidth) / 2
        } else {
            (screenSize.x - selfView.measuredWidth) / 2
        }

        dialog.changeDialogWindowAttributes(positionX, positionY - anchorView.getStatusBarHeight())
    }

    private fun updateDialogPosition(selfView: View, point: Point) {
        val dialog = requireDialog()
        val screenSize = Point()
        dialog.window?.windowManager?.defaultDisplay?.getSize(screenSize)

        var positionX = point.x
        var positionY = point.y

        // 弹出对话框右边超出屏幕
        if (positionX + selfView.measuredWidth > screenSize.x) {
            positionX -= selfView.measuredWidth
            AnimationPivot.left = false
        }
        //弹出的对话框最下方超出屏幕
        if (positionY + selfView.measuredHeight > screenSize.y) {
            positionY -= selfView.measuredHeight
            AnimationPivot.top = false
        }

        //设置弹出框的y值（相对window），默认需减去状态栏高度
        dialog.changeDialogWindowAttributes(positionX, positionY - selfView.getStatusBarHeight())
    }

    private fun updateDialogAnimationStyle() {
        val animationStyle = when {
            AnimationPivot.left && AnimationPivot.top -> R.style.UiKit_AnimationOpenLTCloseRB
            AnimationPivot.left && !AnimationPivot.top -> R.style.Uikit_AnimationOpenLBCloseRT
            !AnimationPivot.left && AnimationPivot.top -> R.style.Uikit_AnimationOpenRTCloseLB
            !AnimationPivot.left && !AnimationPivot.top -> R.style.Uikit_AnimationOpenRBCloseLT
            else -> R.style.UiKit_AnimationOpenLTCloseRB
        }
        requireDialog().window?.setWindowAnimations(animationStyle)
    }

    private fun Dialog.changeDialogWindowAttributes(
        windowX: Int,
        windowY: Int,
        width: Int = WindowManager.LayoutParams.WRAP_CONTENT
    ) {
        this.apply {
            val window = this.window

            val layoutParams = window?.attributes?.apply {
                // 两种属性同时生效必须使用 位与运算
                this.gravity = Gravity.TOP or Gravity.START

                this.x = windowX
                this.y = windowY
                this.width = width
                this.height = WindowManager.LayoutParams.WRAP_CONTENT
            }
            window?.attributes = layoutParams
        }
    }

    private inner class Adapter(val data: List<String>) : RecyclerView.Adapter<Adapter.ViewHolder>() {

        inner class ViewHolder(itemView: View, position: Int) : RecyclerView.ViewHolder(itemView) {
            val menu: TextView = itemView.findViewById(R.id.menu_item)

            init {
                itemView.setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            v.isPressed = true
                        }
                        MotionEvent.ACTION_UP -> {
                            touchUpListener(data[position])
                            requireDialog().dismiss()
                            v.performClick()
                        }
                        MotionEvent.ACTION_CANCEL -> {
                            v.isPressed = false
                        }
                    }
                    true
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.uikit_long_press_menu_item, parent, false)
            return ViewHolder(itemView, viewType)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.menu.text = data[position]
        }

        override fun getItemCount(): Int {
            return data.size
        }

    }

}