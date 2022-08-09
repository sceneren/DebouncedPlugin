package com.github.sceneren.debounced.utils

import java.util.concurrent.Executors

/**
 * @Author: leavesCZY
 * @Date: 2021/12/8 10:57
 * @Desc:
 */
object Log {

    private val logThreadExecutor = Executors.newSingleThreadExecutor()

    fun log(log: Any?) {
        logThreadExecutor.submit {
            println("ASM: $log")
        }
    }

    fun useSuccess() {
        println()
        println("####################################################################")
        println("########                                                    ########")
        println("########                                                    ########")
        println("########            DebouncedPlugin集成成功 v2.0.0           ########")
        println("########                                                    ########")
        println("########                                                    ########")
        println("####################################################################")
        println()
    }

    fun printPluginStart() {
        println()
        println("####################################################################")
        println("########                                                    ########")
        println("########                                                    ########")
        println("########                  ASM去抖动插件开始                   ########")
        println("########                                                    ########")
        println("########                                                    ########")
        println("####################################################################")
        println()
    }

    fun printPluginEnd(mills: Long) {
        println()
        println("####################################################################")
        println("########                                                    ########")
        println("########                                                    ########")
        println("########          ASM去抖动插件结束$mills ms               ########")
        println("########                                                    ########")
        println("########                                                    ########")
        println("####################################################################")
        println()
    }
}