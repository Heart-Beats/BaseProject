package com.hl.utils.mimetype

import androidx.collection.arraySetOf
import java.util.*

/**
 * @author  张磊  on  2022/06/10 at 10:09
 * Email: 913305160@qq.com
 */
enum class MimeType(val mMimeTypeName: String, private val mExtensions: Set<String>) {

	/*****  ============== images ============== *****/
	JPEG("image/jpeg", arraySetOf("jpg", "jpeg")),

	PNG("image/png", arraySetOf("png")),

	GIF("image/gif", arraySetOf("gif")),

	BMP("image/x-ms-bmp", arraySetOf("bmp")),

	WEBP("image/webp", arraySetOf("webp")),


	/*****  ============== videos ============== *****/
	MPEG("video/mpeg", arraySetOf("mpeg", "mpg", "mpe")),

	MP4("video/mp4", arraySetOf("mp4", "mpg4")),

	QUICKTIME("video/quicktime", arraySetOf("mov")),

	THREE_GPP("video/3gpp", arraySetOf("3gp", "3gpp")),

	THREE_GPP2("video/3gpp2", arraySetOf("3g2", "3gpp2")),

	MKV("video/x-matroska", arraySetOf("mkv")),

	WEBM("video/webm", arraySetOf("webm")),

	TS("video/mp2ts", arraySetOf("ts")),

	AVI("video/avi", arraySetOf("avi")),

	M4U("video/vnd.mpegurl", arraySetOf("m4u")),  // 一些视频播放器程序使用的播放列表, 不包含视频文件本身

	M4V("video/x-m4v", arraySetOf("m4v")),    // 苹果使用的视频文件, MP4 文件格式的特殊类型

	ASF("video/x-ms-asf", arraySetOf("asf")),  // windows media 流媒体文件格式

	WMV("video/x-ms-wmv", arraySetOf("wmv")), // 基于微软高级系统格式（ASF）容器格式和压缩与Windows Media压缩的视频文件

	FLV("video/x-flv", arraySetOf("flv")), // 基于微软高级系统格式（ASF）容器格式和压缩与Windows Media压缩的视频文件

	RMVB("application/vnd.rn-realmedia-vbr", arraySetOf("rmvb")), //rmvb是real公司推出的视频格式，在保证一定清晰度的基础上有良好的压缩率


	/*****  ============== audios ============== *****/
	MP3("audio/x-mpeg", arraySetOf("mp2", "mp3")),

	M3U("audio/x-mpegurl", arraySetOf("m3u")),

	M4A("audio/mp4a-latm", arraySetOf("m4a", "m4b", "m4p")),

	MPC("application/vnd.mpohun.certificate", arraySetOf("mpc")),  //使用 Musepack音频格式的音频文件

	WAV("audio/x-wav", arraySetOf("wav")),  // 微软公司专门为 Windows 开发的一种标准数字音频文件, 能记录各种单声道或立体声的声音信息，并能保证声音不失真

	WMA("audio/x-ms-wma", arraySetOf("wma")), // 微软公司推出的与MP3格式齐名的一种新的音频格式，在压缩比和音质方面都超过了 MP3，更是远胜于 RA（RealAudio），即使在较低的采样频率下也能产生较好的音质

	MPGA("audio/mpeg", arraySetOf("mpga")),  //使用MPEG-1 Layer 3压缩编码的压缩音频文件

	OGG("audio/ogg", arraySetOf("ogg")),  //ogg与mp3一样也是一种有损压缩的音频格式文件，ogg音频格式文件与其它音频不同在于它是完全免费、开放、没有专利限制的


	/*****  ============== office ============== *****/
	/*文档 */
	WORD("application/msword", arraySetOf("doc", "docx", "xls", "xlsx")),
	PDF("application/pdf", arraySetOf("pdf")),
	WPS("application/vnd.ms-works", arraySetOf("wps")),  //金山 Office 文字排版文件格式
	TXT("text/plain", arraySetOf("txt", "c", "conf", "cpp", "h", "java", "log", "prop", "rc", "sh", "xml")),
	JSON("text/json", arraySetOf("json")),
	ODT("application/vnd.oasis.opendocument.text", arraySetOf("odt")),
	RTF("application/rtf", arraySetOf("rtf")), // RTF全称Rich Text Format，意为富文本格式，是微软在1987年推出的文档格式

	/* 表格 */
	ET("application/kset", arraySetOf("et")),     // 金山 Office 表格文件格式
	CSV("text/csv", arraySetOf("csv")),
	ODS("application/vnd.oasis.opendocument.spreadsheet", arraySetOf("ods")),

	/* ppt */
	PPT("application/vnd.ms-powerpoint", arraySetOf("ppt", "pps")),
	PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation", arraySetOf("pptx")),
	DPS("application/ksdps", arraySetOf("dps")),    // 金山 Office 演示文稿格式
	ODP("application/vnd.oasis.opendocument.presentation", arraySetOf("odp")),


