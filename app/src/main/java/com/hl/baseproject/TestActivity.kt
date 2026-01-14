package com.hl.baseproject

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.hl.baseproject.databinding.ActivityTestBinding
import com.hl.baseproject.databinding.ItemText2Binding
import com.hl.baseproject.databinding.ItemTextBinding
import com.hl.rvadapter.IDataType
import com.hl.rvadapter.ItemViewType
import com.hl.rvadapter.binding.BaseBindingMultiAdapter
import com.hl.rvadapter.binding.BaseBindingSingleAdapter
import com.hl.rvadapter.binding.itemprovider.BaseBindingItemProvider
import com.hl.rvadapter.binding.viewholder.BaseBindingViewHolder
import com.hl.ui.base.ViewBindingBaseActivity
import com.hl.ui.utils.dpInt
import com.hl.ui.utils.onClick
import com.hl.uikit.recyclerview.decoration.GridSpaceItemDecoration
import com.hl.uikit.recyclerview.decoration.RecyclerViewDividerDecoration
import com.hl.utils.views.setItemDecoration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TestActivity : ViewBindingBaseActivity<ActivityTestBinding>() {

	override fun ActivityTestBinding.onViewCreated(savedInstanceState: Bundle?) {
        val data = mutableListOf<TestData>()
		repeat(16) {
            data.add(TestData(it.toString()))
		}

		this.testRecyclerView1.init(data)

		this.testRecyclerView2.init2(data)

        val adapter =
            // testRecyclerView2.adapter as BaseSingleAdapter<TestData>
            testRecyclerView2.adapter as BaseBindingSingleAdapter<TestData, ItemText2Binding>


		var count = 0

		this.addData.onClick {
            adapter.insertData(TestData("测试数据${count++}"))
		}

		this.removeData.onClick {
            adapter.removeData(TestData("测试数据${count--}"))
			println("更新后获取到的列表数据 == ${adapter.getData()}")
		}

		this.updateData.onClick {
			adapter.updateData(data)
		}
	}

    private fun RecyclerView.init(data: MutableList<TestData>) {
        // val baseMultiAdapter = object : BaseMultiAdapter<TestData>(mutableListOf()) {
		//
        //     override fun registerItemProvider(position: Int, itemData: TestData): BaseItemProvider<out TestData> {
        //         return object : BaseItemProvider<TestData>() {
		//
        //             override val layoutId: Int = R.layout.item_text
		//
        //             override fun onItemBind(viewHolder: BaseViewHolder<TestData>, itemData: TestData) {
        //                 viewHolder.getView<TextView>(R.id.item_text)?.text = itemData.text
        //             }
		//
        //         }
        //     }
        // }

       val baseMultiAdapter = object : BaseBindingMultiAdapter<TestData>(mutableListOf()) {

           override fun registerItemProvider(
               position: Int,
               itemData: TestData
           ): BaseBindingItemProvider<out TestData, out ViewBinding> {
               return object : BaseBindingItemProvider<TestData, ItemTextBinding>() {
                   override fun onItemBind(
	                   viewHolder: BaseBindingViewHolder<TestData, ItemTextBinding>,
	                   itemData: TestData
                   ) {
                       viewHolder.binding.itemText.text = itemData.text
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

    private fun RecyclerView.init2(data: MutableList<TestData>) {
        // val adapter = object : BaseSingleAdapter<TestData>(mutableListOf()) {
		//
        //     override val itemLayout: Int = R.layout.item_text_2
		//
        //     override fun onItemBind(viewHolder: BaseViewHolder<TestData>, itemData: TestData) {
        //         viewHolder.getView<TextView>(R.id.item_text)?.text = itemData.text
        //     }
        // }

       val adapter =
           object : BaseBindingSingleAdapter<TestData, ItemText2Binding>(mutableListOf()) {
               override fun onItemBind(
                   viewHolder: BaseBindingViewHolder<TestData, ItemText2Binding>,
                   itemData: TestData
               ) {
                   viewHolder.binding.itemText.text = itemData.text
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

    private data class TestData(
        val text: String = "",
        override var itemViewType: Int = ItemViewType.DATA.ordinal,
    ) : IDataType
}
