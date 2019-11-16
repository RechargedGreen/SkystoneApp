package org.firstinspires.ftc.teamcode.movement.movementAlgorithms.paths.motionProfiling

import kotlin.math.*


data class ProfileState(
        val t: Double,
        val pos: Double,
        val vel: Double,
        val acc: Double
) {

    val vel2 get() = vel * vel

    fun extrapolate(t: Double) = extrapolate(t, acc)

    fun extrapolate(t: Double, acc: Double): ProfileState {
        val dt = t - this.t
        return ProfileState(
                t,
                pos + vel * dt + 0.5 * acc * dt * dt,
                vel + acc * dt,
                acc
        )
    }

    fun nextTimeAtPos(pos: Double): Double {
        if (pos.kEpsilonEquals(this.pos, kEpsilon))
            return t
        if (acc.kEpsilonEquals(0.0, kEpsilon)) {
            val deltaPos = pos - this.pos
            if (!vel.kEpsilonEquals(0.0, kEpsilon) && deltaPos.sign == vel.sign)
                return deltaPos / vel + t
            return Double.NaN
        }

        // Solve the quadratic formula.
        // ax^2 + bx + c == 0
        // x = dt
        // a = .5 * acc
        // b = vel
        // c = this.pos - pos

        val disc = vel * vel - 2.0 * acc * (this.pos - pos);
        if (disc < 0.0)
            return Double.NaN // Extrapolating this MotionState never reaches the desired pos.

        val sqrt_disc = Math.sqrt(disc);
        val max_dt = (-vel + sqrt_disc) / acc;
        val min_dt = (-vel - sqrt_disc) / acc;
        if (min_dt >= 0.0 && (max_dt < 0.0 || min_dt < max_dt))
            return t + min_dt
        if (max_dt >= 0.0)
            return t + max_dt
        // We only reach the desired pos in the past.
        return Double.NaN
    }

    /**
     * Checks if two MotionStates are epsilon-equals (all fields are equal within a nominal tolerance).
     */
    override operator fun equals(other: Any?): Boolean {
        return (other is ProfileState) && equals(other, kEpsilon)
    }

    /**
     * Checks if two MotionStates are epsilon-equals (all fields are equal within a specified tolerance).
     */
    fun equals(other: ProfileState, epsilon: Double): Boolean {
        return coincident(other, epsilon) && acc.kEpsilonEquals(other.acc, kEpsilon)
    }

    /**
     * Checks if two MotionStates are coincident (t, pos, and vel are equal within a specified tolerance, but
     * acceleration may be different).
     */
    fun coincident(other: ProfileState, epsilon: Double = kEpsilon): Boolean {
        return (t.kEpsilonEquals(other.t, epsilon) && pos.kEpsilonEquals(other.pos, epsilon)
                && vel.kEpsilonEquals(other.vel, epsilon))
    }

    val flipped get() = ProfileState(t, -pos, -vel, -acc)

    companion object {
        val kInvalidState = ProfileState(Double.NaN, Double.NaN, Double.NaN, Double.NaN)
    }
}