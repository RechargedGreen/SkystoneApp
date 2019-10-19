package org.firstinspires.ftc.teamcode.leaguebot.opmode

import com.acmerobotics.dashboard.config.*
import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.*
import org.firstinspires.ftc.teamcode.lib.*
import org.firstinspires.ftc.teamcode.lib.RunData.ALLIANCE
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveFieldCentric_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.setPosition_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_raw
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.goToPosition_mirror

@Config
@TeleOp
class KPStyleProto : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)) {
    companion object {
        @JvmField
        var turnTarget = 0.0

        @JvmField
        var yTarget = 0.0

        @JvmField
        var xTarget = 0.0

        @JvmField
        var turnP = 0.02

        @JvmField
        var moveP = 0.2

        @JvmField
        var turnD = 0.0

        @JvmField
        var moveD = 0.0

        @JvmField
        var move = true
    }

    override fun onMainLoop() {

        if (driver.leftBumper.currentState)
            ALLIANCE = Alliance.BLUE
        if (driver.rightBumper.currentState)
            ALLIANCE = Alliance.RED

        val turnLeft = turnTarget - world_angle_raw.deg
        val yLeft = yTarget - world_y_raw
        val xLeft = xTarget - world_x_raw

        val speed = Speedometer.fieldSpeed

        if (move)
            goToPosition_mirror(24.0, 24.0, 0.0)
        else
            moveFieldCentric_mirror(driver.leftStick.x, driver.leftStick.y, driver.rightStick.x)

        /*if (move)
            moveFieldCentric_raw(xLeft * moveP - speed.x * moveD, yLeft * moveP - speed.y * moveD, turnLeft * turnP - Speedometer.degPerSec * turnD)
        else
            gamepadControl(driver)*/

        if (driver.y.justPressed)
            move = !move

        if (driver.b.currentState)
            setPosition_raw(0.0, 0.0, 0.0)

        combinedPacket.put("drive", move)
        combinedPacket.put("x", world_x_raw)
        combinedPacket.put("y", world_y_raw)
        combinedPacket.put("deg", world_x_raw)
        combinedPacket.put("yLeft", yLeft)
        combinedPacket.put("xLeft", xLeft)
        combinedPacket.put("turnLeft", turnLeft)
    }
}