package org.firstinspires.ftc.teamcode.leaguebot.calibration

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.drive
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.intake
import org.firstinspires.ftc.teamcode.leaguebot.hardware.RobotOdometry
import org.firstinspires.ftc.teamcode.leaguebot.teleop.LeagueTeleOp
import org.firstinspires.ftc.teamcode.movement.Speedometer
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.*

/**
 * Created by David Lukens on 12/2/2019.
 */
@TeleOp(group = "b")
class Diagnostics : LeagueTeleOp() {
    override fun onMainLoop() {
        super.onMainLoop()

        telemetry.addData("drive wheels y pos", drive.y_drivePos)
        telemetry.addData("lf pos", drive.leftFront.encoderTicks)
        telemetry.addData("lb pos", drive.leftBack.encoderTicks)
        telemetry.addData("rf pos", drive.rightFront.encoderTicks)
        telemetry.addData("rb pos", drive.rightBack.encoderTicks)

        telemetry.addData("intakeDistance", intake.sensorDistance)
        telemetry.addData("intakeLoaded", intake.sensorTriggered)

        combinedPacket.put("y fps", Speedometer.yInchPerSec / 12.0)
        combinedPacket.put("x fps", Speedometer.xInchPerSec / 12.0)
        combinedPacket.put("leftInches", RobotOdometry.leftInches)
        combinedPacket.put("rightInches", RobotOdometry.rightInches)
        combinedPacket.put("auxInches", RobotOdometry.auxInches)
        combinedPacket.put("y", DrivePosition.world_y_raw)
        combinedPacket.put("x", DrivePosition.world_x_raw)
        combinedPacket.put("deg", DrivePosition.world_angle_unwrapped_raw.deg)
        combinedPacket.put("raw gyro deg", Robot.gyro.angle1_deg)
    }
}