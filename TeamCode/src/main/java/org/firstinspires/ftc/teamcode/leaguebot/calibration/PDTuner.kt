package org.firstinspires.ftc.teamcode.leaguebot.calibration

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.misc.LeagueBotTeleOpBase
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.gamepadControl
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.setPosition_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_angle_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_x_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_y_raw
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.goToPosition_raw

@TeleOp(group = "c")
@Disabled
class PDTuner : LeagueBotTeleOpBase() {
    var pd = false

    override fun onMainLoop() {
        if (driver.a.currentState)
            setPosition_raw(Pose(0.0, 0.0, 0.0))
        if (driver.b.justPressed)
            pd = !pd

        if (pd)
            goToPosition_raw(0.0, 0.0, 0.0)
        else
            gamepadControl(driver)

        telemetry.addData("x", world_x_raw)
        telemetry.addData("y", world_y_raw)
        telemetry.addData("deg", world_angle_raw.deg)
    }
}