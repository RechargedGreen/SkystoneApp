package org.firstinspires.ftc.teamcode.movement.purePursuit.testing

import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.misc.*
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_point_raw
import org.firstinspires.ftc.teamcode.movement.purePursuit.*
import org.firstinspires.ftc.teamcode.opmodeLib.*

@TeleOp(group = "c")
object GoToFollowPointTest : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)) {
    var driving = false

    override fun onMainLoop() {
        if (driver.a.justPressed)
            driving = !driving
        if (driving)
            PurePursuit.goToFollowPoint(Point(0.0, 0.0), world_point_raw, 0.0)
    }
}

@TeleOp(group = "c")
object SimpleCurveTest : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)) {
    override fun onStart() {
        PurePursuit.reset()
    }

    override fun onMainLoop() {
        val path = PurePursuitPath(8.0)
        path.add(Point(0.0, 48.0))
        path.add(Point(96.0, 48.0))
        path.finalAngle = 0.0
        PurePursuit.followCurve(path, 0.0)
    }
}