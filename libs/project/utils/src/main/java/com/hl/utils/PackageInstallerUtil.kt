package com.hl.utils

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInstaller
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.OutputStream

/**
 * @author  张磊  on  2025/11/05 at 9:46
 * Email: 913305160@qq.com
 */

object PackageInstallerUtil {

	private const val TAG = "PackageInstallerUtil"
	
	private const val INSTALL_SESSION_ID = "install_session_id"
	private const val INSTALL_ACTION = "install_action"
	private const val INSTALL_PACKAGE_NAME = "install_package_name"

	/**
	 * 使用 PackageInstaller API 安装应用
	 * @param context 上下文
	 * @param apkFilePath APK 文件路径
	 * @param packageName 应用包名
	 * @param callback 安装结果回调
	 */
	fun installApp(context: Context, apkFilePath: String, packageName: String, callback: InstallCallback? = null) {
		val file = File(apkFilePath)
		if (!file.exists() || !file.canRead()) {
			Log.e(TAG, "APK 文件不存在或无法读取: $apkFilePath")
			callback?.onInstallResult(false, "APK 文件不存在或无法读取")
			return
		}

		if (context !is FragmentActivity) {
			callback?.onInstallResult(false, "context 不是 FragmentActivity")
			return
		}

		if (hasInstallPermission(context)) {
			startInstallApk(context, apkFilePath, packageName, callback)
		} else {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
				// Android 14+ 直接调用安装方法, 系统会自动显示权限请求弹窗
				startInstallApk(context, apkFilePath, packageName, callback)
				return
			}

			requestInstallPermission(context) {
				if (it) {
					startInstallApk(context, apkFilePath, packageName, callback)
				} else {
					callback?.onInstallResult(false, "安装未知来源应用权限被拒绝")
				}
			}
		}
	}

	/**
	 * 检查是否有安装未知来源应用的权限
	 */
	private fun hasInstallPermission(context: Context): Boolean {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			context.packageManager.canRequestPackageInstalls()
		} else {
			true
		}
	}

	/**
	 * 请求安装未知来源应用的权限
	 * 使用 registerForActivityResult 方式实现
	 *
	 * @param fragmentActivity 活动实例
	 * @param onPermissionResult 权限请求结果回调
	 */
	private fun requestInstallPermission(
		fragmentActivity: FragmentActivity,
		onPermissionResult: (Boolean) -> Unit
	) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// 创建权限请求的Intent
			val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
			intent.data = "package:${fragmentActivity.packageName}".toUri()

			val fragmentManager = fragmentActivity.supportFragmentManager
			val fragment = fragmentManager.findFragmentByTag("PermissionFragment") as? PermissionFragment ?: PermissionFragment()

			fragmentManager.beginTransaction()
				.add(fragment, "PermissionFragment")
				.commitNowAllowingStateLoss()

			fragment.startPermissionRequest(intent) {
				onPermissionResult(hasInstallPermission(fragmentActivity))
			}
		} else {
			// Android 8.0以下版本默认有安装权限
			onPermissionResult(true)
		}
	}

	/**
	 * 注册广播接收器
	 */
	private fun registerBroadcastReceiver(context: Context, receiver: BroadcastReceiver, intentFilter: IntentFilter) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			context.registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
		} else {
			context.registerReceiver(receiver, intentFilter)
		}

		if (context is LifecycleOwner) {
			context.lifecycle.addObserver(object : DefaultLifecycleObserver {
				override fun onDestroy(owner: LifecycleOwner) {
					context.unregisterReceiver(receiver)
				}
			})
		}
	}



	private fun startInstallApk(context: Context, apkFilePath: String, packageName: String, callback: InstallCallback?) {
		try {
			val file = File(apkFilePath)
			if (!file.exists()) {
				callback?.onInstallResult(false, "APK 文件不存在: $apkFilePath")
				return
			}

			// 检查文件权限
			if (!file.canRead()) {
				callback?.onInstallResult(false, "无法读取APK文件，请检查权限: $apkFilePath")
				return
			}

			// 创建安装会话
			val packageInstaller = context.packageManager.packageInstaller
			val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
			params.setAppPackageName(packageName) // 设置要安装的应用包名

			// 对于Android 14，确保设置正确的用户操作要求
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
				params.setRequireUserAction(PackageInstaller.SessionParams.USER_ACTION_REQUIRED)
			}

			Log.d(TAG, "开始创建安装会话，文件路径: $apkFilePath, 包名: $packageName")

			val sessionId = packageInstaller.createSession(params)
			val session = packageInstaller.openSession(sessionId)

			try {
				// 将 APK 文件写入会话
				val out = session.openWrite("install", 0, -1)
				val fis = FileInputStream(file)
				writeStreamToStream(fis, out)
				session.fsync(out)
				out.close()
				fis.close()

				// 创建安装完成的广播接收器
				val intent = Intent(INSTALL_ACTION)
				intent.putExtra(INSTALL_SESSION_ID, sessionId)
				intent.putExtra(INSTALL_PACKAGE_NAME, packageName)
				// 设置组件，使Intent变为显式
				intent.setPackage(context.packageName)

				val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
					// 在Android 12及以上版本，使用FLAG_IMMUTABLE而不是FLAG_MUTABLE, 经测试发现使用 FLAG_IMMUTABLE 会安装失败，因此还使用 FLAG_MUTABLE
					PendingIntent.getBroadcast(
						context,
						sessionId,
						intent,
						PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
					)
				} else {
					PendingIntent.getBroadcast(
						context,
						sessionId,
						intent,
						PendingIntent.FLAG_UPDATE_CURRENT
					)
				}

				// 提交安装会话
				session.commit(pendingIntent.intentSender)
				Log.d(TAG, "安装会话已提交，等待用户确认")
			} finally {
				session.close()
			}

			// 注册安装结果广播接收器
			val installReceiver = InstallResultReceiver(callback)
			val intentFilter = IntentFilter(INSTALL_ACTION)
			registerBroadcastReceiver(context, installReceiver, intentFilter)
		} catch (e: SecurityException) {
			e.printStackTrace()
			callback?.onInstallResult(false, "安装权限被拒绝: ${e.message}")
		} catch (e: IOException) {
			e.printStackTrace()
			callback?.onInstallResult(false, "APK文件读写失败: ${e.message}")
		} catch (e: Exception) {
			e.printStackTrace()
			callback?.onInstallResult(false, "安装失败: ${e.message}")
		}
	}

	/**
	 * 写入流数据
	 */
	@Throws(IOException::class)
	private fun writeStreamToStream(input: FileInputStream, output: OutputStream) {
		val buffer = ByteArray(4096)
		var bytesRead: Int
		while (input.read(buffer).also { bytesRead = it } != -1) {
			output.write(buffer, 0, bytesRead)
		}
	}

	/**
	 * 安装结果回调接口
	 */
	interface InstallCallback {
		fun onInstallResult(success: Boolean, message: String)
	}

	/**
	 * 安装结果广播接收器
	 */
	private class InstallResultReceiver(private val callback: InstallCallback?) : BroadcastReceiver() {

		override fun onReceive(context: Context, intent: Intent) {
			if (INSTALL_ACTION != intent.action) {
				return
			}

			val sessionId = intent.getIntExtra(INSTALL_SESSION_ID, -1)
			val packageName = intent.getStringExtra(INSTALL_PACKAGE_NAME)

			// 获取安装状态和详细错误信息
			val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE)
			val statusMessage = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)

			// 添加更多调试信息
			val localIntent = intent.getParcelable<Intent>(Intent.EXTRA_INTENT)
			val otherPackageName = intent.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME)

			// 打印详细的调试日志
			val debugInfo = buildString {
				append("安装状态: $status, 消息: $statusMessage, SessionID: $sessionId, 包名: $packageName, ")

				if (status == PackageInstaller.STATUS_PENDING_USER_ACTION) {
					val isReplacing = intent.getBooleanExtra(PackageInstaller.EXTRA_PACKAGE_NAME, false)
					append("替换模式: $isReplacing, ")
				}
				append("其他包名: $otherPackageName, ")
				append("额外Intent: $localIntent")
			}
			Log.d(TAG, debugInfo)

			when (status) {
				PackageInstaller.STATUS_PENDING_USER_ACTION -> {
					// 需要用户确认安装
					Log.d(TAG, "需要用户确认安装")
					val confirmIntent = intent.getParcelable<Intent>(Intent.EXTRA_INTENT)
					confirmIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
					try {
						context.startActivity(confirmIntent)
					} catch (e: Exception) {
						e.printStackTrace()
						callback?.onInstallResult(false, "启动安装确认界面失败: ${e.message}")
					}
				}

				PackageInstaller.STATUS_SUCCESS -> {
					// 安装成功
					Log.d(TAG, "应用安装成功: $packageName")
					callback?.onInstallResult(true, "应用安装成功: $packageName")
				}

				PackageInstaller.STATUS_FAILURE -> {
					// 安装失败 - 通用失败
					Log.e(TAG, "安装失败: $statusMessage")
					callback?.onInstallResult(false, "安装失败: $statusMessage")
				}

				PackageInstaller.STATUS_FAILURE_ABORTED -> {
					// 用户取消了安装
					Log.e(TAG, "用户取消安装: $statusMessage")
					callback?.onInstallResult(false, "用户取消安装")
				}

				PackageInstaller.STATUS_FAILURE_BLOCKED -> {
					// 安装被系统或策略阻止
					Log.e(TAG, "安装被阻止: $statusMessage")
					callback?.onInstallResult(false, "安装被系统阻止: $statusMessage")
				}

				PackageInstaller.STATUS_FAILURE_CONFLICT -> {
					// 与现有应用冲突
					Log.e(TAG, "与现有应用冲突: $statusMessage")
					callback?.onInstallResult(false, "与现有应用冲突: $statusMessage")
				}

				PackageInstaller.STATUS_FAILURE_INCOMPATIBLE -> {
					// 应用与设备不兼容
					Log.e(TAG, "应用与设备不兼容: $statusMessage")
					callback?.onInstallResult(false, "应用与设备不兼容: $statusMessage")
				}

				PackageInstaller.STATUS_FAILURE_INVALID -> {
					// APK文件无效
					Log.e(TAG, "APK文件无效: $statusMessage")
					callback?.onInstallResult(false, "APK文件无效: $statusMessage")
				}

				else -> {
					// 其他未知失败
					Log.e(TAG, "未知安装失败，状态码: $status, 消息: $statusMessage")
					callback?.onInstallResult(false, "安装失败: $statusMessage")
				}
			}

			// 非等待确认状态取消广播
			if (PackageInstaller.STATUS_PENDING_USER_ACTION != status) {
				// 注销接收器
				try {
					context.unregisterReceiver(this)
				} catch (e: Exception) {
					// 忽略已经注销的异常
					e.printStackTrace()
				}
			}
		}
	}

	private inline fun <reified T> Intent.getParcelable(key: String): T? {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			this.getParcelableExtra(key, T::class.java)
		} else {
			this.getParcelableExtra(key) as? T
		}
	}
}

/**
 * 用于处理权限请求的临时 Fragment , 解决在错误生命周期状态下无法使用 registerForActivityResult的问题
 */
class PermissionFragment : Fragment() {

	var permissionCallback: (() -> Unit)? = null

	// 定义ActivityResultContract用于处理权限请求结果
	val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
		// 无论用户是确认还是取消，都重新检查权限状态
		permissionCallback?.invoke()

		// 清理Fragment
		permissionCallback = null
		parentFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
	}

	fun startPermissionRequest(intent: Intent, callback: () -> Unit) {
		this.permissionCallback = callback
		// 启动权限请求
		requestPermissionLauncher.launch(intent)
	}
}
