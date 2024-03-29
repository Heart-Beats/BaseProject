package com.hl.previewfile

import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import com.elvishew.xlog.XLog
import com.hjq.http.listener.OnDownloadListener
import com.hl.download.DownloadFileUtil
import com.hl.imageload.GlideUtil
import com.hl.mimetype.MimeType
import com.hl.permission.reqPermissions
import com.hl.popup.showPop
import com.hl.previewfile.databinding.HlPreviewFileActivityPreviewFileBinding
import com.hl.previewfile.utils.getHttpContentLength
import com.hl.previewfile.widgets.DocView
import com.hl.previewfile.widgets.UIKitCircleProgressBar
import com.hl.share.ShareUtil
import com.hl.ui.base.ViewBindingBaseActivity
import com.hl.ui.utils.gone
import com.hl.ui.utils.onClick
import com.hl.ui.utils.replaceFragment
import com.hl.ui.utils.startAct
import com.hl.ui.utils.visible
import com.hl.uikit.toast
import com.hl.videoplayer.UIKitMyStandardGSYVideoPlayer
import com.hl.videoplayer.initPlayer
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale


class PreviewFileActivity : ViewBindingBaseActivity<HlPreviewFileActivityPreviewFileBinding>() {

    companion object {
        const val FILE_NAME = "fileName"
        const val URL = "url"

        @JvmStatic
        fun start(context: Context, fileName: String, url: String?) {
            context.startAct<PreviewFileActivity> {
                putExtra(FILE_NAME, fileName)
                putExtra(URL, url)
            }
        }
    }

    private var mDocView: DocView? = null
    private lateinit var progressBar: UIKitCircleProgressBar

    private var fileUrl: String = ""
    private var fileType: String? = "doc"
    private var fileName: String? = null
    override fun HlPreviewFileActivityPreviewFileBinding.onViewCreated(savedInstanceState: Bundle?) {
        fileUrl = intent.getStringExtra(URL) ?: ""
        fileName = intent.getStringExtra(FILE_NAME)

        initView()
    }

    private fun HlPreviewFileActivityPreviewFileBinding.initView() {
        this@PreviewFileActivity.progressBar = progressBar

        titleTv.text = fileName ?: "文件预览"
        fileType = getFileTypeByName(fileName).lowercase(Locale.getDefault())

        val mimeType = MimeType.getByExtension(fileType ?: "")

        when {
            fileType == "" && mimeType == null -> {
                toast("获取文件类型失败")
            }
            mimeType?.isImage() == true -> {
                mSuperFileView.gone()
                noSupportFileContainer.gone()
                img.visible()
                GlideUtil.load(img.context, fileUrl, img)
            }
            mimeType?.isVideo() == true -> {
                mSuperFileView.gone()
                noSupportFileContainer.gone()
                videoPlayer.visible()

                initPlayer(videoPlayer, fileUrl)
            }
            else -> {
                // 其他文档格式使用 X5 预览
                openWithX5(fileUrl)
            }
        }

        back.onClick { onBackPressed() }
        dot.onClick {
            showPopup(it)
        }
    }

    private fun initPlayer(videoPlayer: UIKitMyStandardGSYVideoPlayer, url: String) {
        reqPermissions(allGrantedAction = {
            videoPlayer.initPlayer(this, url, fileName?.split(".")?.firstOrNull() ?: "") {}
        })
    }

    override fun onBackPressed() {
        back()
    }

    private fun back() {
        val video_player = viewBinding.videoPlayer

        //先返回正常状态
        if (video_player?.orientationUtils?.screenType == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ||
            video_player?.isIfCurrentIsFullscreen == true
        ) {
            video_player?.onBackFullscreen()
        } else {
            //释放所有
            video_player?.setVideoAllCallBack(null)
            finish()
        }
    }


