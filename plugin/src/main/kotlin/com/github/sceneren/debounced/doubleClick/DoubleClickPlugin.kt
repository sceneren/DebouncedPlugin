package com.github.sceneren.debounced.doubleClick

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.github.sceneren.debounced.DebouncedTransform
import com.github.sceneren.debounced.utils.Log
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

/**
 * @Author: leavesCZY
 * @Date: 2021/12/2 16:02
 * @Desc:
 */
class DoubleClickPlugin : Plugin<Project> {

    companion object {
        private const val EXT_NAME = "DebouncedExt"
    }

    override fun apply(project: Project) {
        project.extensions.create<DebouncedExt>(EXT_NAME)
        project.afterEvaluate {
            val debouncedExt = (extensions.findByName(EXT_NAME) as? DebouncedExt)
                ?: DebouncedExt()
            DebouncedExtConfig.debouncedExt = debouncedExt
        }

        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants { variant ->
            variant.instrumentation.transformClassesWith(
                DebouncedTransform::class.java,
                InstrumentationScope.PROJECT
            ) {
            }
            variant.instrumentation.setAsmFramesComputationMode(
                FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
            )
        }
    }
}

open class DebouncedExt {
    var className: String = DebouncedExtConfig.DEFAULT_CLASS_NAME
    var methodName: String = DebouncedExtConfig.DEFAULT_METHOD_NAME
    var checkViewOnClickAnnotation: String = DebouncedExtConfig.DEFAULT_CHECK_VIEW_ON_CLICK_ANNOTATION
    var uncheckViewOnClickAnnotation: String = DebouncedExtConfig.DEFAULT_UNCHECK_VIEW_ON_CLICK_ANNOTATION
}

object DebouncedExtConfig {

    const val DEFAULT_CLASS_NAME: String = "com.github.sceneren.debunced.DebouncedPredictor"
    const val DEFAULT_METHOD_NAME: String = "shouldDoClick"
    const val DEFAULT_CHECK_VIEW_ON_CLICK_ANNOTATION: String =
        "com.github.sceneren.debounced.CheckViewOnClick"
    const val DEFAULT_UNCHECK_VIEW_ON_CLICK_ANNOTATION: String =
        "com.github.sceneren.debounced.UncheckViewOnClick"
    const val DOUBLE_CHECK_METHOD_DESCRIPTOR: String = "(Landroid/view/View;)Z"

    var debouncedExt: DebouncedExt = DebouncedExt()
}