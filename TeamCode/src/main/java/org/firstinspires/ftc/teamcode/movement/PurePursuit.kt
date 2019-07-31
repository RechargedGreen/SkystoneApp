package org.firstinspires.ftc.teamcode.movement

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_x
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_y
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_point
import org.firstinspires.ftc.teamcode.util.*
import kotlin.math.*

object PurePursuit {
    // split these into a provider
    private val smallAdjustSpeed = 0.0
    private val movementYMin = 0.0
    private val movementXMin = 0.0
    private val movementTurnMin = 0.0
    private val xSlipScale = 2.0
    private val ySlipScale = 2.0
    private val turnSlipScale = 2.0
    private val xGunShotoff = 2.0
    private val yGunShotoff = 2.0
    private val turnGunShotoff = 2.0
    private val xSlippageEndSpeed = 0.3
    private val ySlippageEndSpeed = 0.3
    private val turnSlippageEndSpeed = 0.3


    var state_movement_x_prof = ProfileStates.GUNNING
    var state_movement_y_prof = ProfileStates.GUNNING
    var state_movement_turn_prof = ProfileStates.GUNNING


    fun initAll() {
        initForMove()
        initCurve()
    }

    enum class ProfileStates {
        GUNNING,
        SLIPPING,
        FINE_ADJUSTMENT;

        companion object {
            private val vals = values()
        }

        fun next() = vals[(ordinal + 1) % vals.size]
    }

    fun initForMove() {
        state_movement_x_prof = PurePursuit.ProfileStates.GUNNING
        state_movement_y_prof = PurePursuit.ProfileStates.GUNNING
        state_movement_turn_prof = PurePursuit.ProfileStates.GUNNING
    }

    fun followCurve(builder: Builder): Boolean = followCurve(builder.list)

    var followCurveIndex = 0
    fun initCurve() {
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

    // todo finish with different states
    fun goToPosition(targetPoint: Point, point_angle: Double, movement_speed: Double, point_speed: Double) {
        val worldPoint = DriveMovement.world_point
        val worldRad = DriveMovement.world_angle.rad

        val distanceToPoint = worldPoint.distanceTo(targetPoint)
        val angleToPoint = worldPoint.angleTo(targetPoint)
        val deltaAngleToPoint = MathUtil.angleWrap(angleToPoint - worldRad)

        // x and y components for moving to point
        val relative_x_to_point = cos(deltaAngleToPoint) * distanceToPoint
        val relative_y_to_point = sin(deltaAngleToPoint) * distanceToPoint
        val relative_abs_x = relative_x_to_point.absoluteValue
        val relative_abs_y = relative_y_to_point.absoluteValue

        // preserve the shape of the movement but scale by movement speed
        var x_power = (relative_x_to_point / (relative_abs_y + relative_abs_x)) * movement_speed
        var y_power = (relative_y_to_point / (relative_abs_y + relative_abs_x)) * movement_speed

        when (state_movement_x_prof) {
            PurePursuit.ProfileStates.GUNNING         -> {
                if (relative_abs_x < (Speedometer.xSlipPrediction * xSlipScale).absoluteValue || relative_abs_x < xGunShotoff)
                    state_movement_x_prof = state_movement_x_prof.next()
            }
            PurePursuit.ProfileStates.SLIPPING        -> {
                movement_x = 0.0
                if (Speedometer.xInchPerSec.absoluteValue < xSlippageEndSpeed)
                    state_movement_x_prof = state_movement_x_prof.next()
            }
            PurePursuit.ProfileStates.FINE_ADJUSTMENT -> {
                //todo do implementation
            }
        }

        var turnPower = 0.0

        movement_x = x_power
        movement_y = y_power
        movement_turn = turnPower
        verifyMinPower()
    }

    fun verifyMinPower() {
        if (Math.abs(movement_x) > Math.abs(movement_y)) {
            if (Math.abs(movement_x) > Math.abs(movement_turn)) {
                movement_x = minPower(movement_x, movementXMin);
            } else {
                movement_turn = minPower(movement_turn, movementTurnMin);
            }
        } else {
            if (Math.abs(movement_y) > Math.abs(movement_turn)) {
                movement_y = minPower(movement_y, movementYMin);
            } else {
                movement_turn = minPower(movement_turn, movementTurnMin);
            }
        }
    }

    fun minPower(power: Double, min: Double) = min(power.absoluteValue, min.absoluteValue) * power.sign

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