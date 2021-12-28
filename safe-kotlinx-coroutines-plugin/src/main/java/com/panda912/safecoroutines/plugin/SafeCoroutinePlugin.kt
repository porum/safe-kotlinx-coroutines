package com.panda912.safecoroutines.plugin

import com.android.build.api.instrumentation.*
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationVariant
import com.android.build.api.variant.LibraryVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassVisitor

/**
 * Created by panda on 2021/12/24 13:16
 */
@Suppress("UnstableApiUsage")
class SafeCoroutinePlugin : Plugin<Project> {

  override fun apply(project: Project) {
    val androidExtension = project.extensions.getByType(AndroidComponentsExtension::class.java)
    androidExtension.onVariants { variant ->
      when (variant) {
        is ApplicationVariant -> {
          variant.transformClassesWith(
            SafeCoroutineVisitorFactory::class.java,
            InstrumentationScope.ALL
          ) {}
          variant.setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS)
        }
        is LibraryVariant -> {}
        else -> {}
      }
    }
  }
}

@Suppress("UnstableApiUsage")
abstract class SafeCoroutineVisitorFactory :
  AsmClassVisitorFactory<InstrumentationParameters.None> {

  override fun createClassVisitor(
    classContext: ClassContext,
    nextClassVisitor: ClassVisitor
  ): ClassVisitor {
    return SafeCoroutineClassVisitor(nextClassVisitor)
  }

  override fun isInstrumentable(classData: ClassData): Boolean {
    return classData.className == "kotlinx.coroutines.BuildersKt"
  }
}