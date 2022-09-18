package com.hl.shadow.pluginmanager

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import com.hl.shadow.lib.ShadowConstants
import com.tencent.shadow.core.manager.installplugin.InstalledPlugin
import com.tencent.shadow.core.manager.installplugin.InstalledType
import com.tencent.shadow.dynamic.host.EnterCallback
import com.tencent.shadow.dynamic.host.FailedException
import com.tencent.shadow.dynamic.loader.PluginServiceConnection
import com.tencent.shadow.dynamic.manager.PluginManagerThatUseDynamicLoader
import org.json.JSONException
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.*


/**
 * @author 张磊  on  2021/04/08 at 17:22
 * Email: 913305160@qq.com
 */
open class MyPluginManager(context: Context) : PluginManagerThatUseDynamicLoader(context) {
	companion object {
		private const val TAG = "MyPluginManager"
	}

	private val installPluginExecutorService: ExecutorService = ThreadPoolExecutor(1,
		1,
		2,
		TimeUnit.SECONDS,
		ArrayBlockingQueue(Short.MAX_VALUE.toInt()),
		{ r: Runnable? -> Thread(r, "安装插件线程") }) { r: Runnable?, executor: ThreadPoolExecutor? ->
		Log.e(TAG, "$executor 已满载，拒绝执行任务 $r")
	}

	private val mFixedPool: ExecutorService = ThreadPoolExecutor(4,
		4,
		2,
		TimeUnit.SECONDS,
		ArrayBlockingQueue(Short.MAX_VALUE.toInt()),
		{ r: Runnable? -> Thread(r, "解压插件线程") }) { r: Runnable?, executor: ThreadPoolExecutor? ->
		Log.e(TAG, "$executor 已满载，拒绝执行任务 $r")
	}

	/**
	 * @return PluginManager实现的别名，用于区分不同PluginManager实现的数据存储路径
	 */
	override fun getName(): String {
		return ShadowConstants.PLUGIN_MANAGER_NAME
	}

	/**
	 * @return demo插件so的abi
	 */
	// override fun getAbi(): String {
	// 	return ShadowConstants.ABI
	// }

	/**
	 * @return 宿主中注册的PluginProcessService实现的类名
	 */
	protected open fun getPluginProcessServiceName(): String {
		return ShadowConstants.MAIN_PLUGIN_PROCESS_SERVICE_NAME
	}


	/**
	 * @param context  context
	 * @param fromId   标识本次请求的来源位置，用于区分入口
	 * @param bundle   参数列表
	 * @param callback 用于从PluginManager实现中返回View
	 */
	override fun enter(context: Context, fromId: Long, bundle: Bundle, callback: EnterCallback?) {
		Log.d(TAG, "enter: 开始进入插件 -------------- \n formId == $fromId，传入bundle == $bundle")

		// 插件 zip 包地址，可以直接写在这里，也用Bundle可以传进来
		val pluginZipPath = bundle.getString(ShadowConstants.KEY_PLUGIN_ZIP_PATH) ?: return
		val partKey = bundle.getString(ShadowConstants.KEY_PLUGIN_PART_KEY, "")
		val className = bundle.getString(ShadowConstants.KEY_CLASSNAME)
		val intentAction = bundle.getString(ShadowConstants.KEY_INTENT_ACTION)
		val extras = bundle.getBundle(ShadowConstants.KEY_EXTRAS)
		if (className == null) {
			throw NullPointerException("className == null")
		}

		// 打开 Activity 示例
		when (fromId) {
			ShadowConstants.FROM_ID_START_ACTIVITY -> {
				// 开始加载插件了，实现加载布局
				callback?.onShowLoadingView(null)

				installPluginExecutorService.execute {
					launchPluginActivity(context, callback, pluginZipPath, partKey, className, intentAction, extras)
				}
			}
			ShadowConstants.FROM_ID_CALL_SERVICE -> {
				// 打开Server示例
				installPluginExecutorService.execute {
					launchPluginService(context, pluginZipPath, partKey, className, intentAction, extras)
				}
			}
			else -> {
				throw IllegalArgumentException("不认识的fromId == $fromId")
			}
		}
	}

