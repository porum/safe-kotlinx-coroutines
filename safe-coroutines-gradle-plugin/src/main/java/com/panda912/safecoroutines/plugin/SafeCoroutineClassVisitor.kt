package com.panda912.safecoroutines.plugin

import com.android.build.api.instrumentation.ClassContext
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

/**
 * Created by panda on 2021/12/24 18:48
 */
class SafeCoroutineClassVisitor(
  private val classContext: ClassContext,
  private val nextClassVisitor: ClassVisitor
) : ClassNode(Opcodes.ASM9) {

  override fun visitEnd() {
    super.visitEnd()
    accept(nextClassVisitor)
  }
}