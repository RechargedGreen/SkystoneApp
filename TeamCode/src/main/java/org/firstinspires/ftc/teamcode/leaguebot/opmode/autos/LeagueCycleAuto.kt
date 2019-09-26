package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.*
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.*
import org.firstinspires.ftc.teamcode.lib.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive

abstract class LeagueCycleAuto(alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(Field.EAST_WALL - LeagueBot.placeLength / 2.0, Field.SOUTH_WALL + 48.0 - LeagueBot.placeWidth / 2.0, Math.toRadians(-90.0))) {
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

            progStates.startPark    -> {
                stopDrive()
            }

            progStates.finishPark   -> {
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