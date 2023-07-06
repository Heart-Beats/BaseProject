package com.hl.previewfile

import android.os.Environment
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.elvishew.xlog.XLog
import com.hjq.http.listener.OnDownloadListener
import com.hl.download.DownloadFileUtil
import com.hl.ui.utils.onClick
import com.hl.uikit.toast
import com.lxj.xpopup.core.BottomPopupView
import java.io.File

/**
 * @author  张磊  on  2022/04/27 at 19:46
 * Email: 913305160@qq.com
 */
class DownloadPop(private val fragmentActivity: FragmentActivity, private val downloadUrl: String) :
    BottomPopupView(fragmentActivity) {

    private var cancelDownload: TextView? = null

    override fun getImplLayoutId(): Int {
        return R.layout.hl_preview_file_pop_download
    }

    override fun onCreate() {
        super.onCreate()

        val fileName = findViewById<TextView>(R.id.file_name)
        val downloadProgressBar = findViewById<ProgressBar>(R.id.download_progress_bar)
        cancelDownload = findViewById(R.id.cancel_download)

        fileName.text = getDownLoadFile().name

        cancelDownload?.onClick {
            dismiss()

            DownloadFileUtil.stopDownload(downloadUrl)
        }

        startDownload(downloadProgressBar)
    }

    private fun startDownload(downloadProgressBar: ProgressBar) {
        DownloadFileUtil.startDownLoad(
            fragmentActivity, downloadUrl, getDownLoadFile().absolutePath,
            listener = object : OnDownloadListener {
                override fun onDownloadProgressChange(file: File?, progress: Int) {
                    XLog.d("文件下载中 ----------------> $progress")

                    downloadProgressBar.progress = progress
                }

                override fun onDownloadSuccess(file: File?) {
                    XLog.d("下载文件完成 ----------------> $file")

                    this@DownloadPop.popupInfo.run {
                        isDismissOnTouchOutside = true
                        isDismissOnBackPressed = true
                    }

                    fragmentActivity.toast("文件已保存至 ${file?.absolutePath}")

                    cancelDownload?.text = "完成"
                }

                override fun onDownloadFail(file: File?, e: Exception?) {
                    XLog.d("下载文件出错 ----------------> $file", e)
                }
            })
    }

    private fun getDownLoadFile(): File {
        return File(
            fragmentActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath,
            downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1)
        )
    }
}