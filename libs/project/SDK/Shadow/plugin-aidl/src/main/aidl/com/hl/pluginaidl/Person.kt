package com.hl.pluginaidl

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author  张磊  on  2021/06/21 at 15:22
 * Email: 913305160@qq.com
 *
 * 主要用于测试插件向宿主 传递自定义对象
 */

data class Person(var name: String? = null, var age: Int? = null, var sex: String? = null) : Parcelable {

	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString()
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(name)
		parcel.writeValue(age)
		parcel.writeString(sex)
	}

	fun readFromParcel(parcel: Parcel) {
		name = parcel.readString()
		age = parcel.readValue(Int::class.java.classLoader) as? Int
		sex = parcel.readString()
	}


	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<Person> {
		override fun createFromParcel(parcel: Parcel): Person {
			return Person(parcel)
		}

		override fun newArray(size: Int): Array<Person?> {
			return arrayOfNulls(size)
		}
	}
}