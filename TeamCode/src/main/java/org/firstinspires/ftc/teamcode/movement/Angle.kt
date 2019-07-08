package org.firstinspires.ftc.teamcode.movement

data class Angle(var rad: Double, var deg: Double) {
    companion object {
        fun createDeg(deg: Double) = Angle(Math.toRadians(deg), deg)
        fun createRad(rad: Double) = Angle(rad, Math.toRadians(rad))
    }

    fun wrap() = createRad(rad)
}