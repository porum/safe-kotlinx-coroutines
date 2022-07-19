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
        } ?: break@outer

        var startIndex = -1
        if (ifnonnull.next.opcode == Opcodes.GOTO) { // coroutine version 1.5.2
          startIndex = instructions.indexOf((ifnonnull.next as JumpInsnNode).label)
        } else if (ifnonnull.next.opcode == Opcodes.POP && ifnonnull.next.next.opcode == Opcodes.GOTO) { // coroutine version 1.6.1
          startIndex = instructions.indexOf((ifnonnull.next.next as JumpInsnNode).label)
        }

        if (startIndex == -1) {
          break@outer
        }

        var endIndex = -1
        for (index in startIndex + 1 until instructions.size) {
          val insnNode = instructions[index]
          if (insnNode.type == AbstractInsnNode.LABEL) {
            endIndex = index
            break
          }
        }

        if (endIndex == -1) {
          break@outer
        }

        for (index in startIndex + 1 until endIndex) {
          val insnNode = instructions[index]
          if (
            insnNode.type == AbstractInsnNode.METHOD_INSN &&
            insnNode.opcode == Opcodes.INVOKESTATIC &&
            (insnNode as MethodInsnNode).owner == "kotlinx/coroutines/CoroutineExceptionHandlerImplKt" &&
            insnNode.name == "handleCoroutineExceptionImpl" &&
            insnNode.desc == "(Lkotlin/coroutines/CoroutineContext;Ljava/lang/Throwable;)V"
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

    accept(nextClassVisitor)
  }
}