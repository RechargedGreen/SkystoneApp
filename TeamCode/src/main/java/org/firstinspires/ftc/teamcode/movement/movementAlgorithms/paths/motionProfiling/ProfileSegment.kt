package org.firstinspires.ftc.teamcode.movement.movementAlgorithms.paths.motionProfiling

data class ProfileSegment(
        var mStart: ProfileState,
        var mEnd: ProfileState
) {
    val isValid: Boolean
        get() {
            if (!mStart.acc.kEpsilonEquals(mEnd.acc, kEpsilon)) {
                System.err.println("Segment acceleration not constant! Start acc: ${mStart.acc}, end acc: ${mEnd.acc}")
                return false
            }
            if (mStart.vel * mEnd.vel < 0.0 && !mStart.vel.kEpsilonEquals(0.0, kEpsilon) && !mEnd.vel.kEpsilonEquals(0.0, kEpsilon)) {
                // Velocity direction reverses within the segment.
                System.err.println("Segment velocity reverses! Start vel: ${mStart.vel}, End vel: ${mEnd.vel}")
                return false
            }
            if (mStart.extrapolate(mEnd.t) != (mEnd)) {
                // A single segment is not consistent.
                if (mStart.t == mEnd.t && mStart.acc.isInfinite()) // One allowed exception: If acc is infinite and dt is zero.
                    return true
                System.err.println("Segment not consistent! Start: $mStart, End: $mEnd")
                return false
            }
            return true
        }

    fun containtsTime(t: Double) = t >= mStart.t && t <= mEnd.t

    fun containsPos(pos: Double) = pos >= mStart.pos && pos <= mEnd.pos || pos <= mStart.pos && pos >= mEnd.pos
}