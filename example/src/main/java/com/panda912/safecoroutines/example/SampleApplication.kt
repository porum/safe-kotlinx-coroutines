package com.panda912.safecoroutines.example

import android.app.Application
import kotlinx.coroutines.SafeCoroutines

/**
 * Created by panda on 2021/12/27 19:54
 */
class SampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()

SafeCoroutines.setDefaultCaughtExceptionHandler { context, exception ->
  context.fold("SafeCoroutines: ") { acc, element -> "$acc$element " }
    .plus(exception)
    .also(::println)
}
  }
}