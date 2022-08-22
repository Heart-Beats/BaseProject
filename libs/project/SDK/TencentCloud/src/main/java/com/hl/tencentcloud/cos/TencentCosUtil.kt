package com.hl.tencentcloud.cos

import android.content.Context
import android.os.Environment
import com.tencent.cos.xml.CosXmlServiceConfig
import com.tencent.cos.xml.CosXmlSimpleService
import com.tencent.cos.xml.exception.CosXmlClientException
import com.tencent.cos.xml.exception.CosXmlServiceException
import com.tencent.cos.xml.listener.CosXmlResultListener
import com.tencent.cos.xml.model.CosXmlRequest
import com.tencent.cos.xml.model.CosXmlResult
import com.tencent.cos.xml.transfer.*
import com.tencent.cos.xml.transfer.COSXMLUploadTask.COSXMLUploadTaskResult
import java.io.File


/**
 * @author  张磊  on  2022/08/22 at 15:18
 * Email: 913305160@qq.com
 */
object TencentCosUtil {

	private var cosXmlService: CosXmlSimpleService? = null


	/**
	 * 腾讯云对象存储初始化
	 *
	 * @param secretId 密钥 ID
	 * @param secretId 密钥 Key
	 * @param regionName 存储桶所在地域简称，例如广州地区是 ap-guangzhou
	 */
	fun init(context: Context, secretId: String, secretKey: String, regionName: String) {

		val myCredentialProvider = CredentialProvider.getForeverCredential(secretId, secretKey)

		// 创建 CosXmlServiceConfig 对象，根据需要修改默认的配置参数
		val serviceConfig = CosXmlServiceConfig.Builder()
			.setRegion(regionName)
			.isHttps(true) // 使用 HTTPS 请求, 默认为 HTTP 请求
			.builder()

		// 初始化 COS Service，获取实例
		cosXmlService = CosXmlSimpleService(context, serviceConfig, myCredentialProvider)
	}


	/**
	 * 上传文件
	 *
	 * @param bucketName        存储桶名称，由 bucketname-appid 组成，appid 必须填入，可以在 COS控制台查看存储桶名称。 https://console.cloud.tencent.com/cos5/bucket
	 * @param cosPath           对象在存储桶中的位置标识符，即称对象键
	 * @param srcPath           本地文件的绝对路径
	 * @param transferConfig    传输配置，这里使用默认值， 如果需要定制，请参考 SDK 接口文档
	 * @param transferListener  传输回调接口
	 *
	 */
	fun uploadFile(
		bucketName: String,
		cosPath: String,
		srcPath: String,
		transferConfig: TransferConfig = TransferConfig.Builder().build(),
		transferListener: TransferListener? = null
	) {
		cosXmlService ?: throw UninitializedPropertyAccessException("请先调用 init 方法初始化相关属性")

		// 初始化 TransferManager
		val transferManager = TransferManager(cosXmlService, transferConfig)

		//若存在初始化分块上传的 UploadId，则赋值对应的 uploadId 值用于续传；否则，赋值 null
		val uploadId: String? = null

		// 上传文件
		val cosXmlUploadTask: COSXMLUploadTask = transferManager.upload(bucketName, cosPath, srcPath, uploadId)

		cosXmlUploadTask.setTransferListener(transferListener)
	}

	private fun COSXMLTask.setTransferListener(transferListener: TransferListener? = null) {
		//设置传输进度回调
		this.setCosXmlProgressListener { complete, target ->
			val progress = (complete * 100 / target).toInt()

			transferListener?.onTransferProgress(progress)
		}

		//设置传输结果回调
		this.setCosXmlResultListener(object : CosXmlResultListener {
			override fun onSuccess(request: CosXmlRequest, result: CosXmlResult) {
				val uploadResult = result as COSXMLUploadTaskResult
				transferListener?.onTransferSuccess(uploadResult.accessUrl)
			}


			override fun onFail(
				request: CosXmlRequest,
				clientException: CosXmlClientException?,
				serviceException: CosXmlServiceException?
			) {
				val exception = clientException ?: serviceException
				transferListener?.onTransferFail(exception?.message)
			}
		})

		//设置传输状态回调, 可以查看任务传输过程
		this.setTransferStateListener {
			transferListener?.onTransState(it)
		}
	}


