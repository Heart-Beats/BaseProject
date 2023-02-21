package com.hl.utils.aspectj

import org.aspectj.lang.annotation.Aspect

/**
 * @author  张磊  on  2023/01/05 at 13:39
 * Email: 913305160@qq.com
 *
 * AOP 注入类
 */
@Aspect
class AspectInject {

	private val TAG = "Aspect"


	// @Pointcut("execution(@com.hl.utils.aspectj.ClickEvent * android.view.OnClickListener.onClick(..))")
	// private fun ClickEventMethod() {}
	//
	// @Before("ClickEventMethod()")
	// fun onClickEventMethod(joinPoint: JoinPoint) {
	// 	val joinPoint1 = joinPoint
	//
	// 	Log.d(TAG, "onClickEventMethod() called with: joinPoint = $joinPoint")
	// }
	//
}
