package com.hoko.ktblur.task

import android.util.Log
import java.util.concurrent.Executors
import java.util.concurrent.Future

object BlurTaskManager {
    private val TAG: String = BlurTaskManager::class.java.simpleName
    val WORKER_THREADS_COUNT: Int
    get() = if (Runtime.getRuntime().availableProcessors() <= 3) 1 else Runtime.getRuntime().availableProcessors() / 2

    private val ASYNC_BLUR_EXECUTOR = Executors.newFixedThreadPool(WORKER_THREADS_COUNT)
    private val PARALLEL_BLUR_EXECUTOR = Executors.newFixedThreadPool(WORKER_THREADS_COUNT)

    fun <T> submit(task: AsyncBlurTask<T>): Future<*> {
        return ASYNC_BLUR_EXECUTOR.submit(task)
    }

    fun invokeAll(tasks: Collection<BlurSubTask>) {
        if (tasks.isNotEmpty()) {
            try {
                PARALLEL_BLUR_EXECUTOR.invokeAll(tasks)
            } catch (e: InterruptedException) {
                Log.e(TAG, "invoke blur sub tasks error", e)
            }
        }
    }
}