package com.hl.baseproject

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.elvishew.xlog.XLog
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.nisrulz.sensey.Sensey
import com.hl.arch.web.helpers.JsBridgeHelper
import com.hl.arch.web.sdk.ISdk
import com.hl.arch.web.sdk.ISdkImplProvider
import com.hl.baseproject.configs.AppConfig
import com.hl.shadow.Shadow
import com.hl.shadow.logger.LogLevel
import com.hl.umeng.sdk.UMInitUtil
import com.hl.unimp.UniMPHelper
import com.hl.utils.XLogInitUtil
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.TbsListener
import com.tencent.smtt.utils.TbsLogClient
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

		initX5(context, debug)

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


	private fun initX5(context: Context, isPrintLog: Boolean) {
		// 首次初始化冷启动优化, TBS内核首次使用和加载时，ART虚拟机会将Dex文件转为Oat，该过程由系统底层触发且耗时较长，
		// 很容易引起anr问题，解决方法是使用TBS的 ”dex2oat优化方案“。

		// 在调用TBS初始化、创建WebView之前进行如下配置
		val map: HashMap<String, Any> = HashMap()
		map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
		map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
		QbSdk.initTbsSettings(map)
		// 支持移动数据进行初始化下载
		QbSdk.setDownloadWithoutWifi(true)

		if (isPrintLog) {
			QbSdk.setTbsLogClient(object : TbsLogClient(context) {
				override fun i(tag: String?, msg: String?) {
					Log.i(tag, msg ?: "")
				}

				override fun e(tag: String?, msg: String?) {
					Log.e(tag, msg ?: "")
				}

				override fun w(tag: String?, msg: String?) {
					Log.w(tag, msg ?: "")
				}

				override fun d(tag: String?, msg: String?) {
					Log.d(tag, msg ?: "")
				}

				override fun v(tag: String?, msg: String?) {
					Log.v(tag, msg ?: "")
				}
			})
		}

		QbSdk.setTbsListener(object : TbsListener {
			override fun onDownloadFinish(i: Int) {
				//tbs内核下载完成回调
				XLog.d("X5core tbs内核下载完成")
			}

			override fun onInstallFinish(i: Int) {
				//内核安装完成回调，
				XLog.d("X5core tbs内核安装完成")
			}

			override fun onDownloadProgress(i: Int) {
				//下载进度监听
				XLog.d("X5core tbs内核正在下载中----->$i")
			}
		})

		QbSdk.initX5Environment(context, object : QbSdk.PreInitCallback {
			override fun onCoreInitFinished() {
				XLog.d("X5core x5加载结束")
			}

			override fun onViewInitFinished(b: Boolean) {
				XLog.d("X5core x5加载结束$b")
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