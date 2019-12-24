package com.lzx.optimustask

/**
 * 维护当前正在展示的任务
 */
object CurrentRunningTask {

    private var sCurrentTask: OptimusTask? = null

    var currentTask: OptimusTask?
        get() = sCurrentTask
        set(sCurrentTask) {
            CurrentRunningTask.sCurrentTask = sCurrentTask
        }

    fun removeCurrentTask() {
        sCurrentTask = null
    }

    val currentTaskStatus: Boolean
        get() = sCurrentTask != null && sCurrentTask!!.getStatus()
}