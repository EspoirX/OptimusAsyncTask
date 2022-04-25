package com.lzx.optimustask

import java.util.concurrent.PriorityBlockingQueue

abstract class OptimusTask : IOptimusTask {

    private var taskName = ""

    init {
        val clazzName = javaClass.canonicalName
        val cacheList = OptimusTaskManager.cacheTaskNameList
        val name = clazzName + "_" + cacheList.size
        if (!cacheList.contains(name)) {
            cacheList.add(name)
        }
        taskName = name
    }

    /**
     * 默认名称由类名+数字组成，保证不重复
     */
    override fun getTaskName(): String {
        return taskName
    }

    //默认优先级
    private var taskPriority = TaskPriority.DEFAULT

    // 入队次序
    private var mSequence = 0
    private var deferred: PriorityBlockingQueue<Int>? = null

    override fun getDuration(): Long = 0

    override fun setPriority(taskPriority: TaskPriority) {
        this.taskPriority = taskPriority
    }

    override fun getPriority(): TaskPriority {
        return taskPriority
    }

    override fun setSequence(sequence: Int) {
        this.mSequence = sequence
    }

    override fun getSequence(): Int {
        return mSequence
    }

    override fun doNextTask() {
        finishTask()
        OptimusTaskManager.currRunningTask = null
        deferred?.add(1)
        deferred = null
    }

    override fun setDeferred(deferred: PriorityBlockingQueue<Int>) {
        this.deferred = deferred
    }

    override fun compareTo(other: IOptimusTask): Int {
        val me = getPriority()
        val it: TaskPriority = other.getPriority()
        return if (me === it) getSequence() - other.getSequence() else it.ordinal - me.ordinal
    }
}