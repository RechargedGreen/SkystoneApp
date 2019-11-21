package org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.bulkLib.cachedInput
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.LeagueBot.lynx1
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.LeagueBot.lynx2
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementConstantsProvider
import org.firstinspires.ftc.teamcode.odometry.ThreeWheel
import org.firstinspires.ftc.teamcode.odometry.TwoWheel
import kotlin.math.PI

@Config
object LeagueThreeWheelOdometry {
    @JvmField
    var turnTrackWidth = 14.574// was 14.5
    @JvmField
    var auxTrackWidth = -0.125// -3.5 was origanal

    @JvmField
    var leftD = 2.83465
    @JvmField
    var rightD = 2.83465
    @JvmField
    var auxD = 2.83465

    const val CPR = 4000.0

    fun inchesPerTick(radius: Double) = (radius * PI) / CPR

    val leftTicks: Int get() = -lynx1.cachedInput.getEncoder(3)
    val rightTicks: Int get() = lynx1.cachedInput.getEncoder(0)
    val auxTicks: Int get() = -lynx1.cachedInput.getEncoder(2)

    val leftInches: Double get() = leftTicks * inchesPerTick(leftD)
    val rightInches: Double get() = rightTicks * inchesPerTick(rightD)
    val auxInches: Double get() = auxTicks * inchesPerTick(auxD)

    fun updateThreeWheel() = ThreeWheel.update(leftTicks, rightTicks, auxTicks, inchesPerTick(leftD), inchesPerTick(rightD), inchesPerTick(auxD), turnTrackWidth, auxTrackWidth)

    @JvmField
    var yTrackWidth = 7.25

    fun updateTwoWheel() = TwoWheel.update(leftTicks, auxTicks, LeagueBot.gyro.heading_rad, yTrackWidth, auxTrackWidth, inchesPerTick(leftD), inchesPerTick(auxD))
}

@Config
object LeagueMovementConstants : MovementConstantsProvider {
    @JvmField
    var moveP = 0.04
    @JvmField
    var moveD = 0.0
    @JvmField
    var turnP = 0.06
    @JvmField
    var turnD = 0.0
    @JvmField
    var staticT = 0.08
    @JvmField
    var staticY = 0.052
    @JvmField
    var staticX = 0.052

    fun setup() {
        MovementAlgorithms.PD.setup({ turnP }, { turnD }, { moveP }, { moveD }, {staticT}, {staticY}, {staticX})
    }

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