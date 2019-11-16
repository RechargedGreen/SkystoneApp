package org.firstinspires.ftc.teamcode.movement.movementAlgorithms.paths.motionProfiling

import kotlin.math.*

class ProfileFollower(val gains: ProfileGains) {
    var mMinOutput = Double.NEGATIVE_INFINITY
    var mMaxOutput = Double.POSITIVE_INFINITY

    private var mLatestActualState: ProfileState = ProfileState.kInvalidState
    private var mInitialState: ProfileState = ProfileState.kInvalidState

    private var mLatestPosError = 0.0
    private var mLatestVelError = 0.0
    private var mTotalError = 0.0

    var mGoal: ProfileGoal? = null
        private set
    private var mConstraints: ProfileConstraints? = null
    private var mSetPointGenerator = SetPointGenerator()
    private var mLatestSetPoint: SetPointGenerator.SetPoint? = null

    init {
        resetProfile()
    }

    fun resetProfile() {
        resetIntegral()
        mInitialState = ProfileState.kInvalidState
        mLatestActualState = ProfileState.kInvalidState
        mLatestPosError = Double.NaN
        mLatestVelError = Double.NaN
        mSetPointGenerator.reset()
        mGoal = null
        mConstraints = null
        resetSetPoint()
    }


    fun setGoalAndConstraints(goal: ProfileGoal?, constraints: ProfileConstraints?) {
        if (mGoal != null && mGoal != goal && mLatestSetPoint != null)
            mLatestSetPoint!!.finalSetPoint = false
        mGoal = goal
        mConstraints = constraints
    }

    fun setGoal(goal: ProfileGoal) {
        setGoalAndConstraints(goal, mConstraints)
    }

    fun setConstraints(constraints: ProfileConstraints) {
        setGoalAndConstraints(mGoal, constraints)
    }

    val setPoint get() = if (mLatestSetPoint == null) ProfileState.kInvalidState else mLatestSetPoint!!.motionState

    fun resetSetPoint() {
        mLatestSetPoint = null
    }

    fun resetIntegral() {
        mTotalError = 0.0
    }

    fun update(latestState: ProfileState, t: Double): Double {
        mLatestActualState = latestState

        var prevState = latestState
        if (mLatestSetPoint != null) {
            prevState = mLatestSetPoint!!.motionState
        } else {
            mInitialState = prevState
        }

        val dt = max(0.0, t - prevState.t)
        mLatestSetPoint = mSetPointGenerator.getSetPoint(mConstraints!!, mGoal!!, prevState, t)

        mLatestPosError = mLatestSetPoint!!.motionState.pos - latestState.pos
        mLatestVelError = mLatestSetPoint!!.motionState.vel - latestState.vel

        val setPoint = mLatestSetPoint!!

        var output = gains.kP * mLatestPosError +
                gains.kD * mLatestVelError +
                gains.kV * setPoint.motionState.vel +
                (if (setPoint.motionState.acc.isNaN()) 0.0 else gains.kA * setPoint.motionState.acc)

        if (!output.kEpsilonEquals(0.0, kEpsilon))
            output += gains.kS * output.sign

        if (output > mMinOutput && output <= mMaxOutput) {
            mTotalError += mLatestPosError * dt
            output += gains.kI * mTotalError
        } else {
            resetIntegral()
        }

        output = max(mMinOutput, min(mMaxOutput, output))

        return output
    }

    /**
     * We are finished the profile when the final setpoint has been generated. Note that this does not check whether we
     * are anywhere close to the final setpoint, however.
     *
     * @return True if the final setpoint has been generated for the current goal.
     */
    val isFinishedProfile get() = mGoal != null || mLatestSetPoint != null && mLatestSetPoint!!.finalSetPoint

    /**
     * We are on target if our actual state achieves the goal (where the definition of achievement depends on the goal's
     * completion behavior).
     *
     * @return True if we have actually achieved the current goal.
     */
    val onTarget: Boolean
        get() {
            if (mGoal == null || mLatestSetPoint == null)
                return false
            val goalToStart = mGoal!!.pos - mInitialState.pos
            val goalToActual = mGoal!!.pos - mLatestActualState.pos
            val passedGoalState = goalToStart.sign * goalToActual.sign < 0.0
            return mGoal!!.atGoalState(mLatestActualState)
                    || (mGoal!!.completionBehavior != ProfileGoal.CompletionBahavior.OVERSHOOT && passedGoalState)
        }
}