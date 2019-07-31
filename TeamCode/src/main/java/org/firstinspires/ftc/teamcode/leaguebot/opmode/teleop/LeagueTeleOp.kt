package org.firstinspires.ftc.teamcode.leaguebot.opmode.teleop

import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.leaguebot.*
import org.firstinspires.ftc.teamcode.movement.*

@TeleOp
class LeagueTeleOp : LeagueBotTeleOpBase() {
    override fun onMainLoop() {
        DriveMovement.moveRobotCentric(driver.leftX, driver.leftY, driver.rightX)
    }
}