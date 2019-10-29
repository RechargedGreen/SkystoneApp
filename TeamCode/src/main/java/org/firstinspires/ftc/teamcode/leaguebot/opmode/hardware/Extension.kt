package org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.bulkLib.RevHubServo
import org.firstinspires.ftc.teamcode.lib.Globals

@Config
class Extension {
    var state = State.IN

    private val servo = RevHubServo("extension")

    fun retract(){
        state = State.IN
    }

    fun extend(){
        state = State.OUT
    }

    fun update(){
        if(Globals.movementAllowed) {
            val pos = state.position()
            servo.position = pos
        }
    }

    enum class State(internal val position:()->Double) {
        IN({inPosition}),
        OUT({outPosition})
    }

    companion object {
        @JvmField
        var inPosition = 0.0
        @JvmField
        var outPosition = 1.0
    }
}