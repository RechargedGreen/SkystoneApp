package org.firstinspires.ftc.teamcode.leaguebot.opmode.motionProfileTesting

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.leaguebot.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.lib.Alliance
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.RoadRunner

@TeleOp
class MotionProfileStraightTest : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)) {
    override fun onMainLoop() {
        if (changedStage)
            RoadRunner.setTrajectories {
                val builder1 = RoadRunner.newBuilder()
                arrayListOf(builder1.forward(72.0).build())
            }
    }
}

@TeleOp
class MotionProfileTurnTest : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)) {
    override fun onMainLoop() {
        if (changedStage)
            RoadRunner.turn_deg = 90.0
    }
}

@TeleOp
class MotionProfileSplineTest : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)) {
    override fun onMainLoop() {
        if (changedStage)
            RoadRunner.setTrajectories {
                val builder1 = RoadRunner.newBuilder()
                arrayListOf(builder1.splineTo(Pose2d(48.0, -48.0, 0.0)).build())
            }
    }
}

@TeleOp
class MultiTrajectoryTest : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)) {
    override fun onMainLoop() {
        if (changedStage)
            RoadRunner.setTrajectories {
                val traj1 = RoadRunner.newBuilder().splineTo(Pose2d(24.0, 24.0, 0.0)).build()
                val traj2 = RoadRunner.newBuilder(traj1.end()).setReversed(true).splineTo(Pose2d(0.0, 0.0, 0.0)).build()
                arrayListOf(traj1, traj2)
            }
    }
}