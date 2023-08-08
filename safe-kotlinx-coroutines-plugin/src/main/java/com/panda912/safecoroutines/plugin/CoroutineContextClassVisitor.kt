package com.panda912.safecoroutines.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.JumpInsnNode
import org.objectweb.asm.tree.LabelNode
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

      val checkNotNullParameterInsn = instructions.firstOrNull { insnNode ->
        insnNode.type == AbstractInsnNode.METHOD_INSN &&
            insnNode.opcode == Opcodes.INVOKESTATIC &&
            (insnNode as MethodInsnNode).owner == "kotlin/jvm/internal/Intrinsics" &&
            insnNode.name == "checkNotNullParameter" &&
            insnNode.desc == "(Ljava/lang/Object;Ljava/lang/String;)V"
      }

      val labelNode = instructions[instructions.indexOf(checkNotNullParameterInsn) + 1] as LabelNode

      // insert the following code at beginning of `plus` method:
      // val handler = this[CoroutineExceptionHandler]
      // if (handler == null) {
      //   this = CombinedContext(this, globalCoroutineExceptionHandler)
      // }
      val insnList = InsnList().apply {
        add(VarInsnNode(Opcodes.ALOAD, 0))
        add(
          MethodInsnNode(
            Opcodes.GETSTATIC,
            "kotlinx/coroutines/CoroutineExceptionHandler",
            "Key",
            "Lkotlinx/coroutines/CoroutineExceptionHandler\$Key;"
          )
        )
        add(TypeInsnNode(Opcodes.CHECKCAST, "kotlin/coroutines/CoroutineContext\$Key"))
        add(
          MethodInsnNode(
            Opcodes.INVOKEINTERFACE,
            "kotlin/coroutines/CoroutineContext",
            "get",
            "(Lkotlin/coroutines/CoroutineContext\$Key;)Lkotlin/coroutines/CoroutineContext\$Element;"
          )
        )
        add(TypeInsnNode(Opcodes.CHECKCAST, "kotlinx/coroutines/CoroutineExceptionHandler"))
        add(VarInsnNode(Opcodes.ASTORE, 2))
        add(VarInsnNode(Opcodes.ALOAD, 2))
        add(JumpInsnNode(Opcodes.IFNONNULL, labelNode))


        add(TypeInsnNode(Opcodes.NEW, "kotlin/coroutines/CombinedContext"))
        add(InsnNode(Opcodes.DUP))
        add(VarInsnNode(Opcodes.ALOAD, 0))
        add(
          MethodInsnNode(
            Opcodes.INVOKESTATIC,
            "kotlinx/coroutines/GlobalCoroutineExceptionHandlerKt",
            "getGlobalCoroutineExceptionHandler",
            "()Lkotlinx/coroutines/GlobalCoroutineExceptionHandler;"
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
        add(TypeInsnNode(Opcodes.CHECKCAST, "kotlin/coroutines/CoroutineContext"))
        add(VarInsnNode(Opcodes.ASTORE, 0))
      }

      if (checkNotNullParameterInsn != null) {
        method.instructions.insert(checkNotNullParameterInsn, insnList)
      } else {
        method.instructions.insert(insnList)
      }
    }

    accept(nextClassVisitor)
  }
}