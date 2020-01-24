package org.firstinspires.ftc.teamcode.leaguebot.autos

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.leaguebot.misc.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot
import org.firstinspires.ftc.teamcode.opmodeLib.Alliance
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.moveFieldCentric_mirror
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_x_mirror
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_y_mirror
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.pointAngle_mirror
import org.firstinspires.ftc.teamcode.movement.toRadians

abstract class FoundationAuto(alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(72.0 - 9.0, 48.0 - 9.0, 90.0.toRadians)) {
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
                Robot.foundationGrabber.prepForGrab()
                moveFieldCentric_mirror(-0.25, 0.5, 0.0)
                if (world_y_mirror > 48.0)
                    hasCrossedY = true
                if (hasCrossedY)
                    moveFieldCentric_mirror(if (world_x_mirror > 36.0) -1.0 else -0.25, 0.0, 0.0)
                if (world_x_mirror < 22.0 + 9.0) {
                    Robot.foundationGrabber.grab()
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
                Robot.foundationGrabber.release()
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

@Autonomous(group = "r")
class Red_Foundatione : FoundationAuto(Alliance.RED)

@Autonomous(group = "b")
class Blue_Foundation : FoundationAuto(Alliance.BLUE)