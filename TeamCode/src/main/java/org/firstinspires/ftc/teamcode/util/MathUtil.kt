package org.firstinspires.ftc.teamcode.util

import kotlin.math.*

object MathUtil {
    const val TAU = Math.PI * 2.0

    fun angleWrap(radians: Double): Double {
        var angle = radians
        while (angle < -Math.PI)
            angle += TAU
        while (angle > Math.PI)
            angle -= TAU
        return angle
    }
}

fun Double.toRadians() = Math.toRadians(this)

fun Double.threshold(threshold: Double) = if (this.absoluteValue < threshold) 0.0 else this