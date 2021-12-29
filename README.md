# safe-kotlinx-coroutines

这是一个防止因协程发生异常而导致应用崩溃的一个库。

我们知道给协程添加 `CoroutineExceptionHandler` 有两种方式：

1. 在 launch 的时候显式指定一个 `CoroutineExceptionHandler`；
2. 在 resources/META-INF/services/ 目录下创建 `kotlinx.coroutines.CoroutineExceptionHandler` 文件，文件中指定一个全局的 `CoroutineExceptionHandler` 实现类。

协程的异常处理流程如下：

![](./assets/coroutine-exception-handler.png)

所以如果我们想捕获协程的异常且让应用不崩溃，有两种方案：

1. 每次 launch 的时候显式指定一个 `CoroutineExceptionHandler`；
2. 拦截 thread 的 `UncaughtExceptionHandler#uncaughtException`，处理相应的逻辑 。

本库是根据方案1通过 AOP 的方式在 launch 的时候自动注入一个 `CoroutineExceptionHandler`。

选择合适的插入点：

我们先启动一个协程：

```kotlin
lifecycleScope.launch {}
```

反编译查看生成的 class 文件：

```java
Job unused = BuildersKt__Builders_commonKt.launch$default(LifecycleOwnerKt.getLifecycleScope(this), null, null, new MainActivity$launchCoroutineScope$1(null), 3, null);
```

跟进 `BuildersKt__Builders_commonKt.launch$default`：

```java
public static /* synthetic */ Job launch$default(CoroutineScope coroutineScope, CoroutineContext coroutineContext, CoroutineStart coroutineStart, Function2 function2, int i, Object obj) {
    if ((i & 1) != 0) {
        coroutineContext = EmptyCoroutineContext.INSTANCE;
    }
    if ((i & 2) != 0) {
        coroutineStart = CoroutineStart.DEFAULT;
    }
    return BuildersKt.launch(coroutineScope, coroutineContext, coroutineStart, function2);
}
```
跟进 `BuildersKt.launch`：

```java
public static final Job launch(CoroutineScope $this$launch, CoroutineContext context, CoroutineStart start, Function2<? super CoroutineScope, ? super Continuation<? super Unit>, ? extends Object> function2) {
        return BuildersKt__Builders_commonKt.launch($this$launch, context, start, function2);
}
```

发现 `BuilderKt.launch` 方法非常简单，没有任何逻辑，就是调用 `BuildersKt__Builders_commonKt.launch`，所以我们选择在此处插桩，处理字节码会比较方便。

最终 `BuildersKt.launch` 会被处理为：

```java
public static final Job launch(CoroutineScope $this$launch, CoroutineContext context, CoroutineStart start, Function2<? super CoroutineScope, ? super Continuation<? super Unit>, ? extends Object> function2) {
    return BuildersKt__Builders_commonKt.launch($this$launch, new InjectExceptionHandler().plus(context), start, function2);
}
```