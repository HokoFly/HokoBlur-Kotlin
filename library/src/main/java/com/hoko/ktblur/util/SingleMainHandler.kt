package com.hoko.ktblur.util

import android.os.Handler

object SingleMainHandler {
    private val sMainHandler: Handler by lazy {
        Handler(android.os.Looper.getMainLooper())
    }

    fun get(): Handler {
        return sMainHandler
    }
}