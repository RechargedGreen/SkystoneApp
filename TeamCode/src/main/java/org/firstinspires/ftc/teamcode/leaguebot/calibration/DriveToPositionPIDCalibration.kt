package org.firstinspires.ftc.teamcode.leaguebot.calibration

import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.misc.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.gamepadControl
import org.firstinspires.ftc.teamcode.movement.DriveMovement.setPosition_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_mirror
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.goToPosition_mirror
import org.firstinspires.ftc.teamcode.opmodeLib.*

@TeleOp(group = "c")
class DriveToPositionPIDCalibration : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)) {
    var runningToPos = false
    override fun onMainLoop() {
        if (driver.y.justPressed)
            runningToPos = !runningToPos
        if (driver.b.currentState)
            setPosition_mirror(0.0, 0.0, 0.0)

        if (runningToPos)
            goToPosition_mirror(0.0, 0.0, 0.0)
        else
            gamepadControl(driver)

        combinedPacket.put("toPositionXError", world_x_mirror)
        combinedPacket.put("toPositionYError", world_y_mirror)
        combinedPacket.put("toPositionDegError", world_angle_mirror.deg)
    }
}