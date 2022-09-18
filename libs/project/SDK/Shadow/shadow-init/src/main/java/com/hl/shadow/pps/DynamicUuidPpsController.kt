package com.tencent.shadow.dynamic.host

import android.os.IBinder
import android.os.Parcel

/**
 * @author  张磊  on  2022/09/18 at 14:33
 * Email: 913305160@qq.com
 */
class DynamicUuidPpsController(private val remote: IBinder) : PpsController(remote) {

    fun getUuid(): String {
        val _data = Parcel.obtain()
        val _reply = Parcel.obtain()
        val _result: String? = try {
            _data.writeInterfaceToken(PpsBinder.DESCRIPTOR)
            remote.transact(DynamicUuidPpsBinder.TRANSACTION_getUuid, _data, _reply, 0)
            _reply.readException()
            _reply.readString()
        } finally {
            _reply.recycle()
            _data.recycle()
        }
        return _result ?: ""
    }

    fun setUuid(uuid: String) {
        val _data = Parcel.obtain()
        val _reply = Parcel.obtain()
        try {
            _data.writeInterfaceToken(PpsBinder.DESCRIPTOR)
            _data.writeString(uuid)
            remote.transact(DynamicUuidPpsBinder.TRANSACTION_setUuid, _data, _reply, 0)
            val i = _reply.readInt()
            if (i == PpsBinder.TRANSACTION_CODE_FAILED_EXCEPTION) {
                throw FailedException(_reply)
            } else if (i != PpsBinder.TRANSACTION_CODE_NO_EXCEPTION) {
                throw RuntimeException("不认识的Code==$i")
            }
        } finally {
            _reply.recycle()
            _data.recycle()
        }
    }
}