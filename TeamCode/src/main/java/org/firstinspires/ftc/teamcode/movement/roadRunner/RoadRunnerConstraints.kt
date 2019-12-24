package org.firstinspires.ftc.teamcode.movement.roadRunner

import com.acmerobotics.dashboard.config.*
import com.acmerobotics.roadrunner.control.*
import com.acmerobotics.roadrunner.geometry.*
import com.acmerobotics.roadrunner.trajectory.constraints.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_x
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_y

@Config
object RoadRunnerConstraints {
    @JvmField
    var maxVel = 50.0

    @JvmField
    var maxAccel = 60.0

    @JvmField
    var kV_maxVel = 56.0
    val kV get() = 1.0 / kV_maxVel

    const val measured_wb = 11.3386 //288mm
    const val measured_tw = 15.3937 //391mm

    @JvmField
    var axis_pid = PIDCoefficients(0.0, 0.0, 0.0)
    @JvmField
    var heading_pid = PIDCoefficients(0.0, 0.0, 0.0)

    @JvmField
    var effectiveTrackWidth = (measured_tw + measured_wb) / 2.0

    val mecanumConstraints get() = MecanumConstraints(DriveConstraints(0.0, 0.0, 0.0, 0.0, 0.0, 0.0), effectiveTrackWidth)

    fun setVelocity(velocity: Pose2d) {
        val kV = kV

        movement_y = velocity.x * kV
        movement_x = -(velocity.y) * kV
        movement_turn = (velocity.heading * effectiveTrackWidth) * kV
    }

    const val WHEEL_DIAMETER = 100.0 / 25.4
}