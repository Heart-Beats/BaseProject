package com.hl.arch.mvvm.vm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

class EventLiveData<T> : LiveData<T> {

    constructor() : super()

	public override fun postValue(value: T?) {
		super.postValue(value)
	}

	public override fun setValue(value: T?) {
		super.setValue(value)
	}

	/**
	 * 添加新订阅者不会收到任何值，仅仅只有更改值时才会收到
	 */
	override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
		super.observe(owner, {
			if (it != null) {
				observer.onChanged(it)
				value = null
			}
		})
	}
}