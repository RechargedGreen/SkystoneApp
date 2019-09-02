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

        combinedPacket.put("ys", driver.leftStick.y)

        combinedPacket.put("leftInches", LeagueOdometry.leftInches)
        combinedPacket.put("rightInches", LeagueOdometry.rightInches)
        combinedPacket.put("auxInches", LeagueOdometry.auxInches)
        combinedPacket.put("y", world_y)
        combinedPacket.put("x", world_x)
        combinedPacket.put("deg", world_angle_unwrapped.deg)
    }
}