package org.firstinspires.ftc.teamcode.leaguebot.opmode.teleop

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.leaguebot.LeagueBotTeleOpBase
import org.firstinspires.ftc.teamcode.movement.DriveMovement

@TeleOp
class LeagueTeleOp : LeagueBotTeleOpBase() {
    override fun onMainLoop() {
        DriveMovement.moveRobotCentric(driver.leftX, driver.leftY, driver.rightX)
    }
}