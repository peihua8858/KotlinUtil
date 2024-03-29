@file:JvmName("CollectionsUtil")
@file:JvmMultifileClass

package com.fz.common.collections

import java.io.*
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun <T> Collection<T>?.isNonEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNonEmpty != null)
    }
    return this != null && size > 0 && isNotEmpty()
}

/**
 * 集合转成String输出
 *
 * @param <T>  泛型参数，集合中放置的元素数据类型
 * @return 如果集合不为空返回输出字符串，否则返回"null"
 */
@Deprecated("please use splicing method.", ReplaceWith("splicing()"))
fun <T> Collection<T>.string(): String {
    return splicing()
}

/**
 * 集合转成String输出
 *
 * @param <T>  泛型参数，集合中放置的元素数据类型
 * @return 如果集合不为空返回输出字符串，否则返回"null"
 */
fun <T> Collection<T>.splicing(): String {
    return splicing(",")
}

/**
 * 集合转成String输出
 *
 * @param list      集合
 * @param <T>       泛型参数，集合中放置的元素数据类型
 * @param separator 分隔符
 * @return 如果集合不为空返回输出字符串，否则返回"null"
 */
@Deprecated("please use splicing method.", ReplaceWith("splicing(separator)"))
fun <T> Collection<T>.toString(separator: String): String {
    return splicing(separator)
}

/**
 * 集合转成String输出
 *
 * @param <T>       泛型参数，集合中放置的元素数据类型
 * @param separator 分隔符
 * @return 如果集合不为空返回输出字符串，否则返回"null"
 */
fun <T> Collection<T>.splicing(separator: String): String {
    val sb = StringBuilder()
    for ((index, item) in this.withIndex()) {
        if (item != null) {
            sb.append(item)
            if (index < size - 1) {
                sb.append(separator)
            }
        }
    }
    return sb.toString()
}

/**
 * 集合转成String输出
 *
 * @param <T>       泛型参数，集合中放置的元素数据类型
 * @param separator 分隔符
 * @return 如果集合不为空返回输出字符串，否则返回"null"
 */
@Deprecated("please use splicing method.", ReplaceWith("splicing(separator, action)"))
inline fun <T, R> Collection<T>.toString(separator: String, action: (T) -> R): String {
    return splicing(separator, action)
}

/**
 * 集合转成String输出
 *
 * @param <T>       泛型参数，集合中放置的元素数据类型
 * @param separator 分隔符
 * @return 如果集合不为空返回输出字符串，否则返回"null"
 */
inline fun <T, R> Collection<T>.splicing(separator: String, action: (T) -> R): String {
    val sb = StringBuilder()
    for ((index, item) in this.withIndex()) {
        val result = action(item)
        if (result != null) {
            sb.append(result)
            if (index < size - 1) {
                sb.append(separator)
            }
        }
    }
    return sb.toString()
}

/**
 * 对象深度拷贝
 * [T] extend [Serializable]
 * @author dingpeihua
 * @date 2021/3/2 15:29
 * @version 1.0
 */
fun <T : Serializable> Collection<T>.deepClone(): Collection<T>? {
    try {
        ByteArrayOutputStream().use { byteOut ->
            ObjectOutputStream(byteOut).use { out ->
                out.writeObject(this)
                out.flush()
                ObjectInputStream(ByteArrayInputStream(byteOut.toByteArray())).use { input ->
                    return this::class.java.cast(input.readObject())
                }
            }
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        return null
    }
}

fun <T> Collection<T>?.toArrayList(): ArrayList<T> {
    if (this == null) {
        return arrayListOf()
    }
    return ArrayList(this)
}

/**
 * 返回匹配给定 [predicate] 的第一个元素，如果未找到元素，则返回默认返回索引[0]元素。
 * @author dingpeihua
 * @date 2022/4/19 14:26
 * @version 1.0
 */
fun <T> MutableList<T>.findFirst(predicate: (T) -> Boolean): T {
    for (item in this) {
        if (predicate(item)) {
            return item
        }
    }
    return this[0]
}