package com.lzx.optimustask

/**
 * 队列管理
 */
object TaskQueueManager : ITaskQueueManager {

    private val taskMap = mutableMapOf<String, IOptimusTask>()

    override fun addTask(task: IOptimusTask): Boolean {
        return if (hasTask(task)) {
            false
        } else {
            taskMap[task.getTaskName()] = task
            true
        }
    }

    override fun hasTask(task: IOptimusTask): Boolean {
        return hasTask(task.getTaskName())
    }

    override fun hasTask(taskName: String): Boolean {
        return taskMap.containsKey(taskName)
    }

    override fun removeTask(task: IOptimusTask) {
        removeTask(task.getTaskName())
    }

    override fun removeTask(taskName: String) {
        taskMap.remove(taskName)
    }

    override fun getTaskQueueSize() = taskMap.size

    override fun getTaskQueue(): Collection<IOptimusTask> {
        return taskMap.values
    }

    override fun clearTaskQueue() {
        taskMap.clear()
    }
}

interface ITaskQueueManager {
    fun addTask(task: IOptimusTask): Boolean
    fun hasTask(task: IOptimusTask): Boolean
    fun hasTask(taskName: String): Boolean
    fun removeTask(task: IOptimusTask)
    fun removeTask(taskName: String)
    fun getTaskQueueSize(): Int
    fun getTaskQueue(): Collection<IOptimusTask>
    fun clearTaskQueue()
}