package org.firstinspires.ftc.teamcode.leaguebot

import com.acmerobotics.dashboard.config.*
import org.firstinspires.ftc.teamcode.bulkLib.*
import org.firstinspires.ftc.teamcode.leaguebot.LeagueBot.lynx2
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.*
import org.firstinspires.ftc.teamcode.odometry.*
import kotlin.math.*

@Config
object LeagueOdometry {
    @JvmField
    var turnTrackWidth = 14.5
    @JvmField
    var auxTrackWidth = 3.5

    @JvmField
    var leftD = 2.83465
    @JvmField
    var rightD = 2.83465
    @JvmField
    var auxD = 2.83465

    const val CPR = 4000.0

    fun inchesPerTick(radius: Double) = (leftD * PI) / CPR

    val leftTicks: Int get() = lynx2.cachedInput.getEncoder(1)
    val rightTicks: Int get() = lynx2.cachedInput.getEncoder(2)
    val auxTicks: Int get() = lynx2.cachedInput.getEncoder(0)

    val leftInches: Double get() = leftTicks * inchesPerTick(leftD)
    val rightInches: Double get() = rightTicks * inchesPerTick(rightD)
    val auxInches: Double get() = auxTicks * inchesPerTick(auxD)

    fun update() = ThreeWheel.update(leftTicks, rightTicks, auxTicks, inchesPerTick(leftD), inchesPerTick(rightD), inchesPerTick(auxD), turnTrackWidth, auxTrackWidth)
}

@Config
object LeagueMovementConstants : MovementConstantsProvider {
    @JvmField
    var xSlipFactor = 1.0
    @JvmField
    var ySlipFactor = 1.0
    @JvmField
    var turnSlipFactor = 1.0
    @JvmField
    var minY = 0.0
    @JvmField
    var minX = 0.0
    @JvmField
    var minTurn = 0.0

    override fun getXSlipFactor(): Double = xSlipFactor
    override fun getYSlipFactor(): Double = ySlipFactor
    override fun getTurnSlipFactor(): Double = turnSlipFactor
    override fun getMinY(): Double = minX
    override fun getMinX(): Double = minY
    override fun getMinTurn(): Double = minTurn
}