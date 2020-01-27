package org.firstinspires.ftc.teamcode.movement.purePursuit

import org.firstinspires.ftc.teamcode.field.Point
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.movement.Angle
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.goToPosition_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.movement_x
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.movement_y
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_angle_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_pose_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_x_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_y_raw
import org.firstinspires.ftc.teamcode.movement.toDegrees
import org.firstinspires.ftc.teamcode.opmodeLib.Globals.mode
import org.firstinspires.ftc.teamcode.util.notNaN
import kotlin.math.absoluteValue
import kotlin.math.atan2

/**
 * todo things to add
 * modifiable follow radius. Peter's suggestion is drawing parallel line then - medium priority
 * better end position? Peter suggests checking if the distance to end is < a certain amount - high priority
 * prevent skipping? - low priority
 * can't find any intersections - high priority
 *
 * fix the math for intersections - high priority
 * fix infinite line glitch - high priority
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
    fun angleBetween_deg(point: Point, otherPoint: Point): Double {
        return atan2(otherPoint.x - point.x, otherPoint.y - point.y).toDegrees
    }

    fun angleWrap_deg(_angle: Double):Double{
        var angle = _angle
        while(angle < -180)
            angle += 360.0
        while(angle > 180.0)
            angle -= 360.0
        return angle
    }

    fun goToFollowPoint(targetPoint: Point, robotLocation: Point, followAngle: Double) {
        goToPosition_raw(targetPoint.x, targetPoint.y, angleBetween_deg(robotLocation, targetPoint) + followAngle)
        val movementAbs = (movement_y + movement_x).absoluteValue
        /*if(movementAbs != 0.0){
            movement_y /= movementAbs
            movement_x /= movementAbs
        }*/
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

        val followMe = getFollowPoint(allPoints, robotLocation, allPoints[0].followDistance, followAngle)

        goToFollowPoint(followMe, robotLocation.point, followAngle)

        val finalPoint = allPoints.last()

        if (finalAngle.notNaN() && finalPoint.point.distanceTo(robotLocation.point) < finalPoint.followDistance)
            finishingMove = true

        if (finishingMove)
            goToPosition_raw(finalPoint.point.x, finalPoint.point.y, finalAngle)
    }

    var followMe: Point? = null
    fun getFollowPoint(pathPoints: ArrayList<CurvePoint>, robotLocation: Pose, followDistance: Double, followAngle:Double): Point {
        if (followMe == null)
            followMe = pathPoints[0].point

        for (i in 0 until pathPoints.size - 1) {
            val startLine = pathPoints[i]
            val endLine = pathPoints[i + 1]

            val intersections = lineCircleIntersection(robotLocation.point, followDistance, startLine.point.copy(), endLine.point.copy())

            var closestAngle = 1000.0

            mode.telemetry.addData("intersections", intersections.size)
            mode.telemetry.addData("x", world_x_raw)
            mode.telemetry.addData("y", world_y_raw)
            mode.telemetry.addData("deg", world_angle_raw.deg)

            for (intersection in intersections) {
                val angle = angleWrap_deg((angleBetween_deg(robotLocation.point, intersection) + followAngle)).absoluteValue

                if (angle < closestAngle) {
                    closestAngle = angle
                    followMe = intersection
                }
            }
        }

        return followMe!!
    }
}

data class CurvePoint(var point: Point, var followDistance: Double)