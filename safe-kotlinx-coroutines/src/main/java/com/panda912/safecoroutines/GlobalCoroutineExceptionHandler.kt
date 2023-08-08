package com.panda912.safecoroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext


/**
 * Created by panda on 2023/8/8 15:38
 */
internal class GlobalCoroutineExceptionHandler : CoroutineExceptionHandler {

  override val key: CoroutineContext.Key<*>
    get() = CoroutineExceptionHandler

  override fun handleException(context: CoroutineContext, exception: Throwable) {
  }
}

internal val globalHandler = GlobalCoroutineExceptionHandler()