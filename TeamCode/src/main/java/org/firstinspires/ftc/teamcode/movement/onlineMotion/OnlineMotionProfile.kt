package org.firstinspires.ftc.teamcode.movement.onlineMotion

import org.firstinspires.ftc.teamcode.util.*
import kotlin.math.*

class OnlineMotionProfile(private val distance: Double, private val maxAccel: Double, private val maxVel: Double) {
    private var lastTime = Clock.seconds
    var lastVel = 0.0
    var lastDisplacement = 0.0

    fun update(displacement: Double): Double {
        val currTime = Clock.seconds

        val dt = currTime - lastTime
        val remainingDisplacement = distance - displacement
        val maxVelToStop = sqrt(2.0 * maxVel * remainingDisplacement)
        val maxVelFromLast = lastVel + maxVel * dt
        val velocity = minOf(maxVelFromLast, maxVelToStop, maxVel)

        lastTime = currTime
        lastVel = velocity
        lastDisplacement = displacement

        return velocity
    }
}