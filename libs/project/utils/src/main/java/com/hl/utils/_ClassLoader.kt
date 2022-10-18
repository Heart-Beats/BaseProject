package com.hl.utils

import java.lang.reflect.Field

/**
 * @author  张磊  on  2022/09/30 at 19:44
 * Email: 913305160@qq.com
 */


/**
 * 修改 ClassLoader的 parent
 *
 * 注意：未避免产生异常， newParentClassLoader 的 parent 最好为被设置 ClassLoader 的 parent
 *
 * 从而形成如下结构：
 * ---BootClassLoader
 * ----newParentClassLoader
 * ------PathClassLoader（thisClassLoader）
 *
 * @param newParentClassLoader classLoader的新的parent
 * @throws Exception 失败时抛出
 */
@Throws(Exception::class)
fun ClassLoader.setParentClassLoader(
	newParentClassLoader: ClassLoader
) {
	val field: Field = getParentField(this)
		?: throw RuntimeException("在ClassLoader.class中没找到类型为ClassLoader的parent域")
	field.isAccessible = true
	field.set(this, newParentClassLoader)
}

/**
 * 安全地获取到 ClassLoader类的parent域
 *
 * @return ClassLoader类的 parent域.或不能通过反射访问该域时返回null.
 */
private fun getParentField(classLoader: ClassLoader): Field? {
	val parent = classLoader.parent
	var field: Field? = null
	for (f in ClassLoader::class.java.declaredFields) {
		try {
			val accessible = f.isAccessible
			f.isAccessible = true
			val o = f[classLoader]
			f.isAccessible = accessible
			if (o === parent) {
				field = f
				break
			}
		} catch (ignore: IllegalAccessException) {
		}
	}
	return field
}