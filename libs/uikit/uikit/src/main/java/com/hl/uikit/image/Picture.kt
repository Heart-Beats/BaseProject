package com.hl.uikit.image

/**
 * @author  张磊  on  2022/02/12 at 15:20
 * Email: 913305160@qq.com
 */

sealed class Picture

data class UploadPicture(
    /**
     * 本地地址
     */
    val path: String? = null,

    /**
     * 上传后的图片地址
     */
    var url: String? = null,

    /**
     * 上传进度
     */
    var uploadProgress: Int = 0,

    /**
     * 是否上传成功
     */
    var uploadState: UploadState = UploadState.READy,

    /**
     * 图片来源是拍照， 上传成功之后会删除
     */
    var isTakePhoto: Boolean = false,
) : Picture() {
    fun isUploadReady() = uploadState == UploadState.READy

    fun isUploading() = uploadState == UploadState.UPLOADING

    fun isUploadSuccess() = uploadState == UploadState.UPLOAD_SUCCESS

    fun isUploadFailed() = uploadState == UploadState.UPLOAD_FAILED
}

enum class UploadState(val desc: String) {
    READy("准备上传"),
    UPLOADING("上传中"),
    UPLOAD_SUCCESS("上传成功"),
    UPLOAD_FAILED("上传失败")
}

object PickPicture : Picture()
