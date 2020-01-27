package org.firstinspires.ftc.teamcode.movement.purePursuit

import org.firstinspires.ftc.teamcode.field.Point
import java.lang.Math.pow
import kotlin.math.absoluteValue
import kotlin.math.sqrt

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
        val lineLength = line.pointA.distanceTo(line.pointB)

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

fun lineCircleIntersection(center: Point, radius: Double, lineP1: Point, lineP2: Point): ArrayList<Point> {
    //Circle(center, radius).intersectingPoints(Line(line1, line2))

    val center = center.copy()
    val lineP1 = lineP1.copy()
    val lineP2 = lineP2.copy()

    if ((lineP1.y - lineP2.y).absoluteValue < 0.003)
        lineP1.y = lineP2.y + 0.003
    if ((lineP1.x - lineP2.x).absoluteValue < 0.003)
        lineP1.x = lineP2.x + 0.003

    val m1 = (lineP2.y - lineP1.y) / (lineP2.x - lineP2.y)

    val quadA = 1.0 + pow(m1, 2.0)

    val x1 = lineP1.x - center.x
    val y1 = lineP1.y - center.y

    val quadB = (2.0 * m1 * y1) - (2.0 * pow(m1, 2.0) * x1)

    val quadC = (pow(m1, 2.0) * pow(x1, 2.0)) - (2.0 * y1 * m1 * x1) + pow(y1, 2.0) - pow(radius, 2.0)

    val points = ArrayList<Point>()

    try {
        var xRoot1 = (-quadB + sqrt(pow(quadB, 2.0) - (4.0 * quadA * quadC))) / (2.0 * quadA)

        var yRoot1 = m1 * (xRoot1 - x1) + y1

        xRoot1 += center.x
        yRoot1 += center.y

        val minX = if(lineP1.x < lineP2.x) lineP1.x else lineP2.x
        val maxX = if(lineP1.x > lineP2.x) lineP1.x else lineP2.x

        if(xRoot1 > minX && yRoot1 < maxX)
            points.add(Point(xRoot1, yRoot1))

        var xRoot2 = (-quadB - sqrt(pow(quadB, 2.0) - (4.0 * quadA * quadC))) / (2.0 * quadA)

        var yRoot2 = m1 * (xRoot2 - x1) + y1

        xRoot2 += center.x
        yRoot2 += center.y

        if(xRoot2 > minX && yRoot2 < maxX)
            points.add(Point(xRoot2, yRoot2))

    } catch (e: ArithmeticException) {

    }

    return points
}

