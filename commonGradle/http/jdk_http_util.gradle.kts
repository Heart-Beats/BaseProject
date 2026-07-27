import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


val doGet by extra(this::doGetFun)
val doGetWithHead by extra(this::doGetWithHeadFun)
val doPost by extra(this::doPostFun)
val doPostWithHead by extra(this::doPostWithHeadFun)
val uploadFile by extra(this::uploadFileFun)

/**
 * 发送 GET 请求，K-V 形式参数
 *
 * @param url      请求地址
 * @param isForm   是否为表单形式
 * @param params   请求参数
 *
 * @return 请求结果
 */
fun doGetFun(url: String, isForm: Boolean, params: Map<String, String>): String {
    val requestBodyType = if (isForm) RequestBodyType.FORM else RequestBodyType.TEXT
    return doGetWithHeadFun(url, requestBodyType, params, mapOf())
}

/**
 * 发送 GET 请求，K-V 形式参数
 *
 * @return 请求结果
 */
fun doGetWithHeadFun(
    url: String,
    requestBodyType: RequestBodyType,
    params: Map<String, String>,
    heads: Map<String, String>
): String {
    return doHttpRequest(url, RequestMethod.GET, requestBodyType, params, heads)
}

/**
 * 发送 POST 请求，K-V 形式参数
 *
 * @param url      请求地址
 * @param isForm   是否为表单形式
 * @param params   请求参数
 *
 * @return 请求结果
 */
fun doPostFun(url: String, isForm: Boolean, params: Map<String, String>): String {
    val requestBodyType = if (isForm) RequestBodyType.FORM else RequestBodyType.TEXT
    return doPostWithHeadFun(url, requestBodyType, params, mapOf())
}

/**
 * 发送 POST 请求，K-V 形式参数
 *
 * @return 请求结果
 */
fun doPostWithHeadFun(
    url: String,
    requestBodyType: RequestBodyType,
    params: Map<String, String>,
    heads: Map<String, String>
): String {
    return doHttpRequest(url, RequestMethod.POST, requestBodyType, params, heads)
}


enum class RequestMethod(val method: String) {
    GET("GET"),
    POST("POST")
}

enum class RequestBodyType(val contentType: String) {
    TEXT("application/json"),
    FORM("multipart/form-data")
}


/**
 * 发送 http 请求
 *
 * @param params  请求参数
 * @param heads   请求头
 *
 * @return 请求结果
 */
fun doHttpRequest(
    url: String,
    requestMethod: RequestMethod,
    requestBodyType: RequestBodyType,
    params: Map<String, String>,
    heads: Map<String, String>
): String {

    return when (requestBodyType) {
        RequestBodyType.TEXT -> doHttpTextRequest(url, requestMethod, params, heads)
        RequestBodyType.FORM -> doHttpFormRequest(url, requestMethod, params, heads)
    }
}

/**
 * Json 形式的请求
 */
fun doHttpTextRequest(
    url: String,
    requestMethod: RequestMethod,
    params: Map<String, String>,
    heads: Map<String, String>
): String {
    var out: OutputStream? = null
    var bis: BufferedInputStream? = null
    var bos: ByteArrayOutputStream? = null

    var responseString = ""

    try {
        val requestUrl = getRequestURL(url, requestMethod, params)

        val httpURLConnection = requestUrl.openConnection() as HttpURLConnection
        httpURLConnection.commonSetting(requestMethod, heads)

        httpURLConnection.setRequestProperty("Content-Type", "application/json")

        if (requestMethod == RequestMethod.POST) {
            out = httpURLConnection.outputStream
            if (params.isNotEmpty()) {
                out.write(toJSONString(params).toByteArray())
            }
            out.flush()
        }

        val responseCode = httpURLConnection.responseCode
        println("doHttpTextRequest ------->  请求完成:  url == ${requestUrl} , 响应码 == $responseCode")

        if (httpURLConnection.responseCode >= 300) {
            throw Exception("HTTP Request is not success, Response code is " + httpURLConnection.responseCode)
        }

        if (responseCode >= HttpURLConnection.HTTP_OK) {
            if (httpURLConnection.responseCode == HttpURLConnection.HTTP_OK) {
                // 获取请求返回的数据流
                bis = BufferedInputStream(httpURLConnection.inputStream)
                bos = ByteArrayOutputStream()

                var i: Int
                while (bis.read().also { i = it } != -1) {
                    bos.write(i)
                }

                responseString = bos.toString()
            } else {
                // 有些情况需要通过其他标识请求结果, 这里将响应码包装成 code 返回
                val response = "{\"code\":${responseCode}, \"message\":\"${httpURLConnection.responseMessage}\"}"
                responseString = response
            }
        }

    } catch (e: java.lang.Exception) {
        println("请求发生异常 --------->  ${e.message}")
        e.printStackTrace()
    } finally {
        out?.close()
        bis?.close()
        bos?.close()
    }

    return responseString
}

