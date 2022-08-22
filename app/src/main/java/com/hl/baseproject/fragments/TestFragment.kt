package com.hl.baseproject.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import com.blankj.utilcode.util.ImageUtils
import com.elvishew.xlog.XLog
import com.hl.arch.mvvm.fragment.ViewBindingMvvmBaseFragment
import com.hl.arch.mvvm.vm.FlowVM
import com.hl.arch.mvvm.vm.LiveDataVM
import com.hl.baseproject.BuildConfig
import com.hl.baseproject.databinding.FragmentTestBinding
import com.hl.tencentcloud.cos.TencentCosUtil
import com.hl.tencentcloud.cos.TransferListener
import com.hl.uikit.onClick
import com.hl.uikit.toast
import com.hl.utils.BitmapUtil
import com.hl.utils.XLogUtil
import com.hl.utils.span.dsl.buildSpannableString
import com.hl.utils.toFormatString
import com.tencent.cos.xml.transfer.TransferState
import java.util.*

class TestFragment : ViewBindingMvvmBaseFragment<FragmentTestBinding>() {

	override fun isActivityMainPage(): Boolean {
		return false
	}

	override fun onLiveDataVMCreated(liveDataVM: LiveDataVM) {
	}

	override fun onFlowVMCreated(flowVM: FlowVM) {
	}

	override fun FragmentTestBinding.onViewCreated(savedInstanceState: Bundle?) {
		toolbar?.title = "测试标题"
		toolbar?.subtitle="你好"

		testSpan.buildSpannableString {

			this.addText("开始时间："){
				this.setColor(Color.parseColor("#FF31415F"))
				this.setTypeface(Typeface.DEFAULT_BOLD)
				this.setSize(18,true)
			}

			this.addText("2022/04/23 09:00") {
				this.setColor(Color.RED)
				this.setSize(14)
			}

			this.addText("\n")

			this.addImage(com.cjt2325.cameralibrary.R.drawable.ic_camera, marginLeft = 10)
		}

		testSaveImage.onClick {
			val bitmap = ImageUtils.view2Bitmap(testImage)
			BitmapUtil.saveBitmap(requireContext(), bitmap, "测试图片.png", failAction = {
				toast(it)
			}) {
				toast("保存图片成功")
			}
		}

		testUploadLog.onClick {
			TencentCosUtil.init(requireContext(), "", "", "")

			val date = Date().toFormatString("yyyy-MM-dd")
			val logFile = XLogUtil.logFile

			val cosPath = "YMLog/Test/BaseProject/${date}/Android/${BuildConfig.VERSION_NAME}/测试_${logFile?.name}"

			if (logFile == null) {
				toast("日志文件不存在")
				return@onClick
			}
			TencentCosUtil.uploadFile(
				"app-log-1305848703",
				cosPath,
				logFile.absolutePath,
				transferListener = object : TransferListener {
					override fun onTransferProgress(progress: Int) {
						XLog.d("传输中: progress==${progress}")
					}

					override fun onTransferSuccess(accessUrl: String) {
						XLog.i("传输完成: accessUrl==${accessUrl}")
					}

					override fun onTransferFail(message: String?) {
						XLog.e("传输失败: 失败信息==${message}")
					}

					override fun onTransState(transferState: TransferState) {
						XLog.e("传输状态: transferState==${transferState}")
					}
				})
		}
	}
}