package com.hl.arch.web.client

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.webkit.GeolocationPermissions
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.hl.arch.web.helpers.logJs
import com.hl.uikit.image.pictureselector.GlideEngine
import com.hl.uikit.image.pictureselector.MyCompressEngine
import com.hl.utils.reqPermissions
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener

/**
 * @author  张磊  on  2021/12/21 at 12:07
 * Email: 913305160@qq.com
 *
 * 自定义 WebChromeClient 辅助WebView处理图片上传操作【<input type=file> 文件上传标签】
 */
open class MyWebChromeClient(val fragment: Fragment) : WebChromeClient() {

	companion object {
		private const val REQUEST_CODE_SELECT_FILE = 1104
	}

	/**
	 * web 文件图片选择 将文件流回传给web
	 */
	private var mUploadCallbackAboveL: ValueCallback<Array<Uri>>? = null


	// For Android 5.0+
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	override fun onShowFileChooser(
		webView: WebView?,
		filePathCallback: ValueCallback<Array<Uri>>,
		fileChooserParams: FileChooserParams
	): Boolean {
		mUploadCallbackAboveL = filePathCallback
		val acceptTypes: Array<String> = fileChooserParams.acceptTypes
		val isMultipleChoose = fileChooserParams.mode == FileChooserParams.MODE_OPEN_MULTIPLE

		val stringBuffer = StringBuffer()
		for (i in acceptTypes.indices) {
			if (stringBuffer.isEmpty()) {
				stringBuffer.append(acceptTypes[i])
			} else {
				stringBuffer.append("," + acceptTypes[i])
			}
		}

		logJs("选择文件:", "是否多选模式 == ${isMultipleChoose}, 选择文件的类型 == $stringBuffer")
		fileChooser(stringBuffer.toString(), acceptTypes, fileChooserParams.isCaptureEnabled, isMultipleChoose)
		return true
	}

	/**
	 * Android7.0 开始，只有来自安全的链接如 https 才会回调此方法，不安全的来源系统将自动拒绝定位请求。
	 */
	override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
		callback.invoke(origin, true, false)
		logJs("onGeolocationPermissionsShowPrompt", "$origin---------------> 请求定位")
		super.onGeolocationPermissionsShowPrompt(origin, callback)
	}

	private fun fileChooser(
		accept: String,
		acceptTypes: Array<String>,
		captureEnabled: Boolean,
		isMultipleChoose: Boolean
	) {

		var chooseMode: Int? = null

		when {
			accept.startsWith("video") -> {
				chooseMode = SelectMimeType.TYPE_VIDEO
			}
			accept.startsWith("image") -> {
				chooseMode = SelectMimeType.TYPE_IMAGE
			}
			else -> {
				fragment.reqPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
					// val i = Intent(Intent.ACTION_OPEN_DOCUMENT)
					// i.addCategory(Intent.CATEGORY_OPENABLE)
					// i.putExtra(Intent.EXTRA_MIME_TYPES, acceptTypes)
					// i.type = "*/*"
					// activity.startActivityForResult(i, REQUEST_CODE_SELECT_FILE)


					// vivio 手机上述方式不行
					val intent = Intent(Intent.ACTION_GET_CONTENT)
					intent.addCategory(Intent.CATEGORY_OPENABLE)

					// 是否多选
					intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, isMultipleChoose)
					//i.putExtra(Intent.EXTRA_MIME_TYPES, acceptTypes)
					intent.type = "*/*"
					fragment.startActivityForResult(intent, REQUEST_CODE_SELECT_FILE)
				}
			}
		}

		chooseMode?.run {
			val operaType = if (chooseMode == SelectMimeType.TYPE_IMAGE) "选择照片" else "选择录像"

			val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
			fragment.reqPermissions(*permissions) {
				PictureSelector.create(fragment)
					.openGallery(chooseMode)
					.setMaxSelectNum(if (isMultipleChoose) 9 else 1)
					.setCompressEngine(MyCompressEngine())
					.setImageEngine(GlideEngine.createGlideEngine())
					.forResult(object : OnResultCallbackListener<LocalMedia> {
						override fun onResult(result: ArrayList<LocalMedia>?) {
							val availableUris = result?.map { Uri.parse(it.availablePath) }

							logJs("请求${operaType}", "${operaType}成功：结果 == $availableUris")
							availableUris?.run {
								mUploadCallbackAboveL?.onReceiveValue(this.toTypedArray())
							}
						}

						override fun onCancel() {
							logJs("请求${operaType}", "取消${operaType}")
							mUploadCallbackAboveL?.onReceiveValue(arrayOf())
						}
					})
			}
		}
	}

	fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		logJs("onActivityResult", "requestCode == $requestCode,  resultCode == $resultCode")

		if (resultCode == Activity.RESULT_OK) {
			when (requestCode) {
				REQUEST_CODE_SELECT_FILE -> {
					// 这里是针对从系统自带的文件选择器中选择问题

					val clipData = data?.clipData
					val resultUri = if (clipData != null) {
						//有选择多个文件

						val uris = mutableListOf<Uri>()
						repeat(clipData.itemCount) {
							uris.add(clipData.getItemAt(it).uri)
						}

						uris.toTypedArray()
					} else {
						// 单选文件

						data?.data?.let { arrayOf(it) }
					}

					mUploadCallbackAboveL?.onReceiveValue(resultUri)
				}
				else -> {}
			}
		} else {
			if (mUploadCallbackAboveL != null) {
				mUploadCallbackAboveL!!.onReceiveValue(arrayOf())
				mUploadCallbackAboveL = null
			}
		}
	}
}