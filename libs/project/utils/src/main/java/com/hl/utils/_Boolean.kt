package com.hl.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * @author  张磊  on  2023/01/10 at 11:17
 * Email: 913305160@qq.com
 */

@OptIn(ExperimentalContracts::class)
inline fun Boolean?.yes(block: () -> Unit): Boolean? {
	contract {
		callsInPlace(block, InvocationKind.AT_MOST_ONCE)
	}
	if (this == true) block()
	return this
}

@OptIn(ExperimentalContracts::class)
inline fun Boolean?.no(block: () -> Unit): Boolean? {
	contract {
		callsInPlace(block, InvocationKind.AT_MOST_ONCE)
	}
	if (this != true) block()
	return this
}