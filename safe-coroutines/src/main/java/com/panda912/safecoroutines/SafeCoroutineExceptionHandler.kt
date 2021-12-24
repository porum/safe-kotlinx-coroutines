package com.panda912.safecoroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

/**
 * Created by panda on 2021/12/24 11:16
 */
class SafeCoroutineExceptionHandler : CoroutineExceptionHandler {

  override val key: CoroutineContext.Key<*>
    get() = CoroutineExceptionHandler

  override fun handleException(context: CoroutineContext, exception: Throwable) {
    ExceptionHandler.handleException(context, exception)
  }
}

object ExceptionHandler {

  @JvmStatic
  var handler: IExceptionHandler = IExceptionHandler.DEFAULT

  fun handleException(context: CoroutineContext, exception: Throwable) {
    handler.handleException(context, exception)
  }
}

fun interface IExceptionHandler {
  fun handleException(context: CoroutineContext, exception: Throwable)

  companion object DEFAULT : IExceptionHandler {
    override fun handleException(context: CoroutineContext, exception: Throwable) {
      println(exception)
    }
  }
}