package org.firstinspires.ftc.teamcode.movement.roadRunner

import com.acmerobotics.roadrunner.followers.*
import com.acmerobotics.roadrunner.trajectory.*
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.roadRunnerPose2dRaw
import org.firstinspires.ftc.teamcode.movement.roadRunner.RoadRunnerConstraints.axis_pid
import org.firstinspires.ftc.teamcode.movement.roadRunner.RoadRunnerConstraints.heading_pid
import org.firstinspires.ftc.teamcode.opmodeLib.Globals.mode

object TrajectoryFollowing {
    val follower = HolonomicPIDVAFollower(axis_pid, axis_pid, heading_pid)
    private lateinit var currentTrajectory: Trajectory

    fun follow(trajectory: Trajectory) {
        currentTrajectory = trajectory
    }

    fun update(): Boolean {
        val signal = follower.update(roadRunnerPose2dRaw)
        val isFollowing = follower.isFollowing()

        RoadRunnerConstraints.setVelocity(signal.vel)

        val error = follower.lastError
        mode.combinedPacket.put("xError", error.x)
        mode.combinedPacket.put("yError", error.x)
        mode.combinedPacket.put("radError", error.heading)
        mode.combinedPacket.put("degError", error.heading.toDegrees)

        return !isFollowing
    }
}

fun Trajectory.start() {
    TrajectoryFollowing.follow(this)
}