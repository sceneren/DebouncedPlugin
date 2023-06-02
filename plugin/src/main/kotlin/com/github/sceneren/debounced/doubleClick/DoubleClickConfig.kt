package com.github.sceneren.debounced.doubleClick

/**
 * Created by scene on 2023/6/2 15:37
 *
 * Description:配置文件
 */
class DoubleClickConfig(
    private val doubleCheckClass: String = DebouncedExtConfig.DEFAULT_CLASS_NAME,
    val doubleCheckMethodName: String = DebouncedExtConfig.DEFAULT_METHOD_NAME,
    val doubleCheckMethodDescriptor: String = DebouncedExtConfig.DOUBLE_CHECK_METHOD_DESCRIPTOR,
    private val checkViewOnClickAnnotation: String = DebouncedExtConfig.DEFAULT_CHECK_VIEW_ON_CLICK_ANNOTATION,
    private val uncheckViewOnClickAnnotation: String = DebouncedExtConfig.DEFAULT_UNCHECK_VIEW_ON_CLICK_ANNOTATION,
    val hookPointList: List<DoubleClickHookPoint> = extraHookPoints,
) {

    val formatDoubleCheckClass: String
        get() = doubleCheckClass.replace(".", "/")

    val formatCheckViewOnClickAnnotation: String
        get() = "L" + checkViewOnClickAnnotation.replace(".", "/") + ";"

    val formatUncheckViewOnClickAnnotation: String
        get() = "L" + uncheckViewOnClickAnnotation.replace(".", "/") + ";"

}

data class DoubleClickHookPoint(
    val interfaceName: String,
    val methodName: String,
    val methodSign: String,
) {

    val interfaceSignSuffix = "L$interfaceName;"

}

private val extraHookPoints = listOf(
    DoubleClickHookPoint(
        interfaceName = "android/view/View\$OnClickListener",
        methodName = "onClick",
        methodSign = "onClick(Landroid/view/View;)V"
    ),
    DoubleClickHookPoint(
        interfaceName = "com/chad/library/adapter/base/listener/OnItemClickListener",
        methodName = "onItemClick",
        methodSign = "onItemClick(Lcom/chad/library/adapter/base/BaseQuickAdapter;Landroid/view/View;I;)V"
    ),
    DoubleClickHookPoint(
        interfaceName = "com/chad/library/adapter/base/listener/OnItemChildClickListener",
        methodName = "onItemChildClick",
        methodSign = "onItemChildClick(Lcom/chad/library/adapter/base/BaseQuickAdapter;Landroid/view/View;I;)V",
    )
)