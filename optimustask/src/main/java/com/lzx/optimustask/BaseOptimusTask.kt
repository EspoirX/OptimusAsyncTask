package com.lzx.optimustask

import java.util.concurrent.PriorityBlockingQueue

/**
 * 基础任务类
 */
open class BaseOptimusTask : OptimusTask {

    override fun getTaskType(): String {
        return ""
    }

    //默认优先级
    private var mTaskPriority = TaskPriority.DEFAULT

    // 入队次序
    private var mSequence = 0

    // 标志是否仍在展示
    private var mTaskStatus = false

    //任务时间
    private var duration: Long = 0

    //此队列用来实现任务时间不确定的队列阻塞功能
    private val blockQueue = PriorityBlockingQueue<Int>()


    override fun doTask() {
        mTaskStatus = true
        CurrentRunningTask.currentTask = this
    }

    override fun finishTask() {
        mTaskStatus = false
        CurrentRunningTask.removeCurrentTask()
    }

    override fun setPriority(mTaskPriority: TaskPriority): OptimusTask {
        this.mTaskPriority = mTaskPriority
        return this
    }

    override fun getPriority(): TaskPriority {
        return mTaskPriority
    }

    override fun setSequence(mSequence: Int) {
        this.mSequence = mSequence
    }

    override fun getSequence(): Int {
        return mSequence
    }

    override fun getStatus(): Boolean {
        return mTaskStatus
    }

    override fun getDuration(): Long {
        return duration
    }

    override fun blockTask() {
        blockQueue.take() //如果队列里面没数据，就会一直阻塞
    }

    override fun unLockBlock() {
        blockQueue.add(1) //往里面随便添加一个数据，阻塞就会解除
    }

    override fun clearBlock() {
        blockQueue.clear()
    }

    /**
     * 优先级的标准如下：
     * TaskPriority.LOW < TaskPriority.DEFAULT < TaskPriority.HIGH
     * 当优先级相同 按照插入次序排队
     */
    override fun compareTo(other: OptimusTask): Int {
        val me = getPriority()
        val it: TaskPriority = other.getPriority()
        return if (me === it) getSequence() - other.getSequence() else it.ordinal - me.ordinal
    }

    override fun toString(): String {
        return "task name : " + javaClass.simpleName + " sequence : " + mSequence +
                " TaskPriority : " + mTaskPriority
    }

}