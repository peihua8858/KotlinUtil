@file:JvmName("Utils")
@file:JvmMultifileClass
package com.fz.common.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun Any?.isNotNull(): Boolean {
    contract {
        returns(true) implies (this@isNotNull != null)
    }
    return this != null
}

@OptIn(ExperimentalContracts::class)
fun Any?.isNull(): Boolean {
    contract {
        returns(false) implies (this@isNull != null)
    }
    return this == null
}

@OptIn(ExperimentalContracts::class)
fun Any?.checkNotNull(value: Any?, msg: String): Boolean {
    contract {
        returns() implies (value != null)
    }
    return checkNotNull(value) { msg }
}

@OptIn(ExperimentalContracts::class)
fun <T : Any> checkNotNull(value: T?, lazyMessage: () -> Any): Boolean {
    contract {
        returns() implies (value != null)
    }
    if (value == null) {
        val message = lazyMessage()
        throw IllegalStateException(message.toString())
    } else {
        return false
    }
}

fun Any?.checkNotNull(msg: String?): Boolean {
    if (isNotNull()) {
        return true
    }
    throw NullPointerException(msg)
}

fun Any.rangeArray(min: Int, length: Int): Array<String?> {
    val data: Array<String?> = arrayOfNulls(length)
    for (i in 0 until length) {
        data[i] = ((min + i).toString())
    }
    return data
}