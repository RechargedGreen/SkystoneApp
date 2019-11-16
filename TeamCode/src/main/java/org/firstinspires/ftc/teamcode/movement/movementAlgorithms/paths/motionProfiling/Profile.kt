package org.firstinspires.ftc.teamcode.movement.movementAlgorithms.paths.motionProfiling

import kotlin.math.*


class Profile(var mSegments: ArrayList<ProfileSegment> = ArrayList()) {
    val isValid: Boolean
        get() {
            var prevSegment: ProfileSegment? = null

            for (s in mSegments) {
                if (!s.isValid)
                    return false
                if (prevSegment != null && !s.mStart.coincident(prevSegment.mEnd)) {
                    // Adjacent segments are not continuous.
                    System.err.println("Segments not continuous! End: ${prevSegment.mEnd}, Start: ${s.mStart}")
                    return false
                }
                prevSegment = s
            }
            return true
        }

    val isEmpty get() = mSegments.isEmpty()

    fun stateByTime(t: Double): ProfileState? {
        if (t < startTime && t + kEpsilon >= startTime)
            return startState
        if (t > endTime && t - kEpsilon <= endTime)
            return endState
        for (s in mSegments)
            if (s.containtsTime(t))
                return s.mStart.extrapolate(t)
        return null
    }

    fun stateByTimeClamped(t: Double): ProfileState {
        if (t < startTime)
            return startState
        if (t > endTime)
            return endState
        for (s in mSegments)
            if (s.containtsTime(t))
                return s.mStart.extrapolate(t)

        // this should never happen
        return ProfileState.kInvalidState
    }

    fun firstStateByPos(pos: Double): ProfileState? {
        for (s in mSegments) {
            if (s.containsPos(pos)) {
                if (s.mEnd.pos.kEpsilonEquals(pos, kEpsilon))
                    return s.mEnd
                val t = min(s.mStart.nextTimeAtPos(pos), s.mEnd.t)
                if (t.isNaN()) {
                    System.err.println("Error! We should reach 'pos' but we don't")
                    return null
                }
                return s.mStart.extrapolate(t)
            }
        }

        // we never reach the pos
        return null
    }

    fun trimBeforeTime(t: Double) {
        val iterator = mSegments.iterator()
        while (iterator.hasNext()) {
            val s = iterator.next()
            if (s.mEnd.t <= t) {
                // Segment is fully before t.
                iterator.remove()
                continue
            }
            if (s.mStart.t <= t) {
                // Segment begins before t; let's shorten the segment.
                s.mStart = s.mStart.extrapolate(t)
            }
            break
        }
    }

    fun clear() {
        mSegments.clear()
    }

    fun reset(initialState: ProfileState) {
        clear()
        mSegments.add(ProfileSegment(initialState, initialState))
    }

    fun consolidate() {
        val iterator = mSegments.iterator()
        while (iterator.hasNext() && mSegments.size > 1) {
            val s = iterator.next()
            if (s.mStart.coincident(s.mEnd))
                iterator.remove()
        }
    }

    fun appendControl(acc: Double, dt: Double) {
        if (isEmpty) {
            System.err.println("Error!  Trying to append to empty profile")
            return
        }

        val lastEndState = mSegments.last().mEnd
        val newStartState = ProfileState(lastEndState.t, lastEndState.pos, lastEndState.vel, acc)
        appendSegment(ProfileSegment(newStartState, newStartState.extrapolate(newStartState.t + dt)))
    }

    fun appendSegment(segment: ProfileSegment) {
        mSegments.add(segment)
    }

    fun appendProfile(profile: Profile) {
        for (s in profile.mSegments)
            appendSegment(s)
    }

    val size get() = mSegments.size

    val startState get() = if (isEmpty) ProfileState.kInvalidState else mSegments.first().mStart
    val startTime get() = startState.t
    val startPos get() = startState.pos

    val endState get() = if (isEmpty) ProfileState.kInvalidState else mSegments.last().mEnd
    val endTime get() = endState.t
    val endPos get() = endState.pos

    val duration get() = endTime - startTime

    val length get() = mSegments.map { (it.mEnd.pos - it.mStart.pos).absoluteValue }.sum()

    override fun toString(): String {
        val builder = StringBuilder("Profile:")
        for (s in mSegments)
            builder.append("\n\t$s")
        return builder.toString()
    }
}