package org.firstinspires.ftc.teamcode.util

import kotlin.math.*

fun Double.threshold(threshold: Double) = if (this.absoluteValue < threshold) 0.0 else this