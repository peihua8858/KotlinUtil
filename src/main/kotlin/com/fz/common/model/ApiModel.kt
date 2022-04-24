package com.fz.common.model

import com.fz.common.utils.getStackTraceMessage
import kotlinx.coroutines.*

/**
 * api 方法封装
 * @author dingpeihua
 * @date 2021/2/19 17:45
 * @version 1.0
 */
class ApiModel<Response> {

    internal lateinit var request: suspend CoroutineScope.() -> Response

    private var onStart: (() -> Unit?)? = null

    private var onResponse: ((Response) -> Unit?)? = null

    private var onError: ((Throwable) -> Unit?)? = null

    private var onComplete: (() -> Unit?)? = null

    internal fun isOnStart(): Boolean {
        return onStart != null
    }

    internal fun isOnResponse(): Boolean {
        return onResponse != null
    }

    internal fun isOnError(): Boolean {
        return onError != null
    }

    internal fun isOnComplete(): Boolean {
        return onComplete != null
    }

    infix fun onStart(onStart: (() -> Unit?)?): ApiModel<Response> {
        this.onStart = onStart
        return this
    }

    infix fun onRequest(request: suspend CoroutineScope.() -> Response): ApiModel<Response> {
        this.request = request
        return this
    }

    infix fun onResponse(onResponse: ((Response) -> Unit)?): ApiModel<Response> {
        this.onResponse = onResponse
        return this
    }

    infix fun onError(onError: ((Throwable) -> Unit)?): ApiModel<Response> {
        this.onError = onError
        return this
    }

    infix fun onComplete(onComplete: (() -> Unit)?): ApiModel<Response> {
        this.onComplete = onComplete
        return this
    }

    internal suspend fun syncLaunch() {
        coroutineScope {
            onStart?.invoke()
            try {
                val response = request()
                onResponse?.invoke(response)
            } catch (e: Throwable) {
                println(e.getStackTraceMessage()) // 打印错误信息
                onError?.invoke(e)
            } finally {
                onComplete?.invoke()
            }
        }
    }

    internal suspend fun launch() {
        onStart?.invoke()
        try {
            val response = withContext(Dispatchers.IO) {
                request()
            }
            onResponse?.invoke(response)
        } catch (e: Throwable) {
            println(e.getStackTraceMessage()) // 打印错误信息
            onError?.invoke(e)
        } finally {
            onComplete?.invoke()
        }
    }

    internal fun syncLaunch(viewModelScope: CoroutineScope) {
        viewModelScope.launch(Dispatchers.Main) {
            onStart?.invoke()
            try {
                val response = request()
                onResponse?.invoke(response)
            } catch (e: Throwable) {
                println(e.getStackTraceMessage()) // 打印错误信息
                onError?.invoke(e)
            } finally {
                onComplete?.invoke()
            }
        }
    }

    internal fun launch(coroutineScope: CoroutineScope) {
        coroutineScope.launch(Dispatchers.Main) {
            onStart?.invoke()
            try {
                val response = withContext(Dispatchers.IO) {
                    request()
                }
                onResponse?.invoke(response)
            } catch (e: Throwable) {
                println(e.getStackTraceMessage()) // 打印错误信息
                onError?.invoke(e)
            } finally {
                onComplete?.invoke()
            }
        }
    }

    fun invokeOnError(e: Throwable) {
        this.onError?.invoke(e)
    }

    fun invokeOnResponse(response: Response) {
        this.onResponse?.invoke(response)
    }

    fun invokeOnStart() {
        this.onStart?.invoke()
    }

    fun invokeOnComplete() {
        this.onComplete?.invoke()
    }
}