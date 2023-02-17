package com.youma.yky.app.main.viewmodels

import androidx.lifecycle.MutableLiveData
import com.hl.baseproject.repository.network.bean.BannerData
import com.hl.utils.setSafeValue
import com.youma.yky.app.main.base.BaseViewModel

/**
 * @author  张磊  on  2022/08/31 at 17:43
 * Email: 913305160@qq.com
 */
class HomeViewModel : BaseViewModel() {

	val topBannerListLiveData by lazy { MutableLiveData<List<BannerData>?>() }

	fun getTopBannerList() {
		serviceLaunch(reqBlock = { this.getTopBannerList() }) {
			// 返回的数据包括 banner 的位置
			topBannerListLiveData.setSafeValue(it)
		}
	}


	fun reFreshHomeData() {
		getTopBannerList()
	}
}