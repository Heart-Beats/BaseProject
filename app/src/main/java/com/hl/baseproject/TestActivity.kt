package com.hl.baseproject

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hl.arch.adapters.BaseMultiAdapter
import com.hl.arch.adapters.BaseSingleAdapter
import com.hl.arch.adapters.itemprovider.BaseItemProvider
import com.hl.arch.adapters.viewholder.BaseViewHolder
import com.hl.baseproject.databinding.ActivityTestBinding
import com.hl.ui.base.ViewBindingBaseActivity
import com.hl.ui.utils.dpInt
import com.hl.ui.utils.onClick
import com.hl.uikit.recyclerview.decoration.GridSpaceItemDecoration
import com.hl.uikit.recyclerview.decoration.RecyclerViewDividerDecoration
import com.hl.utils.setItemDecoration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TestActivity : ViewBindingBaseActivity<ActivityTestBinding>() {

	override fun ActivityTestBinding.onViewCreated(savedInstanceState: Bundle?) {
		val data = mutableListOf<String>()
		repeat(16) {
			data.add(it.toString())
		}

		this.testRecyclerView1.init(data)

		this.testRecyclerView2.init2(data)

		val adapter = testRecyclerView2.adapter as BaseSingleAdapter<String>


		var count = 0

		this.addData.onClick {
			adapter.insertData("测试数据${count++}")
		}

		this.removeData.onClick {
			adapter.removeData("测试数据${count--}")
			println("更新后获取到的列表数据 == ${adapter.getData()}")
		}

		this.updateData.onClick {
			adapter.updateData(data)
		}
	}

	private fun RecyclerView.init(data: MutableList<String>) {
		val baseMultiAdapter = object : BaseMultiAdapter<String>(mutableListOf()) {

			override fun registerItemProvider(position: Int, itemData: String): BaseItemProvider<out String> {
				return object : BaseItemProvider<String>() {

					override val layoutId: Int = R.layout.item_text
					override val itemViewType: Int = 9

					override fun onItemBind(viewHolder: BaseViewHolder<String>, itemData: String) {
						viewHolder.getView<TextView>(R.id.item_text)?.text = itemData
					}

				}
			}
		}

		baseMultiAdapter.emptyView = TextView(this.context).apply {
			text = "我是空态页面"
		}

		lifecycleScope.launch {
			delay(2000)
			baseMultiAdapter.updateData(data)
		}

		this.adapter = baseMultiAdapter
		this.layoutManager = GridLayoutManager(context, 5)
		this.setItemDecoration(GridSpaceItemDecoration(10.dpInt, 20.dpInt))
	}

	private fun RecyclerView.init2(data: MutableList<String>) {
		val adapter = object : BaseSingleAdapter<String>(mutableListOf()) {

			override val itemLayout: Int = R.layout.item_text_2

			override fun onItemBind(viewHolder: BaseViewHolder<String>, itemData: String) {
				viewHolder.getView<TextView>(R.id.item_text)?.text = itemData
			}
		}

		adapter.headerView = TextView(this.context).apply {
			text = "我是头部"
		}

		adapter.footerView = TextView(this.context).apply {
			text = "我是尾部"
		}

		adapter.emptyView = TextView(this.context).apply {
			text = "我是空态页面"
		}

		this.adapter = adapter
		this.setItemDecoration(RecyclerViewDividerDecoration().apply {
			this.dividerSpace = 20.dpInt
		})
	}

	@Deprecated("Deprecated in Java")
	override fun onBackPressed() {
		setResult(RESULT_OK, Intent().apply {
			this.putExtra("data", "我是测试1页面数据")
		})
		super.onBackPressed()
	}
}
