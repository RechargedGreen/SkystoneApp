package org.firstinspires.ftc.teamcode.movement.movementAlgorithms

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.lib.RunData.ALLIANCE
import org.firstinspires.ftc.teamcode.movement.DriveMovement.clipMovement
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveFieldCentric_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_raw
import org.firstinspires.ftc.teamcode.movement.Speedometer
import org.firstinspires.ftc.teamcode.movement.toRadians
import org.firstinspires.ftc.teamcode.util.epsilonEquals
import kotlin.math.sign

@Config
object MovementAlgorithms {
    lateinit var movementProvider: MovementConstantsProvider
    fun initAll() {
        PurePursuit.init()
        PointControllers.init()
    }

    @Config
    object PD {
        fun setup(turnP: () -> Double, turnD: () -> Double, moveP: () -> Double, moveD: () -> Double, staticT: () -> Double, staticM: () -> Double) {
            this.turnP = turnP
            this.turnD = turnD
            this.moveP = moveP
            this.moveD = moveD
            this.staticT = staticT
            this.staticM = staticM
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
        var staticM = { 0.0 }
        @JvmField
        var staticT = { 0.0 }

        @JvmField
        var slowDownDegrees = 15.0
        @JvmField
        var slowDownAmount = 8.0

        fun goToPosition_raw(x: Double, y: Double, deg: Double, clipSpeed: Boolean = true, slowDownDegrees: Double = this.slowDownDegrees, slowDownAmount: Double = this.slowDownAmount): Pose {
            val turnLeft = deg - world_angle_raw.deg
            val yLeft = y - world_y_raw
            val xLeft = x - world_x_raw

            val speed = Speedometer.fieldSlipPoint

            val xSpeed = xLeft * moveP() - speed.x * moveD()
            val ySpeed = yLeft * moveP() - speed.y * moveD()
            val turnSpeed = turnLeft * turnP() - Speedometer.degPerSec * turnD()

            var xStatic = if (xSpeed epsilonEquals 0.0) 0.0 else xSpeed.sign * staticM()
            var yStatic = if (ySpeed epsilonEquals 0.0) 0.0 else ySpeed.sign * staticM()
            var tStatic = if (turnSpeed epsilonEquals 0.0) 0.0 else turnSpeed.sign * staticT()

            moveFieldCentric_raw(xSpeed + xStatic, ySpeed + yStatic, turnSpeed + tStatic)

            if (clipSpeed)
                clipMovement()

            /*if (!slowDownDegrees.isNaN() && slowDownDegrees < turnLeft.absoluteValue)
                scaleMovement(1.0 / slowDownAmount)*/

            return Pose(xLeft, yLeft, turnLeft.toRadians)
        }

        fun goToPosition_mirror(x: Double, y: Double, deg: Double, clipSpeed: Boolean = true): Pose {
            val s = ALLIANCE.sign
            val r = goToPosition_raw(x * s, y, deg * s, clipSpeed)
            return Pose(r.x * s, r.y * s, r.heading.rad * s)
        }

        fun pointAngle_raw(deg: Double): Double {
            val turnLeft = deg - world_angle_raw.deg
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