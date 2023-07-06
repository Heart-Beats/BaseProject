package com.hl.imageload

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/**
 * @author  张磊  on  2023/07/06 at 16:55
 * Email: 913305160@qq.com
 */

/**
 * 获取视频的首帧图
 * @param url
 */
fun ImageView.loadFirstFrameCover(url: String?, errorResId: Int? = null, placeholderResId: Int? = null) {
	//可以参考Glide，内部也是封装了MediaMetadataRetriever
	Glide.with(this.context)
		.setDefaultRequestOptions(
			RequestOptions().apply {
				frame(1000000)
				// centerCrop()

				errorResId?.let { error(it) }
				placeholderResId?.let { placeholder(it) }
			}
		)
		.load(url)
		.into(this)
}