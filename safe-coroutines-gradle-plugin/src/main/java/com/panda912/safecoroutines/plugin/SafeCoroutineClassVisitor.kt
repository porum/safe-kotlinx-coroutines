package com.panda912.safecoroutines.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

/**
 * Created by panda on 2021/12/24 18:48
 */
class SafeCoroutineClassVisitor(
  private val nextClassVisitor: ClassVisitor
) : ClassNode(Opcodes.ASM9) {

  override fun visitEnd() {
    super.visitEnd()

    for (methodNode in methods) {
      if (methodNode.name == "launch" && methodNode.desc == "(Lkotlinx/coroutines/CoroutineScope;Lkotlin/coroutines/CoroutineContext;Lkotlinx/coroutines/CoroutineStart;Lkotlin/jvm/functions/Function2;)Lkotlinx/coroutines/Job;") {
        for (insnNode in methodNode.instructions.toArray()) {
          if (insnNode.type == AbstractInsnNode.METHOD_INSN) {
            val methodInsnNode = insnNode as MethodInsnNode
            if (
              methodInsnNode.owner == "kotlinx/coroutines/BuildersKt__Builders_commonKt" &&
              methodInsnNode.name == "launch" &&
              methodInsnNode.desc == "(Lkotlinx/coroutines/CoroutineScope;Lkotlin/coroutines/CoroutineContext;Lkotlinx/coroutines/CoroutineStart;Lkotlin/jvm/functions/Function2;)Lkotlinx/coroutines/Job;" &&
              (methodInsnNode.previous.type == AbstractInsnNode.VAR_INSN && (methodInsnNode.previous as VarInsnNode).`var` == 3) &&
              (methodInsnNode.previous.previous.type == AbstractInsnNode.VAR_INSN && (methodInsnNode.previous.previous as VarInsnNode).`var` == 2) &&
              (methodInsnNode.previous.previous.previous.type == AbstractInsnNode.VAR_INSN && (methodInsnNode.previous.previous.previous as VarInsnNode).`var` == 1) &&
              (methodInsnNode.previous.previous.previous.previous.type == AbstractInsnNode.VAR_INSN && (methodInsnNode.previous.previous.previous.previous as VarInsnNode).`var` == 0)
            ) {
              val insnList = InsnList()
              insnList.add(TypeInsnNode(Opcodes.NEW, "kotlinx/coroutines/InjectExceptionHandler"))
              insnList.add(InsnNode(Opcodes.DUP))
              insnList.add(MethodInsnNode(Opcodes.INVOKESPECIAL, "kotlinx/coroutines/InjectExceptionHandler", "<init>", "()V", false))
              insnList.add(VarInsnNode(Opcodes.ALOAD, 1))
              insnList.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "kotlinx/coroutines/InjectExceptionHandler", "plus", "(Lkotlin/coroutines/CoroutineContext;)Lkotlin/coroutines/CoroutineContext;", false))
              methodNode.instructions.remove(methodInsnNode.previous.previous.previous)
              methodNode.instructions.insertBefore(methodInsnNode.previous.previous, insnList)
            }
          }
        }
      }
    }

    accept(nextClassVisitor)
  }
}