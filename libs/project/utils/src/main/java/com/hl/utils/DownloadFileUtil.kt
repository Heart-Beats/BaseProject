package com.hl.utils

import android.Manifest
import android.os.Environment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.FileUtils
import com.elvishew.xlog.XLog
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnDownloadListener
import com.hjq.http.request.DownloadRequest
import com.hl.uikit.toast
import java.io.File

/**
 * @author  张磊  on  2022/08/05 at 16:17
 * Email: 913305160@qq.com
 */
object DownloadFileUtil {

	/**
	 * 保存下载对应的请求
	 */
	private val downloadRequestMap = hashMapOf<DownloadRequest, String>()

	/**
	 * 开始下载文件
	 *
	 * @param lifecycleOwner    请传入 AppCompatActivity 或者 AndroidX.Fragment 子类
	 * @param fileUrl          文件下载地址
	 * @param isSave2AppDir    是否保存在应用私有目录下
	 * @param listener        下载监听回调
	 */
	fun startDownLoad(
		lifecycleOwner: LifecycleOwner,
		fileUrl: String,
		fileName: String? = null,
		isSave2AppDir: Boolean = true,
		listener: OnDownloadListener
	) {
		val cacheFile = getCacheFile(isSave2AppDir, fileUrl, fileName)

		if (!isSave2AppDir) {
			reqPermissions2Download(lifecycleOwner, fileUrl, cacheFile, listener)
		} else {
			download(lifecycleOwner, fileUrl, cacheFile, listener)
		}
	}

	/**
	 * 开始下载文件
	 *
	 * @param lifecycleOwner    请传入 AppCompatActivity 或者 AndroidX.Fragment 子类
	 * @param fileUrl          文件下载地址
	 * @param savePath         文件保存的全路径
	 * @param listener        下载监听回调
	 */
	fun startDownLoad(
		lifecycleOwner: LifecycleOwner,
		fileUrl: String,
		savePath: String,
		listener: OnDownloadListener
	) {
		val cacheFile = File(savePath)
		reqPermissions2Download(lifecycleOwner, fileUrl, cacheFile, listener)
	}

	/**
	 * 请求权限后开始下载
	 */
	private fun reqPermissions2Download(
		lifecycleOwner: LifecycleOwner,
		fileUrl: String,
		cacheFile: File,
		listener: OnDownloadListener
	) {
		when (lifecycleOwner) {
			is FragmentActivity -> {
				lifecycleOwner.reqPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, deniedAction = {
					lifecycleOwner.toast("您拒绝了权限，无法正常下载文件")
				}) {
					download(lifecycleOwner, fileUrl, cacheFile, listener)
				}
			}
			is Fragment -> {
				lifecycleOwner.reqPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, deniedAction = {
					lifecycleOwner.toast("您拒绝了权限，无法正常下载文件")
				}) {
					download(lifecycleOwner, fileUrl, cacheFile, listener)
				}
			}
			else -> {
				throw IllegalArgumentException("lifecycleOwner 必须为 FragmentActivity 或 Fragment 类型")
			}
		}
	}

	/**
	 * 开始下载文件
	 */
	private fun download(
		lifecycleOwner: LifecycleOwner,
		fileUrl: String,
		saveFile: File,
		listener: OnDownloadListener
	) {
		// 创建文件
		if (!FileUtils.createOrExistsFile(saveFile)) {
			XLog.d("创建下载文件失败")
		}

		val downloadRequest = EasyHttp.download(lifecycleOwner).url(fileUrl)
		downloadRequestMap[downloadRequest] = fileUrl

		downloadRequest
			.file(saveFile)
			.listener(object : OnDownloadListener {
				override fun onStart(file: File?) {
					listener.onStart(file)
				}

				override fun onProgress(file: File?, progress: Int) {
					listener.onProgress(file, progress)
				}

				override fun onComplete(file: File?) {
					listener.onComplete(file)
				}

				override fun onError(file: File?, e: Exception?) {
					listener.onError(file, e)
				}

				override fun onEnd(file: File?) {
					listener.onEnd(file)

					// 下载结束移除该请求
					if (downloadRequestMap.contains(downloadRequest)) {
						downloadRequestMap.remove(downloadRequest)
					}
				}
			})
			.start()
	}

	private fun getCacheFile(isSave2AppDir: Boolean, fileUrl: String, fileName: String?): File {
		val downloads = Environment.DIRECTORY_DOWNLOADS
		val saveDir = if (isSave2AppDir) BaseUtil.app.getExternalFilesDir(downloads)
		else Environment.getExternalStoragePublicDirectory(downloads)
		val fileName = fileName ?: fileUrl.substringAfterLast("/")
		return File(saveDir, fileName)
	}


	/**
	 * 停止指定地址的下载请求
	 *
	 * @param fileUrl  下载文件的地址
	 */
	fun stopDownload(fileUrl: String) {
		downloadRequestMap.forEach { (downloadRequest, url) ->
			if (fileUrl == url) {
				downloadRequest.stop()
			}
		}
	}

	/**
	 * 停止所有下载请求
	 */
	fun stopAllDownload() {
		downloadRequestMap.keys.forEach {
			it.stop()
		}
	}

}