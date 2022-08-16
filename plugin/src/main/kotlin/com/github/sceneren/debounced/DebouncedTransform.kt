package com.github.sceneren.debounced

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import com.github.sceneren.debounced.doubleClick.DebouncedExtConfig
import com.github.sceneren.debounced.doubleClick.DoubleClickConfig
import org.objectweb.asm.ClassVisitor

abstract class DebouncedTransform : AsmClassVisitorFactory<InstrumentationParameters.None> {

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        val config = DoubleClickConfig(
            doubleCheckClass = DebouncedExtConfig.debouncedExt.className,
            doubleCheckMethodName = DebouncedExtConfig.debouncedExt.methodName,
            checkViewOnClickAnnotation = DebouncedExtConfig.debouncedExt.checkViewOnClickAnnotation,
            uncheckViewOnClickAnnotation = DebouncedExtConfig.debouncedExt.uncheckViewOnClickAnnotation,
        )
        return DebouncedClassVisitor(nextClassVisitor, config)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        return true
    }
}