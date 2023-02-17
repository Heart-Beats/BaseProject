package com.hl.arch.loading

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.ActivityUtils
import com.hl.utils.createPop
import com.lxj.xpopup.impl.LoadingPopupView
import java.lang.ref.WeakReference

/**
 * @author  张磊  on  2022/03/04 at 19:05
 * Email: 913305160@qq.com
 */


private var loadingPopup: LoadingPopupView? = null
    get() {

        if (field == null) {
            val topActivity = ActivityUtils.getTopActivity() as FragmentActivity

            field = LoadingPopupView(topActivity, 0).createPop {
                // 该 loading 弹窗需要复用
                this.isDestroyOnDismiss(false)
                this.dismissOnBackPressed(false)
                this.dismissOnTouchOutside(false)
                this.hasShadowBg(false)
            }

            val activityLifecycle = topActivity.lifecycle
            activityLifecycle.addObserver(object : DefaultLifecycleObserver {

                override fun onDestroy(owner: LifecycleOwner) {
                    // topActivity 销毁时情况引用，同时移除监听
                    field = null
                    activityLifecycle.removeObserver(this)
                }
            })
        }

        return field
    }


val loadingPopupWeakReference = WeakReference<LoadingPopupView>(loadingPopup)