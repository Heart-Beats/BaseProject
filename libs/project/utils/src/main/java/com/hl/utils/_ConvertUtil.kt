package com.hl.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.ConvertUtils
import java.io.File
import java.io.InputStream
import java.nio.file.Files

/**
 * @author  张磊  on  2023/04/10 at 17:02
 * Email: 913305160@qq.com
 */

/**
 * 将文件转换为字节数组
 */
@RequiresApi(Build.VERSION_CODES.O)
fun File.toBytes() = Files.readAllBytes(this.toPath())

/**
 * 将字节数组转换为文件
 */
@RequiresApi(Build.VERSION_CODES.O)
fun ByteArray.toFile(file: File) = Files.write(file.toPath(), this)

/**
 * 将输入流转换为字节数组
 */
fun InputStream.toBytes() = ConvertUtils.inputStream2Bytes(this)

/**
 * 将字节数组转换为输入流
 */
fun ByteArray.toInputStream() = ConvertUtils.bytes2InputStream(this)
