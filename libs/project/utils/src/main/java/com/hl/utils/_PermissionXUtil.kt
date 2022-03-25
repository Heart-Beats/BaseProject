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
    deniedAction: (List<String>) -> Unit = {},
    allGrantedAction: (List<String>) -> Unit = {},
) {
    PermissionX.init(this)
        .permissions(*permissions)
        // .explainReasonBeforeRequest()
        .onExplainRequestReason { scope, deniedList ->
            val message = "本应用需要您同意以下权限才能正常使用"
            scope.showRequestReasonDialog(deniedList, message, "确定", "取消")
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
    vararg permissions: String = permissionsList, deniedAction: (List<String>) -> Unit = {},
    allGrantedAction: (List<String>) -> Unit = {}
) {
    requireActivity().reqPermissions(*permissions, allGrantedAction = allGrantedAction, deniedAction = deniedAction)
}