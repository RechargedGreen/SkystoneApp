package org.firstinspires.ftc.teamcode.movement

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.field.Point
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.leaguebot.misc.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.gamepadControl
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.movement_y
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.veloControl
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_angle_unwrapped_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_deg_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_point_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_x_raw
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_y_raw
import org.firstinspires.ftc.teamcode.odometry.ThreeWheel.degTraveled
import org.firstinspires.ftc.teamcode.odometry.ThreeWheel.xTraveled
import org.firstinspires.ftc.teamcode.odometry.ThreeWheel.yTraveled
import org.firstinspires.ftc.teamcode.opmodeLib.Alliance
import kotlin.math.atan2
import kotlin.math.hypot

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
class SimpleCurveTest : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 180.0.toRadians)) {
    override fun onStart() {
        PurePursuit.reset()
    }

    override fun onMainLoop() {
        val path = PurePursuitPath(25.0)
        path.add(Point(0.0, 0.0))

        path.add(Point(0.0, 48.0))

        path.moveSpeed = 0.2
        path.followDistance = 10.0
        path.add(Point(24.0, 72.0))

        path.moveSpeed = 1.0
        path.followDistance = 15.0
        path.extrude(80.0, 90.0)
        PurePursuit.followCurve(path, 180.0)

        telemetry.addData("finishingMove", PurePursuit.finishingMove)
    }
}

@TeleOp(group = "c")
class SquareTest : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)) {
    override fun onStart() {
        PurePursuit.reset()
    }

    override fun onMainLoop() {
        val path = PurePursuitPath(8.0)
        path.finalAngle = -180.0
        for (i in 0 until 3) {
            path.moveSpeed = when (i) {
                0 -> 1.0
                1 -> 0.5
                else -> 0.15
            }
            path.add(Point(0.0, 0.0))
            path.add(Point(0.0, 24.0))
            path.add(Point(24.0, 24.0))
            path.add(Point(24.0, 0.0))
        }
        PurePursuit.followCurve(path, 0.0)

        telemetry.addData("finishingMove", PurePursuit.finishingMove)
    }
}

/*@TeleOp(group = "c")
class PPTurnTuner : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)) {
    var driving = false
    override fun onMainLoop() {
        veloControl = false
        if (driver.a.justPressed)
            driving = !driving
        if (driving)
            gamepadControl(driver)
        else
            movement_turn = -(world_deg_raw) * PurePursuitConstants.gun_turn_p - PurePursuitConstants.gun_turn_d * Speedometer.degPerSec
    }
}*/


/*
@TeleOp(group = "c")
class SlippageCalculater : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)) {
    enum class progStages {
        acceleratingStraight,
        cruiseStraight,
        slipStraight,

        waitForUser,

        acceleratingTurn,
        cruiseTurn,
        slipTurn,

        display
    }

    var cruiseVel = 0.0
    var cruiseVelDeg = 0.0

    var distanceFactor = 0.0
    var degFactor = 0.0

    override fun onStart() {
        yTraveled = 0.0
        xTraveled = 0.0
        degTraveled = 0.0
    }

    override fun onMainLoop() {
        val currentStage = progStages.values()[stage]

        veloControl = false

        stopDrive()

        telemetry.addData("currentStage", currentStage)

        when (currentStage) {
            progStages.acceleratingStraight -> {
                movement_y = 1.0
                if (yTraveled > 2.5 * 24.0) {
                    yTraveled = 0.0
                    xTraveled = 0.0
                    nextStage()
                }
            }

            progStages.cruiseStraight -> {
                movement_y = 1.0
                if (yTraveled > 24.0) {
                    cruiseVel = yTraveled / stageTimer.seconds()
                    yTraveled = 0.0
                    xTraveled = 0.0
                    nextStage()
                }
            }

            progStages.slipStraight -> {
                if (driver.a.justPressed) {
                    distanceFactor = hypot(xTraveled, yTraveled) / cruiseVel
                    nextStage()
                }
            }

            progStages.waitForUser -> {
                gamepadControl(driver)
                if (driver.a.justPressed) {
                    degTraveled = 0.0
                    nextStage()
                }
            }

            progStages.acceleratingTurn -> {
                movement_turn = 1.0

                if (degTraveled > 360.0 * 2.0) {
                    degTraveled = 0.0
                    nextStage()
                }
            }

            progStages.cruiseTurn -> {
                movement_turn = 1.0
                if (degTraveled > 360.0) {
                    cruiseVelDeg = degTraveled / stageTimer.seconds()
                    degTraveled = 0.0
                    nextStage()
                }
            }

            progStages.slipTurn -> {
                if (driver.a.justPressed) {
                    degFactor = degTraveled / cruiseVelDeg
                    nextStage()
                }
            }

            progStages.display -> {
                telemetry.addData("degFactor", degFactor)
                telemetry.addData("cruiseVelDeg", cruiseVelDeg)
                telemetry.addData("distanceFactor", distanceFactor)
                telemetry.addData("cruiseVel", cruiseVel)
            }
        }
    }
}*/
