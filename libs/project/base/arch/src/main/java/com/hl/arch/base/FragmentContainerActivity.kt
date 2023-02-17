package com.hl.arch.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.hl.arch.R
import com.hl.arch.base.ViewBindingBaseActivity
import com.hl.arch.databinding.ActivityFragmentContainerBinding
import com.hl.utils.replaceFragment

class FragmentContainerActivity : ViewBindingBaseActivity<ActivityFragmentContainerBinding>() {

    override fun ActivityFragmentContainerBinding.onViewCreated(savedInstanceState: Bundle?) {
        val extras = intent.extras
        val fragmentClass = extras?.getSerializable(DESTINATION_SCREEN) as? Class<Fragment>
            ?: throw Exception("传入的目标fragment不可为空")
        val fragmentArgument = extras.getBundle(DESTINATION_ARGUMENTS)

        val fragment = fragmentClass.getConstructor().newInstance()
        fragment.arguments = fragmentArgument

        // 使用 fragment 名称作为 tag, 便于以后查找它
        replaceFragment(R.id.container, fragment, fragment.javaClass.simpleName)
    }
}

private const val DESTINATION_SCREEN = "目标画面"
private const val DESTINATION_ARGUMENTS = "目标画面参数"

fun Context.startFragment(
    fragmentClass: Class<out Fragment>,
    containerActivityClass: Class<out FragmentContainerActivity>,
    extras: Bundle? = null
) {
    val intent = Intent(this, containerActivityClass)
    intent.putExtra(DESTINATION_SCREEN, fragmentClass)
    if (extras != null) {
        intent.putExtra(DESTINATION_ARGUMENTS, extras)
    }
    this.startActivity(intent)
}

fun Context.startFragment(fragmentClass: Class<out Fragment>, extras: Bundle? = null) {
    this.startFragment(fragmentClass, FragmentContainerActivity::class.java, extras)
}

fun Fragment.startFragment(fragmentClass: Class<out Fragment>, extras: Bundle? = null) {
    requireActivity().startFragment(fragmentClass, extras)
}

fun View.startFragment(fragmentClass: Class<out Fragment>, extras: Bundle? = null) {
    this.context.startFragment(fragmentClass, extras)
}

inline fun <reified T : Fragment> Context.startFragment(argumentsBlock: Bundle.() -> Unit = {}) {
    this.startFragment(T::class.java, FragmentContainerActivity::class.java, bundleOf().apply(argumentsBlock))
}

inline fun <reified T : Fragment> Fragment.startFragment(argumentsBlock: Bundle.() -> Unit = {}) {
    requireActivity().startFragment<T>(argumentsBlock)
}

inline fun <reified T : Fragment> View.startFragment(argumentsBlock: Bundle.() -> Unit = {}) {
    this.context.startFragment<T>(argumentsBlock)
}

