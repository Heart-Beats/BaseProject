package com.hl.baseproject.fragments.home

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.elvishew.xlog.XLog
import com.hl.arch.adapters.BaseAbstractAdapter
import com.hl.arch.mvvm.vm.activityViewModels
import com.hl.arch.web.navigateToWeb
import com.hl.baseproject.R
import com.hl.baseproject.base.BaseFragment
import com.hl.baseproject.databinding.FragmentHomeMiddleBinding
import com.hl.baseproject.repository.network.bean.Article
import com.hl.baseproject.viewmodels.DataViewModel
import com.hl.baseproject.viewmodels.HomeViewModel
import com.hl.uikit.onClick
import com.hl.utils.GlideUtil
import com.hl.utils.date.toFormatString
import com.hl.utils.onceLastObserve
import com.hl.utils.showImage
import java.security.SecureRandom
import java.util.Date

/**
 * @author  张磊  on  2023/02/23 at 17:08
 * Email: 913305160@qq.com
 */
class HomeMiddleFragment : BaseFragment<FragmentHomeMiddleBinding>() {

	private val homeViewModel by activityViewModels<HomeViewModel>()
	private val dataViewModel by activityViewModels<DataViewModel>()

	private lateinit var homeArticledAdapter: BaseAbstractAdapter<Article>

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
		homeArticledAdapter = object : BaseAbstractAdapter<Article>(mutableListOf()) {

			override val itemLayout: Int = R.layout.item_home_article

			override fun onItemInit(viewHolder: ViewHolder, position: Int, itemData: Article?) {
				itemData ?: return

				viewHolder.itemView.onClick {
					it.navigateToWeb(itemData.link ?: return@onClick, isNeedTitle = true)
				}

				viewHolder.getView<ImageView>(R.id.item_article_image)?.onClick {
					val imageUrl = it.getTag()
					it.context.showImage(it as ImageView, imageUrl)
				}
			}

			override fun onItemBind(viewHolder: ViewHolder, itemData: Article?) {
				itemData ?: return

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
		}
	}
}