package org.firstinspires.ftc.teamcode.leaguebot.calibration

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.field.Pose
import org.firstinspires.ftc.teamcode.leaguebot.misc.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.opmodeLib.Alliance
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.gamepadControl
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.movement_y
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.Speedometer
import org.firstinspires.ftc.teamcode.odometry.ThreeWheel.degTraveled
import org.firstinspires.ftc.teamcode.odometry.ThreeWheel.yTraveled
import org.firstinspires.ftc.teamcode.util.pow

@TeleOp(group = "c")
class TrajectoryConstraintsCalibrator : LeagueBotAutoBase(Alliance.RED, Pose(0.0, 0.0, 0.0)) {
    enum class progStates {
        accelerateTurn,
        cruiseTurn,
        setupForDriveForward,
        accelerateForward,
        cruiseForward,
        displayResults
    }

    var measuredInchesAccel = false
    var measuredDegAccel = true

    var accelReadInchesPerSecond = 36.0
    var accelReadDegreesPerSecond = 120.0

    private val accelerateDegrees = 360.0 * 1.0
    private val cruiseDegrees = 360.0 * 5.0
    private val accelerateInches = 24.0 * 2.0
    private val cruiseInches = 24.0 * 4.0

    private var inchesPerSecond = 0.0
    private var degPerSecond = 0.0

    private var cruiseStart = 0.0

    private var degAccel = 0.0
    private var inchAccel = 0.0

    override fun onMainLoop() {
        val currentStage = progStates.values()[stage]
        telemetry.addData("stage", currentStage)

        stopDrive()

        when (currentStage) {
            progStates.accelerateTurn -> {
                if (changedStage)
                    degTraveled = 0.0
                movement_turn = 1.0

                if (!measuredInchesAccel)
                    if (Speedometer.degPerSec > accelReadDegreesPerSecond) {
                        degAccel = degTraveled / (stageTimer.seconds() pow 2.0)
                        measuredDegAccel = true
                    }

                if (degTraveled > accelerateDegrees) {
                    cruiseStart = degTraveled
                    nextStage()
                }
            }

            progStates.cruiseTurn -> {
                movement_turn = 1.0
                if (degTraveled > cruiseDegrees) {
                    degPerSecond = (degTraveled - cruiseStart) / stageTimer.seconds()
                    nextStage()
                }
            }

            progStates.setupForDriveForward -> {
                telemetry.addLine("press a to advance")
                gamepadControl(driver)
                if (driver.a.currentState) {
                    yTraveled = 0.0
                    nextStage()
                }
            }

            progStates.accelerateForward -> {
                movement_y = 1.0

                if (!measuredInchesAccel)
                    if (Speedometer.yInchPerSec > accelReadInchesPerSecond) {
                        inchAccel = yTraveled / (stageTimer.seconds() pow 2.0)
                        measuredInchesAccel = true
                    }

                if (yTraveled > accelerateInches) {
                    cruiseStart = yTraveled
                    nextStage()
                }
            }

            progStates.cruiseForward -> {
                movement_y = 1.0
                if (yTraveled > cruiseInches) {
                    inchesPerSecond = (yTraveled - cruiseStart) / stageTimer.seconds()
                    nextStage()
                }
            }

            progStates.displayResults -> {
                telemetry.addData("inches vel", inchesPerSecond)
                telemetry.addData("deg vel", degPerSecond)
                telemetry.addData("inches accel", inchAccel)
                telemetry.addData("deg accel", degAccel)
            }

        }
    }
}