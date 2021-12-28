package kotlinx.coroutines

import com.panda912.safecoroutines.SafeCoroutines
import kotlin.coroutines.CoroutineContext

/**
 * Created by panda on 2021/12/28 8:52.
 */
internal class InjectExceptionHandler : CoroutineExceptionHandler {

  override val key: CoroutineContext.Key<*>
    get() = CoroutineExceptionHandler

  override fun handleException(context: CoroutineContext, exception: Throwable) {
    SafeCoroutines.handleException(context, exception)
  }
}