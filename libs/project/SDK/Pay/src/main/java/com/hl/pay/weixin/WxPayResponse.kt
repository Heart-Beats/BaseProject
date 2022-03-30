package com.hl.pay.weixin

/**
 * @author  张磊  on  2022/02/23 at 15:08
 * Email: 913305160@qq.com
 */
data class WxPayResponse(
    var appid: String? = null, // wx4b39982c83b00f64
    var noncestr: String? = null, // 1645608357711

    /**
     *  目前固定值： Sign=WXPay
     */
    var `package`: String? = null, // Sign=WXPay
    var partnerid: String? = null, // 1603559134
    var prepayid: String? = null, // wx231725400405189baf90a1aa6e8ccf0000
    var sign: String? = null, // A35E18C410122AE7109216AEE3394D029AB728EB82C9718DD08FBBDCA104EA93
    var timestamp: String? = null // 1645608357
)