package org.firstinspires.ftc.teamcode.movement.basicDriveFunctions

import org.firstinspires.ftc.teamcode.field.Geometry
import org.firstinspires.ftc.teamcode.field.Point
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.field.checkMirror
import org.firstinspires.ftc.teamcode.movement.Angle
import org.firstinspires.ftc.teamcode.movement.Speedometer
import org.firstinspires.ftc.teamcode.odometry.Odometry
import org.firstinspires.ftc.teamcode.odometry.ThreeWheel.degTraveled
import org.firstinspires.ftc.teamcode.odometry.ThreeWheel.xTraveled
import org.firstinspires.ftc.teamcode.odometry.ThreeWheel.yTraveled
import org.firstinspires.ftc.teamcode.opmodeLib.RunData.ALLIANCE
import org.firstinspires.ftc.teamcode.util.epsilonEquals
import kotlin.math.cos
import kotlin.math.sin

object DrivePosition {
    lateinit var odometer: Odometry

    var world_pose_raw = Pose(Point(0.0, 0.0), Angle.createWrappedRad(0.0))
        set(value) {
            field = value
            world_angle_unwrapped_raw = value.heading
        }

    var world_pose_mirror
        get() = world_pose_raw.checkMirror
        set(value) {
            world_pose_raw = value.checkMirror
        }

    val world_x_raw get() = world_pose_raw.x
    val world_x_mirror get() = world_x_raw.checkMirror

    val world_y_raw get() = world_pose_raw.y
    val world_y_mirror get() = world_y_raw

    val world_point_raw: Point get() = world_pose_raw.point
    val world_point_mirror: Point get() = world_point_raw.checkMirror

    val world_angle_raw get() = world_pose_raw.heading
    val world_angle_mirror: Angle get() = world_angle_raw * ALLIANCE.sign
    val world_deg_raw:Double get() = world_angle_raw.deg
    val world_deg_mirror:Double get() = world_angle_mirror.deg

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

        yTraveled += pointDelta.y
        xTraveled += pointDelta.x

        val finalDelta = Geometry.pointDelta(pointDelta, world_angle_raw)

        world_pose_raw = Pose(world_pose_raw.point + finalDelta, finalAngle)
        world_angle_unwrapped_raw = finalAngle

        Speedometer.xInchesTraveled += baseDelta.point.x
        Speedometer.yInchesTraveled += baseDelta.point.y
        Speedometer.update()
    }
}