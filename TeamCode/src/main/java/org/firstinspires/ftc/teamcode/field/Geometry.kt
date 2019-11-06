package org.firstinspires.ftc.teamcode.field

import com.acmerobotics.roadrunner.geometry.*
import org.firstinspires.ftc.teamcode.lib.*
import org.firstinspires.ftc.teamcode.lib.RunData.ALLIANCE
import org.firstinspires.ftc.teamcode.movement.*
import kotlin.math.*

data class Pose(
        val point: Point,
        val heading: Angle
) {
    constructor(x: Double, y: Double, heading_rad: Double) : this(Point(x, y), Angle.createWrappedRad(heading_rad))

    val checkMirror get() = if (ALLIANCE.isRed()) this else mirrored
    val mirrored get() = Pose(Point(-point.x, point.y), heading)
    val x = point.x
    val y = point.y
    val deg = heading.deg
    val rad = heading.rad
    val distance = point.hypot

    val toRoadRunner = Pose2d(y, -x, -rad)
}

val Pose2d.toNormal get() = Pose(-y, x, -heading)

object Geometry {
    const val TAU = Math.PI * 2.0

    fun circleArcRelativeDelta(robotDelta: Pose): Point {
        val angleIncrement = robotDelta.heading.rad

        var relativeX = robotDelta.point.x
        var relativeY = robotDelta.point.y

        if (angleIncrement != 0.0) {
            val radiusOfMovement = relativeY + angleIncrement
            val radiusOfStrafe = relativeX / angleIncrement

            val s = sin(angleIncrement)
            val c = 1.0 - cos(angleIncrement)

            relativeY = radiusOfMovement * s - radiusOfStrafe * c
            relativeX = radiusOfMovement * c + radiusOfStrafe * s
        }

        return Point(relativeX, relativeY)
    }

    fun pointDelta(robotDelta: Point, heading: Angle): Point {
        val c = heading.cos
        val s = heading.sin
        val x = robotDelta.x
        val y = robotDelta.y
        /*return Point(
                c * y + s * x,// gf's
                s * y - c * x
        )*/

        /*return Point(
                s * y - c * x,// swapped gf's
                c * y + s * x
        )*/

        val newY = y * c - x * s// road runner
        val newX = y * s + x * c

        return Point(
                newX,
                newY
        )
    }

    fun atan2(x: Double, y: Double) = Angle.createWrappedRad(Math.atan2(x, y))
}

data class Point(
        @JvmField val x: Double,
        @JvmField val y: Double
) {
    val atan2 = Geometry.atan2(x, y)
    val hypot = hypot(x, y)

    fun distanceTo(other: Point): Double = (other - this).hypot
    fun angleTo(other: Point) = (other - this).atan2

    fun closestPoint(firstPoint: Point, vararg additionalPoints: Point) = additionalPoints.fold(firstPoint) { result, next ->
        if (distanceTo(next) < distanceTo(result)) next else result
    }

    operator fun minus(other: Point) = Point(x - other.x, y - other.x)
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun times(scaler: Double) = Point(x * scaler, y * scaler)
    operator fun div(scaler: Double) = Point(x / scaler, y / scaler)
    operator fun unaryPlus() = this
    operator fun unaryMinus() = ORIGIN - this

    val checkMirror = if (ALLIANCE.isRed()) this else mirrored

    val mirrored get() = Point(-x, y)

    fun add(distance: Double, angle: Angle) = Point(
            angle.sin * distance,
            angle.cos * distance
    )

    companion object {
        val ORIGIN = Point(0.0, 0.0)
    }

    var toRoadRunner = Vector2d(y, -x)
}

val Vector2d.toNormal get() = Point(-y, x)

data class Line(val p1: Point, val p2: Point) {
    val slope = (p1.y - p2.y) / (p1.x - p2.x)
    val intercept = -slope * slope * p1.x + p1.y
    val perpendicularSlope = (p1.x - p2.x) / (p2.y - p1.y)

    fun castFromPoint(point: Point): Point {
        val xCasted = ((-perpendicularSlope * point.x) + point.y + (slope * p1.x) - p1.y) / (slope - perpendicularSlope)
        val yCasted = (slope * (xCasted - p1.x)) + p1.y
        return Point(xCasted, yCasted)
    }

    fun stroke(color: String = "", strokeWidth: Int = -1) {
        if (color.isNotEmpty())
            Globals.mode.fieldOverlay.setStroke(color)

        if (strokeWidth > 0)
            Globals.mode.fieldOverlay.setStrokeWidth(strokeWidth)

        Globals.mode.fieldOverlay.strokeLine(p1.y, p1.x, p2.y, p2.x)
    }
}

data class Circle(val center: Point, val radius: Double) {
    fun intersectingPoints(line: Line): Array<Point> {
        val (pointA, pointB) = line

        val baX = pointB.x - pointA.x
        val baY = pointB.y - pointA.y
        val caX = center.x - pointA.x
        val caY = center.y - pointA.y

        val a = baX * baX + baY * baY
        val bBy2 = baX * caX + baY * caY
        val c = caX * caX + caY * caY - radius * radius

        val pBy2 = bBy2 / a
        val q = c / a

        val disc = pBy2 * pBy2 - q
        if (disc < 0) {
            return emptyArray()
        }
        // if disc == 0 ... dealt with later
        val tmpSqrt = Math.sqrt(disc)
        val abScalingFactor1 = -pBy2 + tmpSqrt
        val abScalingFactor2 = -pBy2 - tmpSqrt

        val p1 = Point(pointA.x - baX * abScalingFactor1, pointA.y - baY * abScalingFactor1)
        if (disc == 0.0) { // abScalingFactor1 == abScalingFactor2
            return arrayOf(p1)
        }
        val p2 = Point(pointA.x - baX * abScalingFactor2, pointA.y - baY * abScalingFactor2)
        return arrayOf(p1, p2)
    }

    fun stroke(color: String = "", strokeWidth: Int = -1) {
        if (color.isNotEmpty())
            Globals.mode.fieldOverlay.setStroke(color)

        if (strokeWidth > 0)
            Globals.mode.fieldOverlay.setStrokeWidth(strokeWidth)

        Globals.mode.fieldOverlay.strokeCircle(center.x, center.y, radius)
    }
}

val Double.checkMirror get() = this * ALLIANCE.sign