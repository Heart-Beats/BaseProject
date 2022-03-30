package com.hl.arch.mvvm.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.zhanglei.myapplication.activities.base.ViewBindingBaseActivity
import com.hl.arch.R
import com.hl.arch.databinding.ActivityFragmentContainerBinding

class FragmentContainerActivity : ViewBindingBaseActivity<ActivityFragmentContainerBinding>() {

    override fun ActivityFragmentContainerBinding.onViewCreated(savedInstanceState: Bundle?) {
        val extras = intent.extras
        val fragmentClass = extras?.getSerializable(DESTINATION_SCREEN) as? Class<Fragment>
            ?: throw Exception("传入的目标fragment不可为空")
        val fragmentArgument = extras.getBundle(DESTINATION_ARGUMENTS)

        val fragment = fragmentClass.getConstructor().newInstance()
        fragment.arguments = fragmentArgument
        supportFragmentManager
            .beginTransaction()
            // 使用 fragment 名称作为 tag, 便于以后查找它
            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
            .commit()
    }
}

private const val DESTINATION_SCREEN = "目标画面"
private const val DESTINATION_ARGUMENTS = "目标画面参数"

fun Activity.startFragment(fragmentClass: Class<out Fragment>, extras: Bundle? = null) {
    val intent = Intent(this, FragmentContainerActivity::class.java)
    intent.putExtra(DESTINATION_SCREEN, fragmentClass)
    if (extras != null) {
        intent.putExtra(DESTINATION_ARGUMENTS, extras)
    }
    this.startActivity(intent)
}

fun Fragment.startFragment(fragmentClass: Class<out Fragment>, extras: Bundle? = null) {
    requireActivity().startFragment(fragmentClass, extras)
}
