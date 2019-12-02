package org.firstinspires.ftc.teamcode.opmodeLib

import com.acmerobotics.dashboard.canvas.*

object Globals {
    lateinit var mode: BaseMode

    val fieldOverlay: Canvas
        get() = mode.fieldOverlay

    val packet get() = mode.combinedPacket.packet

    val movementAllowed get() = mode.isAutonomous || mode.isStarted
    
    @com.acmerobotics.dashboard.config.Config
    object Config {
        @JvmField
        var debugging = false // if we are in debugging mode we might make some changes like not going to park
    }
}


/**
 * contains data about the current match
 */
object RunData {
    var ALLIANCE = Alliance.RED

    var tripsStarted = 0
    fun reset(){
        tripsStarted = 0
    }
}

enum class Alliance(val sign: Double) {
    RED(1.0),
    BLUE(-1.0);

    fun isRed() = this == RED
}