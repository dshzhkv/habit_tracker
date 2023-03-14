package com.example.habittracker

import android.graphics.Color
import kotlin.math.roundToInt


fun Int.toHex(): String = "#${Integer.toHexString(this and 0x00ffffff)}".uppercase()

fun Int.toRgb(): String =
    "${Color.red(this)}, ${Color.green(this)}, ${Color.blue(this)}"

fun Int.toHsv(): String {
    val hsv: FloatArray = floatArrayOf(0f, 0f, 0f)
    Color.colorToHSV(this, hsv)

    return "${hsv[0].roundToInt()}, ${(hsv[1] * 100).roundToInt()}%, ${(hsv[2] * 100).roundToInt()}%"
}


