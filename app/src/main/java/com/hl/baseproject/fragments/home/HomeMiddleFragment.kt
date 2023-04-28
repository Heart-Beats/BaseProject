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
import com.hl.baseproject.viewmodels.HomeViewModel
import com.hl.uikit.onClick
import com.hl.utils.GlideUtil
import com.hl.utils.date.toFormatString
import com.hl.utils.onceLastObserve
import com.hl.utils.showImages
import java.util.Date

/**
 * @author  张磊  on  2023/02/23 at 17:08
 * Email: 913305160@qq.com
 */
class HomeMiddleFragment : BaseFragment<FragmentHomeMiddleBinding>() {

	private val data = listOf(
		"https://i.pinimg.com/originals/97/83/36/9783367be1fd1c73f74832d7a524b5b9.jpg",
		"https://i.pinimg.com/originals/c8/4a/a6/c84aa67da5b7a2878ccefc7a12ef5868.jpg",
		"https://i.etsystatic.com/10602277/r/il/86d93e/3934157230/il_1140xN.3934157230_n44w.jpg",
		"https://64.media.tumblr.com/0bea640a23a8a96dbcd6291d2a9815a2/f36999b3126b4deb-87/s1280x1920/914ae41c4cf8ace5fccb434beef77a4c27611161.jpg",
		"https://i.pinimg.com/originals/aa/2b/05/aa2b051ae04052b4a57b7335e528c62c.jpg",
		"https://i.pinimg.com/originals/78/8e/c2/788ec2b5969b57cea6d40ce549f27437.jpg",
		"https://i.pinimg.com/originals/f0/d9/a0/f0d9a0825411db5943d7a748806ce20b.jpg",
		"https://i.pinimg.com/originals/6d/1c/e7/6d1ce76c04c7e4a89ae212ac78e07971.jpg",
		"https://i.pinimg.com/originals/3f/52/57/3f525750dc364dd0c3073a4526ac79f6.jpg",
		"https://i.pinimg.com/originals/f7/c6/d6/f7c6d6978770ae655dc9c972ea8bbd3f.jpg",
		"https://i.pinimg.com/originals/f5/63/31/f5633182129d24492cd197fffe7f0a4a.jpg",
		"https://i.pinimg.com/originals/9f/ad/88/9fad88b6477a5f4a4cb1790e2bc8fbc9.jpg",
		"https://i.pinimg.com/originals/c2/9f/3f/c29f3f1683e9c50391969af26cfc0ec1.jpg",
		"https://i.pinimg.com/originals/97/38/a5/9738a51fabaa30bc2bf9650b44cc594b.jpg",
		"https://i.pinimg.com/originals/6f/b7/35/6fb7352be3bcf8b40b1e2251fcaa3923.jpg",
		"https://i.pinimg.com/originals/11/c6/a9/11c6a93d0b662e380a6e67e05328ea62.jpg",
		"https://i.pinimg.com/originals/ed/6b/1f/ed6b1f0a20b8806538ba1f1d5b358129.jpg",
		"https://i.pinimg.com/originals/0a/2b/b8/0a2bb8464b72e936098102d83583414e.png",
		"https://i.pinimg.com/originals/86/23/b3/8623b39d6f64a3b8267b159bcdf8f221.jpg",
		"https://i.pinimg.com/originals/6f/dd/e4/6fdde44e9bbf3e1c6b1196733e9bb49d.jpg",
		"https://i.pinimg.com/originals/46/d8/32/46d832ea0eaea0fe48a84e0e2fbb0794.jpg",
		"https://i.pinimg.com/originals/74/36/fa/7436fa1fc24c43146c7d93d749119f7f.jpg",
		"https://i.pinimg.com/originals/71/a9/ca/71a9ca4457e0dca85d404a34cd60d0e4.jpg",
		"https://photo7n.gracg.com/330735_1_e986b8f319211f634da888d66e641069.jpg",
		"https://i.pinimg.com/originals/38/95/56/389556e038e8078248dfd1a3305f858a.jpg",
		"https://i.pinimg.com/originals/7b/e7/6d/7be76d77f7e6735a67eff67641cc0a5d.jpg",
		"https://i.pinimg.com/originals/30/35/b7/3035b770f117a11c5aa6a8a395852e8c.jpg",
		"https://i.pinimg.com/originals/82/21/f5/8221f5e493324074df2da3186fb12493.jpg",
		"https://i.pinimg.com/originals/25/fa/91/25fa911e76320966775f04d676a2b4e2.jpg",
		"https://i.pinimg.com/originals/e0/f9/e8/e0f9e82724c5ed7fa2193c2f2e2d457c.jpg",
		"https://i.pinimg.com/originals/8d/22/5f/8d225f6e120ccd3314deb802456e7cb7.jpg",
		"https://i.pinimg.com/originals/d7/55/86/d7558621cb7b7dfe0b29488e389e77a0.jpg",
		"https://i.pinimg.com/originals/98/79/b7/9879b76eac7ba1d29b4c5974bcaf75c9.jpg",
		"https://i.pinimg.com/originals/38/d9/39/38d93972c6c3f84af22c1dd9bfd02de5.jpg",
		"https://i.pinimg.com/originals/c3/e2/4b/c3e24b2fd7289347d6d399bb8464df2c.jpg",
		"https://i.pinimg.com/originals/f9/6e/41/f96e4180c03a6c1d03fac5bb20dfb01c.jpg",
		"https://i.pinimg.com/originals/67/58/3b/67583b90024b88f9d84a127281d30d31.jpg",
		"https://i.pinimg.com/originals/28/6a/33/286a3365a3e21b2297d5848cd42f3e12.jpg",
		"https://i.pinimg.com/originals/a6/dc/45/a6dc45cb0183e1748721f24a67f7c1bd.jpg",
		"https://i.pinimg.com/originals/70/95/a7/7095a7dd942201d8a17724bf0f18c239.jpg",
		"https://i.pinimg.com/originals/5b/08/eb/5b08ebcb2848f9f04c73328a8cd2d73f.jpg",
		"https://i.pinimg.com/originals/f8/ae/ef/f8aeefc8ae37ad34662bd71fe5b1af4e.jpg",
		"https://i.pinimg.com/originals/43/7a/63/437a63e3d18ed8ed0c61960990c98a15.jpg",
		"https://i.pinimg.com/originals/0a/6a/90/0a6a901050fd2fb9ca4895d58be75612.jpg",
		"https://i.pinimg.com/originals/48/3a/0a/483a0ac625bab4df9eadadbbfe5faf66.jpg",
		"https://i.pinimg.com/originals/ab/c1/c1/abc1c15563a7ad86c2ff8fd9fb9fe5a2.jpg",
		"https://i.pinimg.com/originals/e3/34/4a/e3344a2ac3d9317d9e9db01a7ad56982.jpg",
		"https://i.pinimg.com/originals/3c/8f/a1/3c8fa1ee26ed9d28774e87721f02c9c6.jpg",
		"https://i.pinimg.com/originals/2c/45/a7/2c45a762b366a257c5b9cb8f3bfb2c49.jpg",
		"https://i.pinimg.com/originals/93/b1/8c/93b18cf81ef3512ae561ae0d16566004.jpg",
		"https://i.pinimg.com/originals/83/ce/d1/83ced13d7112d353a3115b09ed2b6dce.jpg",
		"https://i.pinimg.com/originals/b5/71/7f/b5717fefd09deccc2c5f4a0bf042259c.jpg",
		"https://i.pinimg.com/originals/ba/84/20/ba8420a9eb90bd0d5d91d070b963c1d6.jpg",
		"https://i.pinimg.com/originals/a2/b1/f5/a2b1f505eabcde6de14b323a114d6125.jpg",
		"https://i.pinimg.com/originals/bb/e3/c6/bbe3c64f09da6159eaf93d56b772ca94.jpg",
		"https://i.pinimg.com/originals/de/31/c9/de31c9dc0a69aeae0af0e12834cce576.jpg",
		"https://i.pinimg.com/originals/f6/7c/db/f67cdbf7460673d8af21fb3fba8d008c.jpg",
		"https://i.pinimg.com/originals/73/98/49/73984936c7d7e9da2f4f8c2d0f2ca10f.jpg",
		"https://i.pinimg.com/originals/46/e6/48/46e64864168850799ffb29769a3544de.jpg",
		"https://i.pinimg.com/originals/d9/7b/29/d97b29d22612b8f69ca944d92228896e.jpg",
		"https://i.pinimg.com/originals/a9/b6/21/a9b62129de76cda0b9be752e95f725ff.jpg",
		"https://i.pinimg.com/originals/d9/89/5a/d9895a067773a8193fb4c4a59f6e51ff.jpg"
	)


	private val homeViewModel by activityViewModels<HomeViewModel>()

	private lateinit var homeArticledAdapter: BaseAbstractAdapter<Article>

	private var curPage = 0

	override fun FragmentHomeMiddleBinding.onViewCreated(savedInstanceState: Bundle?) {
		initHomeArticleAdapter()

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

	private fun initHomeArticleAdapter() {
		homeArticledAdapter = object : BaseAbstractAdapter<Article>(mutableListOf()) {

			override val itemLayout: Int = R.layout.item_home_article

			override fun onItemInit(viewHolder: ViewHolder, position: Int, itemData: Article?) {
				itemData ?: return

				viewHolder.itemView.onClick {
					it.navigateToWeb(itemData.link ?: return@onClick, isNeedTitle = true)
				}

				viewHolder.getView<ImageView>(R.id.item_article_image)?.onClick {
					it.context.showImages(it as ImageView, position % data.size, data)
				}
			}

			override fun onItemBind(viewHolder: ViewHolder, itemData: Article?) {
				itemData ?: return

				viewHolder.getView<ImageView>(R.id.item_article_image)?.run {
					GlideUtil.load(context, data.random(), this)
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