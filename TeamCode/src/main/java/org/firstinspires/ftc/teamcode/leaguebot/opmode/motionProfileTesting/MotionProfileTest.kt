package org.firstinspires.ftc.teamcode.leaguebot.opmode.motionProfileTesting

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.*
import org.firstinspires.ftc.teamcode.lib.*
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.*

class MotionProfileTest : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)) {
    override fun onMainLoop() {
        if (changedStage)
            RoadRunner.setTrajectories {
                arrayListOf(RoadRunner.newBuilder().build())
            }
    }
}