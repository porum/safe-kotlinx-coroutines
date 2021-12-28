package com.panda912.safecoroutines.example

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

/**
 * Created by panda on 2021/12/28 10:53
 */
class GlobalCoroutineExceptionHandler : CoroutineExceptionHandler {
  override val key: CoroutineContext.Key<*>
    get() = CoroutineExceptionHandler

  override fun handleException(context: CoroutineContext, exception: Throwable) {
    Log.e("GlobalExceptionHandler", "handleException", exception)
  }
}