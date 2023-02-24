package com.hl.baseproject.viewmodels

import androidx.lifecycle.MutableLiveData
import com.hl.baseproject.base.BaseViewModel
import com.hl.baseproject.repository.network.bean.BannerData
import com.hl.baseproject.repository.network.bean.HomeArticleList
import com.hl.utils.setSafeValue

/**
 * @author  张磊  on  2022/08/31 at 17:43
 * Email: 913305160@qq.com
 */
class HomeViewModel : BaseViewModel() {

	val topBannerListLiveData by lazy { MutableLiveData<List<BannerData>?>() }
	val homeArticleListLiveData by lazy { MutableLiveData<HomeArticleList?>() }

	fun getTopBannerList() {
		serviceLaunch(reqBlock = { this.getTopBannerList() }) {
			// 返回的数据包括 banner 的位置
			topBannerListLiveData.setSafeValue(it)
		}
	}

	fun getHomeArticleList(pageIndex: Int = 0) {
		serviceLaunch(reqBlock = { this.getHomeArticleList(pageIndex) }) {
			// 返回的数据包括 banner 的位置
			homeArticleListLiveData.setSafeValue(it)
		}
	}


	fun reFreshHomeData() {
		getTopBannerList()
		getHomeArticleList()
	}
}