package com.hl.utils

import android.app.Application
import android.content.Context
import android.net.*
import android.util.Log
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @Author  张磊  on  2020/11/03 at 11:34
 * Email: 913305160@qq.com
 */
object MyNetworkCallback : ConnectivityManager.NetworkCallback() {

    private const val TAG = "MyNetworkCallback"

    private lateinit var application: Application

    private val listeners by lazy {
        mutableListOf<NetworkListener>()
    }

    /**
     * 初始化 MyNetworkCallback 所需的 Context
     */
    fun init(application: Application, requestBuilderBlock: NetworkRequest.Builder.() -> Unit): MyNetworkCallback {
        MyNetworkCallback.application = application
        val connectivityManager = MyNetworkCallback.application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder().apply(requestBuilderBlock).build()
        connectivityManager.registerNetworkCallback(request, this)
        return this
    }

    private fun checkInit() {
        if (!this::application.isInitialized) throw IllegalStateException("MyNetworkCallback 未调用 init 方法初始化")

    }

    fun addNetworkListener(listener: NetworkListener) {
        listeners.add(listener)
    }

    fun removeNetworkListener(listener: NetworkListener) {
        listeners.remove(listener)
    }

    fun removeMyNetworkCallback() {
        val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(this)
    }

    /**
     * 请求的网络（数据、WIFI...）可用时的回调, 可能会有多次
     */
    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        Log.i(TAG, "onAvailable ==>$network")
    }

    /**
     * 实践中在网络连接正常的情况下，
     * 数据丢失时会有回调
     */
    override fun onLosing(network: Network, maxMsToLive: Int) {
        super.onLosing(network, maxMsToLive)
        Log.i(TAG, "onLosing ==>$network, maxMsToLive==$maxMsToLive")
    }

    /**
     * 网络不可用时回调
     * 与 onAvailable 意义相反，成对出现
     */
    override fun onLost(network: Network) {
        super.onLost(network)
        Log.i(TAG, "onLost ==>$network")
        checkInit()

        MainScope().launch {
            //延迟一秒获取网络状态，确保状态正确
            delay(1000)
            val connected = NetworkUtil.isConnected(application)
            val wifiConnected = NetworkUtil.isWifiConnected(application)
            val mobileData = NetworkUtil.isMobileData(application)

            listeners.forEach {
                if (!connected) {
                    it.onNetworkDisconnected()
                } else {
                    if (!mobileData) {
                        it.onMobileDataChanged(false)
                    }
                    if (!wifiConnected) {
                        it.onWifiChanged(false)
                    }
                }
            }
        }
    }

    override fun onUnavailable() {
        super.onUnavailable()
        Log.i(TAG, "onUnavailable ==>")
    }

    /**
     * 当与此请求相对应的网络更改功能但仍满足请求的条件时调用
     * @param network 新连接网络
     * @param networkCapabilities 新连接网络的一些能力参数
     */
    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        Log.i(TAG, "onCapabilitiesChanged ==>$network, networkCapabilities==$networkCapabilities")

        //WIFI -> CELLULAR
        //[ Transports: CELLULAR Capabilities: INTERNET&NOT_RESTRICTED&TRUSTED&NOT_VPN&VALIDATED LinkUpBandwidth>=51200Kbps LinkDnBandwidth>=102400Kbps Specifier: <3>]

        //CELLULAR -> WIFI
        //==>[ Transports: WIFI Capabilities: INTERNET&NOT_RESTRICTED&TRUSTED&NOT_VPN&VALIDATED LinkUpBandwidth>=1048576Kbps LinkDnBandwidth>=1048576Kbps SignalStrength: -42]
    }

    /**
     * 与此请求相对应的网络属性发生更改时回调
     */
    override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
        super.onLinkPropertiesChanged(network, linkProperties)
        Log.i(TAG, "onLinkPropertiesChanged ==>$network, linkProperties==$linkProperties")

        checkInit()
        MainScope().launch {
            //延迟一秒获取网络状态，确保状态正确
            delay(1000)
            val wifiConnected = NetworkUtil.isWifiConnected(application)
            val mobileData = NetworkUtil.isMobileData(application)

            Log.d(TAG, "onLinkPropertiesChanged : wifi连接 ==$wifiConnected , 移动数据连接 ==$mobileData")
            listeners.forEach {
                if (mobileData) {
                    it.onMobileDataChanged(true)
                } else if (wifiConnected) {
                    it.onWifiChanged(true)
                }
            }
        }
    }
}


open class NetworkListener {

    open fun onNetworkDisconnected() {}

    open fun onWifiChanged(isConnected: Boolean) {}

    open fun onMobileDataChanged(isConnected: Boolean) {}
}