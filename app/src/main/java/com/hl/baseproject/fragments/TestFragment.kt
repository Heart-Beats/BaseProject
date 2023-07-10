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
import com.hl.baseproject.R
import com.hl.baseproject.databinding.FragmentTestBinding
import com.hl.dateutil.toFormatString
import com.hl.tencentcloud.cos.TencentCosUtil
import com.hl.tencentcloud.cos.TransferListener
import com.hl.ui.utils.onClick
import com.hl.uikit.toast
import com.hl.utils.TimeUtil
import com.hl.utils.launchHome
import com.hl.utils.media.MediaPlayerHelper
import com.hl.utils.media.OnPlayListener
import com.hl.utils.registerReceiver
import com.hl.utils.span.dsl.buildSpannableString
import com.tencent.cos.xml.transfer.TransferState
import java.util.Date


class TestFragment : ViewBindingMvvmBaseFragment<FragmentTestBinding>() {

	override fun isActivityMainPage(): Boolean {
		return false
	}

	override fun onLiveDataVMCreated(liveDataVM: LiveDataVM) {
	}

	override fun onFlowVMCreated(flowVM: FlowVM) {
	}

	override fun onBackPressed() {
		launchHome()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		requireActivity().registerReceiver(AppMainFragment.SHOW_FRAGMENT_ACTION) { _, intent ->
			if (intent.getStringExtra(AppMainFragment.SHOW_FRAGMENT_NAME_KEY) == this.javaClass.name) {
				// Navigation 回退，页面显示时恢复默认状态栏配置
				updateSystemBar()
			}
		}
	}

	override fun onResume() {
		super.onResume()
		// 页面显示时恢复默认状态栏配置
		updateSystemBar()
	}

	override fun FragmentTestBinding.onViewCreated(savedInstanceState: Bundle?) {
		toolbar?.title = "测试标题"
		toolbar?.subtitle = "你好"

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
			com.hl.bitmaputil.BitmapUtil.saveBitmap(requireContext(), bitmap, "测试图片.png", failAction = {
				toast(it)
			}) {
				toast("保存图片成功")
			}
		}

		testUploadLog.onClick {
			TencentCosUtil.init(
				requireContext(), "REPLACED",
				"REPLACED", "REPLACED"
			)

			val date = Date().toFormatString(com.hl.dateutil.DatePattern.YMD)
			val logFile = com.hl.xloginit.XLogUtil.logFile

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

		testPlayAudio.onClick {
			val url =
				"https://dl.stream.qqmusic.qq.com/C400002JIJje3z9Tuw.m4a?guid=7562095510&vkey=213887751AFE24DE490EA14902589A1403CBBBA0A7A2C2D9DD1DE40F1601DF57DD6DDBD4BE02276A0F39638958474AD54169B11D2C76E443&uin=913305160&fromtag=120032"

			val mediaPlayerHelper = MediaPlayerHelper(musicSeekBar, object : OnPlayListener {
				override fun onPrepareStart(currentPosition: Long, totalDuration: Long) {
					currentTime.text = TimeUtil.calculateCountTime2String(currentPosition)
					totalTime.text = TimeUtil.calculateCountTime2String(totalDuration)
				}

				override fun onProgress(currentPosition: Long, totalDuration: Long, currentProgress: Int) {
					currentTime.text = TimeUtil.calculateCountTime2String(currentPosition)
					totalTime.text = TimeUtil.calculateCountTime2String(totalDuration)
				}

				override fun onSeekComplete(currentPosition: Long) {
					currentTime.text = TimeUtil.calculateCountTime2String(currentPosition)
				}
			})
			// mediaPlayerHelper.preparePlayUrl(url)

			mediaPlayerHelper.preparePlayRes(requireContext(), R.raw.summer)
		}
	}
}