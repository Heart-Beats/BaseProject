package com.hl.api.monitor

import android.content.Context
import android.os.SystemClock
import android.util.Log
import okhttp3.Call
import okhttp3.Connection
import okhttp3.ConnectionPool
import okhttp3.EventListener
import okhttp3.Handshake
import okhttp3.HttpUrl
import okhttp3.OkHttp
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy

class NetMonitor(
	private val context: Context,
	private val netMonitorCallback: NetMonitorCallback,
	private val openLog: Boolean = false
) : EventListener() {
	private val TAG = "NetMonitor"

	private val mr = MonitorResult()

	private var callStartTime = 0L
	private var dnsStartTime = 0L
	private var dnsEndTime = 0L
	private var connectStartTime = 0L
	private var secureConnectStartTime = 0L
	private var secureConnectEndTime = 0L
	private var connectEndTime = 0L
	private var requestHeadersStartTime = 0L
	private var requestHeadersEndTime = 0L
	private var requestBodyStartTime = 0L
	private var requestBodyEndTime = 0L
	private var responseHeadersStartTime = 0L
	private var responseHeadersEndTime = 0L
	private var responseBodyStartTime = 0L
	private var responseBodyEndTime = 0L
	private var callEndTime = 0L

	/**
	 * 当客户端将调用排队或执行时立即调用。如果是线程或流限制，此调用可能会在处理请求开始之前执行。
	 *
	 * 对于单个 [Call] 只会调用一次。不同路线的重试或重定向将在单个 callStart 和 [ ][.callEnd]/[.callFailed] 对的范围内处理。
	 */
	override fun callStart(call: Call) {
		super.callStart(call)
		callStartTime = SystemClock.elapsedRealtime()

		d("callStart : ${call.request().url}")

		val request = call.request()

		mr.ua = "okhttp/${OkHttp.VERSION}"
		mr.url = request.url.toString()
		mr.requestMethod = request.method
		mr.isHttps = request.isHttps
		mr.netType = NetworkUtils.getNetWorkTypeSimpleName(context)
	}

	/**
	 * 在 DNS 查找之前调用。请参阅[DNS.lookup]。
	 *
	 * 单次[Call]可以调用1次以上。例如，如果响应到 [Call.request] 是重定向到不同的主机。
	 *
	 * 如果[Call]能够重用现有的池化连接，则该方法将不会被使用调用。请参阅[连接池]。
	 */
	override fun dnsStart(call: Call, domainName: String) {
		super.dnsStart(call, domainName)
		dnsStartTime = SystemClock.elapsedRealtime()
		d("dnsStart :  domainName=$domainName")
	}

	/**
	 * DNS 查找后立即调用。
	 *
	 * 该方法在 [dnsStart] 之后调用。
	 */
	override fun dnsEnd(call: Call, domainName: String, inetAddressList: List<@JvmSuppressWildcards InetAddress>) {
		super.dnsEnd(call, domainName, inetAddressList)
		dnsEndTime = SystemClock.elapsedRealtime()
		d("dnsEnd :  domainName=$domainName  inetAddressList=$inetAddressList")

		mr.dnsResult = inetAddressList.toString()
	}

	/**
	 * 在启动套接字连接之前调用。
	 *
	 * 如果[ConnectionPool]中没有现有的连接，则会调用该方法重复使用。
	 *
	 * 单次[Call]可以调用1次以上。例如，如果响应到 [Call.request] 是重定向到不同的地址，或者重试连接。
	 */
	override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
		super.connectStart(call, inetSocketAddress, proxy)
		connectStartTime = SystemClock.elapsedRealtime()
		d("connectStart :  inetSocketAddress=$inetSocketAddress  proxy=$proxy")

		mr.ip = inetSocketAddress.address.hostAddress
		mr.port = inetSocketAddress.port.toString()
		mr.isProxy = proxy.type() != Proxy.Type.DIRECT
	}

	/**
	 * 在启动 TLS 连接之前调用。
	 *
	 * 如果满足以下条件，则调用该方法：
	 *   [Call.request] 需要 TLS。
	 *   [ConnectionPool] 中的现有连接不能重复使用。
	 *
	 * 单次[Call]可以调用1次以上。例如，如果响应到 [Call.request] 是重定向到不同的地址，或者重试连接。
	 */
	override fun secureConnectStart(call: Call) {
		super.secureConnectStart(call)
		secureConnectStartTime = SystemClock.elapsedRealtime()
		d("secureConnectStart")
	}

	/**
	 * 尝试 TLS 连接后立即调用。
	 *
	 * 该方法在 [secureConnectStart] 之后调用。
	 */
	override fun secureConnectEnd(call: Call, handshake: Handshake?) {
		super.secureConnectEnd(call, handshake)
		secureConnectEndTime = SystemClock.elapsedRealtime()
		d("secureConnectEnd :  handshake=$handshake")

		mr.tlsHandshakeInfo = handshake?.toString() ?: ""
	}

	/**
	 * 尝试套接字连接后立即调用。
	 *
	 * 如果 `call` 使用 HTTPS，这将在之后调用 [secureConnectEnd]，否则将在之后调用 [connectStart]。
	 */
	override fun connectEnd(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy, protocol: Protocol?) {
		super.connectEnd(call, inetSocketAddress, proxy, protocol)
		connectEndTime = SystemClock.elapsedRealtime()
		d("connectEnd : inetSocketAddress=$inetSocketAddress proxy=$proxy protocol=$protocol")

		mr.protocol = protocol.toString()
	}

	/**
	 * 当连接尝试失败时调用。如果还有其他路线，则此故障并非终结可用且已启用故障恢复。
	 *
	 * 如果 `call` 使用HTTPS，它将在 [secureConnectEnd] 之后调用，否则它将在 [connectStart] 之后调用。
	 */
	override fun connectFailed(
		call: Call,
		inetSocketAddress: InetSocketAddress,
		proxy: Proxy,
		protocol: Protocol?,
		ioe: IOException
	) {
		super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe)
		callEndTime = SystemClock.elapsedRealtime()
		e(
			"connectFailed : inetSocketAddress=$inetSocketAddress  proxy=$proxy protocol=$protocol ioe=${ioe.localizedMessage}",
			ioe
		)

		mr.protocol = protocol.toString()
		onEventError(call, ioe)
	}


	/**
	 * 在为 “call” 获取连接后调用。
	 *
	 * 单次[Call]可以调用1次以上。例如，如果响应到 [Call.request] 是重定向到不同的地址。
	 */
	override fun connectionAcquired(call: Call, connection: Connection) {
		super.connectionAcquired(call, connection)
		d("connectionAcquired :  connection=$connection")
	}

	/**
	 * 在释放 “call” 连接后调用。
	 *
	 * 该方法总是在[connectionAcquired]之后调用。
	 * 单次[Call]可以调用1次以上。例如，如果响应到 [Call.request] 是重定向到不同的地址。
	 */
	override fun connectionReleased(call: Call, connection: Connection) {
		super.connectionReleased(call, connection)
		d("connectionReleased :   connection=$connection")
	}

	/**
	 * 在发送请求标头之前调用。
	 *
	 * 连接是隐式的，通常与最后一个 [connectionAcquired] 事件相关。
	 * 单次[Call]可以调用1次以上。例如，如果响应到 [Call.request] 是重定向到不同的地址。
	 */
	override fun requestHeadersStart(call: Call) {
		super.requestHeadersStart(call)
		requestHeadersStartTime = SystemClock.elapsedRealtime()
		d("requestHeadersStart")
	}

	/**
	 * 发送请求头后立即调用。
	 *
	 *
	 * 该方法总是在[requestHeadersStart]之后调用。
	 *
	 * @param request 通过网络发送的请求。访问此正文时出错要求。
	 */
	override fun requestHeadersEnd(call: Call, request: Request) {
		super.requestHeadersEnd(call, request)
		requestHeadersEndTime = SystemClock.elapsedRealtime()
		d("requestHeadersEnd : request=$request")
	}

	/**
	 * 在发送请求正文之前调用。仅当请求允许时才会被调用有一个要发送的请求正文。
	 *
	 *
	 * 连接是隐式的，通常与最后一个 [connectionAcquired] 事件相关。
	 *
	 * 单次[Call]可以调用1次以上。例如，如果响应到 [Call.request] 是重定向到不同的地址。
	 */
	override fun requestBodyStart(call: Call) {
		super.requestBodyStart(call)
		requestBodyStartTime = SystemClock.elapsedRealtime()
		d("requestBodyStart")
	}

	/**
	 * 发送请求正文后立即调用。
	 *
	 *
	 * 该方法总是在[requestBodyStart]之后调用。
	 */
	override fun requestBodyEnd(call: Call, byteCount: Long) {
		super.requestBodyEnd(call, byteCount)
		requestBodyEndTime = SystemClock.elapsedRealtime()
		d("requestBodyEnd  byteCount=$byteCount")

		mr.requestBodyByteCount = byteCount
	}

	/**
	 * 请求写入失败时调用。
	 *
	 *
	 * 该方法在[requestHeadersStart]或[requestBodyStart]之后调用。笔记
	 * 请求失败并不一定导致整个调用失败。
	 */
	override fun requestFailed(call: Call, ioe: IOException) {
		super.requestFailed(call, ioe)
		callEndTime = SystemClock.elapsedRealtime()
		e("requestFailed :  ioe=${ioe.localizedMessage}", ioe)

		onEventError(call, ioe)
	}

	/**
	 * 在接收响应标头之前调用。
	 *
	 * 连接是隐式的，通常与最后一个 [connectionAcquired] 事件 相关。
	 *
	 * 单次[Call]可以调用1次以上。例如，如果响应到 [Call.request] 是重定向到不同的地址。
	 */
	override fun responseHeadersStart(call: Call) {
		super.responseHeadersStart(call)
		responseHeadersStartTime = SystemClock.elapsedRealtime()
		d("responseHeadersStart")
	}

	/**
	 * 收到响应头后立即调用。
	 *
	 *
	 * 该方法总是在[responseHeadersStart]之后调用。
	 *
	 * @param response 通过网络收到的响应。访问主体时出错这个回应。
	 */
	override fun responseHeadersEnd(call: Call, response: Response) {
		super.responseHeadersEnd(call, response)
		responseHeadersEndTime = SystemClock.elapsedRealtime()
		d("responseHeadersEnd :  response=$response")

		mr.responseCode = response.code
	}

	/**
	 * 在接收响应正文之前调用。
	 *
	 *
	 * 连接是隐式的，通常与最后一个相关
	 * [connectionAcquired]事件。
	 *
	 *
	 * 对于单个 [Call]，通常只会调用 1 次，
	 * 例外是一组有限的情况，包括故障恢复。
	 */
	override fun responseBodyStart(call: Call) {
		super.responseBodyStart(call)
		responseBodyStartTime = SystemClock.elapsedRealtime()
		d("responseBodyStart")
	}

	/**
	 * 收到响应体并读完后立即调用。
	 *
	 *
	 * 仅对具有响应正文的请求调用，例如不会被调用网络套接字升级。
	 *
	 * 该方法总是在[requestBodyStart]之后调用。
	 */
	override fun responseBodyEnd(call: Call, byteCount: Long) {
		super.responseBodyEnd(call, byteCount)
		responseBodyEndTime = SystemClock.elapsedRealtime()
		d("responseBodyEnd :  byteCount=$byteCount")

		mr.responseBodyByteCount = byteCount
	}

	/**
	 * 当读取响应失败时调用。
	 *
	 * 该方法在[responseHeadersStart]或[responseBodyStart]之后调用。
	 * 请注意，响应失败并不一定导致整个调用失败。
	 */
	override fun responseFailed(call: Call, ioe: IOException) {
		super.responseFailed(call, ioe)
		callEndTime = SystemClock.elapsedRealtime()
		e("responseFailed : ioe=${ioe.localizedMessage}", ioe)

		onEventError(call, ioe)
	}

	/**
	 * 通话完全结束后立即调用。这包括延迟消费调用者的响应正文。
	 *
	 *
	 * 该方法总是在[callStart]之后调用。
	 */
	override fun callEnd(call: Call) {
		super.callEnd(call)
		callEndTime = SystemClock.elapsedRealtime()
		d("callEnd : ${call.request().url}")

		calculateTime()
		netMonitorCallback.onSuccess(call, mr)
	}

	/**
	 * 当调用永久失败时调用。
	 *
	 * 该方法总是在[callStart]之后调用。
	 */
	override fun callFailed(call: Call, ioe: IOException) {
		super.callFailed(call, ioe)
		callEndTime = SystemClock.elapsedRealtime()
		e("callFailed : ioe=${ioe.localizedMessage}", ioe)
		onEventError(call, ioe)
	}

	/**
	 * 当基于验证缓存响应新鲜度从缓存或网络提供响应时调用。网络Response可用后会紧接着cacheHit或cacheMiss。
	 *
	 * 仅当为客户端配置了缓存时才会收到此事件
	 */
	override fun cacheConditionalHit(call: Call, cachedResponse: Response) {
		super.cacheConditionalHit(call, cachedResponse)
		d("cacheConditionalHit : ${call.request().url},  cachedResponse = $cachedResponse")
	}

	/**
	 * 当从缓存提供结果时调用。提供的响应是顶级响应，不会接收正常的事件序列。
	 *
	 * 仅当为客户端配置了缓存时才会收到此事件。
	 */
	override fun cacheHit(call: Call, response: Response) {
		super.cacheHit(call, response)
		d("cacheHit : ${call.request().url},  response = $response")
	}

	/**
	 * 当从网络提供响应时调用。响应将从正常事件序列中获得。
	 *
	 * 仅当为客户端配置了缓存时才会收到此事件。
	 */
	override fun cacheMiss(call: Call) {
		super.cacheMiss(call)
		d("cacheMiss : ${call.request().url}")
	}

	/**
	 * 取消呼叫时调用。
	 *
	 * 与此接口中的所有方法一样，这是在触发事件的线程上调用的。但是，虽然其他事件是顺序发生的；取消可能与其他活动同时发生。例如，线程A可能正在执行[responseBodyStart] ，而线程B执行[canceled] 。实现必须支持此类并发调用。
	 *
	 * 请注意，取消是尽力而为，并且呼叫在取消后可以正常进行。例如，诸如 [requestHeadersStart] 和 [requestHeadersEnd]之类的愉快路径事件可能会在呼叫取消后发生。通常，当需要昂贵的 I/O操作时，取消才会生效。
	 *
	 * 即使 [Call.cancel] 被调用多次，它也最多被调用一次。它可以在呼叫生命周期中的任何时刻调用，包括在[callStart]之前和[callEnd]之后
	 */
	override fun canceled(call: Call) {
		super.canceled(call)
		d("canceled : ${call.request().url}")
	}

	/**
	 * 选择代理后调用。
	 *
	 * 请注意，代理列表永远不会为空，但它可能是包含以下内容的列表
	 *
	 * 仅 [Proxy.NO_PROXY]。这会在几种情况下出现：
	 *
	 * * 如果既没有配置代理也没有配置代理选择器。
	 * * 如果代理显式配置为 [Proxy.NO_PROXY]。
	 * * 如果代理选择器仅返回[Proxy.NO_PROXY]。
	 * * 如果代理选择器返回空列表或null。
	 *
	 * 否则，它将按照尝试的顺序列出代理。
	 *
	 * @param url 仅指定方案、主机名和端口的 URL。
	 */
	override fun proxySelectEnd(call: Call, url: HttpUrl, proxies: List<Proxy>) {
		super.proxySelectEnd(call, url, proxies)
		d("proxySelectEnd : ${call.request().url},  url = $url,  proxies = $proxies")
	}

	/**
	 * 在选择代理之前调用。
	 *
	 * 无论客户端是否选择路由，都会调用此函数
	 * 配置了单个代理、代理选择器或两者都不配置。
	 *
	 * @param url 仅指定方案、主机名和端口的 URL。
	 */
	override fun proxySelectStart(call: Call, url: HttpUrl) {
		super.proxySelectStart(call, url)
		d("proxySelectStart : ${call.request().url},  url = $url")
	}

	/**
	 * 由于缓存规则导致调用失败时调用。
	 * 比如我们被禁止使用网络、缓存不足
	 */
	override fun satisfactionFailure(call: Call, response: Response) {
		super.satisfactionFailure(call, response)
		d("satisfactionFailure : ${call.request().url},  response = $response")
	}

	/**
	 * 计算各个阶段耗时
	 */
	private fun calculateTime() {
		mr.dnsCost = dnsEndTime - dnsStartTime
		mr.tcpCost = secureConnectStartTime - connectStartTime
		mr.tlsCost = secureConnectEndTime - secureConnectStartTime
		mr.connectTotalCost = mr.tcpCost + mr.tlsCost
		mr.requestHeaderCost = requestHeadersEndTime - requestHeadersStartTime
		mr.requestBodyCost = requestBodyEndTime - requestBodyStartTime
		mr.requestTotalCost = mr.requestHeaderCost + mr.requestBodyCost
		mr.responseHeaderCost = responseHeadersEndTime - responseHeadersStartTime
		mr.responseBodyCost = responseBodyEndTime - responseBodyStartTime
		mr.responseTotalCost = mr.responseHeaderCost + mr.responseBodyCost
		mr.callCoat = callEndTime - callStartTime
	}


	/**
	 * 链路中 所有异常 都会调用到这个方法中
	 */
	private fun onEventError(call: Call, ioe: IOException) {
		calculateTime()
		netMonitorCallback.onError(call, mr, ioe)
	}

	private fun d(log: String) {
		if (openLog) {
			Log.d(TAG, log)
		}
	}

	private fun e(log: String, e: Exception) {
		if (openLog) {
			Log.e(TAG, log, e)
		}
	}
}

