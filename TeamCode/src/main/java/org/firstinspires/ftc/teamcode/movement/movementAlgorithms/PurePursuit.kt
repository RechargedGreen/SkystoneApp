package org.firstinspires.ftc.teamcode.movement.movementAlgorithms

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_x
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_y
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_point
import kotlin.math.*

object PurePursuit {

    fun followCurve(builder: Builder): Boolean = followCurve(builder.list)

    var followCurveIndex = 0
    fun init() {
        followCurveIndex = 0
    }

    fun followCurve(allPoints: ArrayList<CurvePoint>): Boolean {
        // todo add visual for path

        val pathExtended = allPoints.clone() as ArrayList<CurvePoint>

        val clippedToPath = clipToPath(allPoints, world_point)
        val currFollowIndex = clippedToPath.index + 1

        val followMe =

                return true
    }

    fun clipToPath(pathPoints: ArrayList<CurvePoint>, point: Point): PointWithIndex {
        var closestClippedDistance = Double.NaN
        var closestClippedIndex = 0
        var clippedToLine = Point(0.0, 0.0)

        for (i in 0 until pathPoints.size - 1) {
            val firstPoint = pathPoints[i]
            val secondPoint = pathPoints[i + 1]

            val currLine = Line(firstPoint.point, secondPoint.point)
            val currClippedToLine = currLine.castFromPoint(point)
            val distanceToClipped = point.distanceTo(currClippedToLine)

            if (closestClippedDistance.isNaN() || distanceToClipped < closestClippedDistance) {
                closestClippedDistance = distanceToClipped
                closestClippedIndex = i
                clippedToLine = currClippedToLine
            }
        }
        return PointWithIndex(clippedToLine, closestClippedIndex)
    }

    fun getFollowPointPath() {

    }

    data class CurvePoint(
            var point: Point, var moveSpeed: Double, var turnSpeed: Double,
            var followDistance: Double, var slowDownRadius: Double, var slowDownTurnAmount: Double, var pointLength: Double = 0.0)

    class Builder(var moveSpeed: Double, var turnSpeed: Double, var followDistance: Double, var slowDownRadius: Double, var slowDownTurnAmount: Double, var pointLength: Double = 0.0) {
        val list = ArrayList<CurvePoint>()
        fun add(point: Point, moveSpeed: Double = this.moveSpeed, turnSpeed: Double = this.turnSpeed, followDistance: Double = this.followDistance, slowDownRadius: Double = this.slowDownRadius, slowDownTurnAmount: Double = this.slowDownTurnAmount, pointLength: Double = this.pointLength): Builder {
            list.add(CurvePoint(point = point, moveSpeed = moveSpeed, turnSpeed = turnSpeed, followDistance = followDistance, slowDownRadius = slowDownRadius, slowDownTurnAmount = slowDownTurnAmount, pointLength = pointLength))
            return this
        }
    }

    data class PointWithIndex(var point: Point, var index: Int)
}