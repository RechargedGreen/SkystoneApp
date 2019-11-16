package org.firstinspires.ftc.teamcode.movement.movementAlgorithms.paths.motionProfiling

import kotlin.math.*

data class ProfileGoal(
        var pos: Double,
        var maxAbsVel: Double = 0.0,
        var completionBehavior: CompletionBahavior = CompletionBahavior.OVERSHOOT,
        var posTolerance: Double = 1E-3,
        var velTolerance: Double = 1E-2
) {
    enum class CompletionBahavior {
        OVERSHOOT, // Overshoot the goal if necessary (at a velocity greater than max_abs_vel) and come back. Only valid if the goal velocity is 0.0 (otherwise VIOLATE_MAX_ACCEL will be used).
        VIOLATE_MAX_ACCEL, // If we cannot slow down to the goal velocity before crossing the goal, allow exceeding the max accel constraint.
        VIOLATE_MAX_ABS_VEL // If we cannot slow down to the goal velocity before crossing the goal, allow exceeding the goal velocity.
    }

    val flipped get() = ProfileGoal(-pos, maxAbsVel, completionBehavior, posTolerance, velTolerance)

    fun atGoalState(state: ProfileState) = atGoalPos(state.pos) && (state.vel.absoluteValue < maxAbsVel + velTolerance || completionBehavior == CompletionBahavior.VIOLATE_MAX_ABS_VEL)

    fun atGoalPos(pos: Double) = pos.kEpsilonEquals(this.pos, posTolerance)

    fun sanityCheck() {
        if (maxAbsVel > velTolerance && completionBehavior == CompletionBahavior.OVERSHOOT)
            completionBehavior = CompletionBahavior.VIOLATE_MAX_ACCEL
    }
}