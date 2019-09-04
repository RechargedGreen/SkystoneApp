package org.firstinspires.ftc.teamcode.movement

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.*
import org.firstinspires.ftc.teamcode.util.*

object Speedometer {
    private var lastUpdateTime = 0.0
    val xInchPerSec: Double get() = robotSlipPoint.x
    val yInchPerSec: Double get() = robotSlipPoint.y

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

        angularVel = (world_angle.rad - lastAngle) / dt
        lastAngle - world_angle.rad

        xInchesTraveled = 0.0
        yInchesTraveled = 0.0

        robotSlipPoint = Point(xSpeed, ySpeed)
    }

    var robotSlipPoint = Point(0.0, 0.0)
        private set(value) {
            field = value
            fieldSlipPoint = Geometry.pointDelta(value, world_angle)
        }

    var fieldSlipPoint: Point = Point(0.0, 0.0)
        private set
}