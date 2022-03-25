package com.youma.pay.weixin

/**
 * @author  张磊  on  2022/02/23 at 19:26
 * Email: 913305160@qq.com
 */
interface PayResultCallBack {
    fun onResult(state: PayState, payResult: String)
}

enum class PayState {
    SUCCESS, FAILED, CANCEL
}