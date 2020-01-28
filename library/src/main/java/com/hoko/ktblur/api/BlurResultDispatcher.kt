package com.hoko.ktblur.api

interface BlurResultDispatcher {

    fun dispatch(runnable: Runnable)
}