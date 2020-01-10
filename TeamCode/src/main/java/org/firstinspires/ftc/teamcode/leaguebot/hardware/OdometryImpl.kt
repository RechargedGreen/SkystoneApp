package org.firstinspires.ftc.teamcode.leaguebot.hardware

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.bulkLib.cachedInput
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.lynx1
import org.firstinspires.ftc.teamcode.odometry.ThreeWheel
import org.firstinspires.ftc.teamcode.odometry.TwoWheel
import kotlin.math.PI

// new odometry has 48mm 1.88976" wheel diameter

@Config
object RobotOdometry {
    @JvmField
    var turnTrackWidth = 14.77// was 14.5
    @JvmField
    var auxTrackWidth = 6.8 //-0.125// -3.5 was origanal

    @JvmField
    var leftD = 1.88976
    @JvmField
    var rightD = 1.88976
    @JvmField
    var auxD = 1.88976

    const val CPR = 8192.0

    fun inchesPerTick(diameter: Double) = (diameter * PI) / CPR

    val leftTicks: Int get() = -lynx1.cachedInput.getEncoder(3)
    val rightTicks: Int get() = lynx1.cachedInput.getEncoder(0)
    val auxTicks: Int get() = -lynx1.cachedInput.getEncoder(2)

    val leftInches: Double get() = leftTicks * inchesPerTick(leftD)
    val rightInches: Double get() = rightTicks * inchesPerTick(rightD)
    val auxInches: Double get() = auxTicks * inchesPerTick(auxD)

    fun updateThreeWheel() = ThreeWheel.update(leftTicks, rightTicks, auxTicks, inchesPerTick(leftD), inchesPerTick(rightD), inchesPerTick(auxD), turnTrackWidth, auxTrackWidth)

    @JvmField
    var yTrackWidth = -7.274

    fun updateTwoWheel() = TwoWheel.update(rightTicks, auxTicks, -Robot.gyro.angle1_rad, yTrackWidth, auxTrackWidth, inchesPerTick(rightD), inchesPerTick(auxD))
}