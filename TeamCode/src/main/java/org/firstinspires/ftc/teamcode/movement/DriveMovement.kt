package org.firstinspires.ftc.teamcode.movement

import org.firstinspires.ftc.teamcode.field.Geometry
import org.firstinspires.ftc.teamcode.field.Point
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.lib.Controller
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.initAll
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.movementProvider
import org.firstinspires.ftc.teamcode.odometry.Odometry

object DriveMovement {
    lateinit var odometer: Odometry

    var world_x = 0.0
    var world_y = 0.0
    val world_point: Point
        get() = Point(world_x, world_y)

    val world_pose: Pose
        get() = Pose(world_point, world_angle)

    var world_angle = Angle.createUnwrappedRad(0.0)
        private set

    var world_angle_unwrapped = Angle.createUnwrappedRad(0.0)
        private set(value) {
            world_angle = value.wrapped()
            field = value
        }

    var movement_y = 0.0
    var movement_x = 0.0
    var movement_turn = 0.0

    fun stopDrive() {
        movement_x = 0.0
        movement_y = 0.0
        movement_turn = 0.0
    }

    fun setPosition(x: Double, y: Double, angle_rad: Double) {
        world_x = x
        world_y = x
        odometer.setAngleRad(angle_rad)
    }

    fun setAngle(angle: Angle) = setAngleRad(angle.rad)
    fun setAngle_deg(angle_deg: Double) = setAngleRad(angle_deg.toRadians)
    fun setAngleRad(angle_rad: Double) = odometer.setAngleRad(angle_rad)

    fun updatePos(baseDelta: Pose, finalAngle: Angle) {
        val circleArcDelta = Geometry.circleArcRelativeDelta(baseDelta)

        val finalDelta = Geometry.pointDelta(circleArcDelta, world_angle)
        world_x += finalDelta.x
        world_y += finalDelta.y
        world_angle_unwrapped = finalAngle

        Speedometer.xInchesTraveled += circleArcDelta.x
        Speedometer.yInchesTraveled += circleArcDelta.y
        Speedometer.update()
    }

    fun moveRobotCentric(x: Double, y: Double, turn: Double) {
        movement_x = x
        movement_y = y
        movement_turn = turn
    }

    fun moveFieldCentric(x: Double, y: Double, turn: Double) {

    }

    fun gamepadControl(gamepad: Controller) {
        movement_y = gamepad.leftStick.y
        movement_x = gamepad.leftStick.x
        movement_turn = gamepad.rightStick.x
    }

    fun verifyMinPower() {
        when {
            movement_x < movement_y && movement_y < movement_turn -> movement_turn = minPower(movement_turn, movementProvider.getMinTurn())
            movement_x < movement_y && movement_turn < movement_y -> movement_turn = minPower(movement_y, movementProvider.getMinY())
            movement_y < movement_x && movement_turn < movement_x -> movement_x = minPower(movement_x, movementProvider.getMinX())
        }
    }

    fun minPower(power: Double, min: Double): Double {
        if (power >= 0 && power <= min)
            return min
        if (power < 0 && power > -min)
            return -min
        return power
    }

    fun resetForOpMode() {
        stopDrive()
        initAll()
    }
}

data class DriveVector(val speed: Double, val angleOnRobot: Angle) {
    operator fun plus(other: DriveVector) = DriveVector(0.0, Angle.createUnwrappedRad(0.0)) //todo finish implementation
    fun apply() {
        // todo apply to x, y velocities
    }
}