package org.firstinspires.ftc.teamcode.leaguebot

import org.firstinspires.ftc.teamcode.leaguebot.opmode.teleop.*
import org.firstinspires.ftc.teamcode.lib.*
import org.firstinspires.ftc.teamcode.sharedhardware.*

object LeagueBot : BaseBot {
    lateinit var drive: SharedDrive

    override fun setup() {
        drive = SharedDrive(LeagueMovementConstants)
    }

    override fun update() {
        LeagueOdometry.update()
        drive.update()
    }

    override val teleopName: String = LeagueTeleOp::class.java.name
}