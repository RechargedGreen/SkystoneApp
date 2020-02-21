package org.firstinspires.ftc.teamcode.movement

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.field.angleWrapDeg
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.clipMovement
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.moveFieldCentric_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_angle_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_x_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_y_raw
import org.firstinspires.ftc.teamcode.opmodeLib.RunData.ALLIANCE
import org.firstinspires.ftc.teamcode.util.clip

@Config
object SimpleMotion {
    @JvmField
    var turnP = 0.03
    @JvmField
    var moveP = 0.06
    @JvmField
    var turnD = 0.0025
    @JvmField
    var moveD = 0.01 // 0.008

    fun goToPosition_raw(x: Double, y: Double, deg: Double, clipSpeed: Boolean = true, slowDownDegrees: Double = 15.0, slowDownAmount: Double = 8.0, yClip: Double = Double.NaN, xClip: Double = Double.NaN): Pose {
        val turnLeft = (deg - world_angle_raw.deg).angleWrapDeg
        val yLeft = y - world_y_raw
        val xLeft = x - world_x_raw

        val speed = Speedometer.fieldSpeed

        var xSpeed = xLeft * moveP - speed.x * moveD
        var ySpeed = yLeft * moveP - speed.y * moveD
        val turnSpeed = turnLeft * turnP - Speedometer.degPerSec * turnD

        if (!xClip.isNaN())
            xSpeed = xSpeed clip xClip

        if (!yClip.isNaN())
            ySpeed = ySpeed clip yClip

        moveFieldCentric_raw(xSpeed, ySpeed, turnSpeed)

        if (clipSpeed)
            clipMovement()

        return Pose(xLeft, yLeft, turnLeft.toRadians)
    }

    fun goToPosition_mirror(x: Double, y: Double, deg: Double, clipSpeed: Boolean = true, yClip: Double = Double.NaN, xClip: Double = Double.NaN): Pose {
        val s = ALLIANCE.sign
        val r = goToPosition_raw(x * s, y, deg * s, clipSpeed)
        return Pose(r.x * s, r.y, r.heading.rad * s)
    }

    fun pointAngle_raw(deg: Double): Double {
        val turnLeft = (deg - world_angle_raw.deg).angleWrapDeg
        movement_turn = turnLeft * turnP - Speedometer.degPerSec * turnD
        return turnLeft
    }

    fun pointAngle_mirror(deg: Double) = pointAngle_raw(deg * ALLIANCE.sign) * ALLIANCE.sign
}