package com.hl.shadow.pps

import android.content.Intent
import android.os.IBinder
import com.tencent.shadow.dynamic.host.*

/**
 * @author  张磊  on  2022/09/18 at 14:45
 * Email: 913305160@qq.com
 */
class DynamicUuidPluginProcessService : PluginProcessService() {

    companion object {
        fun wrapBinder(ppsBinder: IBinder): PpsController {
            return DynamicUuidPpsController(ppsBinder)
        }
    }

    private val myPpsControllerBinder = DynamicUuidPpsBinder(this)

    override fun onBind(intent: Intent?): IBinder? {
        if (mLogger.isInfoEnabled) {
            mLogger.info("onBind:$this")
        }
        return myPpsControllerBinder
    }

    fun getUuid(): String? {
        var uuid: String?
        val uuidField = PluginProcessService::class.java.getDeclaredField("mUuid")
        uuidField.isAccessible = true
        uuid = uuidField.get(this) as? String
        uuidField.isAccessible = false
        return uuid
    }

    fun setUuid(uuid: String?) {
        uuid ?: throw FailedException(FailedException.ERROR_CODE_RESET_UUID_EXCEPTION, "uuid 不可设置为 null")

        val uuidField = PluginProcessService::class.java.getDeclaredField("mUuid")
        uuidField.isAccessible = true
        uuidField.set(this, uuid)
        uuidField.isAccessible = false
    }

}