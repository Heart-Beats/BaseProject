package com.hl.uikit.image.pictureselector

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.hl.uikit.actionsheet.ArrayListSheetDialogFragment
import com.hl.uikit.reqPermissions
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

internal object PickImageUtil {

    /**
     *
     * Fragment中调用startActivityForResult要注意几种情况：
     *      1. 用 getActivity方法发起调用，只有父 Activity 的 onActivityResult 会调用，Fragment 中的 onActivityResult 不会被调用
     *      2. 直接发起 startActivityForResult 调用，当前的 Fragment 的 onActivityResult，和父 Activity 的 onActivityResult 都会调用
     *      3. 用 getParentFragment 发起调用，则只有父 Activity 和父 Fragment 的 onActivityResult 会被调用，当前的 Fragment的onActivityResult
     *      不会被调用。
     *
     *  这里 2 和 3 的前提是如果父 activity 中重写了 onActivityResult，父 Activity 的 onActivityResult 中必须添加 super.onActivityResult()
     *
     *  总结起来就是：从哪里发起调用，最终就会走到哪里。
     *
     */

    fun showSelectImageDialog(activity: FragmentActivity, onImagePathResult: (imagePath: String) -> Unit) {
        ArrayListSheetDialogFragment<String>().apply {
            this.data = listOf("拍照", "相册")
            this.addNegativeButton()

            this.itemClickListener = { dialog, position ->
                val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                reqPermissions(*permissions, deniedAction = {
                    Toast.makeText(activity, "权限授予失败", Toast.LENGTH_SHORT).show()
                }) {
                    dialog.dismiss()
                    when (position) {
                        0 -> startTakePhoto(activity) {
                            onImagePathResult(it.firstOrNull() ?: "")
                        }
                        1 -> startPictureSelect(activity, option = {
                            this.isDisplayCamera(false)
                            this.setMaxSelectNum(1)
                        }) {
                            onImagePathResult(it.firstOrNull() ?: "")
                        }
                    }
                }
            }
        }.show(activity.supportFragmentManager, "")
    }

    fun startTakePhoto(
        context: Context,
        option: PictureSelectionCameraModel.() -> Unit = {},
        onImagePathResult: (imagePaths: List<String>) -> Unit
    ) {
        PictureSelector.create(context)
            .openCamera(SelectMimeType.ofImage())
            .setOutputCameraDir(context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath)
            .setCompressEngine(CompressEngine(true))
            .apply(option)
            .forResultActivity(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>) {
                    result.map { it.availablePath }.run {
                        onImagePathResult(this)

                        this.forEach { photoPath ->
                            //发送广播，通知图库更新
                            val values = ContentValues()
                            values.put(MediaStore.Images.Media.DATA, photoPath)
                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/*")
                            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
                        }
                    }
                }

                override fun onCancel() {}
            })
    }

    fun startPictureSelect(
        context: Context,
        option: PictureSelectionModel.() -> Unit = {},
        onImagePathResult: (imagePaths: List<String>) -> Unit
    ) {
        PictureSelector.create(context)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine())
            .setCompressEngine(CompressEngine())
            .apply(option)
            .forResult(object : OnResultCallbackListener<LocalMedia> {

                override fun onResult(result: ArrayList<LocalMedia>) {
                    result.map { it.availablePath }.run {
                        onImagePathResult(this)
                    }
                }

                override fun onCancel() {}
            })
    }
}