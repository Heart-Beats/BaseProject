package com.hl.uikit.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.hl.uikit.databinding.UikitInputDialogFragmentBinding
import com.hl.viewbinding.inflate

class InputDialogFragment : AlertDialogFragment() {

    private val viewBind by inflate<UikitInputDialogFragmentBinding>()

    private var mInputHint: CharSequence? = null

    var inputHint: CharSequence? = null
        set(value) {
            field = value
            mInputHint = value

            if (!isRootViewCreated()) return

            viewBind.etInput.hint = value ?: ""
        }
        get() {
            return viewBind.etInput?.hint
        }

    private var mInputText: CharSequence? = null

    var inputText: CharSequence? = null
        set(value) {
            field = value
            mInputText = field

            if (!isRootViewCreated()) return

            viewBind.etInput.setText(value ?: "")
        }
        get() {
            return viewBind.etInput.text
        }

    var inputChangedListener: ((DialogFragment, Editable?) -> Unit)? = null

    private var mHintText: CharSequence? = null

    var hintText: CharSequence? = null
        set(value) {
            field = value
            mHintText = value

            if (!isRootViewCreated()) return

            viewBind.tvHint.text = value ?: ""
        }
        get() {
            return viewBind.tvHint.text
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCustomView(viewBind.root)
        viewBind.etInput.hint = mInputHint ?: ""
        viewBind.etInput.setText(mInputText ?: "")
        viewBind.tvHint.text = mHintText ?: ""
        viewBind.etInput.addTextChangedListener { editable ->
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