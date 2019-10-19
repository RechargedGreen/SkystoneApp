package org.firstinspires.ftc.teamcode.movement

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_raw
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.*
import org.firstinspires.ftc.teamcode.util.*

object Speedometer {
    private var lastUpdateTime = 0.0
    val xInchPerSec: Double get() = robotSpeed.x
    val yInchPerSec: Double get() = robotSpeed.y

    var xInchesTraveled = 0.0
    var yInchesTraveled = 0.0

    private var lastAngle = 0.0
    private var angularVel = 0.0

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
    val turnDegSlipPrediction: Double
        get() = degPerSec * turnSlipFactor

    val xSlipFactor: Double
        get() = MovementAlgorithms.movementProvider.getXSlipFactor()
    val ySlipFactor: Double
        get() = MovementAlgorithms.movementProvider.getYSlipFactor()
    val turnSlipFactor: Double
        get() = MovementAlgorithms.movementProvider.getTurnSlipFactor()

    fun update() {
        val currTime = Clock.seconds
        val dt = currTime - lastUpdateTime
        lastUpdateTime = currTime

        val xSpeed = xInchesTraveled / dt
        val ySpeed = yInchesTraveled / dt

        angularVel = (world_angle_raw.rad - lastAngle) / dt
        lastAngle = world_angle_raw.rad

        xInchesTraveled = 0.0
        yInchesTraveled = 0.0

        robotSpeed = Point(xSpeed, ySpeed)
    }

    var robotSpeed = Point(0.0, 0.0)
        private set(value) {
            field = value
            fieldSpeed = Geometry.pointDelta(value, world_angle_raw)
        }

    var fieldSpeed: Point = Point(0.0, 0.0)
        private set
}