package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import com.qualcomm.robotcore.util.*
import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.moveFieldCentric_mirror
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.goToPosition_mirror
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.pointAngle_mirror
import kotlin.math.*

object IntakeStateMachine {
    const val mainSkystoneIntakeAngle = -90.0
    const val pullSpeed = 0.3
    const val intakeSpeed = 0.3

    var currentStone = Stone(0)

    enum class progStates {
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

        nextStage()
        state = progStates.driveTo.ordinal
    }

    fun update(): Boolean {
        when (state) {
            progStates.driveTo.ordinal, progStates.grab.ordinal -> {
                val x = currentStone.side_x + 4.0 + LeagueBot.placeLength
                val y = currentStone.center_y

                val r = goToPosition_mirror(x, y, mainSkystoneIntakeAngle)

                if (state == progStates.driveTo.ordinal) {
                    if (r.deg.absoluteValue < 2.0 && r.distance < 1.0) {
                        nextStage()
                    }
                } else {
                    if (isTimedOut(0.5)) {
                        nextStage()
                    }
                }
            }

            progStates.pull.ordinal                             -> {
                moveFieldCentric_mirror(pullSpeed, 0.0, 0.0)
                pointAngle_mirror(mainSkystoneIntakeAngle)

                if (world_y_mirror > 48.0 + 5)
                    nextStage()
            }

            progStates.intake.ordinal                           -> {
                moveFieldCentric_mirror(-intakeSpeed, 0.0, 0.0)
                pointAngle_mirror(mainSkystoneIntakeAngle)

                if (world_y_mirror < 48.0)
                    return true
            }
        }

        return false
    }
}