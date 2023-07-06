package com.hl.arch.mvp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hl.arch.base.BaseNavigationFragment
import com.hl.uikit.toast

/**
 * fragment基类
 *
 * @param <Presenter>
 * @author 91330
</Presenter> */
abstract class MvpBaseFragment<Presenter : MvpBasePresenter<out MvpBaseView>> : BaseNavigationFragment(), MvpBaseView {

    protected var presenter: Presenter? = null
        private set

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        presenter = createPresenter()?.also {
            it.detachView(this)
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view, savedInstanceState)
        requestData()
    }

    override fun dealNetError(code: Int, e: Throwable) {
        showError("错误信息（" + e.message + "），点击刷新")
    }


    protected abstract fun createPresenter(): Presenter?

    /**
     * @param view               页面对应的 view
     * @param savedInstanceState 保存的状态
     */
    protected abstract fun initView(view: View, savedInstanceState: Bundle?)

    /**
     * 返回请求服务器的数据
     */
    protected open fun requestData() {}

    override fun showMsg(msg: String) {
        toast(msg)
    }

    override fun getAttachContext(): Context {
        return requireActivity()
    }
}