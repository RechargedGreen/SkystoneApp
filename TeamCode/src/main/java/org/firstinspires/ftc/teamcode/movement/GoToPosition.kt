package org.firstinspires.ftc.teamcode.movement

class GoToPosition(val x: Double, val y: Double, degrees: Double) : PathSegment {
    val targetAngle = Angle.createWrappedDeg(degrees)

    override fun trigger() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun periodic(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}