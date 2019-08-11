package org.firstinspires.ftc.teamcode.movement.movementAlgorithms

import com.qualcomm.robotcore.util.*
import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.verifyMinPower
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_pose
import kotlin.math.*

object ToPositionAlgorithms {
    enum class slippageAlgStates {
        gunning,
        slipping,
        fine;

        fun next(): slippageAlgStates = values()[ordinal + 1]

        companion object {
            fun first(): slippageAlgStates = values()[0]
        }
    }

    var x_alg_prog = ToPositionAlgorithms.slippageAlgStates.gunning
    var y_alg_prog = ToPositionAlgorithms.slippageAlgStates.gunning
    var turn_alg_prog = ToPositionAlgorithms.slippageAlgStates.gunning

    fun init() {
        x_alg_prog = slippageAlgStates.first()
        y_alg_prog = slippageAlgStates.first()
        turn_alg_prog = slippageAlgStates.first()
    }

    fun goToPosition_slippage(x: Double, y: Double, deg: Double) = goToPosition_slippage(Pose(Point(x, y), Angle.createWrappedDeg(deg)))

    fun goToPosition_slippage(position: Pose): Boolean {
        //////////////////
        // provider

        val provider = MovementAlgorithms.movementProvider.slippageGoToPosProvider
        val movement_speed = provider.movement_speed
        val turn_speed = provider.turn_speed
        val turnP = provider.turnProvider
        val xP = provider.xProvider
        val yP = provider.yProvider

        ////////////////////////////
        ///// math
        val worldPos = world_pose
        val worldPoint = worldPos.point
        val worldHeading = worldPos.heading

        val targetPoint = position.point
        val targetHeading = position.heading

        val distanceToPoint = worldPoint.distanceTo(targetPoint)
        val angleToPoint = worldPoint.angleTo(targetPoint)
        val deltaAngleToPoint = (angleToPoint - worldHeading).rad

        // x and y components for moving to point
        val relative_x_to_point = cos(deltaAngleToPoint) * distanceToPoint
        val relative_y_to_point = sin(deltaAngleToPoint) * distanceToPoint
        val relative_abs_x = relative_x_to_point.absoluteValue
        val relative_abs_y = relative_y_to_point.absoluteValue

        // preserve the shape of the movement but scale by movement speed
        var x_power = (relative_x_to_point / (relative_abs_y + relative_abs_x)) * movement_speed
        var y_power = (relative_y_to_point / (relative_abs_y + relative_abs_x)) * movement_speed

        when (x_alg_prog) {
            slippageAlgStates.gunning  -> {
                if (relative_abs_x < (Speedometer.xSlipPrediction * xP.endGunSlipScale).absoluteValue || relative_abs_x < xP.endGunDistance)
                    x_alg_prog = x_alg_prog.next()
            }
            slippageAlgStates.slipping -> {
                x_power = 0.0
                if (Speedometer.xInchPerSec.absoluteValue < xP.endSlipSpeed)
                    x_alg_prog = x_alg_prog.next()
            }
            slippageAlgStates.fine     -> {
                x_power = scaleToMax(relative_x_to_point * xP.fine_kP, xP.fineMaxSpeed)
                if (xP.switchBackToGunDistance > 0.0 && relative_abs_x > xP.switchBackToGunDistance)
                    x_alg_prog = slippageAlgStates.first()
            }
        }

        when (y_alg_prog) {
            slippageAlgStates.gunning  -> {
                if (relative_abs_y < (Speedometer.ySlipPrediction * yP.endGunSlipScale).absoluteValue || relative_abs_y < yP.endGunDistance)
                    y_alg_prog = y_alg_prog.next()
            }
            slippageAlgStates.slipping -> {
                y_power = 0.0
                if (Speedometer.yInchPerSec.absoluteValue < yP.endSlipSpeed)
                    y_alg_prog = y_alg_prog.next()
            }
            slippageAlgStates.fine     -> {
                y_power = scaleToMax(relative_y_to_point * yP.fine_kP, yP.fineMaxSpeed)
                if (yP.switchBackToGunDistance > 0.0 && relative_abs_y > yP.switchBackToGunDistance)
                    y_alg_prog = slippageAlgStates.first()
            }
        }

        var turn_power = 0.0

        val degToTarget = angleToPoint.deg
        when (turn_alg_prog) {
            slippageAlgStates.gunning  -> {
                turn_power = if (degToTarget > 0.0) turn_speed else -turn_speed
                if (degToTarget.absoluteValue < Speedometer.turnDegSlipPrediction.absoluteValue * turnP.endGunSlipScale || degToTarget < turnP.endGunDistance)
                    turn_alg_prog = turn_alg_prog.next()
            }
            slippageAlgStates.slipping -> {
                turn_power = 0.0
                if (Speedometer.degPerSec < turnP.endSlipSpeed)
                    turn_alg_prog = turn_alg_prog.next()
            }
            slippageAlgStates.fine     -> {
                turn_power = (degToTarget * turnP.fine_kP).clip(turnP.fineMaxSpeed)
                if (degToTarget > turnP.switchBackToGunDistance)
                    turn_alg_prog = slippageAlgStates.first()
            }
        }

        DriveMovement.movement_x = x_power
        DriveMovement.movement_y = y_power
        DriveMovement.movement_turn = turn_power

        verifyMinPower()

        return distanceToPoint < provider.lateralThreshold && angleToPoint.deg < provider.degreesThreshold
    }

    fun scaleToMax(power: Double, max: Double) = Range.clip(power * max, -max, max)
}

fun Double.clip(max: Double) = Range.clip(this, -max, max)