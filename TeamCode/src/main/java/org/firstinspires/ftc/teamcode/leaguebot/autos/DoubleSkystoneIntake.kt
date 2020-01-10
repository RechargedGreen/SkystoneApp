package org.firstinspires.ftc.teamcode.leaguebot.autos

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.hardware.MainIntake
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.foundationGrabber
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.intake
import org.firstinspires.ftc.teamcode.leaguebot.hardware.Robot.lift
import org.firstinspires.ftc.teamcode.leaguebot.hardware.ScorerState
import org.firstinspires.ftc.teamcode.leaguebot.misc.LeagueBotAutoBase
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveFieldCentric_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_y
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_mirror
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.goToPosition_mirror
import org.firstinspires.ftc.teamcode.movement.SimpleMotion.pointAngle_mirror
import org.firstinspires.ftc.teamcode.movement.Speedometer
import org.firstinspires.ftc.teamcode.movement.toRadians
import org.firstinspires.ftc.teamcode.opmodeLib.Alliance
import org.firstinspires.ftc.teamcode.opmodeLib.RunData.ALLIANCE
import org.firstinspires.ftc.teamcode.vision.SkystoneDetector
import org.firstinspires.ftc.teamcode.vision.SkystoneRandomization
import kotlin.math.absoluteValue

@Config
abstract class DoubleSkystoneIntake(alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(Field.EAST_WALL - 8.625, Field.SOUTH_WALL + 38.25, (-90.0).toRadians)) {
    companion object {
        @JvmField
        var xOffset = 15.0
        @JvmField
        var yOffset = 15.0

        @JvmField
        var intakeAngleOffset = -50.0
        @JvmField
        var intakeSpeed = 0.5

        @JvmField
        var pullOutX = 30.0

        @JvmField
        var preFoundationX = 35.0
        @JvmField
        var preFoundationY = 48.0

        @JvmField
        var backIntoFoundationX = 30.0
        @JvmField
        var foundationTurnSpeed = 1.0

        @JvmField
        var pullFoundationX = 38.0

        @JvmField
        var postFoundationWallX = 36.0
    }


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
        stopDoNothing
    }

    override fun onStart() {
        when (SkystoneDetector.place) {
            SkystoneRandomization.FAR -> {
                stoneOrder.add(Quarry[QuarryLocation.FAR_LEFT])
                stoneOrder.add(Quarry[QuarryLocation.NEAR_LEFT])
            }
            SkystoneRandomization.MID -> {
                stoneOrder.add(Quarry[QuarryLocation.FAR_MIDDLE])
                stoneOrder.add(Quarry[QuarryLocation.NEAR_MIDDLE])
            }
            SkystoneRandomization.NEAR -> {
                stoneOrder.add(Quarry[QuarryLocation.FAR_RIGHT])
                stoneOrder.add(Quarry[QuarryLocation.NEAR_RIGHT])
            }
        }
    }

    override fun onMainLoop() {
        stopDrive()
        val currentStage = progStages.values()[stage]

        telemetry.addData("currentStage", currentStage)

        when (currentStage) {
            progStages.goingToIntakeAngle, progStages.goingToSecondIntakeAngle -> {
                if (changedStage)
                    lift.triggerIntake()
                val stone = stoneOrder[if (currentStage == progStages.goingToIntakeAngle) 0 else 1]
                val error = goToPosition_mirror(stone.center_x + xOffset, stone.center_y + yOffset, -90.0 + intakeAngleOffset)
                telemetry.addData("hypot", error.point.hypot)
                telemetry.addData("deg", error.deg)
                if (error.deg.absoluteValue < 2.0 && error.point.hypot < 3.0 && Speedometer.robotSpeed.hypot < 5.0)
                    nextStage()
            }

            progStages.goForwardToIntake, progStages.goingForwardToSecondIntake -> {
                intake.state = MainIntake.State.IN
                pointAngle_mirror(-90.0 + intakeAngleOffset)

                movement_y = intakeSpeed

                if (isTimedOut(3.0) || world_x_mirror < 20.0 || intake.sensorTriggered)
                    nextStage()
            }

            progStages.backOut, progStages.secondBackOut -> {
                moveFieldCentric_mirror(1.0, 0.0, 0.0)
                pointAngle_mirror(180.0)
                if (world_x_mirror > pullOutX) {
                    lift.lower()
                    intake.state = MainIntake.State.OUT
                    ScorerState.triggerGrab()
                    nextStage()
                }
            }

            progStages.preFoundationCrossField -> {
                if (isTimedOut(0.5))
                    intake.state = MainIntake.State.STOP

                goToPosition_mirror(preFoundationX, preFoundationY, 180.0, yClip = 1.0)
                if (world_y_mirror > 24.0) {
                    ScorerState.triggerExtend()
                    foundationGrabber.prepForGrab()
                    nextStage()
                }
            }

            progStages.preFoundationTurn -> {
                val error = goToPosition_mirror(preFoundationX, preFoundationY, 90.0)
                if (error.deg.absoluteValue < 2.0 && error.point.hypot < 3.0)
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
                moveFieldCentric_mirror(1.0, 0.0, 0.0)
                pointAngle_mirror(90.0)
                if (world_x_mirror > pullFoundationX)
                    nextStage()
            }

            progStages.rotateFoundation -> {
                stopDrive()
                movement_turn = foundationTurnSpeed * ALLIANCE.sign
                if (world_angle_mirror.deg < 0.0 || world_angle_mirror.deg > 175.0) {
                    nextStage()
                    foundationGrabber.release()
                    ScorerState.triggerPullBack()
                }
            }

            progStages.postFoundationAwayFromWall -> {
                val timedOut = isTimedOut(0.25)
                if (timedOut) {
                    moveFieldCentric_mirror(-0.5, -0.5, 0.0)
                    pointAngle_mirror(180.0)
                    if (world_x_mirror < postFoundationWallX)
                        nextStage()
                }
            }

            progStages.crossForSecondStone -> {
                val error = goToPosition_mirror(preFoundationX, stoneOrder[1].center_y + yOffset, 180.0, yClip = 1.0)
                if (error.y.absoluteValue < 4.0 || world_y_mirror < -16.0)
                    nextStage()
            }

            progStages.stopDoNothing -> {
                intake.state = MainIntake.State.STOP
                stopDrive()
                if (isTimedOut(2.0))
                    requestOpModeStop()
            }
        }
    }

    override fun onInitLoop() {
        telemetry.addData("skystone randomization", SkystoneDetector.place)
    }
}

@Autonomous
class DoubleSkystoneIntake_RED : DoubleSkystoneIntake(Alliance.RED)