    private fun showPopup(v: View) {
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.hl_preview_file_menu, popup.menu)

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                //系统打开文件
                R.id.menu_select_other_app_open -> {
                    val cacheFile = getCacheFile()
                    XLog.e("分享:$" + cacheFile.absolutePath)

                    if (cacheFile.exists()) {
                        //系统分享
                        ShareUtil.shareFile(this, cacheFile.absolutePath)
                    } else {
                        DownloadFileUtil.startDownLoad(
                            this@PreviewFileActivity, fileUrl, cacheFile.absolutePath,
                            listener = object : OnDownloadListener {
                                override fun onDownloadProgressChange(file: File?, progress: Int) {
                                    XLog.d("下载中 -------------> $progress")
                                }

                                override fun onDownloadSuccess(file: File?) {
                                    XLog.d("下载完成 ------------->")

                                    ShareUtil.shareFile(this@PreviewFileActivity, cacheFile.absolutePath)
                                }

                                override fun onDownloadFail(file: File?, e: Exception?) {
                                    XLog.d("$file 下载失败  ------------> $e")
                                    toast("文件下载失败，无法分享")
                                }
                            })
                    }
                }
                R.id.menu_save_file -> {
                    saveCacheFile()
                }
            }
            popup.dismiss()
            true
        }
        popup.show()
    }

    private fun saveCacheFile() {
        DownloadPop(this, fileUrl).showPop {
            this.isViewMode(true)
            this.enableDrag(false)
            this.dismissOnTouchOutside(false)
            this.dismissOnBackPressed(false)
        }
    }

    private fun openWithX5(fileUrl: String) {
        reqPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, deniedAction = {
            toast("请授予相关权限")
        }) {
            mDocView = findViewById(R.id.mSuperFileView)

            // 设置打开失败时的处理
            mDocView?.openFailedAction = { openFile ->
                viewBinding.previewFileContent.gone()
                viewBinding.noSupportFileContainer.visible()

                replaceFragment<NoSupportFileFragment>(R.id.no_support_file_container) {
                    this.putString(NoSupportFileFragment.NO_SUPPORT_FILE_KEY, openFile.absolutePath)
                }
            }

            if (!TextUtils.isEmpty(fileUrl)) {
                XLog.e("需要使用 X5 预览的文件path:$fileUrl")

                getFilePathAndShowFile(mDocView!!)
            } else {
                toast("请提供预览文件链接")
            }
        }
    }


    private fun getFilePathAndShowFile(mDocView: DocView) {
        //网络地址要先下载
        if (fileUrl.contains("http")) {
            try {
                downLoadFromNet(fileUrl, mDocView)
            } catch (e: Exception) {
                toast("下载预览文件失败，请重新尝试！")
            }
        } else {
            // 本地文件直接打开
            mDocView.displayFile(File(fileUrl))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDocView != null) {
            mDocView!!.onStopDisplay()
        }
    }

    private fun downLoadFromNet(url: String, mDocView: DocView) {
        lifecycleScope.launch {
            val downloadFileLength = url.getHttpContentLength()

            XLog.e("downLoadFromNet: 下载文件总大小 == $downloadFileLength")

            //1.网络下载、存储路径、
            val cacheFile = getCacheFile()
            if (cacheFile.exists()) {
                if (cacheFile.length() < downloadFileLength) {
                    XLog.e("downLoadFromNet: 缓存文件小于下载文件大小，直接删除")
                    cacheFile.delete()
                } else {
                    XLog.e("downLoadFromNet: 直接预览缓存文件")
                    mDocView.displayFile(cacheFile)
                    return@launch
                }
            }

            XLog.e("downLoadFromNet: 缓存文件不存在，准备下载")

            progressBar.visibility = View.VISIBLE

            DownloadFileUtil.startDownLoad(
                this@PreviewFileActivity, url, cacheFile.absolutePath,
                listener = object : OnDownloadListener {
                    override fun onDownloadProgressChange(file: File?, progress: Int) {
                        XLog.d("下载中 -------------> $progress")
                        progressBar.progress = progress.toFloat()
                    }

                    override fun onDownloadSuccess(file: File?) {
                        XLog.d("下载完成，准备展示文件 ------------->")

                        mDocView.displayFile(file)
                    }

                    override fun onDownloadFail(file: File?, e: Exception?) {
                        XLog.e("$file 下载失败  ------------> ", e)

                        if (file?.exists() == true) {
                            XLog.e("删除下载失败文件")
                            file.delete()
                        }

                        toast("下载预览文件失败")
                    }

                    override fun onDownloadEnd(file: File?) {
                        progressBar.visibility = View.GONE
                    }
                })
        }
    }

    /***
     * 获取缓存目录
     *
     * @return
     */
    private fun getCacheDocDir(): File {
        return getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?: File(Environment.getExternalStorageDirectory().absolutePath, "${packageName}/docs")
    }

    /***
     * 绝对路径获取缓存文件
     *
     * @return
     */
    private fun getCacheFile(): File {
        val cacheFile = File(getCacheDocDir(), fileName ?: "")
        XLog.e("获取缓存文件 = $cacheFile")
        return cacheFile
    }


    private fun getFileTypeByName(fileName: String?): String {
        return fileName?.run {
            if (contains(".")) substring(lastIndexOf(".") + 1)
            else ""
        } ?: ""
    }

}