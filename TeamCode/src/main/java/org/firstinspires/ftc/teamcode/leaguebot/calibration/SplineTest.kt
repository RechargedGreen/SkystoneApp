package org.firstinspires.ftc.teamcode.leaguebot.calibration

import com.acmerobotics.roadrunner.geometry.*
import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.misc.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.roadRunner.*
import org.firstinspires.ftc.teamcode.opmodeLib.*

abstract class SplineTest(alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(0.0, 0.0, 0.0)) {
    enum class progStates {
        forward,
        back,
        nothing
    }

    override fun onMainLoop() {
        val currentState = progStates.values()[stage]
        when (currentState) {
            progStates.forward -> {
                if(changedStage) {
                    SmartTrajectoryBuilder()
                            .splineTo(Pose2d(48.0, 48.0))
                            .start()
                }
                if(TrajectoryFollowing.update())
                    nextStage()
            }
            progStates.back    -> {
                if(changedStage) {
                    SmartTrajectoryBuilder()
                            .splineTo(Pose2d(0.0, 0.0))
                            .start()
                }
                if(TrajectoryFollowing.update())
                    nextStage()
            }
            progStates.nothing -> {
                stopDrive()
            }
        }
    }
}