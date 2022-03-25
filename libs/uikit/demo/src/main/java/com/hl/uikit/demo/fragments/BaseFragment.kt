package com.hl.uikit.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hl.uikit.UIKitToolbar
import com.hl.uikit.demo.R

/**
 * @Author  张磊  on  2020/10/23 at 9:54
 * Email: 913305160@qq.com
 */
abstract class BaseFragment : Fragment() {

    abstract val layout: Int

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<UIKitToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }
}