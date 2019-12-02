package org.firstinspires.ftc.teamcode.movement

import com.acmerobotics.dashboard.config.*
import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.opmodeLib.RunData.ALLIANCE
import org.firstinspires.ftc.teamcode.movement.DriveMovement.clipMovement
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveFieldCentric_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_raw

@Config
object SimpleMotion {
    @JvmField
    var turnP = 0.025
    @JvmField
    var moveP = 0.06
    @JvmField
    var turnD = 0.0
    @JvmField
    var moveD = 0.0

    fun goToPosition_raw(x: Double, y: Double, deg: Double, clipSpeed: Boolean = true, slowDownDegrees: Double = 15.0, slowDownAmount: Double = 8.0): Pose {
        val turnLeft = (deg - world_angle_raw.deg).angleWrapDeg
        val yLeft = y - world_y_raw
        val xLeft = x - world_x_raw

        val speed = Speedometer.fieldSpeed

        val xSpeed = xLeft * moveP - speed.x * moveD
        val ySpeed = yLeft * moveP - speed.y * moveD
        val turnSpeed = turnLeft * turnP - Speedometer.degPerSec * turnD

        moveFieldCentric_raw(xSpeed, ySpeed, turnSpeed)

        if (clipSpeed)
            clipMovement()

        return Pose(xLeft, yLeft, turnLeft.toRadians)
    }

    fun goToPosition_mirror(x: Double, y: Double, deg: Double, clipSpeed: Boolean = true): Pose {
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