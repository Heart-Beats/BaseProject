package com.hl.baseproject

import android.content.Context
import androidx.fragment.app.Fragment
import com.elvishew.xlog.XLog
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.nisrulz.sensey.Sensey
import com.hl.baseproject.configs.AppConfig
import com.hl.shadow.Shadow
import com.hl.shadow.logger.LogLevel
import com.hl.umeng.sdk.UMInitUtil
import com.hl.unimp.UniMPHelper
import com.hl.web.helpers.JsBridgeHelper
import com.hl.web.sdk.ISdk
import com.hl.web.sdk.ISdkImplProvider
import com.hl.xloginit.XLogInitUtil
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.umeng.message.api.UPushRegisterCallback
import io.dcloud.feature.sdk.MenuActionSheetItem

/**
 * @author  张磊  on  2022/06/15 at 11:31
 * Email: 913305160@qq.com
 */
object SDKInitHelper {

	fun initSdk(context: Context) {
		val applicationContext = context.applicationContext

		val debug = BuildConfig.DEBUG

		XLogInitUtil.init {
			this.isPrintLog = debug
		}

		AppConfig.init(debug,"https://www.wanandroid.com/")

		initUM(applicationContext)

		initShadow()

		initWebView()

		initRefreshLayout()

		initUniMP(context)

		Sensey.getInstance().init(context)
	}

	/**
	 * 直接初始化友盟
	 */
	private fun initUM(applicationContext: Context) {
		UMInitUtil.initUM(applicationContext, object : UPushRegisterCallback {
			override fun onSuccess(deviceToken: String) {
				//注册成功会返回deviceToken deviceToken是推送消息的唯一标志
				XLog.d("获取友盟 deviceToken 成功：$deviceToken")
			}

			override fun onFailure(errCode: String, errDesc: String) {
				XLog.d("注册失败 code:$errCode, desc:$errDesc")
			}
		})
	}


	private fun initShadow() {
		val logLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.INFO
		Shadow.initShadowLog(logLevel) { logLevel: LogLevel, message: String, t: Throwable? ->

		}
	}

	private fun initWebView() {
		JsBridgeHelper.setISdkImplProvider(object : ISdkImplProvider() {
			override fun provideProjectSdkImpl(webViewFragment: Fragment, bridgeWebView: BridgeWebView): ISdk {
				return object : ISdk {
				}
			}
		})
	}

	private fun initRefreshLayout() {
		//SmartRefreshLayout 默认配置，后续可在 XMl 中进行更改
		SmartRefreshLayout.setDefaultRefreshInitializer { _, refreshLayout ->
			refreshLayout.layout.tag = "close egg" // 关闭下来刷新死拉彩蛋

			refreshLayout.setHeaderMaxDragRate(1.5f) //最大下拉高度与Header标准高度的倍数
			refreshLayout.setHeaderHeight(80f)  //Header标准高度（显示下拉高度>=标准高度 触发刷新), 默认高度：100dp
			// refreshLayout.setReboundDuration(500) //回弹动画时长（毫秒）默认: 300ms
			// refreshLayout.setHeaderInsetStart(200f) //设置 Header 起始位置偏移量
			// refreshLayout.setPrimaryColorsId(R.color.colorAccent)

			/*	下拉距离 与 HeaderHeight 的比率达到此值时将会触发刷新（默认1，即下拉距离等于头部高度触发刷新，但若最大下拉高度等于头部高度，
				实际上是无法拉满的，因此几乎不会触发刷新动画，即 onStartAnimator 事件）*/
			// refreshLayout.setHeaderTriggerRate(0.6f)

			refreshLayout.setFooterMaxDragRate(1.5f) //最大下拉高度与footer标准高度的倍数
			refreshLayout.setFooterHeight(80f) //Footer标准高度（显示下拉高度>=标准高度 触发刷新), 默认高度：100dp
		}

		//SmartRefreshLayout 设置默认刷新头
		// SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ ->
		//
		//     UIKitCommonRefreshHeader(context).apply {
		//         this.setPullAnimation(url = "https://assets4.lottiefiles.com/packages/lf20_lmk0pfms.json")
		//         this.setRefreshAnimation(url = "https://assets4.lottiefiles.com/packages/lf20_ngcpf3x7.json")
		//     }
		// }

		//目前全局创建 footer 将会导致 footer 的布局被创建两次 ---> 使用view动画会有问题
		//SmartRefreshLayout 设置默认刷新尾
		// SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
		//     UIKitCommonRefreshFooter(context)
		// }
	}

	private fun initUniMP(context: Context) {
		UniMPHelper.initUniMP(context) {
			val item = MenuActionSheetItem("关于", "gy")
			val sheetItems: MutableList<MenuActionSheetItem> = ArrayList()
			sheetItems.add(item)

			this.setCapsule(true)
				.setEnableBackground(true)
				.setMenuDefFontSize("16px")
				.setMenuDefFontColor("#ff00ff")
				.setMenuDefFontWeight("normal")
				.setMenuActionSheetItems(sheetItems)
		}
	}
}