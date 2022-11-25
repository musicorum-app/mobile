package io.musicorum.mobile.utils

object Utils {
    fun interpolateValues(value: Float, x1: Float, x2: Float, y1: Float, y2: Float): Float {
        val percent = (value - x1) / (x2 - x1)
        return percent * (y2 - y1) + y1
    }
}