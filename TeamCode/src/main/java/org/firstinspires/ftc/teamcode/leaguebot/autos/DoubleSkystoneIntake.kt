package org.firstinspires.ftc.teamcode.leaguebot.autos

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.hardware.MainIntake
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.foundationGrabber
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.intake
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.lift
import org.firstinspires.ftc.teamcode.leaguebot.hardware.ScorerState
import org.firstinspires.ftc.teamcode.leaguebot.misc.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.goToPosition_mirror
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.moveD
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.moveP
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.pointAngle_mirror
import org.firstinspires.ftc.teamcode.movement.Speedometer
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.moveFieldCentric_mirror
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.movement_x
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.movement_y
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_angle_mirror
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_x_mirror
import org.firstinspires.ftc.teamcode.movement.basicDriveFunctions.DrivePosition.world_y_mirror
import org.firstinspires.ftc.teamcode.movement.toRadians
import org.firstinspires.ftc.teamcode.odometry.ThreeWheel.yTraveled
import org.firstinspires.ftc.teamcode.opmodeLib.Alliance
import org.firstinspires.ftc.teamcode.opmodeLib.RunData.ALLIANCE
import org.firstinspires.ftc.teamcode.vision.SkystoneDetector
import org.firstinspires.ftc.teamcode.vision.SkystoneRandomization
import kotlin.math.absoluteValue

@Config
object FirstStoneBiases_RED {
    @JvmField
    var far_left = 15.5
    @JvmField
    var far_middle = 16.1
    @JvmField
    var far_right = 17.0
    val biases: ArrayList<Double>
        get() {
            val list = ArrayList<Double>()
            list.add(far_left)
            list.add(far_middle)
            list.add(far_right)
            list.add(0.0)
            list.add(0.0)
            list.add(0.0)
            return list
        }
}

@Config
object FirstStoneBiases_BLUE {
    @JvmField
    var far_left = 16.5
    @JvmField
    var far_middle = 17.0
    @JvmField
    var far_right = 18.0
    val biases: ArrayList<Double>
        get() {
            val list = ArrayList<Double>()
            list.add(far_right)
            list.add(far_middle)
            list.add(far_left)
            list.add(0.0)
            list.add(0.0)
            list.add(0.0)
            return list
        }
}

private val stoneBiases: ArrayList<ArrayList<Double>>
    get() {
        val list = ArrayList<ArrayList<Double>>()
        list.add(if (ALLIANCE == Alliance.RED) FirstStoneBiases_RED.biases else FirstStoneBiases_BLUE.biases)
        list.add(if (ALLIANCE == Alliance.RED) SecondStoneBiases_RED.biases else SecondStoneBiases_BLUE.biases)
        return list
    }

@Config
object SecondStoneBiases_RED {
    @JvmField
    var near_left = 18.5
    @JvmField
    var near_middle = 18.25
    @JvmField
    var near_right = 19.0
    val biases: ArrayList<Double>
        get() {
            val list = ArrayList<Double>()
            list.add(0.0)
            list.add(0.0)
            list.add(0.0)
            list.add(near_left)
            list.add(near_middle)
            list.add(near_right)
            return list
        }
}

@Config
object SecondStoneBiases_BLUE {
    @JvmField
    var near_left = 18.5
    @JvmField
    var near_middle = 19.2
    @JvmField
    var near_right = 19.2
    val biases: ArrayList<Double>
        get() {
            val list = ArrayList<Double>()
            list.add(0.0)
            list.add(0.0)
            list.add(0.0)
            list.add(near_right)
            list.add(near_middle)
            list.add(near_left)
            return list
        }
}

@Config
abstract class Skystones(alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(Field.EAST_WALL - 7.625, Field.SOUTH_WALL + 38.25, (-90.0).toRadians)) {
    companion object {
        @JvmField
        var xOffset = 14.0 // 15.0 gdc ree

        @JvmField
        var intakeAngleOffset = -45.0
        @JvmField
        var intakeSpeed = 0.3

        @JvmField
        var preFoundationX_red = 39.0
        @JvmField
        var preFoundationX_blue = 40.0

        @JvmField
        var preFoundationY = 43.0

        @JvmField
        var backIntoFoundationX = 28.0
        @JvmField
        var foundationTurnSpeed = 1.0

        @JvmField
        var pullFoundationX = 38.0

        @JvmField
        var scoreSpeed = 0.4

        @JvmField
        var scoreY = 30.0

        @JvmField
        var parkY = -1.0
    }

