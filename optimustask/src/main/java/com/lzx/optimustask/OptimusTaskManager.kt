package com.lzx.optimustask

/**
 * 任务管理类
 */
class OptimusTaskManager constructor(startInit: Boolean = true) {

    private val taskQueue = BlockTaskQueue()

    private var dispatcher: TaskDispatcher = TaskDispatcher(taskQueue)

    init {
        if (startInit) {
            startRunning()
        }
    }

    fun startRunning() {
        dispatcher.start()
    }

    fun stopRunning() {
        dispatcher.isRunning = false
    }

    fun clearAllTask() {
        dispatcher.clearAllTask()
    }

    fun clearAllTaskAndFinishTask() {
        dispatcher.clearAllTaskAndFinishTask()
    }

    fun addTask(runNow: Boolean = true, task: OptimusTask) {
        if (runNow && !dispatcher.isRunning) {
            dispatcher.isRunning = true
        }
        taskQueue.add(task)
    }
}