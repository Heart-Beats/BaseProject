package com.hl.uikit.dialog

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import com.hl.uikit.BasicDialogFragment
import com.hl.uikit.R
import com.hl.uikit.onClick

/**
 * @Author  张磊  on  2020/09/01 at 15:47
 * Email: 913305160@qq.com
 */
class AdvertDialogFragment : BasicDialogFragment() {

	var adsImageUrl: String? = null

	var adsClickListener: (View) -> Unit = {}
	var adsCloseListener: (View) -> Unit = {}
	var displayImage: (imageView: ImageView, url: String?) -> Unit = { _, _-> }

	override fun getTheme(): Int {
		return R.style.UiKit_AdvertDialog
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.uikit_alert_advert_fragment, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val adsImage = view.findViewById<ImageView>(R.id.ads_image)
		val adsClose = view.findViewById<ImageView>(R.id.ads_close)

		displayImage(adsImage, adsImageUrl)

		adsImage.onClick {
			dismiss()
			adsClickListener(it)
		}

		adsClose.onClick {
			dismiss()
			adsCloseListener(it)
		}

		isCancelable = false
	}
}