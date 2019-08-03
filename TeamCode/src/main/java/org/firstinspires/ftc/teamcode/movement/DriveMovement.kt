package org.firstinspires.ftc.teamcode.movement

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.odometry.*

object DriveMovement {
    lateinit var odometer: Odometry

    var path = Path()

    fun followSetPath() = path.follow()

    var world_x = 0.0
    var world_y = 0.0
    val world_point: Point
        get() = Point(world_x, world_y)

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
        val circleArcDelta = Geometry.circleArcRelativeDelta(Pose(
                baseDelta.point.x,
                baseDelta.point.x,
                baseDelta.heading
        ))

        val finalDelta = Geometry.pointDelta(circleArcDelta, world_angle.rad)
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
}

data class DriveVector(val speed: Double, val angleOnRobot: Angle) {
    operator fun plus(other: DriveVector) = DriveVector(0.0, Angle.createUnwrappedRad(0.0)) //todo finish implementation
    fun apply() {
        // todo apply to x, y velocities
    }
}
