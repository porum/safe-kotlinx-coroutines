package com.panda912.safecoroutines.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.JumpInsnNode
import org.objectweb.asm.tree.MethodInsnNode

/**
 * Created by panda on 2021/12/24 18:48
 */
class SafeCoroutineClassVisitor(
  private val nextClassVisitor: ClassVisitor
) : ClassNode(Opcodes.ASM9) {

  override fun visitEnd() {
    super.visitEnd()

    outer@ for (methodNode in methods) {
      if (methodNode.name == "handleCoroutineException" && methodNode.desc == "(Lkotlin/coroutines/CoroutineContext;Ljava/lang/Throwable;)V") {
        val instructions = methodNode.instructions.toArray()

        val ifnonnull = instructions.find {
          it.type == AbstractInsnNode.JUMP_INSN && it.opcode == Opcodes.IFNONNULL
        }

        if (ifnonnull != null && ifnonnull.next.type == AbstractInsnNode.JUMP_INSN && ifnonnull.next.opcode == Opcodes.GOTO) {
          val startIndex = instructions.indexOf((ifnonnull.next as JumpInsnNode).label)
          for (index in instructions.indices) {
            val insnNode = instructions[index]
            if (
              insnNode.type == AbstractInsnNode.METHOD_INSN &&
              insnNode.opcode == Opcodes.INVOKESTATIC &&
              (insnNode as MethodInsnNode).owner == "kotlinx/coroutines/CoroutineExceptionHandlerImplKt" &&
              insnNode.name == "handleCoroutineExceptionImpl" &&
              insnNode.desc == "(Lkotlin/coroutines/CoroutineContext;Ljava/lang/Throwable;)V" &&
              index > startIndex
            ) {
              val replaced = MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "kotlinx/coroutines/GlobalCoroutineExceptionHandlerImplKt",
                insnNode.name,
                insnNode.desc
              )
              methodNode.instructions.set(insnNode, replaced)
              break@outer
            }
          }
        }
      }
    }

    accept(nextClassVisitor)
  }
}