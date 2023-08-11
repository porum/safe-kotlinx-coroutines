# safe-kotlinx-coroutines

[![license](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![safe-kotlinx-coroutines](https://img.shields.io/badge/safe--kotlinx--coroutines-0.0.6-brightgreen.svg)](https://search.maven.org/artifact/io.github.porum/safe-kotlinx-coroutines/0.0.6/jar)
[![safe-kotlinx-coroutines-plugin](https://img.shields.io/badge/safe--kotlinx--coroutines--plugin-0.0.6-brightgreen.svg)](https://search.maven.org/artifact/io.github.porum/safe-kotlinx-coroutines-plugin/0.0.6/jar)

在 launch 协程的时候如果 context 中没有 CoroutineExceptionHandler，则自动添加添加一个默认的 CoroutineExceptionHandler 用来兜底异常。

例如下面的代码：
```kotlin
val scope = CoroutineScope(Job())
scope.launch {
  // do something
}
```

在编译期会自动在 launch 的 context 参数上添加一个 GlobalCoroutineExceptionHandler（当然，处理的是字节码，下方 kotlin 代码只是为了方便展示）：
```kotlin
val scope = CoroutineScope(Job())
scope.launch(GlobalCoroutineExceptionHandler()) {
  // do something
}
```

### 配置（以 kts 为例）：

1. 在工程根目录下的 build.gradle.kts 将插件添加到 classpath：

```kotlin
buildscript {
  repositories {
    // 👇👇👇
    mavenCentral()
  }
  dependencies {
    // 👇👇👇
    classpath("io.github.porum:safe-kotlinx-coroutines-plugin:$version")
  }
}
```

2. 在 APP module 的 build.gradle.kts 中应用插件：

```kotlin
plugins {
  id("com.android.application")
  id("kotlin-android")
  // 👇👇👇
  id("safe-kotlinx-coroutines")
}
```

3. 在 APP module 的 build.gradle.kts 中添加依赖：

```kotlin
dependencies {
  // 👇👇👇
  implementation("io.github.porum:safe-kotlinx-coroutines:$version")
}
```

### 用法：

配置完成之后不需要任何额外代码就已经可以使用了。如果想监听异常信息，可以设置全局设置一个默认的异常回调：

```kotlin
// Like Thread.setDefaultUncaughtExceptionHandler
SafeCoroutines.setDefaultCaughtExceptionHandler { context, exception ->
  context.fold("SafeCoroutines: ") { acc, element -> "$acc$element " }
    .plus(exception)
    .also(::println)
}
```

### 协程的异常处理流程

CoroutineExceptionHandler.kt:

```kotlin
public fun handleCoroutineException(context: CoroutineContext, exception: Throwable) {
    // 1. 如果 context 中存在 CoroutineExceptionHandler，则直接回调该 handler 的 handleException，并 return；
    try {
        context[CoroutineExceptionHandler]?.let {
            it.handleException(context, exception)
            return
        }
    } catch (t: Throwable) {
      	// 2. 如果 handleException 时发生异常，fallback 到全局的异常处理
        handleCoroutineExceptionImpl(context, handlerException(exception, t))
        return
    }
    // 3. 或者 context 中不存在 CoroutineExceptionHandler，也走全局的异常处理
    handleCoroutineExceptionImpl(context, exception)
}
```

CoroutineExceptionHandlerImpl.kt:

```kotlin
// 这里的 handlers 是通过 spi 加载的，在 /src/main/resources/META-INF/services/ 目录下创建 kotlinx.coroutines.CoroutineExceptionHandler 文件，然后在文件中指明自定义的全局 handler 的完整包名类名
private val handlers: List<CoroutineExceptionHandler> = ServiceLoader.load(
        CoroutineExceptionHandler::class.java,
        CoroutineExceptionHandler::class.java.classLoader
).iterator().asSequence().toList()

internal actual fun handleCoroutineExceptionImpl(context: CoroutineContext, exception: Throwable) {
    // use additional extension handlers
    for (handler in handlers) {
        try {
            handler.handleException(context, exception)
        } catch (t: Throwable) {
            // 如果 handleException 发生异常，回调当前线程的 uncaughtException
            val currentThread = Thread.currentThread()
            currentThread.uncaughtExceptionHandler.uncaughtException(currentThread, handlerException(exception, t))
        }
    }

    // 上面的 handleException 执行后，继续回调当前线程的 uncaughtException
    val currentThread = Thread.currentThread()
    // addSuppressed is never user-defined and cannot normally throw with the only exception being OOM
    // we do ignore that just in case to definitely deliver the exception
    runCatching { exception.addSuppressed(DiagnosticCoroutineContextException(context)) }
    currentThread.uncaughtExceptionHandler.uncaughtException(currentThread, exception)
}
```

### 实现方案演变：

1. 最初的做法是替换 handleCoroutineExceptionImpl 的实现，将 handleCoroutineException 中调用 handleCoroutineExceptionImpl 处替换成我们自己写的 handleCoroutineExceptionImpl 方法，接管后续的异常处理流程。此方案在 kotlinx-coroutines 1.5.2 版本上运行正常，后面升级到 1.6.1 版本后，由于 CoroutineExceptionHandler.kt 代码变动，导致字节码插桩失效，于是又兼容了一下 1.6.1版本，后面再次升级 kotlinx-coroutines，导致此方案再次失效。由于CoroutineExceptionHandler.kt 文件频繁变动，导致无法找到一个稳定的插桩时机，所以放弃此方案。代码见：https://github.com/porum/safe-kotlinx-coroutines/tree/6e11c89e5aae084046a72c1f981f16b27a909edb
2. 方案1是修改协程的异常处理流程，这样总归不太好，于是在想能否在创建协程的时候，就在 context 中 `+` 一个 handler，这样在发生异常的时候，context 中存在 handler，就会使用该 handler，并且不会执行后续的流程。所以问题就变成需要找到一个稳定的，简单的插入点。由于 CoroutineContext 类不会频繁的变动，所以想法是在 CoroutineContext 的 `+` 方法最前头，判断 context 中是否有 CoroutineExceptionHandler，如果没有则创建 CombinedContext(context, handler)，并重新指向 context（这里不能写成 context + handler，否则就死循环了），否则执行原有的逻辑。代码见：https://github.com/porum/safe-kotlinx-coroutines/tree/265cbe49ea043b4441c9a318c1ef8db88cd9ce61
3. 方案2的注入太过粗暴，只要调用 CoroutineContext `+` 就有可能被添加一个 CoroutineExceptionHandler，其实很多时候是没必要的，因为 CoroutineExceptionHandler 其实只作用于 `launch` 方式的启动的协程，并且只在根协程才有效。通过字节码判断是否是根协程基本不可能，而且如果父协程的 context 中有 CoroutineExceptionHandler，那么子协程的 context 也一定会有，因为子协程的 context = default + parent + self，异常最终都是回溯到父协程处理，所以我们只考虑在 launch 的时候注入，如果 context 不含 CoroutineExceptionHandler，就让 context = context + CoroutineExceptionHandler。