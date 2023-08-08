package com.panda912.safecoroutines.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.TypeInsnNode
import org.objectweb.asm.tree.VarInsnNode

/**
 * Created by panda on 2023/8/8 15:38
 */
class CoroutineContextClassVisitor(
  private val nextClassVisitor: ClassVisitor
) : ClassNode(Opcodes.ASM9) {
  override fun visitEnd() {
    super.visitEnd()

    methods.firstOrNull {
      it.name == "plus" &&
          it.desc == "(Lkotlin/coroutines/CoroutineContext;Lkotlin/coroutines/CoroutineContext;)Lkotlin/coroutines/CoroutineContext;" &&
          it.access == Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC
    }?.let { method ->
      val instructions: Array<AbstractInsnNode> = method.instructions.toArray()

      val checkNotNull = instructions.firstOrNull { insnNode ->
        insnNode.type == AbstractInsnNode.METHOD_INSN &&
            insnNode.opcode == Opcodes.INVOKESTATIC &&
            (insnNode as MethodInsnNode).owner == "kotlin/jvm/internal/Intrinsics" &&
            insnNode.name == "checkNotNullParameter" &&
            insnNode.desc == "(Ljava/lang/Object;Ljava/lang/String;)V"
      }

      // insert code: context = CombineContext(context, globalHandler)
      val insnList = InsnList().apply {
        add(TypeInsnNode(Opcodes.NEW, "kotlin/coroutines/CombinedContext"))
        add(InsnNode(Opcodes.DUP))
        add(VarInsnNode(Opcodes.ALOAD, 1))
        add(
          MethodInsnNode(
            Opcodes.INVOKESTATIC,
            "com/panda912/safecoroutines/GlobalCoroutineExceptionHandlerKt",
            "getGlobalHandler",
            "()Lcom/panda912/safecoroutines/GlobalCoroutineExceptionHandler;"
          )
        )
        add(TypeInsnNode(Opcodes.CHECKCAST, "kotlin/coroutines/CoroutineContext\$Element"))
        add(
          MethodInsnNode(
            Opcodes.INVOKESPECIAL,
            "kotlin/coroutines/CombinedContext",
            "<init>",
            "(Lkotlin/coroutines/CoroutineContext;Lkotlin/coroutines/CoroutineContext\$Element;)V"
          )
        )
        add(VarInsnNode(Opcodes.ASTORE, 0))
      }

      if (checkNotNull != null) {
        method.instructions.insert(checkNotNull, insnList)
      } else {
        method.instructions.insert(insnList)
      }
    }

    accept(nextClassVisitor)
  }
}