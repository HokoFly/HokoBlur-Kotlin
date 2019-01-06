package com.hoko.ktblur.util

import android.os.Handler

object SingleMainHandler {
    private lateinit var sMainHandler: Handler

    fun get(): Handler {
        if (!this::sMainHandler.isInitialized) {
            sMainHandler = Handler(android.os.Looper.getMainLooper())
        }
        return sMainHandler
    }
}