package com.fz.common.coroutine

import com.fz.common.model.ApiModel
import com.fz.common.utils.getStackTraceMessage
import kotlinx.coroutines.*

/**
 * 在主线程开启协程执行[ApiModel.onRequest]
 * @author dingpeihua
 * @date 2021/2/19 17:46
 * @version 1.0
 */
suspend fun <Response> Any?.syncApi(apiDSL: ApiModel<Response>.() -> Unit) {
    ApiModel<Response>().apply(apiDSL).syncLaunch()
}

/**
 *  在IO线程开启协程执行[ApiModel.onRequest]
 * @author dingpeihua
 * @date 2021/2/19 17:46
 * @version 1.0
 */
suspend fun <Response>asyncApi(apiDSL: ApiModel<Response>.() -> Unit) {
    ApiModel<Response>().apply(apiDSL).launch()
}

/**
 * 在主线程开启协程执行[ApiModel.onRequest]
 * @author dingpeihua
 * @date 2021/2/19 17:46
 * @version 1.0
 */
fun <Response> syncApi(coroutineScope: CoroutineScope, apiDSL: ApiModel<Response>.() -> Unit) {
    ApiModel<Response>().apply(apiDSL).syncLaunch(coroutineScope)
}

/**
 * 在IO线程开启协程执行[ApiModel.onRequest]
 * @author dingpeihua
 * @date 2021/2/19 17:46
 * @version 1.0
 */
fun <Response> asyncApi(coroutineScope: CoroutineScope, apiDSL: ApiModel<Response>.() -> Unit) {
    ApiModel<Response>().apply(apiDSL).launch(coroutineScope)
}

suspend fun <T> runCatching(
    request: suspend CoroutineScope.() -> T,
    callback: (T) -> Unit,
    onError: (Throwable) -> Unit,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    complete: () -> Unit = {},
) {
    coroutineScope {
        try {
            val result = withContext(dispatcher) {
                request()
            }
            callback(result)
        } catch (e: Throwable) {
            println( e.getStackTraceMessage() )
            onError(e)
        } finally {
            try {
                cancel()
            } catch (e: Throwable) {
                println(  e.getStackTraceMessage())
            }
            complete()
        }
    }
}