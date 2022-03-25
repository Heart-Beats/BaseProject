package com.youma.pay.alipay

import android.app.Activity
import android.util.Log
import com.alipay.sdk.app.PayTask


/**
 * @author  张磊  on  2022/02/23 at 17:13
 * Email: 913305160@qq.com
 */
class AliPayThread(
    private val orderInfo: String, val activity: Activity,
    private val aliPayResult: (Map<String, String>) -> Unit
) : Thread() {

    private val TAG = "AliPayThread"


    override fun run() {
        val alipay = PayTask(activity)
        val result = alipay.payV2(orderInfo, true)
        Log.d(TAG, "支付宝支付结束： 支付结果 = $result")

        /**
         * 官方result返回结果参考：https://docs.open.alipay.com/204/105302
         * 我这里返回到result格式为：
         * {
         * resultStatus = 9000,
         * result = {
         * "alipay_trade_app_pay_response": {
         * "code": "10000",
         * "msg": "Success",
         * "app_id": "2017112400138529",
         * "auth_app_id": "2017112400138529",
         * "charset": "utf-8",
         * "timestamp": "2018-01-29 14:46:33",
         * "total_amount": "0.01",
         * "trade_no": "2018012921001004940219217398",
         * "seller_id": "2088821472668202",
         * "out_trade_no": "0129144616-2725"
         * }
         * }
         * }
         */
        activity.runOnUiThread {
            aliPayResult(result)
        }
    }
}