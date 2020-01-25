package org.firstinspires.ftc.teamcode.movement.purePursuit

import org.firstinspires.ftc.teamcode.field.*

/**
 * adapted from
 * https://github.com/TeamMeanMachine/meanlib/blob/frc2020/src/main/kotlin/org/team2471/frc/lib/math/Geometry.kt
 */

data class Line(val pointA: Point, val pointB: Point) {
    val slope = (pointB.y - pointA.y) / (pointB.x - pointA.x)
    val intercept = -slope * pointA.x + pointA.y

    operator fun get(x: Double): Double = slope * x + intercept

    operator fun plus(vec: Point) = Line(pointA + vec, pointB + vec)

    operator fun minus(vec: Point) = Line(pointA - vec, pointB - vec)

    fun pointInLine(point: Point): Boolean = point.y == this[point.x]

    fun pointInSegment(point: Point): Boolean =
            pointInLine(point) && point.distanceTo(pointA) + point.distanceTo(pointB) == pointA.distanceTo(pointB)
}

data class Circle(val center: Point, val radius: Double) {
    companion object {
        @JvmStatic
        val UNIT = Circle(Point.ORIGIN, 1.0)
    }

    // adapted from: https://stackoverflow.com/a/13055116
    infix fun intersectingPoints(line: Line): Array<Point> {
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

    operator fun plus(vec: Point) = Circle(center + vec, radius)

    operator fun minus(vec: Point) = Circle(center - vec, radius)

    operator fun times(scalar: Double) = Circle(center, radius * scalar)

    operator fun div(scalar: Double) = Circle(center, radius / scalar)
}

fun lineCircleIntersection(center: Point, radius: Double, line1: Point, line2: Point) = Circle(center, radius).intersectingPoints(Line(line1, line2))