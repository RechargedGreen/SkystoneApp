package org.firstinspires.ftc.teamcode.leaguebot.opmode.calibration

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.leaguebot.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.LeagueBot.gyro
import org.firstinspires.ftc.teamcode.lib.Alliance

@TeleOp
class GyroTest : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)){
    override fun onMainLoop() {
        telemetry.addData("firstAngle", gyro.angle1_deg)
        telemetry.addData("secondAngle", gyro.angle2_deg)
        telemetry.addData("thirdAngle", gyro.angle3_deg)
    }
}