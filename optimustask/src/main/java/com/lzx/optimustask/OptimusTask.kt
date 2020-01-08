package com.lzx.optimustask

/**
 * 任务接口
 */
interface OptimusTask : Comparable<OptimusTask> {
    /**
     * 任务的类型
     */
    fun getTaskType(): String

    /**
     * 执行任务回调
     */
    fun doTask()

    /**
     * 完成任务回调
     */
    fun finishTask()

    /**
     * 设置优先级
     */
    fun setPriority(mTaskPriority: TaskPriority): OptimusTask

    /**
     * 获取优先级
     */
    fun getPriority(): TaskPriority

    /**
     * 当优先级相同 按照插入顺序 先入先出 该方法用来标记插入顺序
     */
    fun setSequence(mSequence: Int)

    /**
     * 获取入队次序
     */
    fun getSequence(): Int

    /**
     * 状态
     */
    fun getStatus(): Boolean

    /**
     * 每个任务执行的时间
     */
    fun getDuration(): Long

    /**
     * 阻塞执行队列
     */
    @Throws(Exception::class)
    fun blockTask()

    /**
     * 解除执行队列的阻塞
     */
    fun unLockBlock()

    fun clearBlock()
}