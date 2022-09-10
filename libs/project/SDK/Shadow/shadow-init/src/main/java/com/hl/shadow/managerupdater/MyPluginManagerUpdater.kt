package com.hl.shadow.managerupdater

import com.tencent.shadow.dynamic.host.PluginManagerUpdater
import java.io.File
import java.util.concurrent.Future

/**
 * @Author  张磊  on  2021/04/08 at 15:09
 * Email: 913305160@qq.com
 */

//用来装载插件，是通过加载一个单独的apk来创建的
class MyPluginManagerUpdater(val pluginManagerApk: File) : PluginManagerUpdater {

    /**
     * @return true 表示之前更新过程中意外中断了
     */
    override fun wasUpdating() = false

    /**
     * 更新
     * @return 当前最新的PluginManager，可能是之前已经返回过的文件，但它是最新的了。
     */
    override fun update(): Future<File>? {
        return null
    }

    /**
     * 获取本地最新可用的
     *
     * @return null 表示本地没有可用的
     */
    override fun getLatest(): File? {
        return pluginManagerApk
    }

    /**
     * 查询是否可用
     *
     * @param file PluginManagerUpdater返回的file
     * @return true 表示可用，false 表示不可用
     */
    override fun isAvailable(file: File?): Future<Boolean>? {
        return null
    }
}