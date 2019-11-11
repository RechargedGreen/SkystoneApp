package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.*
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.*

object IntakeStateMachine {
    fun start() {

    }

    fun update(): Boolean {
        if (RoadRunner.done) {
            LeagueBot.intake.state = MainIntake.State.STOP
            return true
        }
        return false
    }
}