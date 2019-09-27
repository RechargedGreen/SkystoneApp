package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.field.Field
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.field.Quarry
import org.firstinspires.ftc.teamcode.leaguebot.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.LeagueBot
import org.firstinspires.ftc.teamcode.lib.Alliance
import org.firstinspires.ftc.teamcode.lib.Globals
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveFieldCentric_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.goToPosition_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.moveP
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.pointAngle_mirror
import kotlin.math.absoluteValue

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
            progStates.intake -> {
                if (changedStage)
                    IntakeStateMachine.start()
                val isDone = IntakeStateMachine.update()
                if (isDone)
                    nextStage()
            }

            progStates.score -> {
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

            progStates.startPark -> stopDrive()

            /*progStates.startPark -> {
                goToPosition_mirror(48.0, 0.0, -180.0)
                if (world_y_mirror.absoluteValue < 2.0)
                    nextStage()
            }

            progStates.finishPark -> {
                moveFieldCentric_mirror(-0.3, -world_y_mirror * moveP(), 0.0)
                pointAngle_mirror(-180.0)

                val timedOut = isTimedOut(2.0)

                if (timedOut || world_x_mirror < 24.0 + 3.0 + LeagueBot.placeWidth)
                    nextStage()
            }*/

            progStates.endDoNothing -> {
                LeagueBot.endDoNothing()
            }
        }

        if (stage < LeagueFoundationAuto.progStates.park.ordinal && !Globals.Config.debugging && secondsTillEnd < 2.0)
            nextStage(LeagueFoundationAuto.progStates.park.ordinal)
    }
}

@Autonomous
class RedLeagueCycle : LeagueCycleAuto(Alliance.RED)

@Autonomous
class BlueLeagueCycle : LeagueCycleAuto(Alliance.BLUE)