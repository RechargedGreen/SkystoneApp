package org.firstinspires.ftc.teamcode.movement

class Callback(private val callback: () -> Unit) : PathSegment {
    override fun trigger() = callback()
    override fun periodic(): Boolean = true
}