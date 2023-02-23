package com.hl.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import com.tencent.mmkv.MMKV

/**
 * @author  张磊  on  2023/02/23 at 16:26
 * Email: 913305160@qq.com
 */

/**
 * 通过 MMKV 获取 SharedPreferences
 */
fun getPreferencesByMMKV(name: String, isMultiProcess: Boolean, isEncrypted: Boolean): SharedPreferences {
	// 访问模式，是否支持多进程
	val mode = if (!isMultiProcess) MMKV.SINGLE_PROCESS_MODE else MMKV.MULTI_PROCESS_MODE

	// 加密的 key
	val cryptKey = if (isEncrypted) "${name}-encrypted" else null

	return MMKV.mmkvWithID(name, mode, cryptKey).apply {
		loadDataFromSP(name, isMultiProcess, isEncrypted)
	}
}

/**
 * MMKV 从指定 SharedPreferences 迁移数据，并清空 SharedPreferences 旧数据
 */
fun MMKV.loadDataFromSP(name: String, isMultiProcess: Boolean, isEncrypted: Boolean) {
	// 从 oldSharedPreferences 迁移旧数据 到 mmkv
	val oldSharedPreferences = sharedPreferences(name, false, isMultiProcess, isEncrypted)

	this.importFromSharedPreferences(oldSharedPreferences)

	oldSharedPreferences.edit(true) { clear() }
}