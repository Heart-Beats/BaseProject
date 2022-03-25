package com.hl.utils

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.RequiresPermission

/**
 * @Author  张磊  on  2020/09/25 at 9:20
 * Email: 913305160@qq.com
 */

object NetworkUtil {
	/**
	 * 网络是否已连接
	 * @return true:已连接 false:未连接
	 */
	@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
	fun isConnected(@NonNull context: Context): Boolean {
		val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val networkCapabilities: NetworkCapabilities? = manager.getNetworkCapabilities(manager.activeNetwork)
			if (networkCapabilities != null) {
				return (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
						|| networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
						|| networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
			}
		} else {
			val networkInfo = manager.activeNetworkInfo
			return networkInfo != null && networkInfo.isConnected
		}
		return false
	}

	/**
	 * Wifi是否已连接
	 * @return true:已连接 false:未连接
	 */
	fun isWifiConnected(@NonNull context: Context): Boolean {
		val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val networkCapabilities: NetworkCapabilities? = manager.getNetworkCapabilities(manager.activeNetwork)
			if (networkCapabilities != null) {
				return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
			}
		} else {
			val networkInfo = manager.activeNetworkInfo
			return networkInfo != null && networkInfo.isConnected && networkInfo.type == ConnectivityManager.TYPE_WIFI
		}
		return false
	}

	/**
	 * 是否为移动数据
	 */
	fun isMobileData(@NonNull context: Context): Boolean {
		val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val networkCapabilities: NetworkCapabilities? = manager.getNetworkCapabilities(manager.activeNetwork)
			if (networkCapabilities != null) {
				return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
			}
		} else {
			val networkInfo = manager.activeNetworkInfo
			return networkInfo != null && networkInfo.isConnected && networkInfo.type == ConnectivityManager.TYPE_MOBILE
		}
		return false
	}

	fun getNetworkInfo(@NonNull context: Context): Any? {
		val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val networkCapabilities: NetworkCapabilities? = manager.getNetworkCapabilities(manager.activeNetwork)
			if (networkCapabilities != null) {
				return networkCapabilities
			}
		} else {
			return manager.activeNetworkInfo
		}
		return null
	}
}