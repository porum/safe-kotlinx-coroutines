package com.panda912.safecoroutines.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class BuildersKtClassVisitor(private val nextClassVisitor: ClassVisitor) : ClassNode(Opcodes.ASM9) {

  override fun visitEnd() {
    super.visitEnd()

    methods.firstOrNull { it.name == "launch" }?.let {
      val insnList = InsnList().apply {
        add(VarInsnNode(Opcodes.ALOAD, 1))
        add(
          MethodInsnNode(
            Opcodes.GETSTATIC,
            "kotlinx/coroutines/CoroutineExceptionHandler",
            "Key",
            "Lkotlinx/coroutines/CoroutineExceptionHandler\$Key;",
            false
          )
        )
        add(
          MethodInsnNode(
            Opcodes.INVOKEINTERFACE,
            "kotlin/coroutines/CoroutineContext",
            "get",
            "(Lkotlin/coroutines/CoroutineContext\$Key;)Lkotlin/coroutines/CoroutineContext\$Element;",
            true
          )
        )
        add(JumpInsnNode(Opcodes.IFNONNULL, it.instructions.first() as LabelNode))
        add(VarInsnNode(Opcodes.ALOAD, 1))
        add(
          MethodInsnNode(
            Opcodes.INVOKESTATIC,
            "kotlinx/coroutines/GlobalCoroutineExceptionHandlerKt",
            "getGlobalCoroutineExceptionHandler",
            "()Lkotlinx/coroutines/GlobalCoroutineExceptionHandler;",
            false
          )
        )
        add(
          MethodInsnNode(
            Opcodes.INVOKEINTERFACE,
            "kotlin/coroutines/CoroutineContext",
            "plus",
            "(Lkotlin/coroutines/CoroutineContext;)Lkotlin/coroutines/CoroutineContext;",
            true
          )
        )
        add(VarInsnNode(Opcodes.ASTORE, 1))
      }

      it.instructions.insert(insnList)
    }

    accept(nextClassVisitor)
  }
}