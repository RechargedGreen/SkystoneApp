package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.leaguebot.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.LeagueBot
import org.firstinspires.ftc.teamcode.lib.Alliance
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveFieldCentric_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.pointAngle_mirror
import org.firstinspires.ftc.teamcode.movement.toRadians

abstract class NoFanciesFoundation(alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(72.0 - 9.0, 48.0 - 9.0, 90.0.toRadians)) {
    enum class progStates {
        grab,
        pull,
        wait,
        park,
        doNothing
    }

    var hasCrossedY = false

    override fun onMainLoop() {
        val currentState = progStates.values()[stage]

        stopDrive()

        when (currentState) {
            progStates.grab -> {
                LeagueBot.foundationGrabber.prepForGrab()
                moveFieldCentric_mirror(-0.25, 0.5, 0.0)
                if (world_y_mirror > 48.0)
                    hasCrossedY = true
                if (hasCrossedY)
                    moveFieldCentric_mirror(if (world_x_mirror > 36.0) -1.0 else -0.25, 0.0, 0.0)
                if (world_x_mirror < 22.0 + 9.0) {
                    LeagueBot.foundationGrabber.grab()
                    nextStage()
                }
            }

            progStates.pull -> {
                stopDrive()
                if (isTimedOut(0.5))
                    moveFieldCentric_mirror(1.0, 0.0, 0.0)
                timeoutStage(2.0)
            }

            progStates.wait -> {
                LeagueBot.foundationGrabber.release()
                if (secondsTillEnd < 3.0)
                    nextStage()
            }

            progStates.park -> {
                val x = if (world_x_mirror > 72.0 - 5.0) -0.2 else 0.0
                moveFieldCentric_mirror(x, -1.0, 0.0)
                if (world_y_mirror < 5.0)
                    nextStage()
            }

            progStates.doNothing -> {
                if (isTimedOut(1.0))
                    requestOpModeStop()
            }
        }
        if (currentState != progStates.doNothing)
            pointAngle_mirror(90.0)
    }
}

@Autonomous
class Red_NoFanciesFoundatione : NoFanciesFoundation(Alliance.RED)

@Autonomous
class Blue_NoFanciesFoundation : NoFanciesFoundation(Alliance.BLUE)