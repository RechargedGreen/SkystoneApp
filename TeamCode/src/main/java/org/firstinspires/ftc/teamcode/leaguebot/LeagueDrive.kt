package org.firstinspires.ftc.teamcode.leaguebot

import com.acmerobotics.dashboard.config.*
import org.firstinspires.ftc.teamcode.odometry.*

@Config
object LeagueOdometry : ThreeWheelProvider {
    @JvmField
    var turnTrackWidth = 1.0
    @JvmField
    var auxTrackWidth = 1.0
    @JvmField
    var leftTicksPerInch = 1.0
    @JvmField
    var rightTicksPerInch = 1.0
    @JvmField
    var auxTicksPerInch = 1.0

    override fun turnTrackWidth(): Double = turnTrackWidth
    override fun auxTrackWidth(): Double = auxTrackWidth
    override fun leftTicksPerInch(): Double = leftTicksPerInch
    override fun rightTicksPerInch(): Double = rightTicksPerInch
    override fun auxTicksPerInch(): Double = auxTicksPerInch

    fun update() = ThreeWheel.update(0, 0, 0, this)
}