package com.hoko.ktblur.task

class BlurResultRunnable private constructor(private val result: BlurResult) : Runnable {
    companion object {
        fun of(result: BlurResult): BlurResultRunnable {
            return BlurResultRunnable(result)
        }
    }

    override fun run() {
        result.run {
            if (success) {
                callback.onSuccess(bitmap)
            } else {
                callback.onFailed(error)
            }
        }

    }
}