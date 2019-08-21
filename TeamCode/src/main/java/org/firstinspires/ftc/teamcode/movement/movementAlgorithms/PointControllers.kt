package org.firstinspires.ftc.teamcode.movement.movementAlgorithms

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_x
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_y
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_point
import kotlin.math.*

object PointControllers {
    fun init() {

    }

    fun slipAdjustedTarget(x: Double, y: Double, decelDistance: Double): Point {
        val currentPoint = world_point
        val target = Point(x, y)

        val slipPredict = Speedometer.fieldSlipPoint

        val adjustedTarget = target - slipPredict

        val distanceToAdjustedPoint = currentPoint.distanceTo(adjustedTarget)
        val angleToAdjustedPoint = currentPoint.angleTo(adjustedTarget)

        val deltaAngleToAdjustedPoint = angleToAdjustedPoint - world_angle

        val relative_x_to_point = deltaAngleToAdjustedPoint.cos * distanceToAdjustedPoint
        val relative_y_to_point = deltaAngleToAdjustedPoint.sin * distanceToAdjustedPoint
        val absTotal = relative_x_to_point.absoluteValue + relative_y_to_point.absoluteValue

        var movement_x_power = relative_x_to_point / absTotal
        var movement_y_power = relative_y_to_point / absTotal

        if (distanceToAdjustedPoint < decelDistance) {
            val kP = distanceToAdjustedPoint / decelDistance
            movement_x_power *= kP
            movement_y_power *= kP
        }

        movement_x = movement_x_power
        movement_y = movement_y_power

        return target - currentPoint
    }

    fun pdTo(x: Double, y: Double, kP: Double, kD: Double) {
        val currentPoint = world_point
        val target = Point(x, y)

        val distanceToTarget = currentPoint.distanceTo(target)
        val angleToTarget = currentPoint.angleTo(target)
        val deltaAngleToTarget = angleToTarget - world_angle

        val x_error = 0.0
        val y_error = 0.0

        val x_rate = Speedometer.xInchPerSec
        val y_rate = Speedometer.yInchPerSec

        val movement_x_power = x_error * kP - x_rate * kD
        val movement_y_power = y_error * kP - y_rate * kD

        movement_x = movement_x_power
        movement_y = movement_y_power
    }
}