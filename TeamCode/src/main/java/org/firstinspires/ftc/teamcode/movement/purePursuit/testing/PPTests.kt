package org.firstinspires.ftc.teamcode.movement.purePursuit.testing

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.field.Point
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.leaguebot.misc.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_angle_unwrapped_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_point_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_x_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_y_raw
import org.firstinspires.ftc.teamcode.movement.purePursuit.PurePursuit
import org.firstinspires.ftc.teamcode.movement.purePursuit.PurePursuitPath
import org.firstinspires.ftc.teamcode.movement.toDegrees
import org.firstinspires.ftc.teamcode.opmodeLib.Alliance
import kotlin.math.atan2

@TeleOp(group = "c")
class GoToFollowPointTest : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)) {
    var driving = false

    override fun onMainLoop() {
        if (driver.a.justPressed)
            driving = !driving
        if (driving)
            PurePursuit.goToFollowPoint(Point(0.0, 0.0), world_point_raw, 0.0)
        else
            stopDrive()

        telemetry.addData("atan2", atan2(0.0 - world_x_raw, 0.0 - world_y_raw).toDegrees)
        telemetry.addData("angle to point", world_point_raw.angleTo(Point(0.0, 0.0)).deg)
        telemetry.addData("x", world_x_raw)
        telemetry.addData("y", world_y_raw)
        telemetry.addData("heading", world_angle_unwrapped_raw.deg)
    }
}

@TeleOp(group = "c")
class SimpleCurveTest : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)) {
    override fun onStart() {
        PurePursuit.reset()
    }

    override fun onMainLoop() {
        val path = PurePursuitPath(15.0)
        path.finalAngle = 0.0
        //path.add(Point(0.0, 0.0))
        path.add(Point(0.0, 48.0))
        path.add(Point(72.0, 48.0))
        PurePursuit.followCurve(path, 0.0)

        telemetry.addData("finishingMove", PurePursuit.finishingMove)
    }
}