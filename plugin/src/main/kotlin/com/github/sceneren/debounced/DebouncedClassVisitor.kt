package com.github.sceneren.debounced

import com.github.sceneren.debounced.doubleClick.DoubleClickConfig
import com.github.sceneren.debounced.utils.*
import org.objectweb.asm.*
import org.objectweb.asm.tree.*


class DebouncedClassVisitor(nextVisitor: ClassVisitor, private val config: DoubleClickConfig) :
    ClassNode(Opcodes.ASM7) {

    init {
        this.cv = nextVisitor
    }

    private companion object {

        private const val ViewDescriptor = "Landroid/view/View;"

        private const val OnClickViewMethodDescriptor = "(Landroid/view/View;)V"


        private val MethodNode.onlyOneViewParameter: Boolean
            get() = desc == OnClickViewMethodDescriptor

        private fun MethodNode.hasCheckViewAnnotation(config: DoubleClickConfig): Boolean {
            return hasAnnotation(config.formatCheckViewOnClickAnnotation)
        }

        fun MethodNode.hasUncheckViewOnClickAnnotation(config: DoubleClickConfig): Boolean {
            return hasAnnotation(config.formatUncheckViewOnClickAnnotation)
        }

    }


    override fun visitEnd() {
        super.visitEnd()

        if (!methods.isNullOrEmpty()) {
            val shouldHookMethodList = mutableSetOf<String>()
            for (methodNode in methods) {
                //静态、包含 UncheckViewOnClick 注解的方法不用处理
                if (methodNode.isStatic ||
                    methodNode.hasUncheckViewOnClickAnnotation(config)
                ) {
                    continue
                }
                val methodNameWithDesc = methodNode.nameWithDesc
                if (methodNode.onlyOneViewParameter) {
                    if (methodNode.hasCheckViewAnnotation(config)) {
                        //添加了 CheckViewOnClick 注解的情况
                        shouldHookMethodList.add(methodNameWithDesc)
                        continue
                    }
                }
                if (isHookPoint(config, methodNode)) {
                    shouldHookMethodList.add(methodNameWithDesc)
                    continue
                }

                //判断方法内部是否有需要处理的 lambda 表达式
                val invokeDynamicInsnNodes = methodNode.findHookPointLambda(config)
                invokeDynamicInsnNodes.forEach {
                    val handle = it.bsmArgs[1] as? Handle
                    if (handle != null) {
                        shouldHookMethodList.add(handle.name + handle.desc)
                    }
                }
            }
            if (shouldHookMethodList.isNotEmpty()) {
                for (methodNode in methods) {
                    val methodNameWithDesc = methodNode.nameWithDesc
                    if (shouldHookMethodList.contains(methodNameWithDesc)) {
                        val argumentTypes = Type.getArgumentTypes(methodNode.desc)
                        val viewArgumentIndex = argumentTypes?.indexOfFirst {
                            it.descriptor == ViewDescriptor
                        } ?: -1
                        if (viewArgumentIndex >= 0) {
                            val instructions = methodNode.instructions
                            if (instructions != null && instructions.size() > 0) {
                                val list = InsnList()
                                list.add(
                                    VarInsnNode(
                                        Opcodes.ALOAD, getVisitPosition(
                                            argumentTypes,
                                            viewArgumentIndex,
                                            methodNode.isStatic
                                        )
                                    )
                                )
                                list.add(
                                    MethodInsnNode(
                                        Opcodes.INVOKESTATIC,
                                        config.formatDoubleCheckClass,
                                        config.doubleCheckMethodName,
                                        config.doubleCheckMethodDescriptor
                                    )
                                )
                                val labelNode = LabelNode()
                                list.add(JumpInsnNode(Opcodes.IFNE, labelNode))
                                list.add(InsnNode(Opcodes.RETURN))
                                list.add(labelNode)
                                instructions.insert(list)
                            }
                        }
                    }
                }
                val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
                accept(classWriter)
            }
        }
        accept(cv)

    }

    private fun ClassNode.isHookPoint(config: DoubleClickConfig, methodNode: MethodNode): Boolean {
        val myInterfaces = interfaces
        if (myInterfaces.isNullOrEmpty()) {
            return false
        }
        val extraHookMethodList = config.hookPointList
        extraHookMethodList.forEach {
            if (myInterfaces.contains(it.interfaceName) && methodNode.nameWithDesc == it.methodSign) {
                return true
            }
        }
        return false
    }
}