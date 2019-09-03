package org.firstinspires.ftc.teamcode.leaguebot.opmode.teleop

import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.leaguebot.*
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_unwrapped
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y

@TeleOp
class LeagueTeleOp : LeagueBotTeleOpBase() {
    override fun onMainLoop() {
        DriveMovement.gamepadControl(driver)

        //DriveMovement.moveFieldCentric(driver.leftStick.x, driver.leftStick.y, driver.rightStick.x)

        if (driver.b.currentState)
            DriveMovement.setPosition(0.0, 0.0, 0.0)

        combinedPacket.put("ys", driver.leftStick.y)

        combinedPacket.put("y fps", Speedometer.yInchPerSec / 12.0)
        combinedPacket.put("x fps", Speedometer.xInchPerSec / 12.0)
        combinedPacket.put("leftInches", LeagueThreeWheelOdometry.leftInches)
        combinedPacket.put("rightInches", LeagueThreeWheelOdometry.rightInches)
        combinedPacket.put("auxInches", LeagueThreeWheelOdometry.auxInches)
        combinedPacket.put("y", world_y)
        combinedPacket.put("x", world_x)
        combinedPacket.put("deg", world_angle_unwrapped.deg)
    }
}