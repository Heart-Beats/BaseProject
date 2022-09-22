package com.hl.arch.adapters.diffcallback

import androidx.recyclerview.widget.DiffUtil

/**
 * @author  张磊  on  2022/09/22 at 17:53
 * Email: 913305160@qq.com
 */

class MyDiffCallback<T>(private val oldList: List<T>, private val newList: List<T>) : DiffUtil.Callback() {

	/**
	 * 旧数据的size
	 */
	override fun getOldListSize() = oldList.size

	/**
	 * 新数据的size
	 */
	override fun getNewListSize() = newList.size


	/**
	 * 这个方法自由定制 ，
	 * 在对比数据的时候会被调用
	 * 返回 true 被判断为同一个 item
	 */
	override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = true


	/**
	 * areItemsTheSame 方法返回 true 时，
	 * 这个方法才会被 diff 调用
	 * 返回 true 就证明内容相同
	 */
	override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		return oldList[oldItemPosition] == newList[newItemPosition]
	}
}