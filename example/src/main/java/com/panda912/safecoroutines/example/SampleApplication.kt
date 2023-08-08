package com.panda912.safecoroutines.example

import android.app.Application
import kotlinx.coroutines.CaughtExceptionHandler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SafeCoroutines
import kotlin.coroutines.CoroutineContext

/**
 * Created by panda on 2021/12/27 19:54
 */
class SampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    SafeCoroutines.setDefaultCaughtExceptionHandler { context, exception ->
      System.err.println(exception)
    }
  }
}