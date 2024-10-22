package com.hl.api.convert

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.jaxb.JaxbConverterFactory
import retrofit2.converter.protobuf.ProtoConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.reflect.Type

/**
 * @author  张磊  on  2024/10/21 at 16:21
 * Email: 913305160@qq.com
 */

/**
 * Retrofit 数据智能转换器，根据注解来转换，默认使用 JSON
 */
class SmartConverterFactory private constructor() : Converter.Factory() {

	private var jsonFactory: Converter.Factory = GsonConverterFactory.create()
	private var xmlFactory: Converter.Factory? = null
	private var stringFactory: Converter.Factory? = null
	private var protobufFactory: Converter.Factory? = null

	companion object {
		fun create() = SmartConverterFactory()
	}

	override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>, methodAnnotations: Array<Annotation>, retrofit: Retrofit): Converter<*, RequestBody>? {
		for (annotation in methodAnnotations) {
			if (annotation is RequestConverter) {
				return getConverter(annotation.format).requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
			}
		}
		return jsonFactory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
	}

	override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
		for (annotation in annotations) {
			if (annotation is ResponseConverter) {
				return getConverter(annotation.format).responseBodyConverter(type, annotations, retrofit)
			}
		}
		return jsonFactory.responseBodyConverter(type, annotations, retrofit)
	}

	/**
	 * 获取对应的数据转换器
	 */
	private fun getConverter(format: ConverterFormat): Converter.Factory {
		return when (format) {
			ConverterFormat.JSON -> jsonFactory
			ConverterFormat.XML -> xmlFactory ?: JaxbConverterFactory.create()
			ConverterFormat.STRING -> stringFactory ?: ScalarsConverterFactory.create()
			ConverterFormat.PROTOBUF -> protobufFactory ?: ProtoConverterFactory.create()
		}
	}
}