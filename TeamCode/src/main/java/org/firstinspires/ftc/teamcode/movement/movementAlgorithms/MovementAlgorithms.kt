package org.firstinspires.ftc.teamcode.movement.movementAlgorithms

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.field.angleWrapDeg
import org.firstinspires.ftc.teamcode.lib.RunData.ALLIANCE
import org.firstinspires.ftc.teamcode.movement.Angle
import org.firstinspires.ftc.teamcode.movement.DriveMovement.clipMovement
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveFieldCentric_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_raw
import org.firstinspires.ftc.teamcode.movement.Speedometer
import org.firstinspires.ftc.teamcode.movement.toRadians

@Config
object MovementAlgorithms {
    lateinit var movementProvider: MovementConstantsProvider
    fun initAll() {
        PurePursuit.init()
        PointControllers.init()
    }

    @Config
    object PD {
        fun setup(turnP: () -> Double, turnD: () -> Double, moveP: () -> Double, moveD: () -> Double, staticT: () -> Double, staticY: () -> Double, staticX: () -> Double) {
            this.turnP = turnP
            this.turnD = turnD
            this.moveP = moveP
            this.moveD = moveD
            this.staticT = staticT
            this.staticY = staticY
            this.staticX = staticX
        }

        @JvmField
        var turnP = { 0.0 }
        @JvmField
        var moveP = { 0.0 }
        @JvmField
        var turnD = { 0.0 }
        @JvmField
        var moveD = { 0.0 }
        @JvmField
        var staticX = { 0.0 }
        @JvmField
        var staticY = { 0.0 }
        @JvmField
        var staticT = { 0.0 }

        @JvmField
        var slowDownDegrees = 15.0
        @JvmField
        var slowDownAmount = 8.0

        fun goToPosition_raw(x: Double, y: Double, deg: Double, clipSpeed: Boolean = true, slowDownDegrees: Double = this.slowDownDegrees, slowDownAmount: Double = this.slowDownAmount): Pose {
            val turnLeft = (deg - world_angle_raw.deg).angleWrapDeg
            val yLeft = y - world_y_raw
            val xLeft = x - world_x_raw

            val speed = Speedometer.fieldSpeed

            val xSpeed = xLeft * moveP() - speed.x * moveD()
            val ySpeed = yLeft * moveP() - speed.y * moveD()
            val turnSpeed = turnLeft * turnP() - Speedometer.degPerSec * turnD()

            moveFieldCentric_raw(xSpeed, ySpeed, turnSpeed)

            if (clipSpeed)
                clipMovement()

            /*if (!slowDownDegrees.isNaN() && slowDownDegrees < turnLeft.absoluteValue)
                scaleMovement(1.0 / slowDownAmount)*/

            return Pose(xLeft, yLeft, turnLeft.toRadians)
        }

        fun goToPosition_mirror(x: Double, y: Double, deg: Double, clipSpeed: Boolean = true): Pose {
            val s = ALLIANCE.sign
            val r = goToPosition_raw(x * s, y, deg * s, clipSpeed)
            return Pose(r.x * s, r.y, r.heading.rad * s)
        }

        fun pointAngle_raw(deg: Double): Double {
            val turnLeft = (deg - world_angle_raw.deg).angleWrapDeg
            movement_turn = turnLeft * turnP() - Speedometer.degPerSec * turnD()
            return turnLeft
        }

        fun pointAngle_mirror(deg: Double) = pointAngle_raw(deg * ALLIANCE.sign) * ALLIANCE.sign


    }
}

interface MovementConstantsProvider {
    // slippage prediction factors
    fun getXSlipFactor(): Double

    fun getYSlipFactor(): Double
    fun getTurnSlipFactor(): Double

    // minimum powers
    fun getMinY(): Double

    fun getMinX(): Double
    fun getMinTurn(): Double
}