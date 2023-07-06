package com.hl.previewfile

import android.os.Bundle
import com.hl.previewfile.databinding.HlPreviewFileFragmentNoSupportFileBinding
import com.hl.previewfile.utils.AppUtil
import com.hl.share.ShareUtil
import com.hl.ui.base.ViewBindingBaseFragment
import com.hl.ui.utils.onClick
import com.hl.uikit.toast
import java.io.File

class NoSupportFileFragment : ViewBindingBaseFragment<HlPreviewFileFragmentNoSupportFileBinding>() {

	companion object {
		const val NO_SUPPORT_FILE_KEY = "NO_SUPPORT_FILE_KEY"
	}

	private var noSupportFilePath: String? = null

	override fun HlPreviewFileFragmentNoSupportFileBinding.onViewCreated(savedInstanceState: Bundle?) {
		noSupportFilePath = arguments?.getString(NO_SUPPORT_FILE_KEY)

		val noSupportFile = noSupportFilePath?.run { File(this) } ?: null

		fileName.text = noSupportFile?.name ?: "未知文件"

		noSupportDesc.text = "${AppUtil.getAppName()}平台暂不可以打开此类文件，你可以使用其他应用打开并预览。"

		openFileWithOther.onClick {
			if (noSupportFile == null || !noSupportFile.isFile || !noSupportFile.exists()) {

				toast("文件不存在，无法使用其他应用打开")
				return@onClick
			}

			ShareUtil.shareFile(requireContext(), noSupportFilePath ?: return@onClick)
		}
	}

}