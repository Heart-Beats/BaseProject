package com.hl.update

interface DownloadApkObserver {
    fun onMd5CheckResult(result: Boolean)
    fun onStart()
    fun onDownload(progress: Int)
    fun onFinish()
    fun onError(e: Throwable)
}