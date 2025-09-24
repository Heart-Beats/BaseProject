package com.hl.api.launcher

class ApiLauncherAction<BODY> {

	var onSuccessAction: ((body: BODY?) -> Unit) = {}
	var onFailAction: ((failCode: String, failReason: String) -> Unit)? = null

	fun onSuccess(action: (body: BODY?) -> Unit) {
		this.onSuccessAction = action
	}

	fun onFail(action: (failCode: String, failReason: String) -> Unit) {
		this.onFailAction = action
	}
}