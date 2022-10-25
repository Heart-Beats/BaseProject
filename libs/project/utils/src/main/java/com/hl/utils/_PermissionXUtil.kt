package com.hl.utils

import android.Manifest
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX

/**
 * @Author  张磊  on  2020/09/28 at 10:59
 * Email: 913305160@qq.com
 */

val permissionsList = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.INTERNET,
    Manifest.permission.READ_PHONE_STATE
)

fun FragmentActivity.reqPermissions(
    vararg permissions: String = permissionsList,
    needExplainRequestReason: Boolean = false,
    deniedAction: (List<String>) -> Unit = {},
    allGrantedAction: (List<String>) -> Unit = {},
) {
    PermissionX.init(this)
        .permissions(*permissions)
        .apply {
            if (needExplainRequestReason) this.explainReasonBeforeRequest
        }
        .onExplainRequestReason { scope, deniedList ->
            // 用户请求权限之前触发，用于解释获取权限原因，必须调用 explainReasonBeforeRequest() 才会触发
            val message = "本应用需要您同意以下权限才可正常使用"
            scope.showRequestReasonDialog(deniedList, message, "确定", "取消")
        }
        .onForwardToSettings { scope, deniedList ->
            // 用户拒绝权限后再次请求触发
            scope.showForwardToSettingsDialog(deniedList, "您需要在设置中手动允许以下必要的权限", "确定", "取消")
        }
        .request { allGranted, grantedList, deniedList ->
            if (allGranted) {
                allGrantedAction(grantedList)
            } else {
                deniedAction(deniedList)
            }
        }
}

fun Fragment.reqPermissions(
    vararg permissions: String = permissionsList,
    needExplainRequestReason: Boolean = false,
    deniedAction: (List<String>) -> Unit = {},
    allGrantedAction: (List<String>) -> Unit = {}
) {
    PermissionX.init(this)
        .permissions(*permissions)
        .apply {
            if (needExplainRequestReason) this.explainReasonBeforeRequest
        }
        .onExplainRequestReason { scope, deniedList ->
            // 用户请求权限之前触发，用于解释获取权限原因，必须调用 explainReasonBeforeRequest() 才会触发
            val message = "本应用需要您同意以下权限才可正常使用"
            scope.showRequestReasonDialog(deniedList, message, "确定", "取消")
        }
        .onForwardToSettings { scope, deniedList ->
            // 用户拒绝权限后再次请求触发
            scope.showForwardToSettingsDialog(deniedList, "您需要在设置中手动允许以下必要的权限", "确定", "取消")
        }
        .request { allGranted, grantedList, deniedList ->
            if (allGranted) {
                allGrantedAction(grantedList)
            } else {
                deniedAction(deniedList)
            }
        }
}