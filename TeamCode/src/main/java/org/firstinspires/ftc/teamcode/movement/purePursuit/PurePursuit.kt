package org.firstinspires.ftc.teamcode.movement.purePursuit

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.goToPosition_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_pose_raw
import kotlin.math.*

/**
 * todo things to add
 * modifiable follow radius. Peter's suggestion is drawing parallel line then - medium priority
 * better end position? Peter suggests checking if the distance to end is < a certain amount - high priority
 * prevent skipping? - low priority
 * can't find any intersections - high priority
 */

object PurePursuit {
    fun goToFollowPoint(targetPoint: Point, robotLocation: Point, preferredAngle: Double) = goToPosition_raw(targetPoint.x, targetPoint.y, robotLocation.angleTo(targetPoint).deg + preferredAngle)

    var lastIndex = 0
    var finishingMove = false

    fun reset() {
        lastIndex = 0
        finishingMove = false
    }

    fun followCurve(allPoints: ArrayList<CurvePoint>, followAngle: Double = 0.0) {
        val robotLocation = world_pose_raw

        val followMe = getFollowPoint(allPoints, robotLocation, allPoints[0].followDistance)

        goToFollowPoint(followMe.point, robotLocation.point, followAngle)
    }

    fun getFollowPoint(pathPoints: ArrayList<CurvePoint>, robotLocation: Pose, followDistance: Double): CurvePoint {
        val followMe = pathPoints[0].copy()

        for (i in 0 until pathPoints.size - 1) {
            val startLine = pathPoints[i]
            val endLine = pathPoints[i + 1]

            val intersections = lineCircleIntersection(robotLocation.point, followDistance, startLine.point, endLine.point)

            var closestAngle = Double.POSITIVE_INFINITY

            for (intersection in intersections) {
                val angle = robotLocation.point.angleTo(intersection)
                val deltaAngle = (angle - robotLocation.heading).wrapped().rad.absoluteValue

                if (deltaAngle < closestAngle) {
                    closestAngle = deltaAngle
                    followMe.point = intersection
                }
            }
        }

        return followMe
    }
}

fun lineCircleIntersection(center: Point, radius: Double, line1: Point, line2: Point) = Circle(center, radius).intersectingPoints(Line(line1, line2))

data class CurvePoint(var point: Point, var followDistance: Double) {

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