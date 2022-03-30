package com.hl.pay

import android.app.Activity
import com.google.gson.Gson
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.hl.pay.alipay.AliPayThread
import com.hl.pay.weixin.PayResultCallBack
import com.hl.pay.weixin.PayState
import com.hl.pay.weixin.WxPayResponse


/**
 * @author  张磊  on  2022/02/23 at 15:07
 * Email: 913305160@qq.com
 */
object PaymentHelper {

    lateinit var WX_APP_ID: String
    lateinit var payResultCallBack: PayResultCallBack

    /**
     * 微信支付
     */
    fun startWeChatPay(
        activity: Activity,
        configAppId: String,
        wxPayResponse: WxPayResponse,
        payResultCallBack: PayResultCallBack
    ) {
        if (configAppId != wxPayResponse.appid) {
            activity.toastShort("与当前应用注册的 AppId 不一致！")
            return
        }

        this.WX_APP_ID = configAppId
        this.payResultCallBack = payResultCallBack

        val wxapi = WXAPIFactory.createWXAPI(activity, null)
        // 将该app注册到微信
        wxapi.registerApp(wxPayResponse.appid)
        if (!wxapi.isWXAppInstalled) {
            activity.toastShort("您未安装微信，请先安装再重新支付")
            return
        }
        //我们把请求到的参数全部给微信
        val req = PayReq() //调起微信APP的对象
        req.appId = wxPayResponse.appid
        req.partnerId = wxPayResponse.partnerid
        req.prepayId = wxPayResponse.prepayid
        req.packageValue = wxPayResponse.`package`
        req.nonceStr = wxPayResponse.noncestr
        req.timeStamp = wxPayResponse.timestamp
        req.sign = wxPayResponse.sign

        //发送调起微信的请求
        wxapi.sendReq(req)
    }


    /**
     * 支付宝支付
     */
    fun startAliPay(activity: Activity, configAppId: String, aliPayOrderInfo: String, payResultCallBack: PayResultCallBack) {

        // OrderInfoUtil2_0.getTestOrderInfo("100")

        //支付行为需要在独立的非ui线程中执行
        AliPayThread(aliPayOrderInfo, activity) {
            handleAliPayResult(it, payResultCallBack)
        }.start()
    }

    private fun handleAliPayResult(mapPayResult: Map<String, String>, payResultCallBack: PayResultCallBack) {
        val payResult = Gson().toJson(mapPayResult)

        val resultStatus = mapPayResult["resultStatus"]
        // 判断 resultStatus 为“9000”则代表支付成功，具体状态码代表参考：https://docs.open.alipay.com/204/105301

        when (resultStatus) {
            //支付成功
            "9000" -> payResultCallBack.onResult(PayState.SUCCESS, payResult)

            //用户中途取消
            "6001" -> payResultCallBack.onResult(PayState.CANCEL, payResult)

            // 支付失败
            else -> payResultCallBack.onResult(PayState.FAILED, payResult)
        }
    }

}