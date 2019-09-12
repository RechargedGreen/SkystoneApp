package org.firstinspires.ftc.teamcode.sharedhardware

import org.firstinspires.ftc.teamcode.lib.*
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.gamepadControl
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveRobotCentric_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_turn
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_x
import org.firstinspires.ftc.teamcode.movement.DriveMovement.movement_y
import org.firstinspires.ftc.teamcode.movement.DriveMovement.stopDrive
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_unwrapped_raw

object Calibration {
    object SlipPredictions : StateMachine() {
        enum class states {
            control1,
            accelerateForwards,
            slipForwards,

            control2,
            accelerateSideways,
            slipSideways,

            control3,
            accelerateTurn,
            slipTurn,

            doNothing
        }

        val GOING_FORWARDS_SPEED = 0.3
        val GOING_SIDEWAYS_SPEED = 0.3
        val TURNING_SPEED = 0.4

        val ACCELERATION_TIME = 1.0
        val SLIP_TIME = 2.0

        var newSlipYFactor = 0.0
        var newSlipXFactor = 0.0
        var newSlipTurnFactor = 0.0

        var saved_speed_y = 0.0
        var saved_speed_x = 0.0
        var saved_speed_turn = 0.0

        override fun onUpdate(): Boolean {
            outputCalibrationData()

            when (states.values()[Calibration.MinPower.ordinal]) {
                Calibration.SlipPredictions.states.control1, Calibration.SlipPredictions.states.control2, Calibration.SlipPredictions.states.control3 -> {
                    val controller = Globals.mode.driver
                    Globals.mode.combinedPacket.addLine("Press A to continue. Allowing user input")
                    gamepadControl(controller)
                    if (controller.a.currentState)
                        nextStage()
                }
                Calibration.SlipPredictions.states.accelerateForwards                                                                                 -> {
                    moveRobotCentric_raw(0.0, GOING_FORWARDS_SPEED, 0.0)

                    if (isTimedOut(ACCELERATION_TIME)) {
                        saved_speed_y = Speedometer.yInchPerSec
                        nextStage()
                    }
                }
                Calibration.SlipPredictions.states.slipForwards                                                                                       -> {
                    stopDrive()

                    val distance = distanceFromStateStart

                    if (isTimedOut(SLIP_TIME)) {
                        newSlipYFactor = distance / saved_speed_y
                        nextStage()
                    }
                }
                Calibration.SlipPredictions.states.accelerateSideways                                                                                 -> {
                    moveRobotCentric_raw(GOING_SIDEWAYS_SPEED, 0.0, 0.0)

                    if (isTimedOut(ACCELERATION_TIME)) {
                        saved_speed_x = Speedometer.yInchPerSec
                        nextStage()
                    }
                }
                Calibration.SlipPredictions.states.slipSideways                                                                                       -> {
                    stopDrive()

                    val distance = distanceFromStateStart

                    if (isTimedOut(SLIP_TIME)) {
                        newSlipXFactor = distance / saved_speed_x
                        nextStage()
                    }
                }
                Calibration.SlipPredictions.states.accelerateTurn                                                                                     -> {
                    moveRobotCentric_raw(0.0, 0.0, TURNING_SPEED)

                    if (isTimedOut(ACCELERATION_TIME)) {
                        saved_speed_turn = Speedometer.radPerSec
                        nextStage()
                    }
                }
                Calibration.SlipPredictions.states.slipTurn                                                                                           -> {
                    stopDrive()

                    val radsTurned = (world_angle_unwrapped_raw - stateStartAngle).rad

                    if (isTimedOut(SLIP_TIME)) {
                        newSlipTurnFactor = radsTurned / saved_speed_turn
                        nextStage()
                    }

                }
                Calibration.SlipPredictions.states.doNothing                                                                                          -> {
                    stopDrive()
                }
            }
            return ordinal == states.doNothing.ordinal
        }

        fun outputCalibrationData() {
            Globals.mode.combinedPacket.put("new slip y", newSlipYFactor)
            Globals.mode.combinedPacket.put("new slip x", newSlipXFactor)
            Globals.mode.combinedPacket.put("new slip turn", newSlipTurnFactor)
        }

        override fun onStart() {
            newSlipXFactor = 0.0
            newSlipYFactor = 0.0
            newSlipTurnFactor = 0.0
        }
    }

    object MinPower : StateMachine() {
        enum class states {
            forwards,
            stopForwards,
            sideways,
            stopSideways,
            turn,
            stopTurn,

            doNothing
        }

        val RAMP_TO_FULL_SPEED_TIME = 10.0
        val STOP_TIME = 2.0

        val TURN_DEG = 2.0
        val DRIVE_INCH = 0.5

        var newYMin = 0.0
        var newXMin = 0.0
        var newTurnMin = 0.0

        override fun onUpdate(): Boolean {
            when (states.values()[ordinal]) {
                Calibration.MinPower.states.stopForwards, Calibration.MinPower.states.stopSideways, Calibration.MinPower.states.stopTurn -> {
                    stopDrive()
                    if (isTimedOut(STOP_TIME))
                        nextStage()
                }
                Calibration.MinPower.states.forwards                                                                                     -> {
                    moveRobotCentric_raw(0.0, stateSeconds / RAMP_TO_FULL_SPEED_TIME, 0.0)
                    newYMin = movement_y
                    if (distanceFromStateStart > DRIVE_INCH)
                        nextStage()
                }
                Calibration.MinPower.states.sideways                                                                                     -> {
                    moveRobotCentric_raw(stateSeconds / RAMP_TO_FULL_SPEED_TIME, 0.0, 0.0)
                    newXMin = movement_x
                    if (distanceFromStateStart > DRIVE_INCH)
                        nextStage()
                }
                Calibration.MinPower.states.turn                                                                                         -> {
                    moveRobotCentric_raw(0.0, 0.0, stateSeconds / RAMP_TO_FULL_SPEED_TIME)
                    newTurnMin = movement_turn
                    if (angleFromStateStart.deg > TURN_DEG)
                        nextStage()
                }
                Calibration.MinPower.states.doNothing                                                                                    -> {
                    stopDrive()
                }
            }

            return ordinal == states.doNothing.ordinal
        }

        override fun onStart() {
        }
    }
}