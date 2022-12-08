package com.hl.arch.utils

import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView

/**
 * @author  张磊  on  2022/02/22 at 23:59
 * Email: 913305160@qq.com
 */

internal fun <T : BasePopupView> T.showPop(popOptions: XPopup.Builder.() -> Unit = {}) {
    this.createPop(popOptions)
        .show()
}

internal fun <T : BasePopupView> T.createPop(popOptions: XPopup.Builder.() -> Unit = {}): T {
    return XPopup.Builder(this.context)
        .isViewMode(true)  // ViewMode 下不会更改状态栏的效果
        .isDestroyOnDismiss(true)  //对于只使用一次的弹窗，推荐设置这个
        .apply(popOptions)
        .asCustom(this) as T
}