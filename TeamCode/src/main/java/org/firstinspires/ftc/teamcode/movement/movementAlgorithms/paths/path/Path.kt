package org.firstinspires.ftc.teamcode.movement.movementAlgorithms.paths.path

import com.acmerobotics.roadrunner.geometry.*
import com.acmerobotics.roadrunner.path.*
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.paths.motionProfiling.*

class DisplacementTrajectory(var constraints: ProfileConstraints, var path: Path, var fineTune: Boolean = false) {
    var speedProfile = ProfileGen.generateProfile(constraints, ProfileGoal(path.length(), 0.0, ProfileGoal.CompletionBahavior.OVERSHOOT), ProfileState(0.0, 0.0, 0.0, 0.0))

    fun stateAtDisplacement(d: Double) = speedProfile.firstStateByPos(d)!!

    fun velocityAtDisplacement(d: Double): Pose2d {
        val motionState = stateAtDisplacement(d)
        return path.deriv(motionState.pos) * motionState.vel
    }

    fun stateAtTime(t: Double) = speedProfile.stateByTime(t)!!

    fun velocityAtTime(t: Double): Pose2d {
        val motionState = stateAtTime(t)
        return path.deriv(motionState.pos) * motionState.vel
    }
}

class PathContainer(constraints: ProfileConstraints, vararg val paths: Path, fineTuneLast: Boolean = false) {
    val trajectories = Array(paths.size) { i -> DisplacementTrajectory(constraints, paths[i], fineTuneLast && i == paths.size - 1) }
    private var index = 0

    val nextTrajectory: DisplacementTrajectory
        get() {
            val t = trajectories[index]
            index++
            return t
        }
    val hasNewPath = index < trajectories.size
}