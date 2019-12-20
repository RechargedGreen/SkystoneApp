package org.firstinspires.ftc.teamcode.movement.roadRunner

import com.acmerobotics.roadrunner.followers.*
import com.acmerobotics.roadrunner.trajectory.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.roadRunnerPose2dRaw
import org.firstinspires.ftc.teamcode.movement.roadRunner.RoadRunnerConstraints.axis_pid
import org.firstinspires.ftc.teamcode.movement.roadRunner.RoadRunnerConstraints.heading_pid

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

        return !isFollowing
    }
}

fun Trajectory.start() {
    TrajectoryFollowing.follow(this)
}