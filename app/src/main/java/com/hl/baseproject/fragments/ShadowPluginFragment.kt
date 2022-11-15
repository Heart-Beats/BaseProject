package com.hl.baseproject.fragments

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.FileUtils
import com.elvishew.xlog.XLog
import com.hl.baseproject.databinding.FragmentShadowPluginBinding
import com.hl.baseproject.fragments.base.BaseFragment
import com.hl.baseproject.shadow.pps.TestPluginProcessService
import com.hl.baseproject.shadow.pps.ZKYPluginProcessService
import com.hl.shadow.Shadow
import com.hl.shadow.lib.ShadowConstants
import com.hl.uikit.onClick
import com.hl.uikit.toast
import com.hl.utils.AppSizeUtil
import com.hl.utils.copyAssets2Path
import com.hl.utils.reqPermissions
import com.lxj.xpopup.XPopup
import com.tencent.shadow.dynamic.host.EnterCallback
import kotlinx.coroutines.NonDisposableHandle.parent
import java.io.File

/**
 * @author  张磊  on  2022/09/30 at 17:56
 * Email: 913305160@qq.com
 */
class ShadowPluginFragment : BaseFragment<FragmentShadowPluginBinding>() {

	override fun onResume() {
		super.onResume()

		XLog.d("onResume")

		AppSizeUtil.getSize(requireContext()) { cacheSize, dataSize, codeSize ->
			val appSize = cacheSize + dataSize + codeSize
			viewBinding?.displayCacheSize?.text = AppSizeUtil.getFormatSize(appSize)
		}

		val shadowPluginManagerParentDir = File(requireContext().filesDir, "ShadowPluginManager")
		val mPluginUnpackedDir = File(shadowPluginManagerParentDir, "UnpackedPlugin")
		XLog.d(mPluginUnpackedDir)

		FileUtils.listFilesInDir(mPluginUnpackedDir).forEach {
			XLog.d("子文件 == $it,   大小 == ${FileUtils.getSize(it)}")
		}
	}

	override fun FragmentShadowPluginBinding.onViewCreated(savedInstanceState: Bundle?) {
		startShadowPlugin.onClick {
			showSelectShadowPluginDialog()
		}
	}

