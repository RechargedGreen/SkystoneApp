package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.field.Field
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.leaguebot.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.LeagueBot
import org.firstinspires.ftc.teamcode.lib.Alliance
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveFieldCentric_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.goToPosition_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.pointAngle_mirror
import org.firstinspires.ftc.teamcode.movement.toRadians

abstract class LeagueMeet1Auto(alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(Field.EAST_WALL - LeagueBot.placeLength / 2.0, Field.NORTH_WALL - 24.0 - LeagueBot.placeWidth / 2.0, 90.0.toRadians)) {
    enum class progStates {
        forward,
        grab,
        back,
        delay,
        park,
        endDoNothing
    }

    override fun onMainLoop() {
        val s = progStates.values()[stage]
        telemetry.addData("state", s)
        when (s) {
            progStates.forward -> {
                moveFieldCentric_mirror(-0.3, 0.0, 0.0)
                pointAngle_mirror(90.0)
                if (world_x_mirror < 23.0 - 9.0)
                    nextStage()
            }
            progStates.grab -> {
                stopDrive()
                LeagueBot.foundationGrabber.grab()
                timeoutStage(1.5)
            }
            progStates.back -> {
                moveFieldCentric_mirror(0.3, 0.0, 0.0)
                pointAngle_mirror(90.0)
                timeoutStage(4.0)
            }

            progStates.delay -> {
                stopDrive()
                if (isTimedOut(1.0))
                    LeagueBot.foundationGrabber.release()
                timeoutStage(3.0)
            }

            progStates.park -> {
                goToPosition_mirror(12.0, 0.0, 90.0)
                timeoutStage(3.0)
            }

            progStates.endDoNothing -> {
                stopDrive()
                if (isTimedOut(3.0))
                    requestOpModeStop()
            }
        }
    }
}

@Autonomous
class RedLeagueMeet1Auto : LeagueMeet1Auto(Alliance.RED)

@Autonomous
class BlueLeagueMeet1Auto : LeagueMeet1Auto(Alliance.BLUE)