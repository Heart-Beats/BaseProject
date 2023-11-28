package com.hl.api.monitor


data class MonitorResult(
	var netType: String = "", //网络状态 NO，2G，3G，4G，5G，WIFI
	var ua: String = "", //okhttp 版本号
	var url: String = "",
	var requestMethod: String = "",
	var isHttps: Boolean = false,
	var ip: String = "", // 目标机器 ip
	var port: String = "", // 端口号
	var isProxy: Boolean = false, //是否 有代理
	var protocol: String = "", // 协议 具体查看 okhttp3.Protocol 类
	var dnsResult: String = "", // dns 解析出来的 ip 信息，用于分析是否存在域名劫持的问题。
	var tlsHandshakeInfo: String = "", // tls 握手记录
	var requestBodyByteCount: Long = 0, //请求体 大小 byte
	var responseBodyByteCount: Long = 0, //响应体 大小 byte
	var responseCode: Int = 0,
	var dnsCost: Long = 0, // dns 耗时， ms
	var tcpCost: Long = 0, // tcp 耗时，ms
	var tlsCost: Long = 0, // tls 耗时，ms
	var connectTotalCost: Long = 0,   // 连接总耗时， ms   等于 tcpCost + tlsCost
	var requestHeaderCost: Long = 0,  // 请求头耗时， ms
	var requestBodyCost: Long = 0,    // 请求体耗时， ms
	var requestTotalCost: Long = 0,   // 请求报文耗时， ms   等于 requestHeaderCost + requestBodyCost
	var responseHeaderCost: Long = 0,  // 响应头耗时， ms
	var responseBodyCost: Long = 0,    // 响应体耗时， ms
	var responseTotalCost: Long = 0,   // 响应报文耗时， ms    等于 responseHeaderCost + responseBodyCost
	var callCoat: Long = 0             // 整个链路总耗时， ms
) {
	override fun toString(): String {
		return """
			{
				"netType":$netType,
				"ua":$ua,
				"url":$url,
				"requestMethod":$requestMethod,
				"isHttps":$isHttps,
				"ip":$ip,
				"port":$port,
				"isProxy":$isProxy,
				"protocol":$protocol,
				"dnsResult":$dnsResult,
				"tlsHandshakeInfo":$tlsHandshakeInfo,
				"requestBodyByteCount":$requestBodyByteCount B,
				"responseBodyByteCount":$responseBodyByteCount B,
				"responseCode":$responseCode,
				"dnsCost":$dnsCost ms,
				"tcpCost":$tcpCost ms,
				"tlsCost":$tlsCost ms,
				"connectTotalCost":$connectTotalCost ms,
				"requestHeaderCost":$requestHeaderCost ms,
				"requestBodyCost":$requestBodyCost ms,
				"requestTotalCost":$requestTotalCost ms,
				"responseHeaderCost":$responseHeaderCost ms,
				"responseBodyCost":$responseBodyCost ms,
				"responseTotalCost":$responseTotalCost ms,
				"callCoat":$callCoat ms,
			}
		""".trimIndent()
	}
}