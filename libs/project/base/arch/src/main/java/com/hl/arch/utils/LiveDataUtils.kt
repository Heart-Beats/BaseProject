package com.hl.arch.utils

import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * @Author  张磊  on  2020/12/09 at 17:17
 * Email: 913305160@qq.com
 */

/**
 * 设置唯一观察者，若已有观察者则设置不生效
 */
fun <T> LiveData<T>.onceFirstObserve(owner: LifecycleOwner, onChanged: (T) -> Unit) {
	if (!this.hasObservers()) {
		this.observe(owner, onChanged)
	}
}

/**
 * 设置唯一观察者，若已有观察者则全部移除，仅设置的观察者生效
 */
fun <T> LiveData<T>.onceLastObserve(owner: LifecycleOwner, onChanged: (T) -> Unit) {
	if (this.hasObservers()) {
		this.removeObservers(owner)
	}
	this.observe(owner, onChanged)
}

fun <T> MutableLiveData<T>.setSafeValue(obj: T?) {
	if (Looper.myLooper() == Looper.getMainLooper()) {
		this.value = obj
	} else {
		this.postValue(obj)
	}
}