package com.lzx.optimustask

import java.lang.ref.WeakReference

class TaskEvent {
    private var mTask: WeakReference<OptimusTask>? = null
    var mEventType = 0

    fun getTask(): OptimusTask? {
        return mTask?.get()
    }

    fun setTask(mTask: OptimusTask) {
        this.mTask = WeakReference(mTask)
    }

    fun getEventType(): Int {
        return mEventType
    }

    fun setEventType(mEventType: Int) {
        this.mEventType = mEventType
    }

    object EventType {
        const val DO = 0X00
        const val FINISH = 0X01
    }
}