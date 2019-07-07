package org.firstinspires.ftc.teamcode.field

import kotlin.math.cos
import kotlin.math.sin

data class Point(
        val x: Double,
        val y: Double
)

data class Pose(
        val point: Point,
        val heading: Double
) {
    constructor(x: Double, y: Double, heading: Double) : this(Point(x, y), heading)
}

object Geometry {
    fun circleArcRelativeDelta(robotDelta: Pose): Point {
        val angleIncrement = robotDelta.heading

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