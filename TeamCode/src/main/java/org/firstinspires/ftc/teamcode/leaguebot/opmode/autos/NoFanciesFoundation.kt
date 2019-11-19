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
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_mirror

abstract class NoFanciesFoundation(alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(Field.EAST_WALL - 9.0, Field.NORTH_WALL - 24.0 - 9.0, 90.0)) {
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
                moveFieldCentric_mirror(-0.5, 0.5, 0.0)
                if (world_y_mirror > 60.0)
                    hasCrossedY = true
                if (hasCrossedY)
                    moveFieldCentric_mirror(-1.0, 0.0, 0.0)
                if (world_x_mirror < 22.0 + 9.0)
                    LeagueBot.foundationGrabber.grab()
                if (world_x_mirror < 19.0 + 9.0)
                    nextStage()
            }

            progStates.pull -> {
                stopDrive()
                if(isTimedOut(0.5))
                moveFieldCentric_mirror(1.0, 0.0, 0.0)
                timeoutStage(2.0)
            }

            progStates.wait -> {
                LeagueBot.foundationGrabber.release()
                if (secondsTillEnd < 15.0)
                    nextStage()
            }

            progStates.park -> {
                val x = if (world_x_mirror > 72.0 - 5.0) -0.2 else 0.0
                moveFieldCentric_mirror(x, -1.0, 0.0)
                if (world_y_mirror < 5.0)
                    nextStage()
            }

            progStates.doNothing -> {
                if (isTimedOut(2.0))
                    requestOpModeStop()
            }

        }
    }
}

@Autonomous
class RedNoFanciesFoundatione : NoFanciesFoundation(Alliance.RED)

@Autonomous
class BlueNoFanciesFoundation : NoFanciesFoundation(Alliance.BLUE)