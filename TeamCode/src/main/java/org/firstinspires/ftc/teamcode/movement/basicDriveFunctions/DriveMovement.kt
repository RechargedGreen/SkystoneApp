package org.firstinspires.ftc.teamcode.movement.basicDriveFunctions

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_angle_raw
import org.firstinspires.ftc.teamcode.opmodeLib.*
import org.firstinspires.ftc.teamcode.opmodeLib.RunData.ALLIANCE
import kotlin.math.*

object DriveMovement {
    var veloControl = false

    var movement_y = 0.0
    var movement_x = 0.0
    var movement_turn = 0.0

    fun stopDrive() {
        movement_x = 0.0
        movement_y = 0.0
        movement_turn = 0.0
    }

    fun moveRobotCentric_raw(x: Double, y: Double, turn: Double) {
        movement_x = x
        movement_y = y
        movement_turn = turn
    }

    fun moveRobotCentric_mirror(x: Double, y: Double, turn: Double) = moveRobotCentric_raw(x * ALLIANCE.sign, y, turn * ALLIANCE.sign)

    fun stopMove() {
        movement_x = 0.0
        movement_y = 0.0
    }

    fun moveRobotCentricVector_raw(vel: Double, direction: Angle, turn: Double) {
        val sin = direction.sin
        val cos = direction.cos

        movement_x = sin * vel
        movement_y = cos * vel

        movement_turn = turn
    }

    fun moveRobotCentricVector_mirror(vel: Double, direction: Angle, turn: Double) = moveRobotCentricVector_raw(vel, direction * ALLIANCE.sign, turn * ALLIANCE.sign)

    fun moveFieldCentric_raw(x: Double, y: Double, turn: Double) {
        val pointMove = Point(x, y)
        moveRobotCentricVector_raw(
                pointMove.hypot,
                pointMove.atan2 - world_angle_raw,
                turn)
    }

    fun moveFieldCentric_mirror(x: Double, y: Double, turn: Double) = moveFieldCentric_raw(x * ALLIANCE.sign, y, turn * ALLIANCE.sign)


    fun moveFieldCentricVector_raw(speed: Double, angle: Angle, turn: Double) {
        moveRobotCentricVector_raw(speed, angle - world_angle_raw, turn)
    }

    fun moveFieldCentricVector_mirror(speed: Double, angle: Angle, turn: Double) = moveFieldCentricVector_raw(speed, angle * ALLIANCE.sign, turn * ALLIANCE.sign)

    fun gamepadControl(gamepad: Controller) {
        movement_y = gamepad.leftStick.y
        movement_x = gamepad.leftStick.x
        movement_turn = gamepad.rightStick.x

        if (movement_y.absoluteValue < 0.05)
            movement_y = 0.0

        if (movement_x.absoluteValue < 0.05)
            movement_x = 0.0

        if (movement_turn.absoluteValue < 0.05)
            movement_turn = 0.0
    }

    fun scaleMovement(scaler: Double) {
        movement_x *= scaler
        movement_y *= scaler
    }

    fun maxMovement() {
        val total = movement_x.absoluteValue + movement_y.absoluteValue
        if (total != 0.0)
            scaleMovement(1.0 / total)
    }

    fun clipMovement() {
        if (movement_x.absoluteValue + movement_y.absoluteValue > 1.0)
            maxMovement()
    }

    fun resetForOpMode() {
        stopDrive()
    }
}