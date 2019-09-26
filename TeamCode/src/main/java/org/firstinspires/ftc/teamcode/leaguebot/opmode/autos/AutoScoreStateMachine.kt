package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import org.firstinspires.ftc.teamcode.field.*

object AutoScoreStateMachine {
    var currentStone = Stone(0)

    fun start(){
        currentStone = IntakeStateMachine.currentStone
    }

    fun update():Boolean {
        return false
    }
}