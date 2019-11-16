package org.firstinspires.ftc.teamcode.leaguebot.opmode.teleop

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.leaguebot.LeagueBotTeleOpBase
import org.firstinspires.ftc.teamcode.leaguebot.opmode.ScorerState
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.LeagueBot
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.LeagueThreeWheelOdometry
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.MainIntake
import org.firstinspires.ftc.teamcode.movement.DriveMovement
import org.firstinspires.ftc.teamcode.movement.DriveMovement.roadRunnerPose2dRaw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_unwrapped_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_raw
import org.firstinspires.ftc.teamcode.movement.Speedometer
import org.firstinspires.ftc.teamcode.movement.toDegrees

@TeleOp
@Config
class LeagueTeleOp : LeagueBotTeleOpBase() {
    private var highestTower = 0
    private var towerHeight = 0
    private var hasReleased = false

    companion object {
        @JvmField
        var liftBias = 0.0

        @JvmField
        var stoneH = 4.0
    }

    enum class LiftState {
        GOING_TO_STONE_HEIGHT,
        GOING_DOWN
    }

    enum class ExtensionRoutineState {
        OFF,
        GRAB,
        EXTEND
    }

    var extensionRoutineState = ExtensionRoutineState.OFF
    var liftState = LiftState.GOING_DOWN

    override fun onMainLoop() {
        DriveMovement.gamepadControl(driver)

        when {
            operator.dRight.justPressed -> towerHeight = highestTower
            operator.dLeft.justPressed -> towerHeight = 0
            operator.dUp.justPressed || driver.y.justPressed -> towerHeight++
            operator.dDown.justPressed || driver.b.justPressed -> towerHeight--
        }

        if (towerHeight < 0)
            towerHeight = 0

        if (towerHeight > highestTower)
            highestTower = towerHeight

        LeagueBot.intake.state = when {
            gamepad1.right_bumper -> {
                ScorerState.triggerLoad()
                liftState = LiftState.GOING_DOWN
                extensionRoutineState = ExtensionRoutineState.OFF
                hasReleased = false
                if (ScorerState.clearToIntake) MainIntake.State.IN else MainIntake.State.OUT
            }
            gamepad1.left_bumper -> {
                MainIntake.State.OUT
            }
            else -> {
                MainIntake.State.STOP
            }
        }

        if (driver.leftTriggerB.justPressed) {
            extensionRoutineState = when (extensionRoutineState) {
                ExtensionRoutineState.OFF -> ExtensionRoutineState.GRAB
                ExtensionRoutineState.GRAB -> ExtensionRoutineState.EXTEND
                ExtensionRoutineState.EXTEND -> ExtensionRoutineState.GRAB
            }
        }

        if (driver.rightTriggerB.justPressed) {
            when (liftState) {
                LiftState.GOING_DOWN -> {
                    if (extensionRoutineState == ExtensionRoutineState.OFF)
                        extensionRoutineState = ExtensionRoutineState.GRAB
                    liftState = LiftState.GOING_TO_STONE_HEIGHT
                    hasReleased = false
                }
                LiftState.GOING_TO_STONE_HEIGHT -> {
                    liftState = LiftState.GOING_DOWN
                }

            }
        }

        if (driver.leftBumper.currentState && liftState == LiftState.GOING_TO_STONE_HEIGHT)
            hasReleased = true

        ScorerState.state = if (hasReleased || (driver.leftBumper.currentState && extensionRoutineState == ExtensionRoutineState.EXTEND)) ScorerState.State.RELEASE else when (extensionRoutineState) {
            ExtensionRoutineState.OFF -> ScorerState.State.INTAKING
            ExtensionRoutineState.GRAB -> ScorerState.State.GRAB
            ExtensionRoutineState.EXTEND -> ScorerState.State.EXTEND
        }

        when (liftState) {
            LiftState.GOING_DOWN -> LeagueBot.lift.lower()
            LiftState.GOING_TO_STONE_HEIGHT -> {
                if (towerHeight == 0 || !ScorerState.clearToLift)
                    LeagueBot.lift.lower()
                else
                    LeagueBot.lift.heightTarget = towerHeight.toDouble() * stoneH + liftBias
            }
        }

        //DriveMovement.moveFieldCentric(driver.leftStick.x, driver.leftStick.y, driver.rightStick.x)

        if (driver.b.currentState)
            DriveMovement.setPosition_raw(0.0, 0.0, 0.0)

        telemetry.addData("current tower height, ", towerHeight)
        telemetry.addData("highest tower, ", highestTower)
        telemetry.addLine()
        telemetry.addData("extension routine", extensionRoutineState)
        telemetry.addData("lift state", liftState)
        telemetry.addData("hasReleased", hasReleased)
        telemetry.addLine()

        telemetry.addData("drive wheels y pos", LeagueBot.drive.y_drivePos)
        telemetry.addData("lf pos", LeagueBot.drive.leftFront.encoderTicks)
        telemetry.addData("lb pos", LeagueBot.drive.leftBack.encoderTicks)
        telemetry.addData("rf pos", LeagueBot.drive.rightFront.encoderTicks)
        telemetry.addData("rb pos", LeagueBot.drive.rightBack.encoderTicks)

        combinedPacket.put("ys", driver.leftStick.y)

        combinedPacket.put("y fps", Speedometer.yInchPerSec / 12.0)
        combinedPacket.put("x fps", Speedometer.xInchPerSec / 12.0)
        combinedPacket.put("leftInches", LeagueThreeWheelOdometry.leftInches)
        combinedPacket.put("rightInches", LeagueThreeWheelOdometry.rightInches)
        combinedPacket.put("auxInches", LeagueThreeWheelOdometry.auxInches)
        combinedPacket.put("y", world_y_raw)
        combinedPacket.put("x", world_x_raw)
        combinedPacket.put("deg", world_angle_unwrapped_raw.deg)
        combinedPacket.put("rr deg", roadRunnerPose2dRaw.heading.toDegrees)
    }
}