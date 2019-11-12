package org.firstinspires.ftc.teamcode.leaguebot.opmode.motionProfileTesting

import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.leaguebot.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.lib.Alliance
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.RoadRunner

class MotionProfileTest : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)) {
    override fun onMainLoop() {
        if (changedStage)
            RoadRunner.setTrajectories {
                val builder1 = RoadRunner.newBuilder()
                arrayListOf(builder1.forward(72.0).build())
            }
    }
}