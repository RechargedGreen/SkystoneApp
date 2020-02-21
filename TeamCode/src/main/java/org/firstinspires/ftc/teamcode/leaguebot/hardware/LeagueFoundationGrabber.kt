package org.firstinspires.ftc.teamcode.leaguebot.hardware

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.bulkLib.RevHubServo
import org.firstinspires.ftc.teamcode.opmodeLib.Globals

@Config
class LeagueFoundationGrabber {
    private val leftServo = RevHubServo("rightFoundation")
    private val rightServo = RevHubServo("leftFoundation")

    private val upTimer = ElapsedTime()
    private val actingTimer = ElapsedTime()

    val clearsClaw get() = upTimer.seconds() > 0.8
    private val clawCleared get() = actingTimer.seconds() > 0.25

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
        if (state == State.up)
            actingTimer.reset()
        else
            upTimer.reset()

        val state = if (clawCleared) state else State.up

        val l = state.leftPos()
        val r = state.rightPos()
        if (Globals.mode.movementAllowed) {
            leftServo.position = l
            rightServo.position = r
        }
    }

    companion object {
        @JvmField
        var lDown = 0.1
        @JvmField
        var rDown = 0.77
        @JvmField
        var lUp = 1.0
        @JvmField
        var rUp = 0.0

        @JvmField
        var lPrep = 0.18
        @JvmField
        var rPrep = 0.68
    }
}