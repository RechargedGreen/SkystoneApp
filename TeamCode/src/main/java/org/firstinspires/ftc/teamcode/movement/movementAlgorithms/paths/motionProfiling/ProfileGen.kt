package org.firstinspires.ftc.teamcode.movement.movementAlgorithms.paths.motionProfiling

import kotlin.math.*

object ProfileGen {
    fun generateFlippedProfile(constraints: ProfileConstraints, goalState: ProfileGoal, prevState: ProfileState): Profile {
        val profile = generateProfile(constraints, goalState.flipped, prevState.flipped)
        for (s in profile.mSegments) {
            s.mStart = s.mStart.flipped
            s.mEnd = s.mEnd.flipped
        }
        return profile
    }

    fun generateProfile(constraints: ProfileConstraints, goalState: ProfileGoal, prevState: ProfileState): Profile {
        var delta_pos = goalState.pos - prevState.pos
        if (delta_pos < 0.0 || (delta_pos == 0.0 && prevState.vel < 0.0))
            return generateFlippedProfile(constraints, goalState, prevState)

        var startState = ProfileState(prevState.t, prevState.pos,
                                      prevState.vel.sign * min(prevState.vel.absoluteValue, constraints.maxVelocity),
                                      prevState.acc.sign * min(prevState.acc.absoluteValue, constraints.maxAcceleration))
        var profile = Profile()
        profile.reset(startState)

        if (startState.vel < 0.0 && delta_pos > 0.0) {
            val stoppingTime = (startState.vel / constraints.maxAcceleration).absoluteValue
            profile.appendControl(constraints.maxAcceleration, stoppingTime)
            startState = profile.endState
            delta_pos = goalState.pos - startState.pos
        }

        val minAbsVelAtGoalSqr = startState.vel2 - 2.0 * constraints.maxAcceleration * delta_pos
        val minAbsVelAtGoal = sqrt(minAbsVelAtGoalSqr.absoluteValue)
        val maxAbsVelAtGoal = sqrt(startState.vel2 + 2.0 * constraints.maxAcceleration * delta_pos)

        var goalVel = goalState.maxAbsVel
        var maxAcc = constraints.maxAcceleration

        if (minAbsVelAtGoalSqr < 0.0 && minAbsVelAtGoal > (goalState.maxAbsVel + goalState.velTolerance)) {
            when (goalState.completionBehavior) {
                ProfileGoal.CompletionBahavior.VIOLATE_MAX_ABS_VEL -> goalVel = minAbsVelAtGoal
                ProfileGoal.CompletionBahavior.VIOLATE_MAX_ACCEL   -> {
                    if (delta_pos.absoluteValue < goalState.posTolerance) {
                        profile.appendSegment(ProfileSegment(
                                ProfileState(profile.endTime, profile.endPos, profile.endState.vel, Double.NEGATIVE_INFINITY),
                                ProfileState(profile.endTime, profile.endPos, goalVel, Double.NEGATIVE_INFINITY)
                        ))
                        profile.consolidate()
                        return profile
                    }
                    maxAcc = (goalVel * goalVel - startState.vel2) / (2.0 * delta_pos).absoluteValue
                }
                ProfileGoal.CompletionBahavior.OVERSHOOT           -> {
                    val stoppingTime = (startState.vel / constraints.maxAcceleration).absoluteValue
                    profile.appendControl(-constraints.maxAcceleration, stoppingTime)
                    profile.appendProfile(generateFlippedProfile(constraints, goalState, profile.endState))
                    profile.consolidate()
                    return profile
                }
            }
        }

        goalVel = min(goalVel, maxAbsVelAtGoal)

        val vMax = min(constraints.maxVelocity,
                       sqrt((startState.vel2 + goalVel * goalVel) / 2.0 + delta_pos * maxAcc));

        if (vMax > startState.vel) {
            val accelTime = (vMax - startState.vel) / maxAcc;
            profile.appendControl(maxAcc, accelTime);
            startState = profile.endState
        }
        val distance_decel = max(0.0, (startState.vel2 - goalVel * goalVel) / (2.0 * constraints.maxAcceleration))
        val distance_cruise = Math.max(0.0, goalState.pos - startState.pos - distance_decel)
        if (distance_cruise > 0.0) {
            val cruise_time = distance_cruise / startState.vel
            profile.appendControl(0.0, cruise_time)
            startState = profile.endState
        }
        if (distance_decel > 0.0) {
            val decelTime = (startState.vel - goalVel) / maxAcc
            profile.appendControl(-maxAcc, decelTime)
        }


        profile.consolidate()
        return profile
    }
}