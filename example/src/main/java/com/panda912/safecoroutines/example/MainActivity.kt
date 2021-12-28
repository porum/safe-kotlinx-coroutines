package com.panda912.safecoroutines.example

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    findViewById<Button>(R.id.btn_internal).setOnClickListener {
      lifecycleScope.launch {
        throw RuntimeException("Aoh")
      }
    }

    findViewById<Button>(R.id.btn_external).setOnClickListener {
      lifecycleScope.launch(CustomHandler()) {
        throw RuntimeException("Aohhh")
      }
    }
  }
}

class CustomHandler : CoroutineExceptionHandler {
  override val key: CoroutineContext.Key<*>
    get() = CoroutineExceptionHandler

  override fun handleException(context: CoroutineContext, exception: Throwable) {
    Log.e("MainActivity", "handlerException", exception)
  }

}