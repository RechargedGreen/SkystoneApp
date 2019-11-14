package org.firstinspires.ftc.teamcode.leaguebot.opmode

import com.acmerobotics.dashboard.config.*
import com.qualcomm.robotcore.util.*
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.*

@Config
object ScorerState {
    @JvmField
    var grabTime = 0.5

    fun triggerExtend() {
        state = State.EXTEND
    }

    fun triggerGrab() {

    }

    fun triggerRelease() {

    }

    fun triggerLoad() {

    }

    enum class State {
        INTAKING,
        GRAB,
        EXTEND,
        RELEASE
    }

    var state = State.INTAKING

    val clearToIntake get() = timeSpentLoading > 0.5

    val timeSpentGrabbing get() = grabberTimer.seconds()
    val timeSpentLoading get() = intakeTimer.seconds()

    private val grabberTimer = ElapsedTime()
    private val intakeTimer = ElapsedTime()

    fun update() {

        when (state) {
            State.INTAKING -> {
                grabberTimer.reset()

                LeagueBot.grabber.state = Grabber.State.LOAD
                LeagueBot.extension.state = Extension.State.IN
            }

            State.GRAB     -> {
                intakeTimer.reset()

                LeagueBot.grabber.state = Grabber.State.GRAB
                LeagueBot.extension.state = Extension.State.IN
            }

            State.EXTEND   -> {
                intakeTimer.reset()

                LeagueBot.grabber.state = Grabber.State.GRAB
                LeagueBot.extension.state = if (timeSpentGrabbing < grabTime) Extension.State.IN else Extension.State.OUT
            }

            State.RELEASE  -> {
                grabberTimer.reset()
                intakeTimer.reset()

                LeagueBot.grabber.state = Grabber.State.RELEASE
                LeagueBot.extension.state = Extension.State.OUT
            }
        }
    }
}