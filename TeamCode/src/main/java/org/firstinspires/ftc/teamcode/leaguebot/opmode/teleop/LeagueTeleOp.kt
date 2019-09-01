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

        telemetry.addData("leftInches", LeagueOdometry.leftInches)
        telemetry.addData("rightInches", LeagueOdometry.rightInches)
        telemetry.addData("auxInches", LeagueOdometry.auxInches)
        telemetry.addData("y", world_y)
        telemetry.addData("x", world_x)
        telemetry.addData("deg", world_angle_unwrapped.deg)
    }
}