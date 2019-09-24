package org.firstinspires.ftc.teamcode.movement.movementAlgorithms

import org.firstinspires.ftc.teamcode.lib.RunData.ALLIANCE
import org.firstinspires.ftc.teamcode.movement.DriveMovement.clipMovement
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveFieldCentric_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_raw
import org.firstinspires.ftc.teamcode.movement.Speedometer

object MovementAlgorithms {
    lateinit var movementProvider: MovementConstantsProvider
    fun initAll() {
        PurePursuit.init()
        PointControllers.init()
    }

    object PD {
        fun setup(turnP: Double, turnD: Double, moveP: Double, moveD: Double) {
            this.turnP = turnP
            this.turnD = turnD
            this.moveP = moveP
            this.moveD = moveD
        }

        @JvmField
        var turnP = 0.02
        @JvmField
        var moveP = 0.2
        @JvmField
        var turnD = 0.0
        @JvmField
        var moveD = 0.0

        fun goToPosition_raw(x: Double, y: Double, deg: Double, clipSpeed: Boolean = true) {
            val turnLeft = deg - world_angle_raw.deg
            val yLeft = y - world_y_raw
            val xLeft = x - world_x_raw

            val speed = Speedometer.fieldSlipPoint

            val xSpeed = xLeft * moveP - speed.x * moveD
            val ySpeed = yLeft * moveP - speed.y * moveD
            val turnSpeed = turnLeft * turnP - Speedometer.degPerSec * turnD
            moveFieldCentric_raw(xSpeed, ySpeed, turnSpeed)

            if (clipSpeed)
                clipMovement()
        }

        fun goToPosition_mirror(x: Double, y: Double, deg: Double, clipSpeed: Boolean = true) = goToPosition_raw(x * ALLIANCE.sign, y, deg * ALLIANCE.sign, clipSpeed)

        fun pointAngle_raw(deg: Double) {
            val turnLeft = deg - world_angle_raw.deg
            movement_turn = turnLeft * turnP - Speedometer.degPerSec * turnD
        }

        fun pointAngle_mirror(deg: Double) = pointAngle_raw(deg * ALLIANCE.sign)


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