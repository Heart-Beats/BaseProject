package com.hl.utils

import android.app.usage.StorageStats
import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.pm.IPackageStatsObserver
import android.content.pm.PackageManager
import android.content.pm.PackageStats
import android.os.Build
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import androidx.annotation.RequiresApi
import java.io.IOException
import java.lang.reflect.Method
import java.math.BigDecimal
import java.util.*


/**
 * @author  张磊  on  2022/11/15 at 14:05
 * Email: 913305160@qq.com
 */
object AppSizeUtil {

	fun getSize(context: Context, onDataLister: OnDataLister) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			getAppSizeO(context, onDataLister)
		} else {
			getAppSize(context, onDataLister)
		}
	}

	/**
	 * 格式化后的文件大小
	 */
	fun getFormatSize(sizeByte: Long): String {
		val kiloByte = sizeByte / 1000F
		if (kiloByte < 1) {
			return sizeByte.toString() + "Byte"
		}
		val megaByte = kiloByte / 1024F
		if (megaByte < 1) {
			val result1 = BigDecimal(kiloByte.toString())
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString().toString() + "KB"
		}
		val gigaByte = megaByte / 1024F
		if (gigaByte < 1) {
			val result2 = BigDecimal(megaByte.toString())
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString().toString() + "MB"
		}
		val teraBytes = gigaByte / 1024F
		if (teraBytes < 1) {
			val result3 = BigDecimal(gigaByte.toString())
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString().toString() + "GB"
		}
		val result4 = BigDecimal(teraBytes.toString())
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString().toString() + "TB"
	}

	/**
	 * 获取应用的大小
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	private fun getAppSizeO(context: Context, onDataLister: OnDataLister) {
		val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
		val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
		//获取所有应用的StorageVolume列表
		val storageVolumes: List<StorageVolume> = storageManager.storageVolumes
		for (item in storageVolumes) {
			val uuidStr: String? = item.uuid
			val uuid: UUID = if (uuidStr == null) {
				StorageManager.UUID_DEFAULT
			} else {
				UUID.fromString(uuidStr)
			}

			val uid = getUid(context, context.packageName)
			//通过包名获取uid
			var storageStats: StorageStats? = null
			try {
				storageStats = storageStatsManager.queryStatsForUid(uuid, uid)
			} catch (e: IOException) {
				e.printStackTrace()
			}

			onDataLister(
				storageStats?.cacheBytes ?: 0,
				storageStats?.dataBytes ?: 0,
				storageStats?.appBytes ?: 0
			)
		}
	}

	/**
	 * 根据应用包名获取对应uid
	 */
	private fun getUid(context: Context, pakName: String): Int {
		try {
			return context.packageManager.getApplicationInfo(pakName, PackageManager.GET_META_DATA).uid
		} catch (e: PackageManager.NameNotFoundException) {
			e.printStackTrace()
		}
		return -1
	}

	/**
	 * 获取应用大小8.0以下
	 */
	private fun getAppSize(context: Context, onDataLister: OnDataLister) {
		try {
			val method: Method = PackageManager::class.java.getMethod(
				"getPackageSizeInfo", String::class.java, IPackageStatsObserver::class.java
			)
			// 调用 getPackageSizeInfo 方法，需要两个参数：1、需要检测的应用包名；2、回调
			method.invoke(context.packageManager, context.packageName, object : IPackageStatsObserver.Stub() {
				override fun onGetStatsCompleted(pStats: PackageStats, succeeded: Boolean) {
					onDataLister(pStats.cacheSize, pStats.dataSize, pStats.codeSize)
				}
			})
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}

typealias OnDataLister = (cacheSize: Long, dataSize: Long, codeSize: Long) -> Unit