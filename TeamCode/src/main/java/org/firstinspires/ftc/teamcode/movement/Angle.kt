package org.firstinspires.ftc.teamcode.movement

import org.firstinspires.ftc.teamcode.field.Geometry.TAU
import kotlin.math.*

data class Angle(var heading: Double, var unit: Angle.Unit) {

    private val FULL_CIRCLE = when (unit) {
        Unit.RAD -> TAU
        Unit.DEG -> 360.0
    }

    private val HALF_CIRCLE = when (unit) {
        Unit.RAD -> PI
        Unit.DEG -> 180.0
    }

    val deg: Double
        get() = when (unit) {
            Unit.DEG -> heading
            Unit.RAD -> heading.toDegrees
        }

    val rad: Double
        get() = when (unit) {
            Unit.DEG -> heading.toRadians
            Unit.RAD -> heading
        }

    companion object {
        fun createUnwrappedDeg(deg: Double) = Angle(deg, Unit.DEG)
        fun createUnwrappedRad(rad: Double) = Angle(rad, Unit.RAD)
        fun createWrappedDeg(deg: Double) = createUnwrappedDeg(deg).wrapped()
        fun createWrappedRad(rad: Double) = createUnwrappedRad(rad).wrapped()
    }

    enum class Unit {
        RAD,
        DEG
    }

    fun wrapped(): Angle {
        var angle = heading
        while (angle < -HALF_CIRCLE)
            angle += FULL_CIRCLE
        while (angle > HALF_CIRCLE)
            angle -= FULL_CIRCLE
        return createUnwrappedRad(angle)
    }

    operator fun plus(other: Angle) = when (unit) {
        Unit.RAD -> createUnwrappedRad(rad + other.rad)
        Unit.DEG -> createUnwrappedDeg(deg + other.deg)
    }

    operator fun minus(other: Angle) = plus(other.unaryMinus())

    operator fun unaryMinus() = when (unit) {
        Unit.RAD -> createUnwrappedRad(-rad)
        Unit.DEG -> createUnwrappedDeg(-deg)
    }

    val sin = sin(rad)
    val cos = cos(rad)
    val tan = tan(rad)
}

val Double.toRadians get() = Math.toRadians(this)
val Double.toDegrees get() = Math.toDegrees(this)