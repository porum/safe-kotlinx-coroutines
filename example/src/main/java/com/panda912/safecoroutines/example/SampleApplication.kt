package com.panda912.safecoroutines.example

import android.app.Application
import com.panda912.safecoroutines.SafeCoroutines
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

/**
 * Created by panda on 2021/12/27 19:54
 */
class SampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    SafeCoroutines.handler = object : CoroutineExceptionHandler {
      override val key: CoroutineContext.Key<*>
        get() = CoroutineExceptionHandler

      override fun handleException(context: CoroutineContext, exception: Throwable) {
        System.err.println(exception)
      }
    }
  }
}