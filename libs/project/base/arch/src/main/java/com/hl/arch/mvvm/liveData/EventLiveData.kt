package com.hl.arch.mvvm.liveData

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.hl.utils.runOnUiThread
import java.util.concurrent.atomic.AtomicBoolean

class EventLiveData<T> : MutableLiveData<T>() {

	private val pending = AtomicBoolean(false)

	/**
	 * 添加新订阅者不会收到任何值，仅仅只有更改值时才会收到
	 */
	override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
		super.observe(owner) { t ->
			if (pending.compareAndSet(true, false)) {
				observer.onChanged(t)
			}
		}
	}

	override fun postValue(value: T?) {
		runOnUiThread {
			// 因默认 postValue 存在丢失值的可能，这里改为使用 setValue 来确保值的传递
			setValue(value)
		}
	}

	override fun setValue(value: T?) {
		pending.set(true)
		super.setValue(value)
	}
}