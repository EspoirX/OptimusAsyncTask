package com.lzx.optimustask

/**
 * 任务管理类
 */
class OptimusTaskManager constructor(
    startInit: Boolean = true,
    stopRunningWhenQueueEmpty: Boolean = false
) {

    private val taskQueue = BlockTaskQueue()

    private var dispatcher: TaskDispatcher = TaskDispatcher(taskQueue, stopRunningWhenQueueEmpty)

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

    fun clearAndFinishAllTask() {
        dispatcher.clearAndFinishAllTask()
    }

    fun addTask(runNow: Boolean = true, task: OptimusTask) {
        if (runNow && !dispatcher.isRunning) {
            dispatcher.isRunning = true
        }
        taskQueue.add(task)
    }
}