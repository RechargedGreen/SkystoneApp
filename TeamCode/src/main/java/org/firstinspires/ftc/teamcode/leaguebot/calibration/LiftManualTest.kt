package org.firstinspires.ftc.teamcode.leaguebot.calibration

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.lift
import org.firstinspires.ftc.teamcode.leaguebot.misc.LeagueBotTeleOpBase

@TeleOp(group = "c")
class LiftManualTest : LeagueBotTeleOpBase() {
    override fun onMainLoop() {
        lift.ultraManual = driver.rightStick.y
        telemetry.addData("height", lift.height)
        telemetry.addData("raw height", lift.rawHeight)
    }
}