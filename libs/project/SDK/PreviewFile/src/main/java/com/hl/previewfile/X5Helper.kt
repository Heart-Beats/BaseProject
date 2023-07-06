package com.hl.previewfile

import android.content.Context
import com.elvishew.xlog.XLog
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.TbsListener
import com.tencent.smtt.utils.TbsLogClient

/**
 * @author  张磊  on  2023/07/06 at 15:54
 * Email: 913305160@qq.com
 */
object X5Helper {

	fun initX5(context: Context, isPrintLog: Boolean = true) {

		// 首次初始化冷启动优化, TBS内核首次使用和加载时，ART虚拟机会将Dex文件转为Oat，该过程由系统底层触发且耗时较长，
		// 很容易引起anr问题，解决方法是使用TBS的 ”dex2oat优化方案“。

		// 在调用TBS初始化、创建WebView之前进行如下配置
		val map: HashMap<String, Any> = HashMap()
		map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
		map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
		QbSdk.initTbsSettings(map)        // 支持移动数据进行初始化下载
		QbSdk.setDownloadWithoutWifi(true)

		if (isPrintLog) {
			QbSdk.setTbsLogClient(object : TbsLogClient(context) {
				override fun i(tag: String?, msg: String?) {
					XLog.i("%s ----> %s", tag, msg ?: "")
				}

				override fun e(tag: String?, msg: String?) {
					XLog.e("%s ----> %s", tag, msg ?: "")
				}

				override fun w(tag: String?, msg: String?) {
					XLog.w("%s ----> %s", tag, msg ?: "")
				}

				override fun d(tag: String?, msg: String?) {
					XLog.d("%s ----> %s", tag, msg ?: "")
				}

				override fun v(tag: String?, msg: String?) {
					XLog.v("%s ----> %s", tag, msg ?: "")
				}
			})
		}

		QbSdk.setTbsListener(object : TbsListener {
			override fun onDownloadFinish(i: Int) {                //tbs内核下载完成回调
				XLog.d("X5core tbs内核下载完成")
			}

			override fun onInstallFinish(i: Int) {                //内核安装完成回调，
				XLog.d("X5core tbs内核安装完成")
			}

			override fun onDownloadProgress(i: Int) {                //下载进度监听
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
}