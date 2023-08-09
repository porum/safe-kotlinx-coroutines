package com.panda912.safecoroutines.example

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    findViewById<Button>(R.id.button1).setOnClickListener {
      lifecycleScope.launch {
        throw RuntimeException("Aoh")
      }
    }

    findViewById<Button>(R.id.button2).setOnClickListener {
      lifecycleScope.launch(object : CoroutineExceptionHandler {
        override val key: CoroutineContext.Key<*>
          get() = CoroutineExceptionHandler

        override fun handleException(context: CoroutineContext, exception: Throwable) {
          println("handleException: $exception")
        }
      }) {
        throw RuntimeException("Aoh")
      }
    }
  }
}