package com.youma.yky.merchant.aspectj

/**
 * @author  张磊  on  2023/01/05 at 13:46
 * Email: 913305160@qq.com
 */

/**
 * 自定义 AOP 元注解
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class PointCutEvent

@PointCutEvent
annotation class ClickEvent

@PointCutEvent
annotation class QRScanEvent

@PointCutEvent
annotation class LifecycleEvent