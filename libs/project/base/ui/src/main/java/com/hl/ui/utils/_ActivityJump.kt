package com.hl.ui.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment


inline fun <reified T> Context.startAct(block: Intent.() -> Unit = {}) =
    startActivity(Intent(this, T::class.java).apply(block))

inline fun <reified T> Fragment.startAct(block: Intent.() -> Unit = {}) =
    requireActivity().startAct<T>(block)


inline fun <reified T> Activity.startActForResult(reqCode: Int, block: Intent.() -> Unit = {}) =
    startActivityForResult(Intent(this, T::class.java).apply(block), reqCode)

inline fun <reified T> Fragment.startActForResult(reqCode: Int, block: Intent.() -> Unit = {}) =
    startActivityForResult(Intent(requireContext(), T::class.java).apply(block), reqCode)