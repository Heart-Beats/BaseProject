package com.hl.utils.viewbinding

/**
 * @author  张磊  on  2023/02/10 at 16:16
 * Email: 913305160@qq.com
 */
import android.view.View
import androidx.viewbinding.ViewBinding
import com.hl.utils.R

inline fun <reified VB : ViewBinding> View.getBinding() = getBinding(VB::class.java)

@Suppress("UNCHECKED_CAST")
fun <VB : ViewBinding> View.getBinding(clazz: Class<VB>) =
	getTag(R.id.hl_utils_tag_view_binding) as? VB ?: (clazz.getMethod("bind", View::class.java)
		.invoke(null, this) as VB)
		.also { setTag(R.id.hl_utils_tag_view_binding, it) }