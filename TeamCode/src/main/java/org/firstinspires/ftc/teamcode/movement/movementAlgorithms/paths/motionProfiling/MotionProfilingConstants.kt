package org.firstinspires.ftc.teamcode.movement.movementAlgorithms.paths.motionProfiling

import kotlin.math.*

data class ProfileConstraints(
        val maxVelocity: Double,
        val maxAcceleration: Double
)

data class ProfileGains(
        val kP: Double,
        val kI: Double,
        val kD: Double,

        val maxVel: Double,
        val kA: Double,
        val kS: Double
) {
    val kV get() = 1.0 / maxVel
}

const val kEpsilon = 1e-6
fun Double.kEpsilonEquals(other: Double, epsilon: Double) = (this - other).absoluteValue < kEpsilon