/**
 * 获取对不同的请求得到的请求地址
 */
fun getRequestURL(url: String, requestMethod: RequestMethod, params: Map<String, String>): URL {
    var requestUrl = url
    if (requestMethod == RequestMethod.GET) {
        // GET 请求拼接请求参数
        if (params.isNotEmpty()) {
            val sb = java.lang.StringBuilder()
            sb.append("?")

            params.forEach { (key, value) ->
                sb.append(key)
                    .append("=")
                    .append(URLEncoder.encode(value, "UTF-8"))
                    .append("&")
            }

            sb.delete(sb.length - 1, sb.length)
            requestUrl += sb.toString()
        }
    }
    return URL(requestUrl)
}

fun HttpURLConnection.commonSetting(requestMethod: RequestMethod, heads: Map<String, String>) {
    val defaultConnectTimeout = 30 * 1000

    // 设置请求方法
    this.requestMethod = requestMethod.method
    this.connectTimeout = defaultConnectTimeout
    this.readTimeout = defaultConnectTimeout

    // 设置是否从 httpUrlConnection 读入，默认情况下是true;
    this.doInput = true
    // 设置是否向httpUrlConnection输出, POST请求必须设置该属性
    this.doOutput = true

    // 设置字符编码连接参数
    this.setRequestProperty("Connection", "Keep-Alive")
    // 设置字符编码
    this.setRequestProperty("Charset", "UTF-8")

    if (requestMethod == RequestMethod.POST) {
        // Post 请求不能使用缓存
        this.useCaches = false
    }

    heads.entries.forEach {
        this.setRequestProperty(it.key, it.value)
    }
}

/**
 * 表单形式的请求
 */
