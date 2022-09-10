package com.hl.pluginaidl;

import com.hl.pluginaidl.Person;

parcelable Person;  // 声明 Person 类型，只有这样 AIDL 才可找到它并知道它已实现 parcelable 接口

interface PluginAidlListener {

	void  onOpenActivity(boolean result, String openActivityName);

	/*
		调用 AIDL.Stub() 方法的进程为服务端， 调用 AIDL 中相关接口的进程为客户端

		in、out、inout：AIDL 中的定向 tag，表示在跨进程通信中数据的流向，这里的数据流向是针对在客户端中的调用的方法形参而言的
			 in： 表示数据只能由客户端流向服务端，服务端修改数据不会同步返回
			 out： 表示数据只能由服务端流向客户端，会新创建一个无参对象传递到服务端，服务端修改数据会同步返回
			 inout： 表示数据可在服务端与客户端之间双向流通，服务端和客户端同步共用一个对象
	*/

	void onTransPerson(in Person person);
}