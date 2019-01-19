package com.hoko.ktblur.api

import com.hoko.ktblur.task.BlurResultRunnable

interface BlurResultDispatcher {

    fun dispatch(runnable: BlurResultRunnable)
}