package org.firstinspires.ftc.teamcode.leaguebot.opmode.motionProfileTesting

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.*
import org.firstinspires.ftc.teamcode.lib.*
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.*

class MotionProfileTest : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)) {
    override fun onMainLoop() {
        if (changedStage) {
            RoadRunnerPaths.startFresh()
                    .forward(24.0)
                    .back(24.0)
                    .spline(0.0, 0.0, 90.0, Interpolators.spline(0.0, 90.0))
                    .spline(0.0, 0.0, 90.0, Interpolators.spline(90.0, 0.0))
                    .spline(24.0, 24.0, 0.0)
                    .spline(0.0, 0.0, 0.0)
        }
    }
}