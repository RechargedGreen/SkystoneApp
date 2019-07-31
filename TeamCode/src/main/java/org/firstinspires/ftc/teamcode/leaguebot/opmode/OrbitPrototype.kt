package org.firstinspires.ftc.teamcode.leaguebot.opmode

import com.acmerobotics.dashboard.config.*
import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_point

@Config
@TeleOp
class OrbitPrototype : LeagueBotAutoBase() {
    @JvmField
    var useFeedback = true

    @JvmField
    var targetRadius = 0.0

    @JvmField
    var trackWidth = 17.0 // used for the turn:strafe ratio

    @JvmField
    var circleCenter = Point(0.0, 0.0)

    @JvmField
    var strokeWidth = 5

    override fun onMainLoop() {
        if (driver.x)
            useFeedback = false
        if (driver.y)
            useFeedback = true

        var orbitStrafeSpeed = driver.rightTrigger - driver.leftTrigger

        val currentRadius = circleCenter.distanceTo(world_point)
        val angleFromCenter = circleCenter.angleTo(world_point)

        var orbitTurn = 0.0 // calculate to preserve curvature

        var angleError = world_angle.rad - angleFromCenter
        val radiusError = targetRadius

        fieldOverlay.setStrokeWidth(strokeWidth)
    }
}