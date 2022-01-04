package com.panda912.safecoroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

/**
 * Created by panda on 2021/12/28 8:53.
 */
object SafeCoroutines {

  @JvmStatic
  var handler: CoroutineExceptionHandler = object : CoroutineExceptionHandler {
    override val key: CoroutineContext.Key<*>
      get() = CoroutineExceptionHandler

    override fun handleException(context: CoroutineContext, exception: Throwable) {
      println(exception)
    }
  }

  internal fun handleException(context: CoroutineContext, exception: Throwable) {
    handler.handleException(context, exception)
  }
}