package com.hl.rvadapter

/**
 * @author  张磊  on  2022/09/22 at 11:04
 * Email: 913305160@qq.com
 */
interface IDataOperate<T> {

	/**
	 * 向列表尾部插入数据
	 */
	fun insertData(vararg addData: T)

	/**
	 *  删除数据
	 */
	fun removeData(vararg removeData: T)

	/**
	 * 更新当前的数据
	 */
	fun updateData(newData: List<T>)


	/**
	 * 获取当前的数据
	 */
	fun getData(): List<T>
}