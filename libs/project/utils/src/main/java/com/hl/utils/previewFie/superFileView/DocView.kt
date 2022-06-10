package com.hl.utils.previewFie.superFileView

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import com.blankj.utilcode.util.AppUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnCancelListener
import com.lxj.xpopup.interfaces.OnConfirmListener
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.TbsReaderView
import java.io.File

class DocView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
	FrameLayout(context, attrs, defStyleAttr), TbsReaderView.ReaderCallback {

	private companion object {
		private const val TAG = "DocView"
	}

	private var mTbsReaderView: TbsReaderView? = null

	var openFailedAction: (openFile: File) -> Unit = {}

	init {
		reset()
	}

	private fun reset() {
		removeAllViews()
		mTbsReaderView = TbsReaderView(context, this)
		this.addView(mTbsReaderView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
	}

	private fun getTbsReaderView(context: Context): TbsReaderView {
		return TbsReaderView(context, this)
	}

	/**
	 * 使用 X5 浏览文件
	 */
	fun displayFile(mFile: File?) {
		if (mFile != null && !TextUtils.isEmpty(mFile.toString())) {
			//增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
			val tempFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "temp")
			if (!tempFile.exists()) {
				tempFile.mkdirs()
			}

			//加载文件
			val localBundle = Bundle()
			Log.e(TAG, mFile.toString())
			localBundle.putString("filePath", mFile.toString())
			localBundle.putString("tempPath", tempFile.absolutePath)
			if (mTbsReaderView == null) {
				mTbsReaderView = getTbsReaderView(context)
			}

			// QbSdk.setTbsListener(new TbsListener() {
			//     @Override
			//     public void onDownloadFinish(int i) {
			//         //tbs内核下载完成回调
			//         Log.d("X5core", "tbs内核下载完成");
			//     }
			//
			//     @Override
			//     public void onInstallFinish(int i) {
			//         //内核安装完成回调，
			//
			//         Log.d("X5core", "tbs内核安装完成");
			//     }
			//
			//     @Override
			//     public void onDownloadProgress(int i) {
			//         //下载进度监听
			//
			//         Log.d("X5core", "tbs内核正在下载中----->" + i);
			//     }
			// });
			val fileType = getFileType(mFile.toString())
			val bool = mTbsReaderView?.preOpen(fileType, false) ?: false
			if (bool) {
				mTbsReaderView?.openFile(localBundle)
			} else {
				openFileWithQB(fileType, mFile)
			}
		}
	}

	/**
	 * 	TBS 支持的文件格式：doc、docx、ppt、pptx、xls、xlsx、pdf、txt、epub
	 *
	 * 	QQ  浏览器可打开：rar（包含加密格式）、zip（包含加密格式）、tar、bz2、gz、7z（包含加密格式）、
	 * 	        doc、docx、ppt、pptx、xls、xlsx、txt、pdf、epub、chm、html/htm、xml、mht、url、ini、log、bat、php、js、lrc、jpg、jpeg、png、gif、bmp、tiff 、webp、mp3、m4a、aac、amr、wav、ogg、mid、ra、wma、mpga、ape、flac
	 */
	private fun openFileWithQB(fileType: String, mFile: File) {
		if (QbSdk.isSuportOpenFile(fileType, QbSdk.TBSMODE)) {
			// 打开TBS文件阅读器 支持本地文件打开, hashmap 为 miniqb 的扩展功能参数
			QbSdk.openFileReader(context, mFile.absolutePath, null) { result: String ->
				// 1:用 QQ 浏览器打开
				// 2:用 MiniQB 打开
				// 3:调起阅读器弹框
				// -1:filePath 为空 打开失败
				if (result == "-1") {
					openFailedAction(mFile)
				}
			}
		} else if (QbSdk.isSuportOpenFile(fileType, QbSdk.QBMODE) && checkQBIsInstall()) {
			// 文件支持 QQ 浏览器打开时
			XPopup.Builder(this.context)
				.dismissOnTouchOutside(false)
				.isViewMode(true)
				.asConfirm("提示", "当前文件可使用QQ浏览器进行预览，确认打开预览？", object : OnConfirmListener {

					override fun onConfirm() {

						val openFileWithQBResult = QbSdk.openFileWithQB(context, mFile.absolutePath, null)

						// 无法使用 QQ 浏览器正常打开时
						if (openFileWithQBResult != 0) {
							openFailedAction(mFile)
						}
					}

				}, object : OnCancelListener {
					override fun onCancel() {
						openFailedAction(mFile)
					}
				})
				.show()
		} else {
			openFailedAction(mFile)
		}
	}

	/***
	 * 获取文件类型
	 *
	 * @param paramString
	 * @return
	 */
	private fun getFileType(paramString: String): String {
		var str = ""
		if (TextUtils.isEmpty(paramString)) {
			return str
		}
		val i = paramString.lastIndexOf('.')
		if (i <= -1) {
			return str
		}
		str = paramString.substring(i + 1)
		return str
	}

	/**
	 * 检测 QQ 浏览器是否安装
	 */
	private fun checkQBIsInstall() = AppUtils.isAppInstalled("com.tencent.mtt")

	override fun onCallBackAction(integer: Int, o: Any, o1: Any) {
		Log.d(TAG, "onCallBackAction--->integer = $integer, o = $o, o1 = $o1")
	}

	fun onStopDisplay() {
		if (mTbsReaderView != null) {
			//销毁界面的时候一定要加上，否则后面加载文件会发生异常。
			mTbsReaderView!!.onStop()

			QbSdk.closeFileReader(context)
		}
	}
}