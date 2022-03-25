package com.hl.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

private const val SHARE_PREFS_NAME = "com.hl.sharedPreferences"

private fun getPreferences(
    context: Context,
    name: String = SHARE_PREFS_NAME,
    isEncrypted: Boolean = false
): SharedPreferences {
    return when (isEncrypted) {
        true -> {
            val encryptedName = "${name}-encrypted"

            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                encryptedName, masterKeyAlias, context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
        else -> context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }
}

@JvmOverloads
fun Context.sharedPreferences(
    name: String = "${this.packageName}.sharedPreferences",
    isEncrypted: Boolean = false
): SharedPreferences {
    return getPreferences(this, name, isEncrypted = isEncrypted)
}

@JvmOverloads
fun Fragment.sharedPreferences(
    name: String = "${this.requireContext().packageName}.sharedPreferences",
    isEncrypted: Boolean = false
): SharedPreferences? {
    val context = context ?: return null
    return getPreferences(context, name, isEncrypted = isEncrypted)
}


fun SharedPreferences.Editor.putObject(key: String, obj: Any) {
    val gson = Gson()
    putString(key, gson.toJson(obj))
}

inline fun <reified T> SharedPreferences.getObject(key: String): T? {
    val json = getString(key, null) ?: return null
    return try {
        Gson().fromJson(json, T::class.java)
    } catch (e: Exception) {
        null
    }
}

inline fun <reified T> SharedPreferences.getList(key: String): List<T>? {
    val json = getString(key, null) ?: return null
    return try {
        return gsonParseJson2List(json)
    } catch (e: Exception) {
        Log.e("SharedPreferencesUtil", "类型转换错误", e)
        null
    }
}

/**
 * 将字符串数组转换为 List 对象
 */
inline fun <reified T> gsonParseJson2List(json: String?): List<T>? {
    return Gson().fromJson<List<T>>(json, ParameterizedTypeImpl(T::class.java))
}

class ParameterizedTypeImpl(private val clz: Class<*>) : ParameterizedType {
    override fun getRawType(): Type = List::class.java

    override fun getOwnerType(): Type? = null

    override fun getActualTypeArguments(): Array<Type> = arrayOf(clz)
}


