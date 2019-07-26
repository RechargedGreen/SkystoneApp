package org.firstinspires.ftc.teamcode.roverBot.hardware

class RoverIntake {
    enum class State {
        STOP,
        FEED,
        COLLECT,
        EJECT,
        MARKER_EJECT
    }

    var state = State.STOP

    fun stop() {
        state = State.STOP
    }

    fun feed() {
        state = State.FEED
    }

    fun eject() {
        state = State.EJECT
    }

    fun ejectMarker() {
        state = State.MARKER_EJECT
    }

    fun collect() {
        state = State.COLLECT
    }
}