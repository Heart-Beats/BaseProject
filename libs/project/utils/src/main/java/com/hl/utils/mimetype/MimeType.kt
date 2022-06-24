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
	MPEG("video/mpeg", arraySetOf("mpeg", "mpg")),

	MP4("video/mp4", arraySetOf("mp4", "m4v")),

	QUICKTIME("video/quicktime", arraySetOf("mov")),

	THREE_GPP("video/3gpp", arraySetOf("3gp", "3gpp")),

	THREE_GPP2("video/3gpp2", arraySetOf("3g2", "3gpp2")),

	MKV("video/x-matroska", arraySetOf("mkv")),

	WEBM("video/webm", arraySetOf("webm")),

	TS("video/mp2ts", arraySetOf("ts")),

	AVI("video/avi", arraySetOf("avi")),

	/*****  ============== words ============== *****/
	/*文档 */
	WORD("application/msword", arraySetOf("doc", "docx", "xls", "xlsx")),
	PDF("application/pdf", arraySetOf("pdf")),
	WPS("application/vnd.ms-works", arraySetOf("wps")),  //金山 Office 文字排版文件格式
	TXT("text/plain", arraySetOf("txt")),
	ODT("application/vnd.oasis.opendocument.text", arraySetOf("odt")),

	/* 表格 */
	ET("application/kset", arraySetOf("et")),     // 金山 Office 表格文件格式
	CSV("text/csv", arraySetOf("csv")),
	ODS("application/vnd.oasis.opendocument.spreadsheet", arraySetOf("ods")),

	/* ppt */
	PPT("application/vnd.ms-powerpoint", arraySetOf("ppt", "pps")),
	PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation", arraySetOf("pptx")),
	DPS("application/ksdps", arraySetOf("dps")),    // 金山 Office 演示文稿格式
	ODP("application/vnd.oasis.opendocument.presentation", arraySetOf("odp"));

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
		return mMimeTypeName.startsWith("video")
	}

	/**
	 * 是否为 GIF
	 */
	fun isGif(): Boolean {
		return mMimeTypeName == GIF.toString()
	}

	/**
	 * 是否为文档
	 */
	fun isDocument(): Boolean {
		return mMimeTypeName == WORD.mMimeTypeName || mMimeTypeName == PDF.mMimeTypeName || mMimeTypeName == WPS.mMimeTypeName
				|| mMimeTypeName == TXT.mMimeTypeName || mMimeTypeName == ODT.mMimeTypeName || mMimeTypeName == ET.mMimeTypeName
				|| mMimeTypeName == CSV.mMimeTypeName || mMimeTypeName == ODS.mMimeTypeName
	}

	/**
	 * 是否为  PPT
	 */
	fun isPPT(): Boolean {
		return mMimeTypeName == PPT.mMimeTypeName || mMimeTypeName == PPTX.mMimeTypeName || mMimeTypeName == DPS.mMimeTypeName
				|| mMimeTypeName == ODP.mMimeTypeName
	}

	/**
	 * 是否为 office 文件
	 */
	fun isOffice(): Boolean {
		return isDocument() || isPPT()
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
			return EnumSet.of(MPEG, MP4, QUICKTIME, THREE_GPP, THREE_GPP2, MKV, WEBM, TS, AVI)
		}

		/**
		 * 通过文件扩展名获取 MimeType
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