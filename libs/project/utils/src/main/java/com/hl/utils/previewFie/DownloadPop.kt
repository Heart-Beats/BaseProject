package com.hl.utils.previewFie

import android.os.Environment
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.elvishew.xlog.XLog
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnDownloadListener
import com.hjq.http.model.HttpMethod
import com.hl.uikit.onClick
import com.hl.uikit.toast
import com.hl.utils.R
import com.lxj.xpopup.core.BottomPopupView
import kotlinx.android.synthetic.main.item_file_download.view.*
import java.io.File

/**
 * @author  张磊  on  2022/04/27 at 19:46
 * Email: 913305160@qq.com
 */
class DownloadPop(private val fragmentActivity: FragmentActivity, private val downloadUrl: String) :
    BottomPopupView(fragmentActivity) {

    override fun getImplLayoutId(): Int {
        return R.layout.pop_download
    }

    override fun onCreate() {
        super.onCreate()

        val fileName = findViewById<TextView>(R.id.file_name)
        val downloadProgressBar = findViewById<ProgressBar>(R.id.download_progress_bar)
        val cancelDownload = findViewById<TextView>(R.id.cancel_download)

        fileName.text = getDownLoadFile().name

        cancelDownload.onClick {
            dismiss()

            EasyHttp.cancel(downloadUrl)
        }

        startDownload(downloadProgressBar)
    }

    private fun startDownload(downloadProgressBar: ProgressBar) {
        EasyHttp
            .download(fragmentActivity)
            .method(HttpMethod.GET)
            .file(getDownLoadFile())
            .tag(downloadUrl)
            .url(downloadUrl)
            .listener(object : OnDownloadListener {
                override fun onStart(file: File?) {
                    XLog.d("开始下载文件 ----------------> $downloadUrl")
                }

                override fun onProgress(file: File?, progress: Int) {
                    XLog.d("文件下载中 ----------------> $progress")

                    downloadProgressBar.progress = progress
                }

                override fun onComplete(file: File?) {
                    XLog.d("下载文件完成 ----------------> $file")

                    this@DownloadPop.popupInfo.run {
                        isDismissOnTouchOutside = true
                        isDismissOnBackPressed = true
                    }

                    fragmentActivity.toast("文件已保存至 ${file?.absolutePath}")

                    cancel_download.text = "完成"
                }

                override fun onError(file: File?, e: Exception?) {
                    XLog.d("下载文件出错 ----------------> $file", e)
                }

                override fun onEnd(file: File?) {
                }
            })
            .start()
    }

    private fun getDownLoadFile(): File {
        return File(
            fragmentActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath,
            downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1)
        )
    }
}