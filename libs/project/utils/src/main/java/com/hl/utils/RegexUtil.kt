package com.hl.utils

import android.text.TextUtils

/**
 * Created by chibb on 2019/5/18.
 */
object RegexUtil {

    /**
     * 验证手机号是否合法
     * @param mobiles
     * @return
     */
    @JvmStatic
    fun isMobileNO(mobiles: String): Boolean {
        /*
            移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
            联通：130、131、132、152、155、156、185、186
            电信：133、153、180、189、（1349卫通）/^0?1[3|4|5|7|8][0-9]\d{8}$/
            总结起来就是第一位必定为1，第二位必定为3或5或8或7（电信运营商），其他位置的可以为0-9
            支持格式示例-固话：+86-010-40020020，010-40020020 国家代码选填；手机：+86-10-13523458056,  +86-13523458056 ，10-13523458056 ，13523458056 国家代码和区号选填
            */
        //        var regexp:String? = "[1][345789]\\d{9}"//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        var regexp = "^(((\\+\\d{2}-)?0\\d{2,3}-\\d{7,8})|((\\+\\d{2}-)?(\\d{2,3}-)?([1][3,4,5,6,7,8,9][0-9]\\d{8})))$"
        if (regexp.isNullOrEmpty()) {
            regexp = "^(((\\+\\d{2}-)?0\\d{2,3}-\\d{7,8})|((\\+\\d{2}-)?(\\d{2,3}-)?([1][3,4,5,6,7,8,9][0-9]\\d{8})))$"
        }

        return if (TextUtils.isEmpty(mobiles))
            false
        else
            mobiles.matches(regexp.toRegex())
    }

    @JvmStatic
    fun showNo(phone: String?): String {
        return if (phone == null) {
            ""
        } else phone.substring(0, 3) + "****" + phone.substring(7, phone.length)
    }

}
