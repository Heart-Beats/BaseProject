package com.hl.uikit.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData

class FragmentContainerActivity : BaseActivity() {
    val touchEvent by lazy {
        MutableLiveData<MotionEvent?>()
    }

    var onBackPressedListener: () -> Boolean = { false }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)
        val extras = intent.extras
        val fragmentClass = extras?.getSerializable(DESTINATION_SCREEN) as? Class<Fragment>
            ?: throw Exception("传入的目标fragment不可为空")
        val fragmentArgument = extras.getBundle(DESTINATION_ARGUMENTS)

        val fragment = fragmentClass.getConstructor().newInstance()
        fragment.arguments = fragmentArgument
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.replace(R.id.container, fragment)
        beginTransaction.commit()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        touchEvent.value = ev
        return super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
        if (!onBackPressedListener()) {
            super.onBackPressed()
        }
    }
}

private val DESTINATION_SCREEN = "目标画面"
private val DESTINATION_ARGUMENTS = "目标画面参数"

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
