package org.firstinspires.ftc.teamcode.leaguebot.opmode.calibration

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.leaguebot.LeagueBotTeleOpBase
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.LeagueBot.lift

@TeleOp
@Config
class LiftFeedForwardCalibration : LeagueBotTeleOpBase(){
    companion object {
        @JvmField
        var target = 0.0
    }
    override fun onMainLoop() {
        lift.heightTarget = target
        telemetry.addData("lift down", lift.bottomPressed)
        telemetry.addData("raw height", lift.rawHeight)
        telemetry.addData("height", lift.height)
        telemetry.addData("lift ticks", lift.encoder.ticks)
    }
}