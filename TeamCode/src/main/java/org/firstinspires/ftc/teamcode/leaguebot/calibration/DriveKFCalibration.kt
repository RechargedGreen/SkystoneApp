package org.firstinspires.ftc.teamcode.leaguebot.calibration

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.leaguebot.misc.LeagueBotTeleOpBase
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveRobotCentric_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.roadRunner.RoadRunnerConstraints.WHEEL_DIAMETER
import org.firstinspires.ftc.teamcode.odometry.ThreeWheel.yTraveled
import kotlin.math.PI

@TeleOp(group = "c")
class DriveKFCalibration : LeagueBotTeleOpBase() {
    var distanceAtStartOfCruise = 0.0
    var accelerateDistance = 48.0
    var cruiseDistance = 48.0

    var cruiseSpeed = 0.0

    enum class progStates {
        accelerate,
        cruise,
        stop,
    }

    override fun onMainLoop() {
        val currentStage = progStates.values()[stage]
        telemetry.addData("state", stage)

        when (currentStage) {
            progStates.accelerate -> {
                if (changedStage)
                    yTraveled = 0.0
                moveRobotCentric_raw(0.0, 1.0, 0.0)
                if (yTraveled > accelerateDistance) {
                    distanceAtStartOfCruise = yTraveled
                    nextStage()
                }
            }
            progStates.cruise -> {
                moveRobotCentric_raw(0.0, 1.0, 0.0)
                if(yTraveled > cruiseDistance + accelerateDistance){
                    cruiseSpeed = (yTraveled - distanceAtStartOfCruise) / stageTimer.seconds()
                    nextStage()
                }
            }
            progStates.stop -> {
                val ticksPerSecond = (cruiseSpeed / WHEEL_DIAMETER / PI) * 28.0 * 19.2
                val kF = 32767.0 / (ticksPerSecond)

                telemetry.addData("cruiseSpeed", cruiseSpeed)
                telemetry.addData("kF", kF)

                stopDrive()
            }
        }
    }
}