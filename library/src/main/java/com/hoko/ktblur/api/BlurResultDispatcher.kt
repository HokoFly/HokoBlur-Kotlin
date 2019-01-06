package com.hoko.ktblur.api

import com.hoko.ktblur.task.BlurResult

interface BlurResultDispatcher {

    fun dispatch(result: BlurResult)
}