package com.hoko.ktblur.task

import android.os.Handler
import com.hoko.ktblur.api.BlurResultDispatcher
import com.hoko.ktblur.util.SingleMainHandler
import java.util.concurrent.Executor

class AndroidBlurResultDispatcher (handler: Handler) : BlurResultDispatcher {
    companion object {
        internal val MAIN_THREAD_DISPATCHER : BlurResultDispatcher = AndroidBlurResultDispatcher(SingleMainHandler.get())
    }

    private val resultExecutor: Executor = Executor { runnable ->
        handler.post(runnable)
    }

    override fun dispatch(runnable: BlurResultRunnable) {
        resultExecutor.execute(runnable)
    }
}