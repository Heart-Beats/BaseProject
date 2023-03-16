package com.hl.pay.alipay.util

import com.hl.pay.alipay.AlipayConfig
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author 张磊  on  2022/02/23 at 16:32
 * Email: 913305160@qq.com
 */
internal object OrderInfoUtil2_0 {

    fun getTestOrderInfo(payRMB: String): String {
        if (AlipayConfig.PARTNER.isBlank() || AlipayConfig.RSA2_PRIVATE.isBlank() || AlipayConfig.SELLER.isBlank()) {
            return ""
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         * 点击支付按钮出现的错误码，请查看：https://tech.open.alipay.com/support/knowledge/index.htm?categoryId=24120&scrollcheck=1#/?_k=d783mj
         * orderInfo的获取必须来自服务端；
         */
        val rsa2: Boolean = AlipayConfig.RSA2_PRIVATE.length > 0
        val params: Map<String, String> = buildOrderParamMap(AlipayConfig.APPID, rsa2, payRMB)
        val orderParam: String = buildOrderParam(params) //拼接订单信息
        val privateKey: String = if (rsa2) AlipayConfig.RSA2_PRIVATE else AlipayConfig.RSA_PRIVATE
        val sign: String = getSign(params, privateKey, rsa2) //然后并对订单信息使用私钥进行RSA加密
        return "$orderParam&$sign"
    }

    /**
     * 构造支付订单参数列表
     */
    fun buildOrderParamMap(app_id: String, rsa2: Boolean, price: String?): Map<String, String> {
        val keyValues: MutableMap<String, String> = HashMap()
        keyValues["app_id"] = app_id
        keyValues["biz_content"] = bizCotent(price)
        keyValues["charset"] = "utf-8"
        keyValues["method"] = "alipay.trade.app.pay"
        keyValues["sign_type"] = if (rsa2) "RSA2" else "RSA"
        keyValues["timestamp"] = "2016-07-29 16:55:53"
        keyValues["version"] = "1.0"
        return keyValues
    }

    private fun bizCotent(price: String?): String {
        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。


        // var bizValue = "{\"timeout_express\":\"15m\","
        // bizValue += "\"product_code\":\"QUICK_MSECURITY_PAY\","
        // // 商品金额
        // bizValue += "\"total_amount\":\" + \"\"\" + price + \"\","
        // // 商品名称
        // bizValue += "\"subject\":\" + \"\"\" + \"兔泊哥停取车交付款\" + \"\","
        // // 商品详情
        // bizValue += "\"body\":\" + \"\"\" + \"兔泊哥停取车付款界面\" + \"\","
        // //商户网站唯一订单号
        // bizValue += "\"out_trade_no\":\" + \"\"\" + getOutTradeNo() + \"\"}"

        return """
        {
           "timeout_express":"15m",
           "product_code":"QUICK_MSECURITY_PAY",
           "total_amount":"$price",
           "subject":"兔泊哥停取车交付款",
           "body":"兔泊哥停取车付款界面",
           "out_trade_no":"${getOutTradeNo()}",
        """.trimIndent()
    }

    /**
     * 构造支付订单参数信息
     *
     * @param map
     * 支付订单参数
     * @return
     */
    fun buildOrderParam(map: Map<String, String?>): String {
        val keys: List<String> = ArrayList(map.keys)
        val sb = StringBuilder()
        for (i in 0 until keys.size - 1) {
            val key = keys[i]
            val value = map[key] ?: ""
            sb.append(buildKeyValue(key, value, true))
            sb.append("&")
        }
        val tailKey = keys[keys.size - 1]
        val tailValue = map[tailKey] ?: ""
        sb.append(buildKeyValue(tailKey, tailValue, true))
        return sb.toString()
    }

    /**
     * 拼接键值对
     *
     * @param key
     * @param value
     * @param isEncode
     * @return
     */
    private fun buildKeyValue(key: String, value: String, isEncode: Boolean): String {
        val sb = java.lang.StringBuilder()
        sb.append(key)
        sb.append("=")
        if (isEncode) {
            try {
                sb.append(URLEncoder.encode(value, "UTF-8"))
            } catch (e: UnsupportedEncodingException) {
                sb.append(value)
            }
        } else {
            sb.append(value)
        }
        return sb.toString()
    }


    /**
     * 对支付参数信息进行签名
     *
     * @param map
     * 待签名授权信息
     *
     * @return
     */
    fun getSign(map: Map<String, String?>, rsaKey: String?, rsa2: Boolean): String {
        val keys: List<String> = ArrayList(map.keys)
        // key排序
        Collections.sort(keys)
        val authInfo = java.lang.StringBuilder()
        for (i in 0 until keys.size - 1) {
            val key = keys[i]
            val value = map[key]
            authInfo.append(buildKeyValue(key, value!!, false))
            authInfo.append("&")
        }
        val tailKey = keys[keys.size - 1]
        val tailValue = map[tailKey]
        authInfo.append(buildKeyValue(tailKey, tailValue!!, false))
        val oriSign: String = SignUtils.sign(authInfo.toString(), rsaKey, rsa2)
        var encodedSign = ""
        try {
            encodedSign = URLEncoder.encode(oriSign, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return "sign=$encodedSign"
    }

    /**
     * 要求外部订单号必须唯一。
     */
    fun getOutTradeNo(): String {
        val format = SimpleDateFormat("MMddHHmmss", Locale.getDefault())
        val date = Date()
        var key = format.format(date)
        val r = Random()
        key += r.nextInt()
        key = key.substring(0, 15)
        return key
    }
}