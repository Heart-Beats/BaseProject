package com.hl.baseproject

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hl.arch.adapters.BaseAbstractAdapter
import com.hl.arch.mvvm.activity.ViewBindingBaseActivity
import com.hl.baseproject.databinding.ActivityTestBinding
import com.hl.uikit.dpInt
import com.hl.uikit.recyclerview.decoration.GridSpaceItemDecoration
import com.hl.utils.setItemDecoration

class TestActivity : ViewBindingBaseActivity<ActivityTestBinding>() {

	override fun ActivityTestBinding.onViewCreated(savedInstanceState: Bundle?) {
		val data = mutableListOf<String>()
		repeat(20) {
			data.add(it.toString())
		}

		this.textRecyclerView.init(data)
	}

	private fun RecyclerView.init(data: MutableList<String>) {
		val adapter = object : BaseAbstractAdapter<String>(data) {
			override val itemLayout: Int = R.layout.item_text

			override fun onItemBind(viewHolder: ViewHolder, itemData: String?) {
				viewHolder.getView<TextView>(R.id.item_text)?.text = itemData
			}
		}

		this.adapter = adapter
		this.setItemDecoration(GridSpaceItemDecoration(10.dpInt, 20.dpInt))
	}

	override fun onBackPressed() {
		setResult(RESULT_OK, Intent().apply {
			this.putExtra("data", "我是测试1页面数据")
		})
		super.onBackPressed()
	}
}
