package org.firstinspires.ftc.teamcode.movement

import org.firstinspires.ftc.teamcode.util.*
import kotlin.math.*

object Speedometer {
    private var lastUpdateTime = 0.0
    var xInchPerSec = 0.0
        private set
    var yInchPerSec = 0.0
        private set

    private val obviousHighValue = 12.0 * 8.0 // bot can't go 8'/sec

    var xInchesTraveled = 0.0
    var yInchesTraveled = 0.0

    private var lastAngle = 0.0
    private var angularVel = 0.0

    private var hz = 25.0

    val radPerSec: Double
        get() = angularVel
    val degPerSec: Double
        get() = Math.toDegrees(angularVel)


    val xSlipPrediction: Double
        get() = xInchPerSec * xSlipFactor
    val ySlipPrediction: Double
        get() = yInchPerSec * ySlipFactor
    val turnRadSlipPrediction: Double
        get() = radPerSec * turnSlipFactor

    //todo make a provider
    val xSlipFactor = 1.0
    val ySlipFactor = 1.0
    val turnSlipFactor = 1.0

    fun update() {
        val currTime = Clock.seconds
        val elapsedTime = currTime - lastUpdateTime
        if (elapsedTime > 1.0 / hz) {
            val newSpeedX = xInchesTraveled / elapsedTime
            val newSpeedY = yInchesTraveled / elapsedTime

            if (newSpeedY.absoluteValue < obviousHighValue && newSpeedX.absoluteValue < obviousHighValue) {
                xInchPerSec = newSpeedX
                yInchPerSec = newSpeedY
            }

            angularVel = (DriveMovement.world_angle_unwrapped.rad - lastAngle) / elapsedTime
            lastAngle = DriveMovement.world_angle_unwrapped.rad

            xInchesTraveled = 0.0
            yInchesTraveled = 0.0
            lastUpdateTime = currTime
        }
    }
}