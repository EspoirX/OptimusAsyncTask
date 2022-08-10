package com.lzx.optimustask

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

class OptimusTaskManager {

    companion object {
        private const val TAG = "OptimusTaskManager"
        internal val cacheTaskNameList = mutableListOf<String>()

        var currRunningTask: IOptimusTask? = null

        @JvmStatic
        fun getRunningTask() = currRunningTask
    }

    private var channel = Channel<IOptimusTask>(Channel.UNLIMITED)

    //使用SupervisorJob 的 coroutineScope, 异常不会取消父协程
    private var scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val atomicInteger = AtomicInteger()
    private val deferred = PriorityBlockingQueue<Int>()

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.i(TAG, "handlerCatch -> $exception")
    }

    init {
        loop()
    }

    fun addTask(task: IOptimusTask) {
        scope.launch(handler) { addTaskSuspend(task) }
    }


    private suspend fun addTaskSuspend(task: IOptimusTask) {
        task.setDeferred(deferred)
        val result = if (checkChannelActive()) {
            sendTask(task)
        } else {
            reset()
            reSendTaskIfNeed() //重新发送缓存任务
            sendTask(task)     //最后发送当前任务
        }
        Log.i(TAG, "addTask name = " + task.getTaskName() + " result = " + result)
    }

    private suspend fun sendTask(task: IOptimusTask): Boolean {
        return runCatching {
            if (checkChannelActive()) {
                task.setSequence(atomicInteger.incrementAndGet())
                TaskQueueManager.addTask(task)
                channel.send(task)
                return true
            } else {
                TaskQueueManager.removeTask(task)
                cacheTaskNameList.remove(task.getTaskName())
                return false
            }
        }.onFailure {
            Log.i(TAG, "addTaskCatch -> $it")
            TaskQueueManager.removeTask(task)
            cacheTaskNameList.remove(task.getTaskName())
        }.getOrElse { false }
    }

    private fun loop() {
        scope.launch(handler) {
            channel.consumeEach {
                tryToHandlerTask(it)
            }
        }
    }

    /**
     * 重置数据
     */
    private fun reset() {
        channel = Channel(Channel.BUFFERED)
        loop()
    }

    private suspend fun reSendTaskIfNeed() {
        if (TaskQueueManager.getTaskQueueSize() > 0) {
            val list = mutableListOf<IOptimusTask>()
            TaskQueueManager.getTaskQueue().forEach { list.add(it) }
            TaskQueueManager.clearTaskQueue()
            list.forEach { sendTask(it) }
        }
    }

    private suspend fun tryToHandlerTask(it: IOptimusTask) {
        try {
            currRunningTask = it
            withContext(Dispatchers.Main) { it.doTask() }
            if (it.getDuration() != 0L) {
                delay(it.getDuration())
                withContext(Dispatchers.Main) { it.finishTask() }
                currRunningTask = null
            } else {
                deferred.take()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.i(TAG, "handlerTaskCatch -> $ex")
        } finally {
            TaskQueueManager.removeTask(it)
            cacheTaskNameList.remove(it.getTaskName())
        }
    }

    /**
     * 检查管道是否激活
     */
    private fun checkChannelActive(): Boolean {
        return !channel.isClosedForReceive && !channel.isClosedForSend
    }


    fun clear() {
        runCatching {
            deferred.add(1)
            channel.close()
            currRunningTask?.finishTask()
            cacheTaskNameList.clear()
            TaskQueueManager.clearTaskQueue()
            currRunningTask = null
        }
    }
}