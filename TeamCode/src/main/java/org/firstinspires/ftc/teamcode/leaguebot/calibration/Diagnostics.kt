package org.firstinspires.ftc.teamcode.leaguebot.calibration

import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.leaguebot.hardware.*
import org.firstinspires.ftc.teamcode.leaguebot.teleop.*
import org.firstinspires.ftc.teamcode.movement.*

/**
 * Created by David Lukens on 12/2/2019.
 */
@TeleOp(group = "b")
class Diagnostics : LeagueTeleOp() {
    override fun onMainLoop() {
        super.onMainLoop()

        telemetry.addData("drive wheels y pos", Robot.drive.y_drivePos)
        telemetry.addData("lf pos", Robot.drive.leftFront.encoderTicks)
        telemetry.addData("lb pos", Robot.drive.leftBack.encoderTicks)
        telemetry.addData("rf pos", Robot.drive.rightFront.encoderTicks)
        telemetry.addData("rb pos", Robot.drive.rightBack.encoderTicks)

        combinedPacket.put("y fps", Speedometer.yInchPerSec / 12.0)
        combinedPacket.put("x fps", Speedometer.xInchPerSec / 12.0)
        combinedPacket.put("leftInches", RobotOdometry.leftInches)
        combinedPacket.put("rightInches", RobotOdometry.rightInches)
        combinedPacket.put("auxInches", RobotOdometry.auxInches)
        combinedPacket.put("y", DriveMovement.world_y_raw)
        combinedPacket.put("x", DriveMovement.world_x_raw)
        combinedPacket.put("deg", DriveMovement.world_angle_unwrapped_raw.deg)
        combinedPacket.put("rr deg", DriveMovement.roadRunnerPose2dRaw.heading.toDegrees)
        combinedPacket.put("raw gyro deg", Robot.gyro.angle1_deg)
    }
}