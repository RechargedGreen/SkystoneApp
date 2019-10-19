package org.firstinspires.ftc.teamcode.movement.movementAlgorithms

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_x
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_y
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_point_raw
import kotlin.math.*

object PointControllers {
    fun init() {

    }

    fun slipAdjustedTarget(x: Double, y: Double, decelDistance: Double): Point {
        val currentPoint = world_point_raw
        val target = Point(x, y)

        val slipPredict = Speedometer.fieldSpeed

        val adjustedTarget = target - slipPredict

        val distanceToAdjustedPoint = currentPoint.distanceTo(adjustedTarget)
        val angleToAdjustedPoint = currentPoint.angleTo(adjustedTarget)

        val deltaAngleToAdjustedPoint = angleToAdjustedPoint - world_angle_raw

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
}