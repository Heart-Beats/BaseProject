package com.hl.utils.previewFie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.AppUtils
import com.hl.uikit.onClick
import com.hl.utils.R
import com.hl.utils.share.ShareUtil
import kotlinx.android.synthetic.main.hl_utils_fragment_no_support_file.*
import java.io.File

class NoSupportFileFragment : Fragment() {

	companion object {
		const val NO_SUPPORT_FILE_KEY = "NO_SUPPORT_FILE_KEY"
	}

	private var noSupportFile: File? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.hl_utils_fragment_no_support_file, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		noSupportFile = arguments?.getSerializable(NO_SUPPORT_FILE_KEY) as File?

		file_name.text = noSupportFile?.name ?: "未知文件"

		no_support_desc.text = "${AppUtils.getAppName()}平台暂不可以打开此类文件，你可以使用其他应用打开并预览。"

		open_file_with_other.onClick {
			ShareUtil.shareFile(requireContext(), noSupportFile?.absolutePath ?: return@onClick)
		}
	}

}