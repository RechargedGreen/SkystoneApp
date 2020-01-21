package org.firstinspires.ftc.teamcode.leaguebot.hardware

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.teamcode.bulkLib.RevHubServo
import org.firstinspires.ftc.teamcode.opmodeLib.Globals

@Config
class LeagueFoundationGrabber {
    private val leftServo = RevHubServo("rightFoundation")
    private val rightServo = RevHubServo("leftFoundation")// todo make not jank name


    enum class State(val leftPos: () -> Double, val rightPos: () -> Double) {
        up({ lUp }, { rUp }),
        down({ lDown }, { rDown }),
        prep({ lPrep }, { rPrep })
    }

    var state = State.up

    fun grab() {
        state = State.down
    }

    fun release() {
        state = State.up
    }

    fun prepForGrab() {
        state = State.prep
    }

    fun update() {
        val l = state.leftPos()
        val r = state.rightPos()
        if (Globals.mode.movementAllowed) {
            leftServo.position = l
            rightServo.position = r
        }
    }

    companion object {
        @JvmField
        var lDown = 1.0
        @JvmField
        var rDown = 0.0
        @JvmField
        var lUp = 0.0
        @JvmField
        var rUp = 1.0

        @JvmField
        var lPrep = 0.67
        @JvmField
        var rPrep = 0.2
    }
}