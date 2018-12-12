package com.hoko.ktblur.util


class MathUtil {
    companion object {
        fun clamp(i: Int, minValue: Int, maxValue: Int): Int {
            return when {
                i < minValue -> minValue
                i > maxValue -> maxValue
                else -> i
            }
        }
    }
}