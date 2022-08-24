package com.hl.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log


inline fun <reified T> Context?.startAct(block: Intent.() -> Unit = {}) =
    this?.let { startActivity(Intent(it, T::class.java).apply(block)) }

inline fun <reified T> Activity?.startActForResult(reqCode: Int, block: Intent.() -> Unit = {}) =
    this?.let { startActivityForResult(Intent(it, T::class.java).apply(block), reqCode) }

