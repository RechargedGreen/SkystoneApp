package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.*
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.*
import org.firstinspires.ftc.teamcode.lib.*

abstract class LeagueCycleAuto(alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(0.0, 0.0, Math.toRadians(-90.0))) {
    enum class progStates {
        intake,
        score,
        startPark,
        finishPark,
        endDoNothing
    }

    override fun onMainLoop() {
        val currentStage = progStates.values()[stage]
        telemetry.addData("stage", currentStage)

        when (currentStage) {
            progStates.intake       -> {
                if (changedStage)
                    IntakeStateMachine.start()
                val isDone = IntakeStateMachine.update()
                if (isDone)
                    nextStage()
            }

            progStates.score        -> {
                if (changedStage)
                    AutoScoreStateMachine.start()
                val isDone = AutoScoreStateMachine.update()
                if (isDone) {
                    if (Quarry.doneWithStones())
                        nextStage()
                    else
                        nextStage(progStates.intake.ordinal)
                }
            }

            progStates.endDoNothing -> {
                LeagueBot.endDoNothing()
            }
        }
    }
}

@Autonomous
class RedLeagueCycle : LeagueCycleAuto(Alliance.RED)

@Autonomous
class BlueLeagueCycle : LeagueCycleAuto(Alliance.BLUE)