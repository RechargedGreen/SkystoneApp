package org.firstinspires.ftc.teamcode.movement.purePursuit

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.goToPosition_raw

object PurePursuit {
    fun goToPoint_raw(targetPoint: Point, robotLocation: Point, preferredAngle: Double) = goToPosition_raw(targetPoint.x, targetPoint.y, robotLocation.angleTo(targetPoint).deg + preferredAngle)

    var lastIndex = 0

    fun reset() {
        lastIndex = 0
    }

    fun followCurve(curve: ArrayList<CurvePoint>) {

    }

    fun getFollowPoint(pathPoints: ArrayList<CurvePoint>, robotLocation: Point, followDistance: Double): CurvePoint {
        val followMe = pathPoints[0].copy()

        for (i in 0 until pathPoints.size - 1) {
            val startLine = pathPoints[i]
            val endLine = pathPoints[i + 1]

            val intersections = lineCircleIntersection(robotLocation, followDistance, startLine.point, endLine.point)

            val closestAngle = Double.POSITIVE_INFINITY
        }

        return followMe
    }
}

fun lineCircleIntersection(center: Point, radius: Double, line1: Point, line2: Point) = Circle(center, radius).intersectingPoints(Line(line1, line2))

data class CurvePoint(val point: Point, val followDistance: Double) {

}

data class PurePursuitBuilder(var followDistance: Double) {
    val points = arrayListOf<CurvePoint>()

    val firstFollowDistance = followDistance

    fun resetArgs() {
        followDistance = firstFollowDistance
    }

    fun add(point: Point) {
        points.add(CurvePoint(point, followDistance))
    }


}