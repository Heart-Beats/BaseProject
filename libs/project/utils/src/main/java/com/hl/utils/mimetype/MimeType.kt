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

	AVI("video/avi", arraySetOf("avi"));

	override fun toString(): String {
		return mMimeTypeName
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
			return EnumSet.of(GIF)
		}

		fun ofGif(): Set<MimeType> {
			return ofImage(true)
		}

		fun ofVideo(): Set<MimeType> {
			return EnumSet.of(MPEG, MP4, QUICKTIME, THREE_GPP, THREE_GPP2, MKV, WEBM, TS, AVI)
		}

		fun isImage(mimeType: String?): Boolean {
			return mimeType?.startsWith("image") ?: false
		}

		fun isVideo(mimeType: String?): Boolean {
			return mimeType?.startsWith("video") ?: false
		}

		fun isGif(mimeType: String?): Boolean {
			return if (mimeType == null) false else mimeType == GIF.toString()
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