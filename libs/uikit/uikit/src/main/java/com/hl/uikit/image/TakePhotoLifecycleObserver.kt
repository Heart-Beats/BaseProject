package com.hl.uikit.image

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author  张磊  on  2022/02/12 at 20:43
 * Email: 913305160@qq.com
 *
 * 使用时必须在 Activity 显示之前实例化
 */
class TakePhotoLifecycleObserver(private val activity: FragmentActivity) : DefaultLifecycleObserver {

    private lateinit var takePhoto: ActivityResultLauncher<Uri>

    private var onTakePhotoResult: (String) -> Unit = {}
    private lateinit var saveFile: File

    override fun onCreate(owner: LifecycleOwner) {
        takePhoto =
            activity.activityResultRegistry.register("take_photo", owner, ActivityResultContracts.TakePicture()) {
                if (it) {
                    onTakePhotoResult(saveFile.absolutePath)
                }
            }
    }

    fun takePhoto(onTakePhotoResult: (String) -> Unit) {
        this.onTakePhotoResult = onTakePhotoResult
        val takePhotoUri = getTakePhotoUri()
        takePhoto.launch(takePhotoUri)
    }

    private fun getTakePhotoUri(): Uri {
        val fileName = getFormattedNowDateTime() + ".jpg"
        saveFile = File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
        return file2Uri(activity, saveFile.absolutePath)
    }


    /**
     * 获取当前日期时间
     * @param
     * @return 格式化后的日期时间字符串
     */
    private fun getFormattedNowDateTime(): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.format(Date())
    }

    private fun file2Uri(context: Context, filePath: String): Uri {
        val file = File(filePath)
        if (!file.exists()) {
            if (file.parentFile?.exists() == false) {
                file.parentFile?.mkdirs()
            }
            if (file.parentFile?.exists() == true) {
                file.createNewFile()
            }
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 需要配置文件 fileprovider，注意配置的 authority
            FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
        } else {
            Uri.fromFile(file)
        }
    }
}