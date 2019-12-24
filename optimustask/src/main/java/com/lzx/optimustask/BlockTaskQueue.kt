package com.lzx.optimustask

import android.util.Log
import java.util.concurrent.BlockingQueue
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * 使用优先级阻塞队列实现的一个可以插队的排队机制
 */
class BlockTaskQueue {

    companion object {
        const val TAG = "BlockTaskQueue"
    }

    private val atomicInteger = AtomicInteger()

    //阻塞队列
    private val taskQueue: BlockingQueue<OptimusTask> = PriorityBlockingQueue<OptimusTask>()


    /**
     * 插入时 因为每一个showTask都实现了 comparable 接口 所以队列会按照 showTask 复写的 compare()方法定义的优先级次序进行插入
     * 当优先级相同时，使用AtomicInteger原子类自增 来为每一个task 设置sequence
     * **sequence**的作用是标记两个相同优先级的任务入队的次序
     * @see BaseOptimusTask.compareTo
     */
    fun <T : OptimusTask?> add(task: T): Int {
        if (!taskQueue.contains(task)) {
            task?.setSequence(atomicInteger.incrementAndGet())
            taskQueue.add(task)
            Log.d(TAG, "\n add task " + task.toString())
        }
        return taskQueue.size
    }

    fun <T : OptimusTask?> remove(task: T) {
        if (taskQueue.contains(task)) {
            Log.d(TAG, "\n" + "task has been finished. remove it from task queue")
            taskQueue.remove(task)
        }
    }

    fun poll(): OptimusTask? {
        return taskQueue.poll()
    }

    @Throws(InterruptedException::class)
    fun take(): OptimusTask? {
        return taskQueue.take()
    }

    fun clear() {
        taskQueue.clear()
    }

    fun size(): Int {
        return taskQueue.size
    }
}