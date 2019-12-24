# OptimusAsyncTask
基于 PriorityBlockingQueue 的优先级异步任务队列

适合于排队执行的任务实现，比如直播间动画排队播放。

# 特点：
1. 基于 PriorityBlockingQueue 实现
2. 可设置优先级
3. 使用简单

<a href="art/WechatIMG3.jpeg"><img src="art/WechatIMG3.jpeg" width="30%"/></a>
<a href="art/WechatIMG4.jpeg"><img src="art/WechatIMG4.jpeg" width="30%"/></a>

# 介绍
OptimusAsyncTask 将每个任务抽象成 OptimusTask，OptimusTask 可以设置任务执行时间，任务优先级，获取任务状态等。
同时 OptimusAsyncTask 提供了 OptimusTask 的默认实现类 BaseOptimusTask，它实现了 Comparable 接口，任务按照优先级排序。

**优先级 TaskPriority**
 优先级的标准如下：
 TaskPriority.LOW < TaskPriority.DEFAULT < TaskPriority.HIGH
 当优先级相同 按照插入次序排队
 默认优先级是 TaskPriority.DEFAULT

**任务**
任务种类可分为 2 种，分别是 执行时间不确定 的任务和 执行时间确定 的任务。执行时间不确定的任务在任务执行完后需要调用
 unLockBlock() 方法解除阻塞，方可继续执行下一个任务。
执行时间确定的任务需要重写 getDuration() 方法返回具体的执行时间，单位是毫秒，执行时间到了会自动执行下一个任务。

# 使用方法

1. 创建执行时间不确定的任务
```kotlin
private class Task1(var textView: TextView) : BaseOptimusTask() {

    @SuppressLint("SetTextI18n")
    override fun doTask() {
        super.doTask()
        textView.text = "执行时间不确定的任务-> " + getSequence()

        //模拟该任务耗时两秒
        textView.postDelayed({
            unLockBlock() //解除阻塞
        }, 2000)
    }

    override fun finishTask() {
        super.finishTask()
        Log.i("Task1", "finishTask-> " + getSequence())
    }
}
```
2. 创建执行时间确定的任务
```kotlin
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
```

3. 通过任务管理器 OptimusTaskManager 开始执行任务

一个 OptimusTaskManager 管理着一个任务队列。

OptimusTaskManager 的构造方法有一个 startInit 参数，决定是否在创建 OptimusTaskManager 时就开始遍历任务队列。
该参数决定了是否任务入队就马上执行。默认为 true

```kotlin
val taskManager1 = OptimusTaskManager()

val taskManager2 = OptimusTaskManager(false)
```
当参数为 false 时，OptimusTaskManager 提供了一个开始方法 startRunning()，调用它即可开始执行任务。


添加任务 addTask(runNow: Boolean = true, task: OptimusTask)

addTask 有两个参数，第一个 runNow 默认为 true，遍历任务队列由一个叫 isRunning 的参数控制着是否继续遍历，当 runNow
 为 true 时，会判断如果 isRunning 为 false ，则设为 true。第二个参数即任务。

```kotlin
btn1.setOnClickListener {
    taskManager1!!.addTask(task = Task1(text1))
}

btn2.setOnClickListener {
    taskManager2!!.addTask(task = Task1(text2))
}
btn2Start.setOnClickListener {
    taskManager2!!.startRunning()
}
```

更多例子请查看 demo。
