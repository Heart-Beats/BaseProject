package com.hl.mmkvsharedpreferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.hl.json.GsonUtil


private const val SHARE_PREFS_NAME = "com.hl.sharedPreferences"

private fun getPreferences(
    name: String = SHARE_PREFS_NAME,
    isUseMMKV: Boolean = true,
    isMultiProcess: Boolean = false,
    isEncrypted: Boolean = false
): SharedPreferences {
    val app = getApp()

    return if (isUseMMKV) {
        // 使用 MMKV  时
        getPreferencesByMMKV(name, isMultiProcess, isEncrypted)
    } else {
        if (isMultiProcess) error("仅 MMKV 支持多进程存储共享数据！")

        // 使用 SP  时， 多进程间通过  SP 来共享数据是不安全的, 因此不支持 isMultiProcess 参数
        when (isEncrypted) {
            true -> {
                val encryptedName = "${name}-encrypted"

                val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
                EncryptedSharedPreferences.create(
                    encryptedName, masterKeyAlias, app,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            }
            else -> app.getSharedPreferences(name, Context.MODE_PRIVATE)
        }
    }
}

/**
 * @param name            sp 文件名称
 * @param isUseMMKV       是否使用 MMKV
 * @param isMultiProcess  是否支持多进程访问，仅使用 MMKV 时才支持
 * @param isEncrypted     是否对存储数据进行加密
 */
@JvmOverloads
fun sharedPreferences(
    name: String = "${getApp().packageName}.sharedPreferences",
    isUseMMKV: Boolean = true,
    isMultiProcess: Boolean = false,
    isEncrypted: Boolean = false
): SharedPreferences {
    return getPreferences(name, isUseMMKV = isUseMMKV, isMultiProcess = isMultiProcess, isEncrypted = isEncrypted)
}


fun SharedPreferences.Editor.putObject(key: String, obj: Any) {
    putString(key, GsonUtil.toJson(obj))
}

inline fun <reified T> SharedPreferences.getObject(key: String): T? {
    val json = getString(key, null) ?: return null
    return try {
        GsonUtil.fromJson<T>(json)
    } catch (e: Exception) {
        null
    }
}

