package org.firstinspires.ftc.teamcode.movement.movementAlgorithms

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_point

object PurePursuit {
    fun init(){

    }

    private var curvePoints = ArrayList<CurvePoint>()

    fun followCurve() {
        val followMe = getFollowPointPath(curvePoints, world_point, 1.0)
    }

    fun getFollowPointPath(pathPoints: ArrayList<CurvePoint>, robotPoint: Point, followRadius: Double): Point {
        var followMe = pathPoints[0].point

        for (i in 0 until pathPoints.size - 1) {
            val startLine = pathPoints[i]
            val endLine = pathPoints[i + 1]

            val line = Line(startLine.point, endLine.point)
            val intersections = Circle(world_point, followRadius).intersectingPoints(line)

            var closestAngle = Double.NaN
            for (intersection in intersections) {
                val angle = robotPoint.angleTo(intersection)
                val deltaAngle = (angle - world_angle).wrapped().rad

                if (closestAngle.isNaN() || deltaAngle < closestAngle) {
                    closestAngle = deltaAngle
                    followMe = intersection
                }
            }
        }
        return followMe
    }

    object Builder {
        private var curvePoints = ArrayList<CurvePoint>()
        fun add(x: Double, y: Double, followRadius: Double) {
            curvePoints.add(CurvePoint(Point(x, y), followRadius))
        }
    }
}

data class CurvePoint(val point: Point, val followRadius: Double)