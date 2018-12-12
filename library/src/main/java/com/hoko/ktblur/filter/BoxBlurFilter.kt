package com.hoko.ktblur.filter

import com.hoko.ktblur.params.Direction
import com.hoko.ktblur.util.MathUtil.Companion.clamp

internal class BoxBlurFilter {

    companion object {
        @ExperimentalUnsignedTypes
        fun doBlur(data: UIntArray, width: Int, height: Int, radius: Int, direction: Direction) {
            val result = UIntArray(width * height)

            when (direction) {
                Direction.HORIZONTAL -> {
                    blurHorizontal(data, result, width, height, radius)
                    result.copyInto(data, 0, 0, result.size)
                }
                Direction.VERTICAL -> {
                    blurVertical(data, result, width, height, radius)
                    result.copyInto(data, 0, 0, result.size)
                }
                else -> {
                    blurHorizontal(data, result, width, height, radius)
                    blurVertical(result, data, width, height, radius)
                }
            }
        }

        @ExperimentalUnsignedTypes
        private fun blurHorizontal(input: UIntArray, output: UIntArray, width: Int, height: Int, radius: Int) {
            val widthMinus1 = width - 1
            val tableSize = 2 * radius + 1
            // construct a query table from 0 to 255
            val divide = UIntArray(256 * tableSize) { i ->
                (i / tableSize).toUInt()
            }

            var inIndex = 0

            //
            for (y in 0 until height) {
                var ta: UInt= 0u
                var tr: UInt= 0u
                var tg: UInt= 0u
                var tb: UInt= 0u // ARGB

                for (i in -radius..radius) {
                    val rgb = input[inIndex + clamp(i, 0, width - 1)]
                    ta += rgb shr 24 and 0xffu
                    tr += rgb shr 16 and 0xffu
                    tg += rgb shr 8 and 0xffu
                    tb += rgb and 0xffu
                }

                val baseIndex = y * width
                for (x in 0 until width) { // Sliding window computation.
                    output[baseIndex + x] = divide[ta.toInt()] shl 24 or (divide[tr.toInt()] shl 16) or (divide[tg.toInt()] shl 8) or divide[tb.toInt()]

                    var i1 = x + radius + 1
                    if (i1 > widthMinus1)
                        i1 = widthMinus1
                    var i2 = x - radius
                    if (i2 < 0)
                        i2 = 0
                    val rgb1 = input[inIndex + i1]
                    val rgb2 = input[inIndex + i2]

                    ta += (rgb1 shr 24 and 0xffu) - (rgb2 shr 24 and 0xffu)
                    tr += (rgb1 and 0xff0000u) - (rgb2 and 0xff0000u) shr 16
                    tg += (rgb1 and 0xff00u) - (rgb2 and 0xff00u) shr 8
                    tb += (rgb1 and 0xffu) - (rgb2 and 0xffu)
                    //                outIndex += height;
                }
                inIndex += width
            }
        }

        @ExperimentalUnsignedTypes
        private fun blurVertical(input: UIntArray, output: UIntArray, width: Int, height: Int, radius: Int) {
            val heightMinus1 = height - 1
            val tableSize = 2 * radius + 1

            // construct a query table from 0 to 255
            val divide = UIntArray(256 * tableSize) { i ->
                (i / tableSize).toUInt()
            }

            for (x in 0 until width) {
                var ta: UInt = 0u
                var tr: UInt = 0u
                var tg: UInt = 0u
                var tb: UInt = 0u // ARGB

                for (i in -radius..radius) {
                    val rgb = input[x + clamp(i, 0, height - 1) * width]
                    ta += rgb shr 24 and 0xffu
                    tr += rgb shr 16 and 0xffu
                    tg += rgb shr 8 and 0xffu
                    tb += rgb and 0xffu
                }

                for (y in 0 until height) { // Sliding window computation
                    output[y * width + x] = divide[ta.toInt()] shl 24 or (divide[tr.toInt()] shl 16) or (divide[tg.toInt()] shl 8) or divide[tb.toInt()]

                    var i1 = y + radius + 1
                    if (i1 > heightMinus1)
                        i1 = heightMinus1
                    var i2 = y - radius
                    if (i2 < 0)
                        i2 = 0
                    val rgb1 = input[x + i1 * width]
                    val rgb2 = input[x + i2 * width]

                    ta += (rgb1 shr 24 and 0xffu) - (rgb2 shr 24 and 0xffu)
                    tr += (rgb1 and 0xff0000u) - (rgb2 and 0xff0000u) shr 16
                    tg += (rgb1 and 0xff00u) - (rgb2 and 0xff00u) shr 8
                    tb += (rgb1 and 0xffu) - (rgb2 and 0xffu)
                }
            }
        }
    }


}
