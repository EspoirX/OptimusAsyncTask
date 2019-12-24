package com.lzx.optimusasynctask

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lzx.optimustask.BaseOptimusTask
import com.lzx.optimustask.OptimusTask
import com.lzx.optimustask.OptimusTaskManager
import com.lzx.optimustask.TaskPriority
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    var taskManager1: OptimusTaskManager? = null
    var taskManager2: OptimusTaskManager? = null
    var taskManager3: OptimusTaskManager? = null
    var taskManager4: OptimusTaskManager? = null
    var taskManager5: OptimusTaskManager? = null
    var taskManager6: OptimusTaskManager? = null
    var taskManager7: OptimusTaskManager? = null
    var taskManager8: OptimusTaskManager? = null
    var taskManager9: OptimusTaskManager? = null
    var taskManager10: OptimusTaskManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskManager1 = OptimusTaskManager()
        taskManager2 = OptimusTaskManager(false)
        taskManager3 = OptimusTaskManager()
        taskManager4 = OptimusTaskManager(false)
        taskManager5 = OptimusTaskManager()

        btn1.setOnClickListener {
            taskManager1!!.addTask(task = Task1(text1))
        }

        btn2.setOnClickListener {
            taskManager2!!.addTask(task = Task1(text2))
        }
        btn2Start.setOnClickListener {
            taskManager2!!.startRunning()
        }

        btn3.setOnClickListener {
            taskManager3!!.addTask(task = Task2(text3))
        }

        btn4.setOnClickListener {
            taskManager4!!.addTask(task = Task2(text4))
        }
        btn4Start.setOnClickListener {
            taskManager4!!.startRunning()
        }


        btn5.setOnClickListener {
            taskManager1!!.addTask(task = Task3(text1))
        }


        btn9.setOnClickListener {
            for (i in 1..10) {
                taskManager1!!.addTask(task = Task1(text1))
                taskManager3!!.addTask(task = Task2(text2))
                taskManager5!!.addTask(task = Task2(text3))
            }
        }

    }


    private class Task1(var textView: TextView) : BaseOptimusTask() {

        @SuppressLint("SetTextI18n")
        override fun doTask() {
            super.doTask()
            textView.text = "执行时间不确定的任务-> " + getSequence()

            //模拟该任务耗时两秒
            textView.postDelayed({
                unLockBlock()
            }, 2000)
        }

        override fun finishTask() {
            super.finishTask()
            Log.i("Task1", "finishTask-> " + getSequence())
        }
    }

    private class Task2(var textView: TextView) : BaseOptimusTask() {

        @SuppressLint("SetTextI18n")
        override fun doTask() {
            super.doTask()
            textView.text = "执行时间确定的任务-> " + getSequence()
        }

        //确定该任务耗时时间
        override fun getDuration(): Long {
            return 2000
        }

        override fun finishTask() {
            super.finishTask()
            Log.i("Task1", "finishTask-> " + getSequence())
        }
    }


    private class Task3(var textView: TextView) : BaseOptimusTask() {

        @SuppressLint("SetTextI18n")
        override fun doTask() {
            super.doTask()
            textView.text = "执行时间不确定的任务(高优先级)-> " + getSequence()

            //模拟该任务耗时两秒
            textView.postDelayed({
                unLockBlock()
            }, 2000)
        }

        override fun getPriority(): TaskPriority {
            return TaskPriority.HIGH
        }

        override fun finishTask() {
            super.finishTask()
            Log.i("Task1", "finishTask-> " + getSequence())
        }
    }

}
