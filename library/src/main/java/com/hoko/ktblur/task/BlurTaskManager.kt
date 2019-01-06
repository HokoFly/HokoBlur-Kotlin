package com.hoko.ktblur.task

import java.util.concurrent.Executors
import java.util.concurrent.Future

object BlurTaskManager {
    private val TAG: String = BlurTaskManager::class.java.simpleName
    private val EXECUTOR_THREADS_COUNT: Int =
        if (Runtime.getRuntime().availableProcessors() <= 3) 1 else Runtime.getRuntime().availableProcessors() / 2

    private val ASYNC_BLUR_EXECUTOR = Executors.newFixedThreadPool(EXECUTOR_THREADS_COUNT)
    private val PARALLEL_BLUR_EXECUTOR = Executors.newFixedThreadPool(EXECUTOR_THREADS_COUNT)

    fun submit(task: AsyncBlurTask): Future<*> {
        return ASYNC_BLUR_EXECUTOR.submit(task)
    }


    fun getWorkersCount() : Int = EXECUTOR_THREADS_COUNT

}