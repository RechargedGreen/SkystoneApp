package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.field.Quarry
import org.firstinspires.ftc.teamcode.field.Stone
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.LeagueBot
import org.firstinspires.ftc.teamcode.lib.Globals
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveFieldCentric_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.goToPosition_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.pointAngle_mirror
import kotlin.math.absoluteValue

object IntakeStateMachine {
    const val mainSkystoneIntakeAngle = -90.0
    const val pullSpeed = 0.3
    const val intakeSpeed = 0.3

    var currentStone = Stone(0)

    enum class Type {
        skystone,
        wallSkystone,
        normal
    }

    var type = Type.skystone

    enum class skystoneProgStates {
        crossLine,
        driveTo,
        grab,
        pull,
        intake
    }

    var state = 0
    var stageCompleted = true

    private var timer = ElapsedTime()

    fun isTimedOut(seconds: Double) = timer.seconds() > seconds

    fun nextStage() {
        state++
        stageCompleted = true
        timer.reset()
    }

    fun start() {
        currentStone = Quarry.popStone()

        type = when {
            currentStone.isSkystone && currentStone.isEndFarFromBuildZone -> Type.wallSkystone
            currentStone.isSkystone -> Type.skystone
            else -> Type.normal
        }

        nextStage()
        state = when (type) {
            IntakeStateMachine.Type.skystone -> if (acrossLine) skystoneProgStates.driveTo.ordinal else skystoneProgStates.crossLine.ordinal
            IntakeStateMachine.Type.normal -> 0
            IntakeStateMachine.Type.wallSkystone -> 0
        }
    }

    val acrossLine: Boolean get() = world_y_mirror < 0.0

    fun update(): Boolean {
        return when (type) {
            IntakeStateMachine.Type.skystone -> updateSkystone()
            IntakeStateMachine.Type.normal -> updateNormal()
            IntakeStateMachine.Type.wallSkystone -> updateWallSkystone()
        }
    }

    private fun updateNormal(): Boolean {
        return false
    }

    private fun updateWallSkystone(): Boolean {
        return false
    }

    private fun updateSkystone(): Boolean {
        Globals.mode.telemetry.addData("Intake state", skystoneProgStates.values()[state])
        when (state) {
            skystoneProgStates.crossLine.ordinal -> {
                goToPosition_mirror(48.0, currentStone.center_y, -180.0)
                if (acrossLine)
                    nextStage()
            }
            skystoneProgStates.driveTo.ordinal, skystoneProgStates.grab.ordinal -> {
                val x = currentStone.side_x + 4.0 + LeagueBot.placeLength / 2.0
                val y = currentStone.center_y

                val r = goToPosition_mirror(x, y, mainSkystoneIntakeAngle)

                if (state == skystoneProgStates.driveTo.ordinal) {
                    Globals.mode.telemetry.addData("distance", r.distance)
                    Globals.mode.telemetry.addData("deg", r.deg)
                    if (r.deg.absoluteValue < 2.0 && r.distance < 1.0) {
                        nextStage()
                    }
                } else {
                    if (isTimedOut(0.5)) {
                        nextStage()
                    }
                }
            }

            skystoneProgStates.pull.ordinal -> {
                moveFieldCentric_mirror(pullSpeed, 0.0, 0.0)
                pointAngle_mirror(mainSkystoneIntakeAngle)

                if (world_x_mirror > 48.0 + 5)
                    nextStage()
            }

            skystoneProgStates.intake.ordinal -> {
                moveFieldCentric_mirror(-intakeSpeed, 0.0, 0.0)
                pointAngle_mirror(mainSkystoneIntakeAngle)

                if (world_x_mirror < 48.0)
                    return true
            }
        }

        return false
    }
}