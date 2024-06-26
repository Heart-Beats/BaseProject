package com.hl.baseproject.fragments.home

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.elvishew.xlog.XLog
import androidx.fragment.app.activityViewModels
import com.hl.baseproject.R
import com.hl.baseproject.base.BaseFragment
import com.hl.baseproject.databinding.FragmentHomeMiddleBinding
import com.hl.baseproject.repository.network.bean.Article
import com.hl.baseproject.viewmodels.DataViewModel
import com.hl.baseproject.viewmodels.HomeViewModel
import com.hl.imageload.GlideUtil
import com.hl.dateutil.toFormatString
import com.hl.utils.onceLastObserve
import com.hl.utils.views.setItemTouchHelper
import com.hl.popup.showImage
import com.hl.web.navigateToWeb
import java.security.SecureRandom
import java.util.Date

/**
 * @author  张磊  on  2023/02/23 at 17:08
 * Email: 913305160@qq.com
 */
class HomeMiddleFragment : BaseFragment<FragmentHomeMiddleBinding>() {

	private val homeViewModel by activityViewModels<HomeViewModel>()
	private val dataViewModel by activityViewModels<DataViewModel>()

	private lateinit var homeArticledAdapter: com.hl.rvadapter.BaseSingleAdapter<Article>

	private var curPage = 0

	override fun FragmentHomeMiddleBinding.onViewCreated(savedInstanceState: Bundle?) {
		dataViewModel.imagesLiveData.onceLastObserve(viewLifecycleOwner) {
			initHomeArticleAdapter(it)
		}

		// 解决 CoordinatorLayout + AppbarLayout + NestedScrollView + Banner 滑动冲突
		// homeArticleList.isNestedScrollingEnabled = false

		this.refreshLayout.run {
			setOnRefreshListener {
				curPage = 0
				homeViewModel.getHomeArticleList(curPage)
			}
			setOnLoadMoreListener {
				homeViewModel.getHomeArticleList(++curPage)
			}
		}

		homeViewModel.homeArticleListLiveData.onceLastObserve(viewLifecycleOwner) {
			curPage = it?.curPage ?: 0

			val articles = (it?.datas ?: listOf()).toMutableList()
			if (curPage <= 1) {
				XLog.d("更新全部数据")

				homeArticledAdapter.updateData(articles)
				this.refreshLayout.finishRefresh()
			} else {
				XLog.d("插入更新数据")

				homeArticledAdapter.insertData(*articles.toTypedArray())
				this.refreshLayout.finishLoadMore()
			}

			refreshLayout.setNoMoreData(homeArticledAdapter.getData().size >= (it?.total ?: 0))
		}
	}

	private fun initHomeArticleAdapter(images: List<String>) {
		homeArticledAdapter = object : com.hl.rvadapter.BaseSingleAdapter<Article>(mutableListOf()) {

			override val itemLayout: Int = R.layout.item_home_article

			override fun onItemClick(itemView: View, position: Int, itemData: Article) {
				itemView.navigateToWeb(itemData.link ?: return, isNeedTitle = true)
			}

			override fun onItemInit(viewHolder: com.hl.rvadapter.viewholder.BaseViewHolder<Article>) {
				viewHolder.setChildClick(R.id.item_article_image) { childView, _, _ ->
					val imageUrl = childView.getTag()
					childView.context.showImage(childView as ImageView, imageUrl)
				}
			}

			override fun onItemBind(viewHolder: com.hl.rvadapter.viewholder.BaseViewHolder<Article>, itemData: Article) {
				// SecureRandom 可产生真随机数
				val randomImageUrl = images[SecureRandom().nextInt(images.size)]
				viewHolder.getView<ImageView>(R.id.item_article_image)?.run {
					this.setTag(randomImageUrl)
					GlideUtil.load(context, randomImageUrl, this)
				}
				viewHolder.getView<TextView>(R.id.item_article_title)?.text = itemData.title?.trim()

				val authorOrSharerName = if (itemData.author.isNullOrBlank()) itemData.shareUser else itemData.author
				viewHolder.getView<TextView>(R.id.item_article_author_or_sharer)?.text = authorOrSharerName?.trim()

				viewHolder.getView<TextView>(R.id.item_article_publish_time)?.text =
					" • ${Date(itemData.publishTime ?: 0).toFormatString()}"
			}
		}

		viewBinding.homeArticleList.run {
			this.adapter = homeArticledAdapter

			val itemDragCallBack = com.hl.rvadapter.drag.ItemDragCallBack(homeArticledAdapter.getData(), true)
			this.setItemTouchHelper(itemDragCallBack)
		}
	}
}