	private fun showSelectShadowPluginDialog() {
		val items = listOf(
			"启动 SunFlower 插件",
			"启动自定义测试插件",
			"启动依赖库的Service",
			"启动自定义Service",
			"启动自定义IntentService",
			"启动优码智客云插件",
			"启动优码行销拓客插件",
			"启动优码工程协同插件",
		)

		XPopup.Builder(requireContext()).asCenterList("启动 Shadow 插件", items.toTypedArray()) { position, _ ->

			val bundle = Bundle().apply {
				// 插件 zip 的路径
				val pluginSavePath =
					File(requireContext().getExternalFilesDir(null), "plugins/plugin-test-release.zip").absolutePath
				val pluginZipPath =
					requireContext().copyAssets2Path("plugins/plugin-test-release.zip", pluginSavePath)

				putString(ShadowConstants.KEY_PLUGIN_ZIP_PATH, pluginZipPath)
			}

			when (position) {
				0 -> {
					//启动插件中的对应的 Activity
					bundle.putString(
						ShadowConstants.KEY_CLASSNAME,
						"com.google.samples.apps.sunflower.GardenActivity"
					)

					// partKey 每个插件都有自己的 partKey 用来区分多个插件，需要与插件打包脚本中的 packagePlugin{ partKey xxx} 一致
					bundle.putString(ShadowConstants.KEY_PLUGIN_PART_KEY, "sunflower")
					bundle.putLong(ShadowConstants.KEY_FROM_ID, ShadowConstants.FROM_ID_START_ACTIVITY)
				}
				1 -> {
					bundle.putString(ShadowConstants.KEY_CLASSNAME, "com.hl.myplugin.MainActivity")
					bundle.putString(ShadowConstants.KEY_PLUGIN_PART_KEY, "test")
					bundle.putLong(ShadowConstants.KEY_FROM_ID, ShadowConstants.FROM_ID_START_ACTIVITY)
				}
				2 -> {
					bundle.putString(
						ShadowConstants.KEY_CLASSNAME,
						"com.tsinglink.android.update.CheckUpdateIntentService"
					)
					bundle.putString(ShadowConstants.KEY_PLUGIN_PART_KEY, "test")
					bundle.putLong(ShadowConstants.KEY_FROM_ID, ShadowConstants.FROM_ID_CALL_SERVICE)
					bundle.putString(
						ShadowConstants.KEY_INTENT_ACTION,
						"com.tsinglink.android.update.ACTION_START_DOWNLOAD"
					)
					bundle.putBundle(ShadowConstants.KEY_EXTRAS, Bundle().apply {
						this.putString(
							"com.tsinglink.android.update.extra.DOWNLOAD_URL", "http://down.qq" +
									".com/qqweb/QQ_1/android_apk/Androidqq_8.4.10.4875_537065980.apk"
						)
					})
				}
				3 -> {
					bundle.putString(ShadowConstants.KEY_CLASSNAME, "com.hl.myplugin.TestService")
					bundle.putString(ShadowConstants.KEY_PLUGIN_PART_KEY, "test")
					bundle.putLong(ShadowConstants.KEY_FROM_ID, ShadowConstants.FROM_ID_CALL_SERVICE)
				}
				4 -> {

					// val receiver: ResultReceiver = TestResultReceiver(Handler(Looper.getMainLooper()))
					//
					// bundle.putString(ShadowConstants.KEY_CLASSNAME, "com.hl.myplugin.TestIntentService")
					// bundle.putString(ShadowConstants.KEY_PLUGIN_PART_KEY, "test")
					// bundle.putLong(ShadowConstants.KEY_FROM_ID, ShadowConstants.FROM_ID_CALL_SERVICE)
					//
					// bundle.putString(
					// 	ShadowConstants.KEY_INTENT_ACTION,
					// 	"com.hl.myplugin.action.FOO"
					// )
					// bundle.putBundle(ShadowConstants.KEY_EXTRAS, Bundle().apply {
					// 	this.putString("com.hl.myplugin.extra.PARAM1", "我是参数1")
					// 	this.putParcelable("com.hl.myplugin.extra.PARAM2", receiver)
					// })
				}
				5 -> {
					val pluginSavePath =
						File(
							requireContext().getExternalFilesDir(null),
							"plugins/plugin-zky-release.zip"
						).absolutePath
					val pluginZipPath =
						requireContext().copyAssets2Path("plugins/plugin-zky-release.zip", pluginSavePath)
					bundle.putString(ShadowConstants.KEY_PLUGIN_ZIP_PATH, pluginZipPath)

					bundle.apply {
						putString(
							ShadowConstants.KEY_CLASSNAME,
							"com.example.txwang.yidongyanfang.moudle.login.LoginActivity"
						)

						// partKey 每个插件都有自己的 partKey 用来区分多个插件，需要与插件打包脚本中的 packagePlugin{ partKey xxx} 一致
						putString(ShadowConstants.KEY_PLUGIN_PART_KEY, "zky")
					}
					bundle.putLong(ShadowConstants.KEY_FROM_ID, ShadowConstants.FROM_ID_START_ACTIVITY)
				}
				6 -> {
					val pluginSavePath =
						File(
							requireContext().getExternalFilesDir(null),
							"plugins/plugin-xxtk-release.zip"
						).absolutePath
					val pluginZipPath =
						requireContext().copyAssets2Path("plugins/plugin-xxtk-release.zip", pluginSavePath)
					bundle.putString(ShadowConstants.KEY_PLUGIN_ZIP_PATH, pluginZipPath)

					bundle.apply {
						putString(
							ShadowConstants.KEY_CLASSNAME,
							"com.zhac.xxtk.app.main.login.LoginActivity"
						)

						// partKey 每个插件都有自己的 partKey 用来区分多个插件，需要与插件打包脚本中的 packagePlugin{ partKey xxx} 一致
						putString(ShadowConstants.KEY_PLUGIN_PART_KEY, "xxtk")
					}
					bundle.putLong(ShadowConstants.KEY_FROM_ID, ShadowConstants.FROM_ID_START_ACTIVITY)
				}
				7 -> {
					val pluginSavePath =
						File(
							requireContext().getExternalFilesDir(null),
							"plugins/plugin-cjsxt-release.zip"
						).absolutePath
					val pluginZipPath =
						requireContext().copyAssets2Path("plugins/plugin-cjsxt-release.zip", pluginSavePath)
					bundle.putString(ShadowConstants.KEY_PLUGIN_ZIP_PATH, pluginZipPath)

					bundle.apply {
						putString(
							ShadowConstants.KEY_CLASSNAME,
							"com.youma.cjspro.PluginSplashActivity"
						)

						// partKey 每个插件都有自己的 partKey 用来区分多个插件，需要与插件打包脚本中的 packagePlugin{ partKey xxx} 一致
						putString(ShadowConstants.KEY_PLUGIN_PART_KEY, "cjsxt")
					}
					bundle.putLong(ShadowConstants.KEY_FROM_ID, ShadowConstants.FROM_ID_START_ACTIVITY)
				}
			}

			if (bundle.getBundle(ShadowConstants.KEY_EXTRAS) == null) {
				bundle.putBundle(ShadowConstants.KEY_EXTRAS, Bundle().apply {
					this.putString("测试数据", "我是宿主传过来的数据")
				})
			}

			val permissions = arrayOf(
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE
			)
			this.reqPermissions(*permissions, allGrantedAction = {
				startShadowPlugin(requireContext(), bundle)
			})
		}.show()
	}

	private fun startShadowPlugin(context: Context, bundle: Bundle) {
		val ppsName = when (bundle.getString(ShadowConstants.KEY_PLUGIN_PART_KEY)) {
			"sunflower", "test" -> TestPluginProcessService::class.java.name
			"zky" -> ZKYPluginProcessService::class.java.name
			else -> TestPluginProcessService::class.java.name
		}
		bundle.putString(ShadowConstants.KEY_PLUGIN_PROCESS_SERVICE_NAME_KEY, ppsName)

		// 这里取插件包不带后缀的文件名作为插件的唯一标识
		val pluginName = bundle.getString(ShadowConstants.KEY_PLUGIN_ZIP_PATH)?.run {
			File(this).nameWithoutExtension
		} ?: ""

		val pluginManager = Shadow.getMultiPluginManager(context, pluginName)

		/**
		 * context context
		 * formId  标识本次请求的来源位置，用于区分入口
		 * bundle  参数列表, 建议在参数列表加入自己的验证
		 * callback 用于从PluginManager实现中返回View
		 */
		pluginManager?.enter(context, bundle.getLong(ShadowConstants.KEY_FROM_ID), bundle, object : EnterCallback {
			override fun onShowLoadingView(view: View?) {}
			override fun onCloseLoadingView() {}
			override fun onEnterComplete() {
				// 启动成功
				toast("启动成功")
			}
		})
	}
}