	/*****  ============== html 网页 ============== *****/
	HTML("text/html", arraySetOf("htm", "html", "shtml")),

	XHTML("application/xhtml+xml", arraySetOf("xht", "xhtml")),


	/*****  ============== 压缩包 ============== *****/
	APK("application/vnd.android.package-archive", arraySetOf("apk")),

	JAR("application/java-archive", arraySetOf("jar")), //一种软件包文件格式，通常用于聚合大量的Java类文件、相关的元数据和资源（文本、图片等）文件到一个文件，以便分发Java平台应用软件或库

	GTAR("application/x-gtar", arraySetOf("gtar")),   // 使用GNU Tar 标准创建的文件压缩包

	GZ("application/x-gzip", arraySetOf("gz")),   // GZ 是UNIX系统中的压缩文件

	TAR("application/x-tar", arraySetOf("tar")),   // Unix和类Unix系统上文件打包工具所打包出来的压缩包

	TGZ("application/x-compressed", arraySetOf("tgz")), // .tar.gz 或者.tgz 的文件一般是在 UNIX下用 tar 和 gzip 压缩的文件

	RAR("application/x-rar-compressed", arraySetOf("rar")),

	ZIP("application/zip", arraySetOf("zip")),


	/*****  ============== 文件 ============== *****/
	BIN("application/octet-stream", arraySetOf("bin", "class", "exe")),

	JS("application/x-javascript", arraySetOf("js")),

	MSG("application/vnd.ms-outlook", arraySetOf("msg")), // 微软 outlook 邮件客户端的邮件格式

	;

	override fun toString(): String {
		return mMimeTypeName
	}

	/**
	 * 是否为图片
	 */
	fun isImage(): Boolean {
		return mMimeTypeName.startsWith("image")
	}

	/**
	 * 是否为视频
	 */
	fun isVideo(): Boolean {
		return mMimeTypeName.startsWith("video") || this == RMVB
	}

	/**
	 * 是否为音频
	 */
	fun isAudio(): Boolean {
		return mMimeTypeName.startsWith("audio") || this == MPC
	}

	/**
	 * 是否为 GIF
	 */
	fun isGif(): Boolean {
		return mMimeTypeName == GIF.toString()
	}

	/**
	 * 是否为文本
	 */
	fun isText(): Boolean {
		return mMimeTypeName.startsWith("text")
	}

	/**
	 * 是否为文档
	 */
	fun isDocument(): Boolean {
		return this == WORD || this == PDF || this == WPS || this == TXT || this == JSON || this == ODT
				|| this == ET || this == CSV || this == ODS || this == RTF
	}

	/**
	 * 是否为  PPT
	 */
	fun isPPT(): Boolean {
		return this == PPT || this == PPTX || this == DPS || this == ODP
	}

	/**
	 * 是否为 office 文件
	 */
	fun isOffice(): Boolean {
		return isDocument() || isPPT()
	}

	/**
	 * 是否为 Html 网页
	 */
	fun isHTML(): Boolean {
		return this == HTML || this == XHTML
	}

	/**
	 * 是否为归档文件
	 */
	fun isArchiveFiles(): Boolean {
		return this == APK || this == JAR || this == GTAR || this == GZ || this == TAR
				|| this == TGZ || this == RAR || this == ZIP
	}


	companion object {

		fun ofAll(): Set<MimeType> {
			return EnumSet.allOf(MimeType::class.java)
		}

		fun of(type: MimeType?, vararg rest: MimeType?): Set<MimeType> {
			return EnumSet.of(type, *rest)
		}

		fun ofImage(): Set<MimeType> {
			return EnumSet.of(JPEG, PNG, GIF, BMP, WEBP)
		}

		fun ofImage(onlyGif: Boolean): Set<MimeType> {
			return if (onlyGif) ofGif() else ofImage()
		}

		fun ofGif(): Set<MimeType> {
			return EnumSet.of(GIF)
		}

		fun ofVideo(): Set<MimeType> {
			return EnumSet.of(MPEG, MP4, QUICKTIME, THREE_GPP, THREE_GPP2, MKV, WEBM, TS, AVI, M4U,
						M4V, ASF, WMV, FLV, RMVB)
		}

		fun ofAudio(): Set<MimeType> {
			return EnumSet.of(MP3, M3U, M4A, MPC, WAV, WMA, MPGA, OGG)
		}

		/**
		 * 通过文件扩展名获取 com.hl.utils.mimetype.MimeType
		 */
		fun getByExtension(extension: String): MimeType? {
			for (value in values()) {
				if (value.mExtensions.contains(extension)) {
					return value
				}
			}

			return null
		}
	}
}