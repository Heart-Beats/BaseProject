package com.hl.pictureselector

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import com.luck.picture.lib.basic.PictureSelectionCameraModel
import com.luck.picture.lib.basic.PictureSelectionModel
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener


/**
 * @author  张磊  on  2022/01/14 at 23:06
 * Email: 913305160@qq.com
 */

object PickImageUtil {

	fun startTakePhoto(
		context: Context,
		option: PictureSelectionCameraModel.() -> Unit = {},
		onSelectCancel: () -> Unit = {},
		onSelectResult: (imagePaths: List<String>) -> Unit
	) {
		PictureSelector.create(context)
			.openCamera(SelectMimeType.ofImage())
			.setOutputCameraDir(context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath)
			.setCompressEngine(CompressEngine(true))
			.apply(option)
			.forResultActivity(object : OnResultCallbackListener<LocalMedia> {
				override fun onResult(result: ArrayList<LocalMedia>) {
					result.map { it.availablePath }.run {
						onSelectResult(this)

						this.forEach { photoPath ->
							//发送广播，通知图库更新
							val values = ContentValues()
							values.put(MediaStore.Images.Media.DATA, photoPath)
							values.put(MediaStore.Images.Media.MIME_TYPE, "image/*")
							val uri =
								context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
							context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
						}
					}
				}

				override fun onCancel() {
					onSelectCancel()
				}
			})
	}

	fun startPictureSelect(
		context: Context,
		option: PictureSelectionModel.() -> Unit = {},
		onSelectCancel: () -> Unit = {},
		onSelectResult: (imagePaths: List<String>) -> Unit
	) {
		PictureSelector.create(context)
			.openGallery(SelectMimeType.ofImage())
			.setImageEngine(GlideEngine())
			.setCompressEngine(CompressEngine())
			.apply(option)
			.forResult(object : OnResultCallbackListener<LocalMedia> {

				override fun onResult(result: ArrayList<LocalMedia>) {
					result.map { it.availablePath }.run {
						onSelectResult(this)
					}
				}

				override fun onCancel() {
					onSelectCancel()
				}
			})
	}
}