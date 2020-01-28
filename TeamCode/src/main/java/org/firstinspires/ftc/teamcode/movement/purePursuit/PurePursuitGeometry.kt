package org.firstinspires.ftc.teamcode.movement.purePursuit

import org.firstinspires.ftc.teamcode.field.Point
import java.lang.Math.pow
import kotlin.math.absoluteValue
import kotlin.math.sqrt

fun lineCircleIntersection(center: Point, radius: Double, lineP1: Point, lineP2: Point): ArrayList<Point> {
    //Circle(center, radius).intersectingPoints(Line(line1, line2))

    val center = center.copy()
    val lineP1 = lineP1.copy()
    val lineP2 = lineP2.copy()

    if ((lineP1.y - lineP2.y).absoluteValue < 0.01)
        lineP1.y = lineP2.y + 0.01
    if ((lineP1.x - lineP2.x).absoluteValue < 0.01)
        lineP1.x = lineP2.x + 0.01

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

        if(xRoot1 > minX && xRoot1 < maxX)
            points.add(Point(xRoot1, yRoot1))

        var xRoot2 = (-quadB - sqrt(pow(quadB, 2.0) - (4.0 * quadA * quadC))) / (2.0 * quadA)

        var yRoot2 = m1 * (xRoot2 - x1) + y1

        xRoot2 += center.x
        yRoot2 += center.y

        if(xRoot2 > minX && xRoot2 < maxX)
            points.add(Point(xRoot2, yRoot2))

    } catch (e: ArithmeticException) {

    }

    return points
}

