package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.*
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.*

object IntakeStateMachine {
    fun start(){
        RoadRunnerPaths.startFresh()
                .spline(0.0, 0.0, 0.0)
                .spline(0.0, 0.0, 0.0)
                .callback { LeagueBot.intake.state = MainIntake.State.IN }
        RoadRunnerPaths.build()
    }

    fun update():Boolean{
        if(RoadRunner.done) {
            LeagueBot.intake.state = MainIntake.State.STOP
            return true
        }
        return false
    }
}