	/**
	 * 下载文件
	 *
	 * @param context           Context
	 * @param bucketName        存储桶名称，由 bucketname-appid 组成，appid 必须填入，可以在 COS控制台查看存储桶名称。 https://console.cloud.tencent.com/cos5/bucket
	 * @param cosPath           对象在存储桶中的位置标识符，即称对象键
	 * @param fileName          下载文件的保存名称，默认 null，与 COS 上的文件名一样
	 * @param saveDir           下载保存文件的所在目录
	 * @param transferConfig    传输配置，这里使用默认值， 如果需要定制，请参考 SDK 接口文档
	 * @param transferListener  传输回调接口
	 *
	 */
	fun downloadFile(
		context: Context,
		bucketName: String,
		cosPath: String,
		fileName: String? = null,
		saveDir: String,
		transferConfig: TransferConfig = TransferConfig.Builder().build(),
		transferListener: TransferListener? = null
	) {
		cosXmlService ?: throw UninitializedPropertyAccessException("请先调用 init 方法初始化相关属性")

		// 初始化 TransferManager
		val transferManager = TransferManager(cosXmlService, transferConfig)

		// 下载文件
		val cosXmlDownloadTask: COSXMLDownloadTask =
			transferManager.download(context, bucketName, cosPath, saveDir, fileName)
		cosXmlDownloadTask.setTransferListener(transferListener)
	}


	/**
	 * 下载文件
	 *
	 * @param context           Context
	 * @param bucketName        存储桶名称，由 bucketname-appid 组成，appid 必须填入，可以在 COS控制台查看存储桶名称。 https://console.cloud.tencent.com/cos5/bucket
	 * @param cosPath           对象在存储桶中的位置标识符，即称对象键
	 * @param fileName          下载文件的保存名称，默认 null，与 COS 上的文件名一样
	 * @param isSave2AppDir      是否保存在应用私有目录下
	 * @param transferConfig    传输配置，这里使用默认值， 如果需要定制，请参考 SDK 接口文档
	 * @param transferListener  传输回调接口
	 *
	 */
	fun downloadFile(
		context: Context,
		bucketName: String,
		cosPath: String,
		fileName: String? = null,
		isSave2AppDir: Boolean = true,
		transferConfig: TransferConfig = TransferConfig.Builder().build(),
		transferListener: TransferListener? = null
	) {
		val saveDir = getCacheFileDir(context, isSave2AppDir)
		downloadFile(context, bucketName, cosPath, fileName, saveDir.absolutePath, transferConfig, transferListener)
	}

	/**
	 * 下载文件
	 *
	 * @param context           Context
	 * @param bucketName        存储桶名称，由 bucketname-appid 组成，appid 必须填入，可以在 COS控制台查看存储桶名称。 https://console.cloud.tencent.com/cos5/bucket
	 * @param cosPath           对象在存储桶中的位置标识符，即称对象键
	 * @param saveFilePath      保存文件的绝对路径
	 * @param transferConfig    传输配置，这里使用默认值， 如果需要定制，请参考 SDK 接口文档
	 * @param transferListener  传输回调接口
	 *
	 */
	fun downloadFile(
		context: Context,
		bucketName: String,
		cosPath: String,
		saveFilePath: String,
		transferConfig: TransferConfig = TransferConfig.Builder().build(),
		transferListener: TransferListener? = null
	) {
		val saveFile = File(saveFilePath)
		val saveDir = saveFile.parentFile.absolutePath
		downloadFile(context, bucketName, cosPath, saveFile.name, saveDir, transferConfig, transferListener)
	}


	private fun getCacheFileDir(context: Context, isSave2AppDir: Boolean): File {
		val downloads = Environment.DIRECTORY_DOWNLOADS
		val saveDir = if (isSave2AppDir) context.getExternalFilesDir(downloads)
		else Environment.getExternalStoragePublicDirectory(downloads)

		if (saveDir?.exists() == false) {
			saveDir.mkdirs()
		}
		return saveDir!!
	}
}