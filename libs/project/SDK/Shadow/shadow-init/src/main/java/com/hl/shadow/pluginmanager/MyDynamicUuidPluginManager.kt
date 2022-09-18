package com.tencent.shadow.dynamic.manager

import android.content.ComponentName
import android.content.Context
import android.os.DeadObjectException
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.google.gson.Gson
import com.hl.shadow.lib.ShadowConstants
import com.hl.shadow.pluginmanager.MyPluginManager
import com.hl.shadow.pps.DynamicUuidPluginProcessService
import com.tencent.shadow.core.manager.BasePluginManager
import com.tencent.shadow.core.manager.installplugin.InstalledDao
import com.tencent.shadow.core.manager.installplugin.InstalledPlugin
import com.tencent.shadow.dynamic.host.DynamicUuidPpsController

/**
 * @author  张磊  on  2022/09/18 at 15:50
 * Email: 913305160@qq.com
 */
class MyDynamicUuidPluginManager(context: Context) : MyPluginManager(context) {
    companion object {
        private const val TAG = "MyDynamicUuidPluginMana"
    }


    override fun getPluginProcessServiceName(): String {
        return ShadowConstants.DYNAMIC_UUID_PLUGIN_PROCESS_SERVICE_NAME
    }


    override fun onPluginServiceConnected(name: ComponentName?, service: IBinder) {
        onDynamicUuidPpsConnected(name, service)
    }

    @Throws(RuntimeException::class)
    private fun onDynamicUuidPpsConnected(name: ComponentName?, service: IBinder) {
        Log.d(TAG, "onDynamicUuidPpsConnected() called with: name = $name, service = $service")

        mPpsController = DynamicUuidPluginProcessService.wrapBinder(service)
        try {
            mPpsController.setUuidManager(UuidManagerBinder(this))
        } catch (e: DeadObjectException) {
            Log.e(TAG, "onDynamicUuidPpsConnected: onServiceConnected RemoteException", e)
        } catch (e: RemoteException) {
            if (e.javaClass.simpleName == "TransactionTooLargeException") {
                Log.e(TAG, "onDynamicUuidPpsConnected: onServiceConnected TransactionTooLargeException", e)
            } else {
                throw RuntimeException(e)
            }
        }

        try {
            val iBinder = mPpsController.pluginLoader
            if (iBinder != null) {
                mPluginLoader = BinderPluginLoader(iBinder)
            }
        } catch (e: RemoteException) {
            Log.e(TAG, "onDynamicUuidPpsConnected: onServiceConnected mPpsController getPluginLoader", e)
        }
    }

    override fun startLoadPlugin(uuid: String, partKey: String) {
        Log.d(TAG, "startLoadPlugin() called with: uuid = $uuid, partKey = $partKey")

        if (mPpsController is DynamicUuidPpsController) {
            (mPpsController as DynamicUuidPpsController).run {
                Log.d(TAG, "loadPlugin: 当前的 UUID = ${this.getUuid()},  请求的UUID == $uuid")

                if (this.getUuid() != uuid) {
                    // 当前的 UUID 与请求不同时进行修改
                    this.setUuid(uuid)
                }

                Log.d(TAG, "loadPlugin: 修改后请求的 UUID = ${this.getUuid()}")
            }
        }

        mPluginLoader.loadPlugin(partKey)
    }


    override fun getPluginPartByPartKey(uuid: String?, partKey: String?): InstalledPlugin.Part {
        Log.d(TAG, "getPluginPartByPartKey() called with: uuid = $uuid, partKey = $partKey")

        val mInstalledDaoFiled = BasePluginManager::class.java.getDeclaredField("mInstalledDao")
        mInstalledDaoFiled.isAccessible = true
        val mInstalledDao = mInstalledDaoFiled.get(this) as InstalledDao
        mInstalledDaoFiled.isAccessible = false

        val installedPlugin = mInstalledDao.getInstalledPluginByUUID(uuid)

        Log.d(TAG, "getPluginPartByPartKey: installedPlugin == ${Gson().toJson(installedPlugin)}")

        if (installedPlugin == null) {
            Log.d(TAG, "getPluginPartByPartKey: 没有找到uuid:$uuid")
            throw RuntimeException("没有找到uuid:$uuid")
        }

        val part = installedPlugin.getPart(partKey)
        if (part == null) {
            Log.d(TAG, "没有找到Part partKey:$partKey")

            throw RuntimeException("没有找到Part partKey:$partKey")
        }
        return part
    }
}