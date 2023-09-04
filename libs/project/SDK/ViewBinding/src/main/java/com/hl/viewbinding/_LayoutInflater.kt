package com.hl.viewbinding

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding

/**
 * @author  张磊  on  2023/08/30 at 17:50
 * Email: 913305160@qq.com
 */

/**
 * 通过 layoutInflater 获取 VB
 */
inline fun <reified VB : ViewBinding> LayoutInflater.inflateBinding() = inflateBinding<VB>(this)