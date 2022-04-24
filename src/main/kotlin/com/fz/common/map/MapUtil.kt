@file:JvmName("MapUtil")
@file:JvmMultifileClass

package com.fz.common.map

import com.fz.common.text.deleteEndChar
import java.io.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun <K, V> Map<K, V>?.isNonEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNonEmpty != null)
    }
    return this != null && size > 0 && isNotEmpty()
}

/**
 * map<K,Collection<V> 浅拷贝
 * @author dingpeihua
 * @date 2021/2/26 11:00
 * @version 1.0
 */
fun <K, V> Map<K, MutableList<V>>?.copyOfMapList(): MutableMap<K, MutableList<V>> {
    val cloneMap = mutableMapOf<K, MutableList<V>>()
    if (this.isNullOrEmpty()) {
        return cloneMap
    }
    val it = this.iterator()
    while (it.hasNext()) {
        val entry = it.next()
        cloneMap[entry.key] = java.util.ArrayList(entry.value)
    }
    return cloneMap
}

/**
 * 对象深度拷贝
 * [T] extend [Serializable]
 * @author dingpeihua
 * @date 2021/3/2 15:29
 * @version 1.0
 */
fun <K : Any, V : Any> Map<K, V>.deepClone(): Map<K, V>? {
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


/**
 * 返回匹配给定 [predicate] 的第一个元素，如果没有找到这样的元素，则返回 `null`。
 * @param <T>       泛型参数，集合中放置的元素数据类型
 * @param predicate 给定条件操作符
 * @return 如果集合不为空返回输出字符串，否则返回"null"
 */
inline fun <K, V> Map<K, V>.findKey(predicate: (Map.Entry<K, V>) -> Boolean): K? {
    for (item in this) {
        if (predicate(item)) {
            return item.key
        }
    }
    return null
}

/**
 * 返回匹配给定 [predicate] 的所有元素列表，如果没有找到这样的元素，则返回空列表。
 * @param <T>       泛型参数，集合中放置的元素数据类型
 * @param predicate 给定条件操作符
 * @return 如果集合不为空返回输出字符串，否则返回"null"
 */
inline fun <K, V> Map<K, V>.findKeys(predicate: (Map.Entry<K, V>) -> Boolean): MutableList<K> {
    return findTo(mutableListOf(), predicate)
}
/**
 * 返回匹配给定 [predicate] 的所有元素列表，如果没有找到这样的元素，则返回空列表。
 * @param <T>       泛型参数，集合中放置的元素数据类型
 * @param destination 目标列表
 * @param predicate 给定条件操作符
 * @author dingpeihua
 * @date 2022/4/11 10:20
 * @version 1.0
 */
inline fun <K, V, M : MutableList<in K>> Map<out K, V>.findTo(
    destination: M,
    predicate: (Map.Entry<K, V>) -> Boolean
): M {
    for (element in this) {
        if (predicate(element)) {
            destination.add(element.key)
        }
    }
    return destination
}

/**
 * 集合转成String输出
 *
 * @param <T>       泛型参数，集合中放置的元素数据类型
 * @return 如果集合不为空返回输出字符串，否则返回"null"
 */
inline fun <K, V, R> Map<K, V>.splicing(action: (Map.Entry<K, V>) -> R): String {
    return splicing(",", action)
}

/**
 * 集合转成String输出
 *
 * @param <T>       泛型参数，集合中放置的元素数据类型
 * @param separator 分隔符
 * @return 如果集合不为空返回输出字符串，否则返回"null"
 */
inline fun <K, V, R> Map<K, V>.splicing(separator: String, action: (Map.Entry<K, V>) -> R): String {
    val sb = StringBuilder()
    for (item in this) {
        sb.append(action(item)).append(separator)
    }
    return sb.deleteEndChar(separator).toString()
}