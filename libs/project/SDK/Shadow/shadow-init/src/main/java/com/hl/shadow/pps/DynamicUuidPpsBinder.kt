package com.tencent.shadow.dynamic.host

import android.os.Parcel
import com.hl.shadow.pps.DynamicUuidPluginProcessService

/**
 * @author  张磊  on  2022/09/18 at 14:46
 * Email: 913305160@qq.com
 */
internal class DynamicUuidPpsBinder(private val dynamicUuidPps: DynamicUuidPluginProcessService) : PpsBinder(dynamicUuidPps) {

    companion object {
        const val TRANSACTION_getUuid = FIRST_CALL_TRANSACTION + 6
        const val TRANSACTION_setUuid = FIRST_CALL_TRANSACTION + 7
    }

    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        when (code) {
            TRANSACTION_getUuid -> {
                data.enforceInterface(DESCRIPTOR)
                val uuid = dynamicUuidPps.getUuid()
                reply?.writeNoException()
                reply?.writeString(uuid)
                return true
            }
            TRANSACTION_setUuid -> {
                data.enforceInterface(DESCRIPTOR)
                val _arg0: String? = data.readString()
                try {
                    dynamicUuidPps.setUuid(_arg0)
                    reply?.writeInt(TRANSACTION_CODE_NO_EXCEPTION)
                } catch (e: FailedException) {
                    reply?.run {
                        writeInt(TRANSACTION_CODE_FAILED_EXCEPTION)
                        e.writeToParcel(this, 0)
                    }
                }
                return true
            }
        }

        return super.onTransact(code, data, reply, flags)
    }
}