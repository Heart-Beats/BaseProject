package com.hl.uikit.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.uikit_input_dialog_fragment.*
import com.hl.uikit.R

class InputDialogFragment : AlertDialogFragment() {
    private var mInputHint: CharSequence? = null
    var inputHint: CharSequence? = null
        set(value) {
            field = value
            mInputHint = value
            etInput?.hint = value ?: ""
        }
        get() {
            return etInput?.hint
        }
    private var mInputText: CharSequence? = null
    var inputText: CharSequence? = null
        set(value) {
            field = value
            mInputText = field
            etInput?.setText(value ?: "")
        }
        get() {
            return etInput?.text
        }
    var inputChangedListener: ((DialogFragment, Editable?) -> Unit)? = null
    private var mHintText: CharSequence? = null
    var hintText: CharSequence? = null
        set(value) {
            field = value
            mHintText = value
            tvHint?.text = value ?: ""
        }
        get() {
            return tvHint?.text
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCustomView(R.layout.uikit_input_dialog_fragment)
        etInput?.hint = mInputHint ?: ""
        etInput?.setText(mInputText ?: "")
        tvHint?.text = mHintText ?: ""
        etInput?.addTextChangedListener { editable ->
            inputChangedListener?.invoke(this, editable)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun dismiss() {
                val context = context
                val view = view
                if (view == null) {
                    super.dismiss()
                    return
                }
                val manager =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                manager?.hideSoftInputFromWindow(view.windowToken, 0)
                super.dismiss()
            }
        }
    }
}