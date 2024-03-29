package com.hoko.ktblur.util

import android.os.Handler
import android.os.Looper

internal object SingleMainHandler {
    private val sMainHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    fun get(): Handler {
        return sMainHandler
    }
}