package com.hl.pluginaidl;

import com.hl.pluginaidl.PluginAidlListener;  // AIDL 中涉及到的自定义类型都需要手动导包

interface PluginAidlInterface {

	void openActivity(String activityClassName, in Bundle extras);


	// AIDL 中的方法涉及到接口时，这个接口也必须得由 AIDL 生成
	void setPluginListener(PluginAidlListener PluginAidlListener);

}