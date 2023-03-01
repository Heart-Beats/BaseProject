package com.hl.utils.views

import android.view.MotionEvent
import androidx.core.view.children
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * @author  张磊  on  2023/03/01 at 10:41
 * Email: 913305160@qq.com
 */

/**
 * 拦截 bottomNavigationMenuView 的默认点击与长按处理（消除长按的 toast 提示，且长按处理与点击处理一致）
 */
fun BottomNavigationView.initTouchHandle() {

	//拦截 bottomNavigationMenuView 的点击与长按处理（消除长按的 toast 提示，且长按处理与点击处理一致）
	val bottomNavigationMenuView = this.children.first() as BottomNavigationMenuView
	bottomNavigationMenuView.children.forEach {
		if (it is BottomNavigationItemView) {
			it.setOnTouchListener { v, event ->
				if (event.action == MotionEvent.ACTION_UP) {
					if (event.x >= 0 && event.x <= v.width && event.y >= 0 && event.y <= v.height) {
						//仅当触摸点在 view 之内才执行 view 原有的点击事件
						v.performClick()
					}
				}
				true
			}
		}
	}
}

/**
 * 将 BottomNavigationView 与 ViewPager2 关联起来，实现彼此联动
 *
 * @param viewPager2    联动的 viewPager2
 * @param smoothScroll  点击BottomNavigationView 的菜单项时，viewPager2 跳转时是否需要动画
 */
fun BottomNavigationView.setupWithViewPager2(viewPager2: ViewPager2, smoothScroll: Boolean = true) {
	this.setOnItemSelectedListener { item ->
		if (!item.isChecked) {
			val index = this.menu.children.indexOf(item)
			viewPager2.setCurrentItem(index, smoothScroll)
			true
		} else {
			true
		}
	}

	val bottomNavigationMenuView = this
	viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
		override fun onPageSelected(position: Int) {
			bottomNavigationMenuView.selectedItemId = bottomNavigationMenuView.menu.getItem(position).itemId
		}
	})
}