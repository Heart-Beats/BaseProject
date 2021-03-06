package com.hl.utils.previewFie

import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.elvishew.xlog.XLog
import com.gyf.immersionbar.ImmersionBar
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnDownloadListener
import com.hl.uikit.gone
import com.hl.uikit.onClick
import com.hl.uikit.progressbar.UIKitCircleProgressBar
import com.hl.uikit.toast
import com.hl.uikit.video.UIKitMyStandardGSYVideoPlayer
import com.hl.uikit.visible
import com.hl.utils.*
import com.hl.utils.mimetype.MimeType
import com.hl.utils.previewFie.superFileView.DocView
import com.hl.utils.videoplayer.initPlayer
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_preview_file.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.*


class PreviewFileActivity : FragmentActivity() {

    companion object {
        const val FILE_NAME = "fileName"
        const val URL = "url"

        private const val TAG = "PreviewFileActivity"

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .statusBarColorInt(Color.WHITE)
            .statusBarDarkFont(true)
            .init()


        setContentView(R.layout.activity_preview_file)

        fileUrl = intent.getStringExtra(URL) ?: ""
        fileName = intent.getStringExtra(FILE_NAME)

        initView()
    }

    private fun initView() {
        progressBar = findViewById(R.id.progress_bar)

        titleTv.text = fileName ?: "????????????"
        fileType = getFileTypeByName(fileName).lowercase(Locale.getDefault())

        val mimeType = MimeType.getByExtension(fileType ?: "")

        when {
            fileType == "" && mimeType == null -> {
                toast("????????????????????????")
            }
            mimeType?.isImage() == true -> {
                mSuperFileView.visibility = View.GONE
                no_support_file_container.visibility = View.GONE
                img.visibility = View.VISIBLE
                GlideUtil.load(this, fileUrl, img)
            }
            mimeType?.isVideo() == true -> {
                mSuperFileView.visibility = View.GONE
                no_support_file_container.visibility = View.GONE
                video_player.visibility = View.VISIBLE

                initPlayer(video_player, fileUrl)
            }
            else -> {
                // ???????????????????????? X5 ??????
                openWithX5(fileUrl)
            }
        }

        back.onClick { onBackPressed() }
        dot.onClick {
            showPopup(it)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
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
        //?????????????????????
        if (video_player?.orientationUtils?.screenType == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ||
            video_player?.isIfCurrentIsFullscreen == true
        ) {
            video_player?.onBackFullscreen()
        } else {
            //????????????
            video_player?.setVideoAllCallBack(null)
            finish()
        }
    }


    private fun showPopup(v: View) {
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.preview_file_menu, popup.menu)

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                //??????????????????
                R.id.menu_select_other_app_open -> {
                    val cacheFile = getCacheFile()
                    Log.e(TAG, "??????:$" + cacheFile.absolutePath)

                    if (cacheFile.exists()) {
                        //????????????
                        OpenFileUtil.openFileShare(this, cacheFile.absolutePath)
                    } else {
                        EasyHttp.download(this)
                            .url(fileUrl)
                            .file(cacheFile)
                            .listener(object : OnDownloadListener {
                                override fun onStart(file: File?) {
                                    XLog.d("???????????? -------------> $file")
                                }

                                override fun onProgress(file: File?, progress: Int) {
                                    XLog.d("????????? -------------> $progress")
                                }

                                override fun onComplete(file: File?) {
                                    XLog.d("???????????? ------------->")

                                    OpenFileUtil.openFileShare(this@PreviewFileActivity, cacheFile.absolutePath)
                                }

                                override fun onError(file: File?, e: Exception?) {
                                    XLog.d("$file ????????????  ------------> $e")
                                    toast("?????????????????????????????????")
                                }

                                override fun onEnd(file: File?) {}
                            })
                            .start()
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
        PermissionX.init(this)
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            .request { allGranted: Boolean, _: List<String?>?, _: List<String?>? ->
                if (allGranted) {
                    mDocView = findViewById(R.id.mSuperFileView)

                    // ??????????????????????????????
                    mDocView?.openFailedAction = { openFile ->
                        preview_file_content.gone()
                        no_support_file_container.visible()

                        replaceFragment(R.id.no_support_file_container, NoSupportFileFragment().apply {
                            this.arguments = bundleOf(NoSupportFileFragment.NO_SUPPORT_FILE_KEY to openFile)
                        })
                    }

                    if (!TextUtils.isEmpty(fileUrl)) {
                        Log.e(TAG, "???????????? X5 ???????????????path:$fileUrl")

                        getFilePathAndShowFile(mDocView!!)
                    } else {
                        toast("???????????????????????????")
                    }
                } else {
                    toast("?????????????????????")
                }
            }

    }


    private fun getFilePathAndShowFile(mDocView: DocView) {
        //????????????????????????
        if (fileUrl.contains("http")) {
            try {
                downLoadFromNet(fileUrl, mDocView)
            } catch (e: Exception) {
                toast("?????????????????????????????????????????????")
            }
        } else {
            // ????????????????????????
            mDocView.displayFile(File(fileUrl))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        if (mDocView != null) {
            mDocView!!.onStopDisplay()
        }
    }

    private fun downLoadFromNet(url: String, mDocView: DocView) {
        lifecycleScope.launch {
            val downloadFileLength = getDownloadFileLength(url)

            Log.e(TAG, "downLoadFromNet: ????????????????????? == $downloadFileLength")

            //1.??????????????????????????????
            val cacheFile = getCacheFile()
            if (cacheFile.exists()) {
                if (cacheFile.length() < downloadFileLength) {
                    Log.e(TAG, "downLoadFromNet: ???????????????????????????????????????????????????")
                    cacheFile.delete()
                } else {
                    Log.e(TAG, "downLoadFromNet: ????????????????????????")
                    mDocView.displayFile(cacheFile)
                    return@launch
                }
            }

            Log.e(TAG, "downLoadFromNet: ????????????????????????????????????")

            progressBar.visibility = View.VISIBLE

            EasyHttp.download(this@PreviewFileActivity)
                .url(url)
                .file(cacheFile)
                .listener(object : OnDownloadListener {
                    override fun onStart(file: File?) {
                        XLog.d("???????????? -------------> $file")
                    }

                    override fun onProgress(file: File?, progress: Int) {
                        XLog.d("????????? -------------> $progress")
                        progressBar.progress = progress.toFloat()
                    }

                    override fun onComplete(file: File?) {
                        XLog.d("????????????????????????????????? ------------->")

                        mDocView.displayFile(file)
                    }

                    override fun onError(file: File?, e: Exception?) {
                        XLog.d("$file ????????????  ------------> $e")

                        if (file?.exists() == true) {
                            Log.e(TAG, "????????????????????????")
                            file.delete()
                        }

                        toast("????????????????????????")
                    }

                    override fun onEnd(file: File?) {
                        progressBar.visibility = View.GONE
                    }
                })
                .start()
        }
    }

    private suspend fun getDownloadFileLength(url: String): Long {
        return try {
            withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url(url)
                    .head()
                    .build()

                OkHttpClient().newCall(request).execute().header("Content-Length")?.toLongOrNull() ?: 0
            }
        } catch (e: Exception) {
            XLog.e(e)

            0
        }
    }

    /***
     * ??????????????????
     *
     * @return
     */
    private fun getCacheDocDir(): File {
        return getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?: File(Environment.getExternalStorageDirectory().absolutePath, "${packageName}/docs")
    }

    /***
     * ??????????????????????????????
     *
     * @return
     */
    private fun getCacheFile(): File {
        val cacheFile = File(getCacheDocDir(), fileName ?: "")
        Log.e(TAG, "?????????????????? = $cacheFile")
        return cacheFile
    }


    private fun getFileTypeByName(fileName: String?): String {
        return fileName?.run {
            if (contains(".")) substring(lastIndexOf(".") + 1)
            else ""
        } ?: ""
    }

}