fun doHttpFormRequest(
    url: String,
    requestMethod: RequestMethod,
    params: Map<String, String>,
    heads: Map<String, String>,
    uploadFilePath: String? = null
): String {
    val lineEnd = "\r\n"
    val newLine = "\r\n"
    val twoHyphens = "--"
    val boundary = java.util.UUID.randomUUID().toString()
    var ds: DataOutputStream? = null
    var inputStream: InputStream? = null
    var inputStreamReader: InputStreamReader? = null
    var reader: BufferedReader? = null
    var resultBuffer = StringBuffer()
    var tempLine: String?
    try {
        // 统一资源
        val requestUrl = getRequestURL(url, requestMethod, params)
        // http的连接类
        val httpURLConnection = requestUrl.openConnection() as HttpURLConnection

        httpURLConnection.commonSetting(requestMethod, heads)

        // 设置请求内容类型
        httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=$boundary")

        // 设置DataOutputStream
        ds = DataOutputStream(httpURLConnection.outputStream)

        //======传递参数======
        params.entries.forEach {
            val formData = buildString {
                this.append(twoHyphens + boundary + lineEnd)
                this.append("Content-Disposition: form-data; name=\"${it.key}\"" + lineEnd)
                this.append("Content-Type:text/plain$lineEnd")

                // multipart/form-data 报文固定格式， 参数与实际内容之间必须要有换行
                this.append(newLine)
                this.append(it.value + lineEnd)
            }

            // 使用写入 字节数组的方式避免中文传输乱码
            ds.write(formData.toByteArray())
        }
        //======传递参数end======

        if (uploadFilePath != null) {
            // 上传文件参数不为 null 时写入文件
            uploadFile(twoHyphens, boundary, uploadFilePath, ds)
        }

        //写入标记结束位
        ds.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)
        ds.flush()

        val responseCode = httpURLConnection.responseCode
        println("doHttpFormRequest ------->  请求完成:  url == ${requestUrl} , 响应码 == $responseCode")

        if (responseCode >= 300) {
            throw Exception("HTTP Request is not success, Response code is " + responseCode)
        }

        if (responseCode >= HttpURLConnection.HTTP_OK) {
            resultBuffer = StringBuffer()

            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.inputStream
                inputStreamReader = InputStreamReader(inputStream)
                reader = BufferedReader(inputStreamReader)
                tempLine = null
                while ((reader.readLine().also { tempLine = it }) != null) {
                    resultBuffer.append(tempLine)
                    resultBuffer.append("\n")
                }
            } else {
                // 有些情况需要通过其他标识请求结果, 这里将响应码包装成 code 返回
                val response = "{\"code\":${responseCode}, \"message\":\"${httpURLConnection.responseMessage}\"}"
                resultBuffer.append(response)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        ds?.close()
        reader?.close()
        inputStreamReader?.close()
        inputStream?.close()
    }
    return resultBuffer.toString()
}


/**
 * Map转Json字符串
 */
fun toJSONString(map: Map<String, String>): String {
    val i = map.entries.iterator()
    if (!i.hasNext()) {
        return "{}"
    }
    val sb = StringBuilder()
    sb.append('{')
    while (true) {
        val (key, value) = i.next()
        sb.append("\"")
        sb.append(key)
        sb.append("\"")
        sb.append(':')
        sb.append("\"")
        sb.append(value)
        sb.append("\"")
        if (!i.hasNext()) {
            return sb.append('}').toString()
        }
        sb.append(',').append(' ')
    }
}

/**
 * 开始上传文件
 *
 * */
fun uploadFile(
    twoHyphens: String,
    boundary: String,
    uploadFilePath: String,
    ds: DataOutputStream
) {
    val lineEnd = "\r\n"
    val newLine = "\r\n"

    //======传递文件======
    val formData = buildString {
        this.append(twoHyphens + boundary + lineEnd)
        this.append("Content-Disposition: form-data; name=\"file\"; filename=\"$uploadFilePath\"$lineEnd")
        this.append("Content-Type:text/plain$lineEnd")

        // multipart/form-data 报文固定格式， 参数与实际内容之间必须要有换行
        this.append(newLine)
    }
    ds.write(formData.toByteArray())

    val fStream = FileInputStream(uploadFilePath)
    val bufferSize = 1024
    val buffer = ByteArray(bufferSize)
    var length: Int
    var totalSize = 0
    val uploadFileSize = File(uploadFilePath).length()
    var lastUploadProcess = 0

    println("开始上传文件 --------------> $uploadFilePath")
    while ((fStream.read(buffer).also { length = it }) != -1) {
        ds.write(buffer, 0, length)

        totalSize += length
        val currentUploadProcess = (totalSize.toFloat() / uploadFileSize * 100).toInt()
        if (currentUploadProcess != lastUploadProcess) {
            // println("上传中 -------------->  ${currentUploadProcess}%")
            lastUploadProcess = currentUploadProcess
        }
    }
    ds.writeBytes(lineEnd)
    fStream.close()
    //======传递文件end======
}

/**
 * 文件上传
 * @param httpUrl         文件上传的请求 url
 * @param uploadFilePath  需要上传的文件路径
 * @param params          传递的参数
 * @return
 */
fun uploadFileFun(httpUrl: String, uploadFilePath: String, params: Map<String, String>): String {
    return doHttpFormRequest(httpUrl, RequestMethod.POST, params, hashMapOf(), uploadFilePath)
}