package org.firstinspires.ftc.teamcode.leaguebot.teleop

import com.acmerobotics.dashboard.config.*
import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.leaguebot.hardware.*
import org.firstinspires.ftc.teamcode.leaguebot.misc.*
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.roadRunnerPose2dRaw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_unwrapped_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_raw

@TeleOp(group = "a")
@Config
open class LeagueTeleOp : LeagueBotTeleOpBase() {
    var grabbingFoundationToggle = false

    private var highestTower = 0
    private var towerHeight = 0
    private var hasReleased = false

    companion object {
        @JvmField
        var liftBias = -0.0 //-0.4 // -0.3

        @JvmField
        var stoneH = 4.1
    }

    var inputBias = 0.0

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

        if(driver.b.justPressed)
            Robot.cap.deployed = !Robot.cap.deployed

        DriveMovement.gamepadControl(driver)

        when {
            operator.rightTriggerB.justPressed                        -> towerHeight = highestTower
            operator.leftTriggerB.justPressed                         -> towerHeight = 0
            operator.dUp.justPressed || driver.y.justPressed   -> towerHeight++
            operator.dDown.justPressed || driver.x.justPressed -> towerHeight--
        }

        when {
            operator.y.justPressed -> inputBias += 0.25
            operator.a.justPressed -> inputBias -= 0.25
            operator.x.justPressed -> inputBias = 0.0
        }

        if (towerHeight < 0)
            towerHeight = 0

        if (towerHeight > highestTower)
            highestTower = towerHeight

        Robot.intake.state = when {
            gamepad1.right_bumper -> {
                ScorerState.triggerLoad()
                liftState = LiftState.GOING_DOWN
                extensionRoutineState = ExtensionRoutineState.OFF
                hasReleased = false
                if (ScorerState.clearToIntake) MainIntake.State.IN else MainIntake.State.OUT
            }
            gamepad1.left_bumper  -> {
                MainIntake.State.OUT
            }
            else                  -> {
                MainIntake.State.STOP
            }
        }

        if (driver.leftTriggerB.justPressed) {
            extensionRoutineState = when (extensionRoutineState) {
                ExtensionRoutineState.OFF    -> ExtensionRoutineState.GRAB
                ExtensionRoutineState.GRAB   -> ExtensionRoutineState.EXTEND
                ExtensionRoutineState.EXTEND -> ExtensionRoutineState.GRAB
            }
        }

        if (driver.rightTriggerB.justPressed) {
            when (liftState) {
                LiftState.GOING_DOWN            -> {
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
            ExtensionRoutineState.OFF    -> ScorerState.State.INTAKING
            ExtensionRoutineState.GRAB   -> ScorerState.State.GRAB
            ExtensionRoutineState.EXTEND -> ScorerState.State.EXTEND
        }

        when (liftState) {
            LiftState.GOING_DOWN            -> Robot.lift.lower()
            LiftState.GOING_TO_STONE_HEIGHT -> {
                if (towerHeight == 0 || !ScorerState.clearToLift)
                    Robot.lift.lower()
                else
                    Robot.lift.heightTarget = towerHeight.toDouble() * stoneH + liftBias + inputBias
            }
        }

        if (driver.dUp.justPressed)
            grabbingFoundationToggle = !grabbingFoundationToggle
        if (grabbingFoundationToggle)
            Robot.foundationGrabber.grab()
        else
            Robot.foundationGrabber.release()

        //DriveMovement.moveFieldCentric(driver.leftStick.x, driver.leftStick.y, driver.rightStick.x)

        /*if (operator.b.currentState)
            DriveMovement.setPosition_raw(0.0, 0.0, 0.0)*/

        telemetry.addData("current tower height, ", towerHeight)
        telemetry.addData("highest tower, ", highestTower)
        telemetry.addData("input bias", inputBias)
        telemetry.addLine()
        telemetry.addData("grabbingFoundation", grabbingFoundationToggle)
        telemetry.addData("extension routine", extensionRoutineState)
        telemetry.addData("lift state", liftState)
        telemetry.addData("hasReleased", hasReleased)
        telemetry.addLine()
    }
}