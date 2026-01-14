package com.hl.baseproject.fragments.home

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import com.elvishew.xlog.XLog
import com.hl.baseproject.base.BaseFragment
import com.hl.baseproject.databinding.FragmentHomeMiddleBinding
import com.hl.baseproject.databinding.ItemHomeArticleBinding
import com.hl.baseproject.repository.network.bean.Article
import com.hl.baseproject.viewmodels.DataViewModel
import com.hl.baseproject.viewmodels.HomeViewModel
import com.hl.dateutil.toFormatString
import com.hl.imageload.GlideUtil
import com.hl.popup.showImage
import com.hl.rvadapter.binding.BaseBindingSingleAdapter
import com.hl.rvadapter.binding.viewholder.BaseBindingViewHolder
import com.hl.utils.onceLastObserve
import com.hl.utils.views.setItemTouchHelper
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

	private lateinit var homeArticledAdapter: BaseBindingSingleAdapter<Article, ItemHomeArticleBinding>

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
		homeArticledAdapter = object : BaseBindingSingleAdapter<Article, ItemHomeArticleBinding>(mutableListOf()) {

				override fun onItemClick(itemView: View, position: Int, itemData: Article) {
					itemView.navigateToWeb(itemData.link ?: return, isNeedTitle = true)
				}

				override fun onItemInit(viewHolder: BaseBindingViewHolder<Article, ItemHomeArticleBinding>) {
					viewHolder.setChildClick({ this.itemArticleImage }) { childView, _, _ ->
						val imageUrl = childView.tag
						childView.context.showImage(childView as ImageView, imageUrl)
					}
				}

				override fun onItemBind(
					viewHolder: BaseBindingViewHolder<Article, ItemHomeArticleBinding>,
					itemData: Article
				) {
                    // SecureRandom 可产生真随机数
                    val randomImageUrl = images[SecureRandom().nextInt(images.size)]
                    viewHolder.binding.itemArticleImage.run {
                        this.tag = randomImageUrl
                        GlideUtil.load(context, randomImageUrl, this)
                    }
                    viewHolder.binding.itemArticleTitle.text = itemData.title?.trim()
                    val authorOrSharerName = if (itemData.author.isNullOrBlank()) itemData.shareUser else itemData.author
                    viewHolder.binding.itemArticleAuthorOrSharer.text = authorOrSharerName?.trim()
                    viewHolder.binding.itemArticlePublishTime.text =
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