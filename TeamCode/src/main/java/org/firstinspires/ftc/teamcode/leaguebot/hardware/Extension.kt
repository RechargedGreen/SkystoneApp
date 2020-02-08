package org.firstinspires.ftc.teamcode.leaguebot.hardware

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.bulkLib.RevHubServo
import org.firstinspires.ftc.teamcode.opmodeLib.Globals

@Config
class Extension {
    var state = State.IN

    private val servo = RevHubServo("grabberExtend")

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
        var inPosition = 0.89 // 0.0 with gobilda
        @JvmField
        var outPosition = 0.065 // 1.0 with gobilda
    }
}