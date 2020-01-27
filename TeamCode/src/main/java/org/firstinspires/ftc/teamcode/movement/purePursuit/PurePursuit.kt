package org.firstinspires.ftc.teamcode.movement.purePursuit

import org.firstinspires.ftc.teamcode.field.Point
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.goToPosition_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_pose_raw
import org.firstinspires.ftc.teamcode.movement.toDegrees
import org.firstinspires.ftc.teamcode.util.notNaN
import kotlin.math.atan2

/**
 * todo things to add
 * modifiable follow radius. Peter's suggestion is drawing parallel line then - medium priority
 * better end position? Peter suggests checking if the distance to end is < a certain amount - high priority
 * prevent skipping? - low priority
 * can't find any intersections - high priority
 */

class PurePursuitPath(var followDistance: Double) {
    val curvePoints = ArrayList<CurvePoint>()

    val firstFollowDistance = followDistance

    fun resetArgs() {
        followDistance = firstFollowDistance
    }

    fun add(point: Point) {
        curvePoints.add(CurvePoint(point, followDistance))
    }

    var finalAngle = Double.NaN
}


object PurePursuit {
    fun angleBetween_deg(point:Point, otherPoint: Point):Double{
        return atan2(otherPoint.x - point.x, otherPoint.y - point.y).toDegrees
    }

    fun goToFollowPoint(targetPoint: Point, robotLocation: Point, followAngle: Double) {
        goToPosition_raw(targetPoint.x, targetPoint.y, angleBetween_deg(robotLocation, targetPoint) + followAngle)
    }

    var lastIndex = 0
    var finishingMove = false

    fun reset() {
        lastIndex = 0
        finishingMove = false
        followMe = null
    }

    fun followCurve(path: PurePursuitPath, followAngle: Double = 0.0) {
        val allPoints = path.curvePoints
        val finalAngle = path.finalAngle

        val robotLocation = world_pose_raw

        val followMe = getFollowPoint(allPoints, robotLocation, allPoints[0].followDistance)

        goToFollowPoint(followMe, robotLocation.point, followAngle)

        val finalPoint = allPoints.last()

        if (finalAngle.notNaN() && finalPoint.point.distanceTo(robotLocation.point) < finalPoint.followDistance)
            finishingMove = true

        if (finishingMove)
            goToPosition_raw(finalPoint.point.x, finalPoint.point.y, finalAngle)
    }

    var followMe: Point? = null
    fun getFollowPoint(pathPoints: ArrayList<CurvePoint>, robotLocation: Pose, followDistance: Double): Point {
        if (followMe == null)
            followMe = pathPoints[0].point

        for (i in 0 until pathPoints.size - 1) {
            val startLine = pathPoints[i]
            val endLine = pathPoints[i + 1]

            val intersections = lineCircleIntersection(robotLocation.point, followDistance, startLine.point, endLine.point)

            var closestDistance = Double.POSITIVE_INFINITY

            for (intersection in intersections) {
                val angle = robotLocation.point.angleTo(intersection)
                val distance = endLine.point.distanceTo(robotLocation.point)

                if (distance < closestDistance) {
                    closestDistance = distance
                    followMe = intersection
                }
            }
        }

        return followMe!!
    }
}

data class CurvePoint(var point: Point, var followDistance: Double)