    val preFoundationX get() = if (ALLIANCE == Alliance.RED) preFoundationX_red else preFoundationX_blue

    val intakeAngle get() = -90 + intakeAngleOffset

    val stoneOrder = ArrayList<Stone>()

    enum class progStages {
        goingToIntakeAngle,
        goForwardToIntake,
        backOut,
        preFoundationCrossField,
        preFoundationTurn,
        backIntoFoundation,
        pullFoundation,
        rotateFoundation,
        postFoundationAwayFromWall,
        crossForSecondStone,
        goingToSecondIntakeAngle,
        goingForwardToSecondIntake,
        secondBackOut,

        secondStoneBackForScore,
        secondStoneRotate,
        secondStoneRechargedRam,
        secondStoneRelease,

        yeetToPark,

        stopDoNothing
    }

    override fun onStart() {
        when (SkystoneDetector.place) {
            SkystoneRandomization.FAR -> {
                stoneOrder.add(Quarry[QuarryLocation.FAR_LEFT])
                stoneOrder.add(Quarry[QuarryLocation.NEAR_LEFT])
                stoneOrder.add(Quarry[QuarryLocation.NEAR_RIGHT])
            }
            SkystoneRandomization.MID -> {
                stoneOrder.add(Quarry[QuarryLocation.FAR_MIDDLE])
                stoneOrder.add(Quarry[QuarryLocation.NEAR_MIDDLE])
                stoneOrder.add(Quarry[QuarryLocation.FAR_LEFT])
            }
            SkystoneRandomization.NEAR -> {
                stoneOrder.add(Quarry[QuarryLocation.FAR_RIGHT])
                stoneOrder.add(Quarry[QuarryLocation.NEAR_RIGHT])
                stoneOrder.add(Quarry[QuarryLocation.FAR_LEFT])
            }
        }
    }

    private var hasStartedParking = false

    fun tryToStartParking() {
        if (!hasStartedParking) {
            nextStage(progStages.yeetToPark.ordinal)
            ScorerState.state = ScorerState.State.PULL_BACK_WHILE_RELEASED
        }
        hasStartedParking = true
    }

