package org.firstinspires.ftc.teamcode.movement.basicDriveFunctions

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.odometry.*
import org.firstinspires.ftc.teamcode.odometry.ThreeWheel.degTraveled
import org.firstinspires.ftc.teamcode.odometry.ThreeWheel.xTraveled
import org.firstinspires.ftc.teamcode.odometry.ThreeWheel.yTraveled
import org.firstinspires.ftc.teamcode.opmodeLib.RunData.ALLIANCE
import org.firstinspires.ftc.teamcode.util.*
import kotlin.math.*

object DrivePosition {
    lateinit var odometer: Odometry

    var world_pose_raw = Pose(Point(0.0, 0.0), Angle.createWrappedRad(0.0))

    var world_pose_mirror
        get() = world_pose_raw.checkMirror
        set(value) {
            world_pose_raw = value.checkMirror
        }

    val world_x_raw get() = world_pose_raw.x
    val world_x_mirror get() = world_x_raw.checkMirror

    val world_y_raw get() = world_pose_raw.y
    val world_y_mirror get() = world_y_raw.checkMirror

    val world_point_raw: Point get() = world_pose_raw.point
    val world_point_mirror: Point get() = world_point_raw.checkMirror

    val world_angle_raw = world_pose_raw.heading
    val world_angle_mirror: Angle get() = world_angle_raw * ALLIANCE.sign

    var world_angle_unwrapped_raw = Angle.createUnwrappedRad(0.0)
    val world_angle_unwrapped_mirror: Angle get() = world_angle_unwrapped_raw * ALLIANCE.sign

    fun setPosition_raw(pose: Pose) {
        world_pose_raw = pose
        world_angle_unwrapped_raw = pose.heading
        odometer.setAngleRad(pose.rad)
    }

    fun setPosition_mirror(pose: Pose) = setPosition_raw(pose.checkMirror)

    fun updatePos(baseDelta: Pose, finalAngle: Angle) {
        degTraveled += baseDelta.heading.deg

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

        yTraveled += finalDelta.y
        xTraveled += finalDelta.x

        world_pose_raw = Pose(world_pose_raw.point + finalDelta, finalAngle)
        world_angle_unwrapped_raw = finalAngle

        Speedometer.xInchesTraveled += baseDelta.point.x
        Speedometer.yInchesTraveled += baseDelta.point.y
        Speedometer.update()
    }
}