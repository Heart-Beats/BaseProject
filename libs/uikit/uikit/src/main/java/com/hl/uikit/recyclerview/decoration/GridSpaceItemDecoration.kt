package com.hl.uikit.recyclerview.decoration

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author  张磊  on  2022/08/03 at 16:10
 * Email: 913305160@qq.com
 *
 * @param  horizontalSpacing: 网格水平方向间隔距离
 * @param  verticalSpacing:   网格垂直方向间隔距离
 */
class GridSpaceItemDecoration(@Px val horizontalSpacing: Int, @Px val verticalSpacing: Int = horizontalSpacing) :
	RecyclerView.ItemDecoration() {

	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

		val layoutManager = parent.layoutManager
		if (layoutManager !is GridLayoutManager) {
			return
		}

		// 网格的总列数
		val spanCount = layoutManager.spanCount

		// 当前 item 的位置
		val position: Int = parent.getChildAdapterPosition(view) // item position

		// 当前 item 所在的列数
		val column = position % spanCount // item column

		outRect.left = column * horizontalSpacing / spanCount   //  (column / spanCount) * spacing
		outRect.right =
			horizontalSpacing - (column + 1) * horizontalSpacing / spanCount // spacing -  ((column + 1) / spanCount) * spacing
		if (position >= spanCount) {
			outRect.top = verticalSpacing // item top
		}
	}
}