    override fun onMainLoop() {
        stopDrive()
        val currentStage = progStages.values()[stage]

        if (currentStage == progStages.yeetToPark)
            tryToStartParking()

        telemetry.addData("currentStage", currentStage)
        telemetry.addData("x", world_x_mirror)
        telemetry.addData("y", world_y_mirror)
        telemetry.addData("deg", world_angle_mirror.deg)

        when (currentStage) {
            progStages.stopDoNothing -> {
                intake.state = MainIntake.State.STOP
                stopDrive()
                if (isTimedOut(2.0))
                    requestOpModeStop()
            }

            progStages.goingToIntakeAngle, progStages.goingToSecondIntakeAngle -> {
                val run = if (currentStage == progStages.goingToIntakeAngle) 0 else 1
                lift.triggerIntake()
                val stone = stoneOrder[run]
                val yOffset = stoneBiases[run][stone.index]
                val error = goToPosition_mirror(stone.center_x + xOffset, stone.center_y + yOffset, -90.0 + intakeAngleOffset)
                telemetry.addData("hypot", error.point.hypot)
                telemetry.addData("deg", error.deg)
                if (error.deg.absoluteValue < 2.0 && error.point.hypot < 3.0 && Speedometer.robotSpeed.hypot < 5.0) {
                    nextStage()
                    intake.state = MainIntake.State.IN
                }
            }

            progStages.goForwardToIntake, progStages.goingForwardToSecondIntake -> {
                pointAngle_mirror(-90.0 + intakeAngleOffset)

                movement_y = intakeSpeed

                if (isTimedOut(2.0)) {
                    stopDrive()
                    pointAngle_mirror(180.0)
                }

                if (isTimedOut(3.0) || world_x_mirror < 20.0 || intake.sensorTriggered) {
                    nextStage()
                    intake.state = MainIntake.State.FINISH_AUTO_INTAKE
                }
            }

            progStages.backOut, progStages.secondBackOut -> {
                val xSpeed = (preFoundationX - world_x_mirror) * moveP - Speedometer.fieldSpeed.x.checkMirror * moveD
                moveFieldCentric_mirror(xSpeed, xSpeed.absoluteValue * 0.5, 0.0)
                pointAngle_mirror(-90.0 - 30.0)
                //pointAngle_mirror(180.0)

                if ((preFoundationX - world_x_mirror).absoluteValue < 2.0)
                    nextStage()
            }

            progStages.preFoundationCrossField -> {
                val error = goToPosition_mirror(preFoundationX, preFoundationY, 180.0, yClip = 1.0)

                if(error.deg.absoluteValue > 15.0){
                    movement_y = 0.0
                    movement_x = 0.0
                }

                if (world_y_mirror > 24.0) {
                    ScorerState.triggerExtend()
                    foundationGrabber.prepForGrab()
                    nextStage()
                }
            }

            progStages.preFoundationTurn -> {
                val error = goToPosition_mirror(preFoundationX, preFoundationY, 90.0)
                if (error.deg.absoluteValue < 2.0 && error.point.hypot < 3.0 && Speedometer.robotSpeed.hypot < 5.0 && Speedometer.degPerSec < 5.0)
                    nextStage()
            }

            progStages.backIntoFoundation -> {
                pointAngle_mirror(90.0)
                if (isTimedOut(.25))
                    movement_y = -0.5
                if (world_x_mirror < backIntoFoundationX) {
                    foundationGrabber.grab()
                    ScorerState.triggerRelease()
                    nextStage()
                }
            }

            progStages.pullFoundation -> {
                if (isTimedOut(1.0)) {
                    moveFieldCentric_mirror(1.0, 0.0, 0.0)
                    pointAngle_mirror(90.0)
                }
                if (world_x_mirror > pullFoundationX)
                    nextStage()
            }

            progStages.rotateFoundation -> {
                stopDrive()
                val error = pointAngle_mirror(180.0).absoluteValue

                movement_turn = Range.clip(movement_turn, -0.5, 0.5)

                if (error < 6.0) {
                    nextStage()
                    foundationGrabber.release()
                    ScorerState.triggerPullBack()
                }
            }

            progStages.postFoundationAwayFromWall -> {
                val timedOut = isTimedOut(0.25)
                if (timedOut) {
                    moveFieldCentric_mirror((preFoundationX - world_x_mirror) * moveP, -0.5, 0.0)
                    pointAngle_mirror(180.0)
                    if ((world_x_mirror - preFoundationX).absoluteValue < 2.0)
                        nextStage()
                }
            }

            progStages.crossForSecondStone -> {
                val error = goToPosition_mirror(preFoundationX, stoneOrder[1].center_y + 16.5, 180.0, yClip = 1.0)
                if (error.y.absoluteValue < 4.0 || world_y_mirror < -16.0)
                    nextStage()
            }

            progStages.secondStoneBackForScore -> {
                if (world_y_mirror > 8.0)
                    ScorerState.triggerExtend()

                val error = goToPosition_mirror(preFoundationX, scoreY, 180.0)
                if (error.point.hypot < 4.0 && Speedometer.fieldSpeed.hypot < 5.0)
                    nextStage()
            }

            progStages.secondStoneRotate -> {
                val error = pointAngle_mirror(if (ALLIANCE == Alliance.RED) 220.0 else 200.0)
                if (error.absoluteValue < 3.0 && Speedometer.degPerSec < 5.0)
                    nextStage()
            }

            progStages.secondStoneRechargedRam -> {
                movement_y = -scoreSpeed
                if (isTimedOut(1.0))
                    nextStage()
            }

            progStages.secondStoneRelease -> {
                if (ScorerState.timeSpentExtended > 0.75)
                    ScorerState.triggerRelease()
                if (ScorerState.timeSpentReleased > 0.5) {
                    nextStage()
                    yTraveled = 0.0
                }
            }

            progStages.yeetToPark -> {
                var error = goToPosition_mirror(preFoundationX - 5.0, parkY, 180.0)
                if (yTraveled > 12.0)
                    ScorerState.triggerLoad()
                if (error.point.hypot < 3.0 && Speedometer.fieldSpeed.hypot < 5.0)
                    nextStage()
            }
        }
    }

    override fun onInitLoop() {
        telemetry.addData("skystone randomization", SkystoneDetector.place)
    }
}

@Autonomous
class Skystones_BLUE : Skystones(Alliance.BLUE)

@Autonomous
class Skystones_RED : Skystones(Alliance.RED)