package org.firstinspires.ftc.teamcode.leaguebot

import com.acmerobotics.dashboard.config.*
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.*
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

@Config
object LeagueMovementConstants : MovementConstantsProvider {
    @JvmField
    var xSlipFactor = 1.0

    override fun getXSlipFactor(): Double = xSlipFactor

    override fun getYSlipFactor(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTurnSlipFactor(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMinY(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMinX(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMinTurn(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}