	private fun launchPluginActivity(
		context: Context,
		callback: EnterCallback?,
		pluginZipPath: String,
		partKey: String,
		className: String,
		intentAction: String?,
		extras: Bundle?
	) {
		try {
			//这个调用是阻塞的
			val installedPlugin = installPlugin(pluginZipPath, null, true)
			val pluginIntent = Intent(intentAction)
			pluginIntent.setClassName(context.packageName, className)
			if (extras != null) {
				pluginIntent.replaceExtras(extras)
			}
			startPluginActivity(installedPlugin, partKey, pluginIntent)
		} catch (e: Exception) {
			Log.e(TAG, "enter: startPluginActivity 失败", e)
		}
		if (callback != null) {
			val uiHandler = Handler(Looper.getMainLooper())
			uiHandler.post {

				// 这里插件就启动完成了
				callback.onCloseLoadingView()
				callback.onEnterComplete()
			}
		}
	}

	private fun launchPluginService(
		context: Context,
		pluginZipPath: String,
		partKey: String,
		className: String,
		intentAction: String?,
		extras: Bundle?
	) {
		try {
			//这个调用是阻塞的
			val installedPlugin = installPlugin(pluginZipPath, null, true)
			loadPlugin(installedPlugin.UUID, partKey)
			val pluginIntent = Intent(intentAction)
			pluginIntent.setClassName(context.packageName, className)
			if (extras != null) {
				pluginIntent.replaceExtras(extras)
			}

			var serviceConnection: ServiceConnection? = null
			try {
				// 在宿主中定义的接口，用来回调 PluginServiceConnection 相关的方法
				val instance = Class.forName("com.hl.shadow.dynamic.impl.ServiceConnectionIml").newInstance()
				serviceConnection = instance as ServiceConnection
			} catch (e: Exception) {
				e.printStackTrace()
			}

			val finalServiceConnection = serviceConnection

			Log.d(TAG, "launchPluginService: 开始绑定插件中的 Service, ServiceName == $className")
			val callSuccess = mPluginLoader.bindPluginService(pluginIntent, object : PluginServiceConnection {
				override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
					// 在这里实现AIDL进行通信操作
					Log.d(TAG, "onServiceConnected（service 已连接）: componentName ==$componentName")
					finalServiceConnection?.onServiceConnected(componentName, iBinder)
				}

				override fun onServiceDisconnected(componentName: ComponentName) {
					Log.d(TAG, "onServiceConnection（service 断开连接）: componentName ==$componentName")
					finalServiceConnection?.onServiceDisconnected(componentName)
					throw RuntimeException("onServiceConnection")
				}
			}, Service.BIND_AUTO_CREATE)

			if (!callSuccess) {
				throw RuntimeException("bind service 失败, ServiceName ==$className")
			} else {
				Log.d(TAG, "launchPluginService: bindPluginService 成功, ServiceName == $className")
				mPluginLoader.startPluginService(pluginIntent)
			}
		} catch (e: Exception) {
			Log.e(TAG, "launchPluginService: bindPluginService 失败, ServiceName == $className", e)
		}
	}

	/**
	 * 从压缩包中解压安装插件
	 *
	 * @param zip  压缩包路径
	 * @param hash 压缩包hash
	 * @param odex 插件是否需要 odex 优化
	 */
	@Throws(IOException::class, JSONException::class, InterruptedException::class, ExecutionException::class)
	private fun installPlugin(zip: String, hash: String?, odex: Boolean): InstalledPlugin {
		val pluginConfig = installPluginFromZip(File(zip), hash)
		val uuid = pluginConfig.UUID
		val futures: MutableList<Future<out Any?>> = LinkedList()

		if (pluginConfig.runTime != null && pluginConfig.pluginLoader != null) {
			val odexRuntime = mFixedPool.submit<Any?> {
				oDexPluginLoaderOrRunTime(uuid, InstalledType.TYPE_PLUGIN_RUNTIME, pluginConfig.runTime.file)
				null
			}
			futures.add(odexRuntime)
			val odexLoader = mFixedPool.submit<Any?> {
				oDexPluginLoaderOrRunTime(uuid, InstalledType.TYPE_PLUGIN_LOADER, pluginConfig.pluginLoader.file)
				null
			}
			futures.add(odexLoader)
		}

		val extractSoFutures: MutableList<Future<android.util.Pair<String, String>>> = LinkedList()
		for ((partKey, value) in pluginConfig.plugins) {
			val apkFile = value.file
			val extractSo = mFixedPool.submit(
				Callable<android.util.Pair<String, String>> {
					extractSo(uuid, partKey, apkFile)
				})

			futures.add(extractSo)
			extractSoFutures.add(extractSo)

			if (odex) {
				val odexPlugin = mFixedPool.submit<Any?> {
					oDexPlugin(uuid, partKey, apkFile)
					null
				}
				futures.add(odexPlugin)
			}
		}
		for (future in futures) {
			future.get()
		}

		val soDirMap: MutableMap<String, String> = HashMap()
		for (future in extractSoFutures) {
			val pair = future.get()
			soDirMap[pair.first] = pair.second
		}

		// 该方法每次执行会将插件相关的数据写入数据库，同时会更新 uuid 的插件安装时间作为所有相同 uuid 的插件的安装时间
		onInstallCompleted(pluginConfig, soDirMap)

		return getInstalledPlugins(1)[0]
	}

	/**
	 * 打开安装的插件中对应的 Activity
	 *
	 * @param installedPlugin 安装的插件
	 * @param partKey         安装的插件对应的 partKey
	 * @param pluginIntent    需要打开的 activity 对应的 intent
	 */
	@Throws(RemoteException::class, TimeoutException::class, FailedException::class)
	private fun startPluginActivity(installedPlugin: InstalledPlugin, partKey: String, pluginIntent: Intent?) {
		val intent = convertActivityIntent(installedPlugin, partKey, pluginIntent)
		intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
		mPluginLoader.startActivityInPluginProcess(intent)
	}

	@Throws(RemoteException::class, TimeoutException::class, FailedException::class)
	private fun convertActivityIntent(
		installedPlugin: InstalledPlugin,
		partKey: String,
		pluginIntent: Intent?
	): Intent {
		loadPlugin(installedPlugin.UUID, partKey)
		return mPluginLoader.convertActivityIntent(pluginIntent)
	}

	/**
	 * 载入插件, 会根据插件 apk 生成对应的
	 * pluginPackageManager
	 * pluginClassLoader
	 * resources
	 * pluginInfo
	 * shadowApplication
	 * appComponentFactory
	 */
	@Throws(RemoteException::class, TimeoutException::class, FailedException::class)
	private fun loadPlugin(uuid: String, partKey: String) {
		Log.d(TAG, "loadPlugin ------> uuid=${uuid}, partKey=${partKey}")

		loadPluginLoaderAndRuntime(uuid)
		val map = mPluginLoader.loadedPlugin

		Log.d(TAG, "载入 Plugin ---------------")
		if (!map.containsKey(partKey)) {
			startLoadPlugin(uuid, partKey)
		}
		Log.d(TAG, "--------------- Plugin  加载结束")

		val isCall = map[partKey] as Boolean?
		if (isCall == null || !isCall) {
			mPluginLoader.callApplicationOnCreate(partKey)
		}
	}

	@Throws(RemoteException::class, TimeoutException::class, FailedException::class)
	private fun loadPluginLoaderAndRuntime(uuid: String) {
		Log.d(TAG, "loadPluginLoaderAndRuntime: uuid=$uuid")

		if (mPpsController == null) {
			val pluginProcessServiceName: String = getPluginProcessServiceName()

			Log.d(TAG, "PPS 开始绑定 --------------- $pluginProcessServiceName")
			bindPluginProcessService(pluginProcessServiceName)
			Log.d(TAG, "PPS 等待绑定 ---------------- $pluginProcessServiceName")

			waitServiceConnected(10, TimeUnit.SECONDS)
		}

		Log.d(TAG, "载入 RunTime ---------------")
		loadRunTime(uuid)    // runtime 已加载之后不会进行重新加载， 即 PluginProcessService 的 mUuid 不会被更改
		Log.d(TAG, "--------------- RunTime  加载结束")

		Log.d(TAG, "载入 Loader -------------------")
		loadPluginLoader(uuid)   // Loader 已加载之后不会进行重新加载, 即 PluginProcessService 的 mUuid 不会被更改
		Log.d(TAG, "--------------- Loader  加载结束")
	}

	protected open fun startLoadPlugin(uuid: String, partKey: String) {
		Log.d(TAG, "startLoadPlugin() called with: uuid = $uuid, partKey = $partKey")

		mPluginLoader.loadPlugin(partKey)
	}
}