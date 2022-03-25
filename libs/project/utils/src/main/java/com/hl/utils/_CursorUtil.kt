package com.hl.utils

import android.database.Cursor

/**
 * @author  张磊  on  2021/07/23 at 10:50
 * Email: 913305160@qq.com
 */

inline fun <reified T> Cursor.getColumnValue(columnName: String): T {
	val columnIndex = this.getColumnIndexOrThrow(columnName)
	when {
		byteArrayOf() is T ->
			return this.getBlob(columnIndex) as T

		"" is T ->
			return this.getString(columnIndex) as T

		0.toShort() is T ->
			return this.getShort(columnIndex) as T

		0 is T ->
			return this.getInt(columnIndex) as T

		0.0 is T ->
			return this.getDouble(columnIndex) as T

		0f is T ->
			return this.getFloat(columnIndex) as T

		0L is T ->
			return this.getLong(columnIndex) as T

		else ->
			throw Exception("Cursor 不支持获取此类型的值")
	}
}