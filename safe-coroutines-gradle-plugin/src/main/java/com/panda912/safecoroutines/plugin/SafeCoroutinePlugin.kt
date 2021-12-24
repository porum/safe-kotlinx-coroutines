package com.panda912.safecoroutines.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
import com.android.build.api.instrumentation.InstrumentationParameters
import com.android.build.api.instrumentation.InstrumentationScope.PROJECT
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationVariant
import com.android.build.api.variant.LibraryVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.create
import org.objectweb.asm.ClassVisitor

/**
 * Created by panda on 2021/12/24 13:16
 */
class SafeCoroutinePlugin : Plugin<Project> {

  override fun apply(project: Project) {
    val extension = project.extensions.create<SafeCoroutineExtension>("safeCoroutine")
    val androidExtension = project.extensions.getByType(AndroidComponentsExtension::class.java)
    androidExtension.onVariants { variant ->
      when (variant) {
        is ApplicationVariant -> {
          variant.transformClassesWith(SafeCoroutineVisitorFactory::class.java, PROJECT) {
            it.excludes.set(extension.excludes)
          }
          variant.setAsmFramesComputationMode(COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS)
        }
        is LibraryVariant -> {}
        else -> {}
      }
    }
  }
}

interface ExcludeParams : InstrumentationParameters {
  @get:Input
  val excludes: Property<List<String>>
}

abstract class SafeCoroutineVisitorFactory : AsmClassVisitorFactory<ExcludeParams> {

  override fun createClassVisitor(
    classContext: ClassContext,
    nextClassVisitor: ClassVisitor
  ): ClassVisitor {
    return SafeCoroutineClassVisitor(classContext, nextClassVisitor)
  }

  override fun isInstrumentable(classData: ClassData): Boolean {
    return !parameters.get().excludes.get().contains(classData.className)
  }
}