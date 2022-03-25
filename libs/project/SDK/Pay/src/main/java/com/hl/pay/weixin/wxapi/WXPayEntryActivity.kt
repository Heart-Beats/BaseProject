package com.youma.pay.weixin.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.youma.pay.PaymentHelper
import com.youma.pay.weixin.PayState


class WXPayEntryActivity : Activity(), IWXAPIEventHandler {

    private val TAG = "WXPayEntryActivity"

    private lateinit var iwxapi: IWXAPI


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 可由第三方App个性化展示支付结果
        // setContentView(R.layout.activity_wxpay_entry)

        iwxapi = WXAPIFactory.createWXAPI(this, PaymentHelper.WX_APP_ID)
        iwxapi.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        iwxapi.handleIntent(intent, this)
    }


    override fun onReq(baseReq: BaseReq) {
        Log.d(TAG, "微信支付开始: 支付内容 == $baseReq")
    }

    /**
     * 微信支付结果回调
     */
    override fun onResp(baseResp: BaseResp) {
        val payResult = Gson().toJson(baseResp)
        Log.d(TAG, "微信支付结束: 支付结果 ==$payResult ")

        // 结果码参考：https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_5
        if (baseResp.type == ConstantsAPI.COMMAND_PAY_BY_WX) {
            when (baseResp.errCode) {
                BaseResp.ErrCode.ERR_OK -> {
                    finish()
                    PaymentHelper.payResultCallBack.onResult(PayState.SUCCESS, payResult)
                }
                BaseResp.ErrCode.ERR_COMM -> {
                    finish()
                    // 支付失败
                    // 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的 APPID 与设置的不匹配、其他异常等
                    PaymentHelper.payResultCallBack.onResult(PayState.FAILED, payResult)
                }
                BaseResp.ErrCode.ERR_USER_CANCEL -> {
                    finish()
                    PaymentHelper.payResultCallBack.onResult(PayState.CANCEL, payResult)
                }
            }
        }
    }
}