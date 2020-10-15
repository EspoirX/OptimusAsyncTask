package com.lzx.optimustask

/**
 * 维护当前正在展示的任务
 */
object CurrentRunningTask {

    /**
     * 考虑到可能有多个 OptimusTaskManager 实例，所以当前 OptimusTask 用 map 保存
     */
    private var currTaskMap = hashMapOf<String, OptimusTask?>()

    fun getCurrentTask(group: String): OptimusTask? = currTaskMap[group]

    fun setCurrentTask(group: String, task: OptimusTask?) {
        currTaskMap[group] = task
    }

    fun removeCurrentTask(group: String) {
        currTaskMap.remove(group)
    }

    fun clear() {
        currTaskMap.clear()
    }

    fun getCurrentTaskStatus(group: String): Boolean = getCurrentTask(group)?.getStatus() ?: false
}