package org.firstinspires.ftc.teamcode.field

import org.firstinspires.ftc.teamcode.lib.*
import org.firstinspires.ftc.teamcode.movement.*
import kotlin.math.*

data class Pose(
        val point: Point,
        val heading: Angle
) {
    constructor(x: Double, y: Double, heading_rad: Double) : this(Point(x, y), Angle.createWrappedRad(heading_rad))
}

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
            val c = 1.0 * cos(angleIncrement)

            relativeY = radiusOfMovement * s - radiusOfStrafe * c
            relativeX = radiusOfMovement * c + radiusOfStrafe * s
        }

        return Point(relativeX, relativeY)
    }

    fun pointDelta(robotDelta: Point, heading_rad: Double): Point {
        val c = cos(heading_rad)
        val s = sin(heading_rad)
        val x = robotDelta.x
        val y = robotDelta.y
        return Point(
                c * y + s * x,
                s * y - c * x
        )
    }
}

data class Point(
        @JvmField val x: Double,
        @JvmField val y: Double
) {
    fun distanceTo(other: Point): Double = hypot(x - other.x, y - other.y)
    fun angleTo(other: Point) = Angle.createWrappedRad(atan2(other.y - y, other.x - x))

    fun closestPoint(firstPoint: Point, vararg additionalPoints: Point) = additionalPoints.fold(firstPoint) { result, next ->
        if (distanceTo(next) < distanceTo(result)) next else result
    }
}

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

        Globals.mode.fieldOverlay.strokeLine(p1.x, p1.y, p2.x, p2.y)
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