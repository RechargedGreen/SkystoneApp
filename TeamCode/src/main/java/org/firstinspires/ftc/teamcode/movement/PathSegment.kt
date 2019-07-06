package org.firstinspires.ftc.teamcode.movement

interface PathSegment {
    fun trigger()
    fun periodic(): Boolean
}