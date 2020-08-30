package com.hoko.ktblur.task

import android.util.Log
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

object BlurTaskManager {
    private val TAG: String = BlurTaskManager::class.java.simpleName
    val WORKER_THREADS_COUNT: Int = if (Runtime.getRuntime().availableProcessors() <= 3) 1 else Runtime.getRuntime().availableProcessors() / 2

    private val ASYNC_BLUR_DISPATCHER = Executors.newFixedThreadPool(WORKER_THREADS_COUNT).asCoroutineDispatcher() + CoroutineName("async_blur")
    private val PARALLEL_BLUR_EXECUTOR = Executors.newFixedThreadPool(WORKER_THREADS_COUNT)

    fun submit(block: suspend () -> Unit) {
        CoroutineScope(ASYNC_BLUR_DISPATCHER).launch {
            block()
        }
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