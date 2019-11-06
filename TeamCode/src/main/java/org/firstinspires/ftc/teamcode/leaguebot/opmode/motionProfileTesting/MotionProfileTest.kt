package org.firstinspires.ftc.teamcode.leaguebot.opmode.motionProfileTesting

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.*
import org.firstinspires.ftc.teamcode.lib.*
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.*

class MotionProfileTest : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)) {
    override fun onMainLoop() {
        if (changedStage) {
            RoadRunnerPaths.startFresh()
                    .spline(0.0, 0.0, 0.0)
                    .spline(10.0, 10.0, 0.0)
                    .callback {
                        RoadRunnerPaths.startInterrupted()
                                .spline(0.0, 0.0, 0.0)
                                .build()
                    }
                    .reverse()
                    .spline(0.0, 0.0, 0.0)
                    .build()
        }
    }
}