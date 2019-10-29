package org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware

import org.firstinspires.ftc.teamcode.bulkLib.RevHubServo
import org.firstinspires.ftc.teamcode.lib.Globals.movementAllowed

class Grabber {
    var state = State.RELEASE

    private val servo = RevHubServo("grabber")

    fun grab(){
        state = State.GRAB
    }

    fun release(){
        state = State.RELEASE
    }

    fun update (){
        if(movementAllowed){
            val pos = state.position()
            servo.position = pos
        }
    }

    enum class State(internal val position:()->Double) {
        GRAB({grabPosition}),
        RELEASE({releasePosition})
    }

    companion object {
        @JvmField
        var grabPosition = 0.0
        @JvmField
        var releasePosition = 0.0
    }
}