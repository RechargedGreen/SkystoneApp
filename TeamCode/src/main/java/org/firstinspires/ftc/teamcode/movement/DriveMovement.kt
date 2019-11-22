package org.firstinspires.ftc.teamcode.movement

import org.firstinspires.ftc.teamcode.field.Geometry
import org.firstinspires.ftc.teamcode.field.Point
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.lib.Controller
import org.firstinspires.ftc.teamcode.lib.RunData.ALLIANCE
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.initAll
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.movementProvider
import org.firstinspires.ftc.teamcode.odometry.Odometry
import org.firstinspires.ftc.teamcode.util.epsilonEquals
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

object DriveMovement {
    val roadRunnerPose2dRaw get() = Pose(world_x_raw, world_y_raw, world_angle_unwrapped_raw.rad).toRoadRunner

    lateinit var odometer: Odometry

    var world_x_raw = 0.0
    var world_x_mirror: Double
        set(value) {
            world_x_raw = value * ALLIANCE.sign
        }
        get() = world_x_raw * ALLIANCE.sign
    var world_y_raw = 0.0
    var world_y_mirror: Double
        get() = world_y_raw
        set(value) {
            world_y_raw = value
        }
    val world_point_raw: Point get() = Point(world_x_raw, world_y_raw)
    val world_point_mirror: Point get() = Point(world_x_mirror, world_y_mirror)

    val world_pose_raw: Pose
        get() = Pose(world_point_mirror, world_angle_mirror)

    var world_angle_raw = Angle.createUnwrappedRad(0.0)
        private set
    val world_angle_mirror: Angle get() = world_angle_raw * ALLIANCE.sign

    var world_angle_unwrapped_raw = Angle.createUnwrappedRad(0.0)
        private set(value) {
            world_angle_raw = value.wrapped()
            field = value
        }
    val world_angle_unwrapped_mirror: Angle get() = world_angle_unwrapped_raw * ALLIANCE.sign

    var movement_y = 0.0
    var movement_x = 0.0
    var movement_turn = 0.0

    fun stopDrive() {
        movement_x = 0.0
        movement_y = 0.0
        movement_turn = 0.0
    }

    fun setPosition_raw(x: Double, y: Double, angle_rad: Double) {
        world_x_raw = x
        world_y_raw = x
        odometer.setAngleRad(angle_rad)
    }

    fun setPosition_mirror(x: Double, y: Double, angle_rad: Double) = setPosition_raw(x * ALLIANCE.sign, y, angle_rad * ALLIANCE.sign)

    fun setAngle_raw(angle: Angle) = setAngleRad_raw(angle.rad)
    fun setAngle_mirror(angle: Angle) = setAngle_raw(angle * ALLIANCE.sign)
    fun setAngle_deg_raw(angle_deg: Double) = setAngleRad_raw(angle_deg.toRadians)
    fun setAngle_deg_mirror(angle_deg: Double) = setAngle_deg_raw(angle_deg * ALLIANCE.sign)
    fun setAngleRad_raw(angle_rad: Double) = odometer.setAngleRad(angle_rad)
    fun setAngleRad_mirror(angle_rad: Double) = setAngleRad_raw(angle_rad * ALLIANCE.sign)

    fun updatePos(baseDelta: Pose, finalAngle: Angle) {
        /*val circleArcDelta = Geometry.circleArcRelativeDelta(baseDelta)

        val finalDelta = Geometry.pointDelta(circleArcDelta, world_angle)*/
        //val finalDelta = Geometry.pointDelta(baseDelta.point, world_angle)

        val dtheta = baseDelta.heading.rad
        val (sineTerm, cosTerm) = if (dtheta epsilonEquals 0.0) {
            1.0 - dtheta * dtheta / 6.0 to dtheta / 2.0
        } else {
            sin(dtheta) / dtheta to (1.0 - cos(dtheta)) / dtheta
        }
        val move = sineTerm * baseDelta.point.y - cosTerm * baseDelta.point.x
        val strafe = cosTerm * baseDelta.point.y + sineTerm * baseDelta.point.x

        val pointDelta = Point(
                strafe,
                move
        )

        val finalDelta = Geometry.pointDelta(pointDelta, world_angle_raw)

        world_x_raw += finalDelta.x
        world_y_raw += finalDelta.y
        world_angle_unwrapped_raw = finalAngle

        /*Speedometer.xInchesTraveled += circleArcDelta.x
        Speedometer.yInchesTraveled += circleArcDelta.y*/
        Speedometer.xInchesTraveled += baseDelta.point.x
        Speedometer.yInchesTraveled += baseDelta.point.y
        Speedometer.update()
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
        initAll()
    }
}