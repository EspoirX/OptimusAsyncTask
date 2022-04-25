package com.lzx.optimustask

import java.util.concurrent.PriorityBlockingQueue


interface IOptimusTask : Comparable<IOptimusTask> {
    /**
     * 任务名称，可作为唯一标记
     */
    fun getTaskName(): String

    /**
     * 执行任务回调
     */
    fun doTask()

    /**
     * 完成任务回调
     */
    fun finishTask()

    /**
     * 执行下一个任务
     */
    fun doNextTask()

    /**
     * 每个任务执行的时间
     */
    fun getDuration(): Long

    /**
     * 设置优先级
     */
    fun setPriority(taskPriority: TaskPriority)

    /**
     * 获取优先级
     */
    fun getPriority(): TaskPriority

    /**
     * 当优先级相同 按照插入顺序 先入先出 该方法用来标记插入顺序
     */
    fun setSequence(sequence: Int)

    /**
     * 获取入队次序
     */
    fun getSequence(): Int

    fun setDeferred(deferred: PriorityBlockingQueue<Int>)
}