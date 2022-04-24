@file:JvmName("AnyUtils")
@file:JvmMultifileClass

package com.fz.common.utils

import java.io.*
import java.lang.reflect.Field
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

/**
 * 对象深度拷贝
 * @author dingpeihua
 * @date 2021/3/2 15:29
 * @version 1.0
 */
fun <T : Serializable> T.deepCloneSerializable(): T? {
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
 * 将[source]源数据对象（被复制对象）的不为空的数据值拷贝到[this]目标对象中对应的属性值
 * [this] 是目标对象，也是返回对象
 * @param source 源对象
 * @return [this]
 * @author dingpeihua
 * @date 2019/3/21 09:17
 * @version 1.0
 */
fun <T : Any> T.copyField(source: T): T {
    val sourceFields: Array<Field> = source.javaClass.declaredFields
    val targetFields: Array<Field> = javaClass.declaredFields
    for (i in sourceFields.indices) {
        try {
            val sourceField = sourceFields[i]
            val targetField = targetFields[i]
            sourceField.isAccessible = true
            targetField.isAccessible = true
            val sourceValue = sourceField[source]
            val targetValue = targetField[this]
            if (sourceValue != null) {
                if (sourceValue.checkData() || targetValue == null) {
                    if ("" != sourceValue) {
                        //不替换整型基本类型为0的数据
                        if (sourceField.type == Int::class.javaPrimitiveType && 0 == sourceValue as Int) {
                            continue
                        }
                        targetField[this] = sourceValue
                    }
                } else {
                    targetValue.copyField(sourceValue)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return this
}

/**
 * 检查当前类型是否是基本数据类型（包括[Date]、
 * [BigDecimal]、[BigInteger]及[Character]）
 *
 * @author dingpeihua
 * @date 2019/4/1 14:29
 * @version 1.0
 */
fun Any?.checkData(): Boolean {
    return this != null && (this is String || this is Int || this is Byte
            || this is Long || this is Double || this is Float
            || this is Char || this is Short || this is Boolean
            || this is BigDecimal || this is BigInteger || this is Date)
}