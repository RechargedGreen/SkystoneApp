package org.firstinspires.ftc.teamcode.leaguebot.opmode

import com.acmerobotics.dashboard.config.*
import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.leaguebot.*
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveFieldCentric
import org.firstinspires.ftc.teamcode.movement.DriveMovement.setPosition
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y

@Config
@TeleOp
class KPStyleProto : LeagueBotAutoBase() {
    companion object {
        @JvmField
        var turnTarget = 0.0

        @JvmField
        var yTarget = 0.0

        @JvmField
        var xTarget = 0.0

        @JvmField
        var turnP = 0.01

        @JvmField
        var yP = 0.1

        @JvmField
        var xP = 0.1

        @JvmField
        var turnD = 0.0

        @JvmField
        var yD = 0.0

        @JvmField
        var xD = 0.0

        @JvmField
        var move = true
    }

    override fun onMainLoop() {
        val turnLeft = turnTarget - world_angle.deg
        val yLeft = yTarget - world_y
        val xLeft = xTarget - world_x

        val speed = Speedometer.fieldSlipPoint

        if (move) {
            moveFieldCentric(xLeft * xP - speed.x * xD, yLeft * yP - speed.y * yD, turnLeft * turnP - Speedometer.degPerSec * turnD)
        } else {
            stopDrive()
        }

        if (driver.y.justPressed) {
            move = !move
        }

        if (driver.b.currentState)
            setPosition(0.0, 0.0, 0.0)

        combinedPacket.put("drive", move)
        combinedPacket.put("x", world_x)
        combinedPacket.put("y", world_y)
        combinedPacket.put("deg", world_x)
        combinedPacket.put("yLeft", yLeft)
        combinedPacket.put("xLeft", xLeft)
        combinedPacket.put("turnLeft", turnLeft)
    }
}