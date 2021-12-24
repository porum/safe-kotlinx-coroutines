package com.panda912.safecoroutines.plugin

import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

/**
 * Created by panda on 2021/12/24 16:42
 */
interface ParametersImpl : InstrumentationParameters {
  @get:Input
  val intValue: Property<Int>

  @get:Internal
  val listOfStrings: ListProperty<String>
}