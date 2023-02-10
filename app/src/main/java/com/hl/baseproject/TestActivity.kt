package com.hl.baseproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hl.arch.adapters.BaseAbstractAdapter
import com.hl.arch.base.ViewBindingBaseActivity
import com.hl.baseproject.databinding.ActivityTestBinding
import com.hl.uikit.recyclerview.decoration.GridSpaceItemDecoration
import com.hl.uikit.recyclerview.decoration.RecyclerViewDividerDecoration
import com.hl.utils.dpInt
import com.hl.utils.setItemDecoration
import kotlin.math.roundToInt

class TestActivity : ViewBindingBaseActivity<ActivityTestBinding>() {

	override fun ActivityTestBinding.onViewCreated(savedInstanceState: Bundle?) {
		val data = mutableListOf<String>()
		repeat(16) {
			data.add(it.toString())
		}

		this.textRecyclerView.init(data)

		this.testRecyclerView.init2(data)
	}

	private fun RecyclerView.init(data: MutableList<String>) {
		val adapter = object : BaseAbstractAdapter<String>(data) {
			override val itemLayout: Int = R.layout.item_text

			override fun onItemBind(viewHolder: ViewHolder, itemData: String?) {
				viewHolder.getView<TextView>(R.id.item_text)?.text = itemData
			}
		}

		this.adapter = adapter
		this.layoutManager = MyGridLayoutManager(context, 5)
		this.setItemDecoration(GridSpaceItemDecoration(10.dpInt, 20.dpInt))
	}

	private fun RecyclerView.init2(data: MutableList<String>) {
		val adapter = object : BaseAbstractAdapter<String>(data) {
			override val itemLayout: Int = R.layout.item_text_2

			override fun onItemBind(viewHolder: ViewHolder, itemData: String?) {
				viewHolder.getView<TextView>(R.id.item_text)?.text = itemData
			}
		}

		this.adapter = adapter
		this.setItemDecoration(RecyclerViewDividerDecoration().apply {
			this.dividerSpace = 20.dpInt
		})
	}

	override fun onBackPressed() {
		setResult(RESULT_OK, Intent().apply {
			this.putExtra("data", "我是测试1页面数据")
		})
		super.onBackPressed()
	}

	private class MyGridLayoutManager(context: Context, spanCount: Int) : GridLayoutManager(context, spanCount) {

		override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
			val itemCount = itemCount
			val remainder = itemCount % spanCount
			if (remainder == 0) {
				// 刚好满格
				super.onLayoutChildren(recycler, state)
			} else {
				//得到子view的宽和高，这里的item的宽高都是一样的，所以只需要取第一个进行一次测量
				val scrap: View = recycler.getViewForPosition(0)
				addView(scrap)
				measureChildWithMargins(scrap, 0, 0)
				//计算测量布局的宽高
				val decoratedChildWidth = getDecoratedMeasuredWidth(scrap)
				val decoratedChildHeight = getDecoratedMeasuredHeight(scrap)

				for (i in 0 until getItemCount()) {
					if (i < itemCount - remainder) {
						// 仍是满行
						super.onLayoutChildren(recycler, state)
					} else {

						println("decoratedChildWidth ====== $decoratedChildWidth")
						val startX = ((getHorizontalSpace().toFloat() - decoratedChildWidth) / 2).roundToInt()
						layoutDecorated(
							recycler.getViewForPosition(i),
							startX,
							0,
							startX + decoratedChildWidth,
							decoratedChildHeight
						)
						println("$i 开始居中布局， left= $startX, top= 0, right=${startX + decoratedChildWidth}, bottom = $decoratedChildHeight")
					}
				}
			}
		}

		private fun getHorizontalSpace(): Int {
			return width - paddingLeft - paddingRight
		}

		private fun getVerticalSpace(): Int {
			return height - paddingTop - paddingBottom
		}
	}
}
