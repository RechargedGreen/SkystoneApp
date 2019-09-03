package org.firstinspires.ftc.teamcode.util

import kotlin.math.*

fun Double.threshold(threshold: Double) = if (this.absoluteValue < threshold) 0.0 else this

fun ma(vararg vals: Double) = vals.map { it.absoluteValue }.max()

const val EPSILON = 1e-6
infix fun Double.epsilonEquals(other: Double) = abs(this - other) < EPSILON

infix fun Double.pow(exponent: Double) = Math.pow(this, exponent)