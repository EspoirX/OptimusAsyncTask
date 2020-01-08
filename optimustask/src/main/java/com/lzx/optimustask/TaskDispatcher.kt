package com.lzx.optimustask

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.annotation.NonNull
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * 任务调度器，用来遍历队列
 */
class TaskDispatcher constructor(
    private var taskQueue: BlockTaskQueue,
    private var stopRunningWhenQueueEmpty: Boolean = false
) {

    companion object {
        const val PROGRESS_UPDATE_INTERNAL: Long = 1000
        const val PROGRESS_UPDATE_INITIAL_INTERVAL: Long = 100
    }

    private val doTakeList: MutableList<OptimusTask> = mutableListOf()
    var logInft: LogInft? = null

    private val mExecutorService: ScheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor()
    private var mScheduleFuture: ScheduledFuture<*>? = null

    fun startToPoll() {
        stopToPoll()
        if (!mExecutorService.isShutdown) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate({
                pollTask.run()
            },
                PROGRESS_UPDATE_INITIAL_INTERVAL,
                PROGRESS_UPDATE_INTERNAL,
                TimeUnit.MILLISECONDS)
        }
    }

    fun stopToPoll() {
        mScheduleFuture?.cancel(false)
    }

    fun isShutdown(): Boolean = mExecutorService.isShutdown

    private fun clearPoll() {
        stopToPoll()
        mExecutorService.shutdown()
        handler.removeCallbacksAndMessages(null)
    }

    private val pollTask: Runnable = Runnable {
        val task = taskQueue.take()
        task?.let {
            //执行任务
            val doEvent = TaskEvent()
            doEvent.setTask(task)
            doEvent.setEventType(TaskEvent.EventType.DO)
            handler.obtainMessage(0x1000, doEvent).sendToTarget()

            if (task.getDuration() != 0L) {
                val finishEvent = TaskEvent()
                finishEvent.setTask(task)
                finishEvent.setEventType(TaskEvent.EventType.FINISH)
                val message = Message.obtain()
                message.what = 0x2000
                message.obj = finishEvent
                handler.sendMessageDelayed(message, task.getDuration())
            }
            task.blockTask()
            //完成任务
            if (task.getDuration() == 0L) {
                val finishEvent = TaskEvent()
                finishEvent.setTask(task)
                finishEvent.setEventType(TaskEvent.EventType.FINISH)
                handler.obtainMessage(0x3000, finishEvent).sendToTarget()
            }
        }
    }

    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(@NonNull msg: Message) {
            super.handleMessage(msg)
            val taskEvent = msg.obj as TaskEvent
            when (msg.what) {
                0x1000 -> {
                    logInft?.i("TaskDispatcher", "-------doTask---------")
                    taskEvent.getTask()!!.doTask()
                    doTakeList.add(taskEvent.getTask()!!)
                }
                0x2000 -> {
                    logInft?.i("TaskDispatcher", "-------finishTask---------")
                    taskEvent.getTask()!!.unLockBlock()
                    finishTask(taskEvent)
                }
                0x3000 -> {
                    finishTask(taskEvent)
                }
                else -> {
                }
            }
        }
    }

    private fun finishTask(taskEvent: TaskEvent) {
        taskEvent.getTask()!!.finishTask()
        doTakeList.remove(taskEvent.getTask()!!)
        if (stopRunningWhenQueueEmpty && taskQueue.size() == 0) {
            clearPoll()
        }
    }

    fun clearAllTask() {
        taskQueue.clear()
        doTakeList.clear()
        handler.removeCallbacksAndMessages(null)
    }

    fun clearAndFinishAllTask() {
        taskQueue.clear()
        for (task in doTakeList) {
            task.finishTask()
        }
        doTakeList.clear()
        clearPoll()
    }
}