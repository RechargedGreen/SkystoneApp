package org.firstinspires.ftc.teamcode.leaguebot.opmode

import com.acmerobotics.dashboard.config.*
import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_point
import kotlin.math.*

@Config
@TeleOp
class OrbitPrototype : LeagueBotAutoBase() {
    @JvmField
    var useFeedback = true

    @JvmField
    var targetRadius = 0.0

    @JvmField
    var ratio = 1.0 // higher turns slower

    @JvmField
    var circleCenter = Point(0.0, 0.0)

    @JvmField
    var strokeWidth = 5

    override fun onMainLoop() {
        if (driver.x.currentState)
            useFeedback = false
        if (driver.y.currentState)
            useFeedback = true

        var orbitOpenTurn = driver.rightTrigger - driver.leftTrigger

        val currentRadius = circleCenter.distanceTo(world_point)
        val angleFromCenter = circleCenter.angleTo(world_point)

        var orbitOpenStrafe = orbitOpenTurn * currentRadius * ratio


        val orbitOpenTotal = orbitOpenStrafe.absoluteValue + orbitOpenTurn.absoluteValue
        if (orbitOpenTotal > 1.0) { // prevent vector from overpowering the correction
            orbitOpenTurn /= orbitOpenTotal
            orbitOpenStrafe /= orbitOpenStrafe
        }

        var angleError = world_angle.rad - angleFromCenter
        val radiusError = targetRadius


        val targetCircle = Circle(circleCenter, targetRadius)
        val currentCircle = Circle(circleCenter, currentRadius)
        fieldOverlay.setStrokeWidth(strokeWidth)
        targetCircle.stroke()
        currentCircle.stroke()
    }
}