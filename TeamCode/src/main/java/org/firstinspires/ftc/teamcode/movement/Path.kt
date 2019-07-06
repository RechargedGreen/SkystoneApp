package org.firstinspires.ftc.teamcode.movement

class Path(vararg segments: PathSegment) {
    val remainingSegments = arrayListOf(*segments)
    var currentSegment: PathSegment? = null
        private set

    fun follow(): Boolean {
        if (currentSegment == null) {
            if (remainingSegments.isEmpty())
                return true
            currentSegment = remainingSegments.first()
            remainingSegments.removeAt(0)
            currentSegment!!.trigger()
        }

        if (currentSegment!!.periodic())
            currentSegment = null

        return currentSegment == null && remainingSegments.isEmpty()
    }

    fun addSegments(vararg segments: PathSegment) = remainingSegments.addAll(segments)
}