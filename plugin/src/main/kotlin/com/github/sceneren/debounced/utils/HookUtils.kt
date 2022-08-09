package com.github.sceneren.debounced.utils

import com.github.sceneren.debounced.doubleClick.DoubleClickConfig
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InvokeDynamicInsnNode
import org.objectweb.asm.tree.MethodNode

/**
 * @Author: leavesCZY
 * @Date: 2021/12/7 11:07
 * @Desc:
 */
val MethodNode.nameWithDesc: String
    get() = name + desc

val MethodNode.isStatic: Boolean
    get() = access and Opcodes.ACC_STATIC != 0

fun MethodNode.hasAnnotation(annotationDesc: String): Boolean {
    return visibleAnnotations?.find { it.desc == annotationDesc } != null
}


fun MethodNode.findLambda(
    filter: (InvokeDynamicInsnNode) -> Boolean
): List<InvokeDynamicInsnNode> {
    val handleList = mutableListOf<InvokeDynamicInsnNode>()
    val instructions = instructions?.iterator() ?: return handleList
    while (instructions.hasNext()) {
        val nextInstruction = instructions.next()
        if (nextInstruction is InvokeDynamicInsnNode) {
            if (filter(nextInstruction)) {
                handleList.add(nextInstruction)
            }
        }
    }
    return handleList
}

fun MethodNode.findHookPointLambda(config: DoubleClickConfig): List<InvokeDynamicInsnNode> {
    val onClickListenerLambda = findLambda {
        val nodeName = it.name
        val nodeDesc = it.desc
        val find = config.hookPointList.find { point ->
            nodeName == point.methodName && nodeDesc.endsWith(point.interfaceSignSuffix)
        }
        return@findLambda find != null
    }
    return onClickListenerLambda
}

fun ClassNode.getVisitPosition(
    argumentTypes: Array<Type>,
    parameterIndex: Int,
    isStaticMethod: Boolean
): Int {
    if (parameterIndex < 0 || parameterIndex >= argumentTypes.size) {
        throw Error("getVisitPosition error")
    }
    return if (parameterIndex == 0) {
        if (isStaticMethod) {
            0
        } else {
            1
        }
    } else {
        getVisitPosition(
            argumentTypes,
            parameterIndex - 1,
            isStaticMethod
        ) + argumentTypes[parameterIndex - 1].size
    }
}