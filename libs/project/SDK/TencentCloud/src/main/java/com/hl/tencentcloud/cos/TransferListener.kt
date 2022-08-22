package com.hl.tencentcloud.cos

import com.tencent.cos.xml.transfer.TransferState


/**
 * @author  张磊  on  2022/08/22 at 16:03
 * Email: 913305160@qq.com
 */

/**
 * 文件传输过程中的监听
 */
interface TransferListener {

	/**
	 * 传输进度
	 */
	fun onTransferProgress(progress: Int) {}

	/**
	 * 传输成功
	 *
	 * @param accessUrl  访问地址
	 */
	fun onTransferSuccess(accessUrl: String) {}

	/**
	 * 传输失败
	 *
	 * @param message 失败原因
	 */
	fun onTransferFail(message: String?) {}

	/**
	 * 传输状态
	 *
	 * @param transferState  传输状态
	 */
	fun onTransState(transferState: TransferState) {}
}