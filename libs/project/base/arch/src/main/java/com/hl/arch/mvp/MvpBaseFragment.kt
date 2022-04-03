package com.hl.arch.mvp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hl.arch.ToastUtils

/**
 * fragment基类
 *
 * @param <Presenter>
 * @author 91330
</Presenter> */
abstract class MvpBaseFragment<Presenter : MvpBasePresenter<out BaseView>> : Fragment(), BaseView {
    var presenter: Presenter? = null
        private set

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layoutId, container, false)

        presenter = createPresenter()?.also {
            it.detachView(this)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view, savedInstanceState)
        requestData()
    }

    override fun dealNetError(code: Int, e: Throwable) {
        showError("错误信息（" + e.message + "），点击刷新")
    }

    /**
     * @return 布局 id
     */
    protected abstract val layoutId: Int

    protected abstract fun createPresenter(): Presenter?

    /**
     * @param view               页面对应的 view
     * @param savedInstanceState 保存的状态
     */
    protected abstract fun initView(view: View?, savedInstanceState: Bundle?)

    /**
     * 返回请求服务器的数据
     */
    protected fun requestData() {}

    override fun showMsg(msg: String) {
        ToastUtils.showShort(requireContext(), msg)
    }

    override fun getContext(): Context {
        return requireActivity()
    }


}