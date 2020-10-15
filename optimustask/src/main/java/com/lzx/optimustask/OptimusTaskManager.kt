package com.lzx.optimustask

/**
 * 任务管理类
 */
class OptimusTaskManager constructor(
    taskGroup: String,
    private val startInit: Boolean = true,
    stopRunningWhenQueueEmpty: Boolean = false
) {

    private val taskQueue = BlockTaskQueue()

    private var dispatcher: TaskDispatcher = TaskDispatcher(taskGroup,taskQueue, stopRunningWhenQueueEmpty)

    init {
        if (startInit) {
            startRunning()
        }
    }

    var logInft: LogInft? = null
        set(value) {
            field = value
            taskQueue.logInft = value
            dispatcher.logInft = value
        }

    fun startRunning() {
        dispatcher.startToPoll()
    }

    fun stopRunning() {
        dispatcher.stopToPoll()
    }

    fun clearAllTask() {
        dispatcher.clearAllTask()
    }

    fun clearAndFinishAllTask() {
        dispatcher.clearAndFinishAllTask()
    }

    fun addTask(runNow: Boolean = true, task: OptimusTask) {
        logInft?.i("OptimusTaskManager", "addTask#isShutdown = " + dispatcher.isShutdown())
        if (runNow) {
            if (startInit && !dispatcher.isShutdown()) {
                startRunning()
            }
        }
        taskQueue.add(task)
    }
}