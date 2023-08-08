package kotlinx.coroutines

import kotlinx.coroutines.SafeCoroutines.defaultHandler
import kotlin.coroutines.CoroutineContext


/**
 * Created by panda on 2023/8/8 15:38
 */
internal class GlobalCoroutineExceptionHandler : CoroutineExceptionHandler {

  override val key: CoroutineContext.Key<*>
    get() = CoroutineExceptionHandler

  override fun handleException(context: CoroutineContext, exception: Throwable) {
    defaultHandler?.invoke(context, exception)
  }
}

internal val globalCoroutineExceptionHandler = GlobalCoroutineExceptionHandler()


////////////////////////////////////////////////////////////////////////////////////////////////////


typealias CaughtExceptionHandler = (context: CoroutineContext, exception: Throwable) -> Unit

object SafeCoroutines {

  internal var defaultHandler: CaughtExceptionHandler? = null

  @JvmStatic
  fun setDefaultCaughtExceptionHandler(handler: CaughtExceptionHandler) {
    defaultHandler = handler
  }
}