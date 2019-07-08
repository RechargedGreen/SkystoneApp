package org.firstinspires.ftc.teamcode.leaguebot

import org.firstinspires.ftc.teamcode.lib.BaseBot
import org.firstinspires.ftc.teamcode.sharedhardware.SharedDrive

object LeagueBot : BaseBot {
    lateinit var drive: SharedDrive

    override fun setup() {
        drive = SharedDrive()
    }

    override fun update() {
        LeagueOdometry.update()
        drive.update()
    }
}