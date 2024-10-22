package com.hl.api.convert

/**
 * @author  张磊  on  2024/10/21 at 16:19
 * Email: 913305160@qq.com
 */

/**
 * 数据解析的方式
 * json或者xml
 */
enum class ConverterFormat {
	STRING,
	JSON,
	XML,
	PROTOBUF,
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RequestConverter(val format: ConverterFormat = ConverterFormat.JSON)


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ResponseConverter(val format: ConverterFormat = ConverterFormat.JSON)