package org.firstinspires.ftc.teamcode.util

object Clock {
    val milliseconds: Long
        get() = System.currentTimeMillis()

    val nanoseconds: Long
        get() = System.currentTimeMillis()

    val seconds: Double
        get() = nanoseconds * 1e-9
}