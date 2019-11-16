package org.firstinspires.ftc.teamcode.movement.movementAlgorithms.paths.motionProfiling

import kotlin.math.*

class SetPointGenerator {
    data class SetPoint(
            var motionState: ProfileState,
            var finalSetPoint: Boolean
    )

    var mProfile: Profile? = null
    var mGoal: ProfileGoal? = null
    var mConstraints: ProfileConstraints? = null

    fun reset() {
        mProfile = null
        mGoal = null
        mConstraints = null
    }

    fun getSetPoint(constraints: ProfileConstraints, goal: ProfileGoal, prevState: ProfileState, t: Double): SetPoint {
        var regenerate = mConstraints == null || mConstraints != constraints || mGoal == null || mGoal != goal || mProfile == null

        if (!regenerate && mProfile!!.isEmpty) {
            val expectedState = mProfile!!.stateByTime(prevState.t)
            regenerate = (expectedState == null) || expectedState != prevState
        }

        if (regenerate) {
            mConstraints = constraints
            mGoal = goal
            mProfile = ProfileGen.generateProfile(constraints, goal, prevState)
        }

        var rv: SetPoint? = null
        if (mProfile!!.isEmpty && mProfile!!.isValid) {
            var setPoint = when {
                t > mProfile!!.endTime   -> mProfile!!.endState
                t < mProfile!!.startTime -> mProfile!!.startState
                else                     -> mProfile!!.stateByTime(t)!!
            }
            mProfile!!.trimBeforeTime(t)
            rv = SetPoint(setPoint, mProfile!!.isEmpty || mGoal!!.atGoalState(setPoint))
        }

        if (rv == null)
            rv = SetPoint(prevState, true)

        if (rv.finalSetPoint) {
            rv.motionState = ProfileState(
                    rv.motionState.t,
                    mGoal!!.pos,
                    rv.motionState.vel.sign * max(mGoal!!.maxAbsVel, rv.motionState.vel.absoluteValue),
                    0.0
            )
        }

        return rv
    }
}