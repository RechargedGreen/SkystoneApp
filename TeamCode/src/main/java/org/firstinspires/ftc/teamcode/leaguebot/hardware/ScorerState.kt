package org.firstinspires.ftc.teamcode.leaguebot.hardware

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.util.ElapsedTime

@Config
object ScorerState {
    @JvmField
    var grabTime = 0.5

    @JvmField
    var retractBeforeLoadTime = 0.75

    fun triggerExtend() {
        state = State.EXTEND
    }

    fun triggerGrab() {
        state = State.GRAB
    }

    fun triggerRelease() {
        state = State.RELEASE
    }

    fun triggerLoad() {
        state = State.INTAKING
    }

    fun triggerBackRelease() {
        state = ScorerState.State.BACK_RELEASE
    }

    enum class State {
        INTAKING,
        GRAB,
        EXTEND,
        RELEASE,
        PULL_BACK_WHILE_RELEASED,
        BACK_RELEASE
    }

    fun triggerPullBack() {
        state = State.PULL_BACK_WHILE_RELEASED
    }

    var state = State.INTAKING

    val clearToIntake get() = timeSpentLoading > 0.5
    val clearToLift get() = timeSpentGrabbing > 0.5 || state == State.RELEASE

    val timeSpentGrabbing get() = grabberTimer.seconds()
    val timeSpentLoading get() = intakeTimer.seconds()
    val timeSpentExtended get() = extendTimer.seconds()
    val timeSpentReleased get() = releaseTimer.seconds()

    private val releaseTimer = ElapsedTime()
    private val pullBackTimer = ElapsedTime()
    private val grabberTimer = ElapsedTime()
    private val intakeTimer = ElapsedTime()

    private val extendTimer = ElapsedTime()

    private val retractTimer = ElapsedTime()

    fun update() {
        if (state != State.PULL_BACK_WHILE_RELEASED)
            pullBackTimer.reset()

        if (state != ScorerState.State.RELEASE) {
            releaseTimer.reset()
            if (state != ScorerState.State.EXTEND)
                extendTimer.reset()
        }

        when (state) {
            State.INTAKING -> {
                grabberTimer.reset()

                Robot.grabber.state = if (retractTimer.seconds() > retractBeforeLoadTime) Grabber.State.LOAD else Grabber.State.PRE_LOAD
                Robot.extension.state = Extension.State.IN
            }

            State.GRAB -> {
                intakeTimer.reset()

                Robot.grabber.state = Grabber.State.GRAB
                Robot.extension.state = Extension.State.IN
            }

            State.EXTEND -> {
                retractTimer.reset()
                intakeTimer.reset()

                Robot.grabber.state = Grabber.State.GRAB
                Robot.extension.state = if (timeSpentGrabbing < grabTime) Extension.State.IN else Extension.State.OUT
            }

            State.RELEASE -> {
                retractTimer.reset()
                grabberTimer.reset()
                intakeTimer.reset()

                Robot.grabber.state = Grabber.State.RELEASE
                Robot.extension.state = Extension.State.OUT
            }

            State.PULL_BACK_WHILE_RELEASED -> {
                grabberTimer.reset()
                intakeTimer.reset()

                val seconds = pullBackTimer.seconds()

                Robot.extension.state = Extension.State.IN
                Robot.grabber.state = if (seconds > 1.0) Grabber.State.LOAD else Grabber.State.RELEASE
                if (seconds > 1.0) {
                    state = State.INTAKING
                }
            }
            State.BACK_RELEASE -> {
                intakeTimer.reset()
                grabberTimer.reset()

                Robot.extension.state = Extension.State.IN
                Robot.grabber.state = Grabber.State.RELEASE
            }
        }
    }
}