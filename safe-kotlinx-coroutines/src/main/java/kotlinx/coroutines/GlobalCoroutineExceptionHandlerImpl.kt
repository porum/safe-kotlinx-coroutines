package kotlinx.coroutines

import com.panda912.safecoroutines.SafeCoroutines
import kotlin.coroutines.CoroutineContext

/**
 * Created by panda on 2021/12/29 12:51
 */

internal class GlobalCoroutineExceptionHandler : CoroutineExceptionHandler {

  override val key: CoroutineContext.Key<*>
    get() = CoroutineExceptionHandler

  override fun handleException(context: CoroutineContext, exception: Throwable) {
    SafeCoroutines.handleException(context, exception)
  }
}

private val globalHandler: CoroutineExceptionHandler = GlobalCoroutineExceptionHandler()

internal fun handleCoroutineExceptionImpl(context: CoroutineContext, exception: Throwable) {
  try {
    globalHandler.handleException(context, exception)
  } catch (t: Throwable) {
    // Use thread's handler if custom handler failed to handle exception
    val currentThread = Thread.currentThread()
    currentThread.uncaughtExceptionHandler.uncaughtException(currentThread, handlerException(exception, t))
  }
}

internal fun handlerException(originalException: Throwable, thrownException: Throwable): Throwable {
  if (originalException === thrownException) return originalException
  return RuntimeException("Exception while trying to handle coroutine exception", thrownException).apply {
    addSuppressed(originalException)
  }
}