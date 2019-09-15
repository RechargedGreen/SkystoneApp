package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.*
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.*
import org.firstinspires.ftc.teamcode.lib.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveFieldCentric_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.goToPosition_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.pointAngle_mirror
import kotlin.math.*

abstract class LeagueFoundationAuto(alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(Field.EAST_WALL - LeagueBot.placeLength / 2.0, Field.NORTH_WALL - 24.0 - LeagueBot.placeWidth / 2.0, Math.toRadians(-90.0))) {
    enum class progStates {
        grab,
        pull,
        wait,
        park,
        completePark,
        doNothing
    }

    val pullSlowSpeed = 0.3

    val parkTime = 3.0

    override fun onInitLoop() {
    }

    override fun onMainLoop() {
        val currentStage = progStates.values()[stage]
        telemetry.addData("stage", currentStage)

        when (currentStage) {
            progStates.grab         -> {
                moveFieldCentric_mirror(-1.0, 0.0, 0.0)
                pointAngle_mirror(-90.0)
                if (world_x_mirror < 24.0 + 9.0)
                    nextStage()
            }

            progStates.pull         -> {
                LeagueBot.foundationGrabber.grab()
                stopDrive()
                if (stageTimer.seconds() > 0.25) {
                    moveFieldCentric_mirror(if (world_x_mirror > Field.EAST_WALL - 9.0 - 6.0) pullSlowSpeed else 1.0, 0.0, 0.0)
                    pointAngle_mirror(-90.0)
                }

                timeoutStage(3.0)
            }

            progStates.wait         -> {
                stopDrive()

                LeagueBot.foundationGrabber.release()
                if (secondsTillEnd < parkTime)
                    nextStage()
            }

            progStates.park         -> {
                goToPosition_mirror(Field.EAST_WALL - 20, Field.NORTH_WALL / 2.0, -90.0)

                if ((world_y_mirror - Field.NORTH_WALL / 2.0).absoluteValue < 2.0)
                    nextStage()
            }

            progStates.completePark -> {
                moveFieldCentric_mirror(0.3, 0.0, 0.0)
                timeoutStage(1.0)
            }

            progStates.doNothing    -> {
                stopDrive()
            }
        }
    }
}

@Autonomous
class RedLeagueFoundation : LeagueFoundationAuto(Alliance.RED)

@Autonomous
class BlueLeagueFoundation : LeagueFoundationAuto(Alliance.BLUE)

