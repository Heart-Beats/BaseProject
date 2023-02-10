package com.hl.arch.utils.viewbinding

/**
 * @author  张磊  on  2023/02/10 at 16:48
 * Email: 913305160@qq.com
 */
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

inline fun <reified VB : ViewBinding> RecyclerView.ViewHolder.withBinding(block: VB.(RecyclerView.ViewHolder) -> Unit) = apply {
	block(getBinding(), this@withBinding)
}

fun <VB : ViewBinding> BindingViewHolder<VB>.withBinding(block: VB.(BindingViewHolder<VB>) -> Unit) = apply {
	block(binding, this@withBinding)
}

inline fun <reified VB : ViewBinding> RecyclerView.ViewHolder.getBinding() = itemView.getBinding<VB>()

inline fun <reified VB : ViewBinding> BindingViewHolder(parent: ViewGroup) =
	BindingViewHolder(inflateBinding<VB>(parent))

class BindingViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root) {

	constructor(parent: ViewGroup, inflate: (LayoutInflater, ViewGroup, Boolean) -> VB) :
			this(inflate(LayoutInflater.from(parent.context), parent, false))
}