package org.firstinspires.ftc.teamcode.leaguebot.hardware

import org.firstinspires.ftc.teamcode.bulkLib.RevHubMotor
import org.firstinspires.ftc.teamcode.bulkLib.Go_3_7

class MainIntake {
    enum class State {
        STOP,
        IN,
        OUT,
    }

    private val left = RevHubMotor("leftIntake", Go_3_7::class).openLoopControl.float
    private val right = RevHubMotor("rightIntake", Go_3_7::class).openLoopControl.float

    var state = State.STOP

    fun update() {
        val power = when (state) {
            State.STOP -> STOP_POWER
            State.IN -> IN_POWER
            State.OUT -> OUT_POWER
        }

        left.power = power
        right.power = power
    }

    companion object {
        const val IN_POWER = 1.0
        const val STOP_POWER = 0.0
        const val OUT_POWER = -1.0
    }
}