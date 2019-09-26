package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import com.qualcomm.robotcore.util.*
import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.MovementAlgorithms.PD.goToPosition_mirror
import kotlin.math.*

object AutoScoreStateMachine {
    var currentStone = Stone(0)

    enum class progStates {
        drive,
        drop
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
        currentStone = IntakeStateMachine.currentStone
        nextStage()
        state = progStates.drive.ordinal
    }

    fun update(): Boolean {
        val x = Field.EAST_WALL - 24.0
        val y = Field.NORTH_WALL - 48.0
        val deg = -180.0

        val r = goToPosition_mirror(x, y, deg)

        when (state) {
            progStates.drive.ordinal -> {
                if (r.deg.absoluteValue < 2.0 && r.distance < 2.0)
                    nextStage()
            }

            progStates.drop.ordinal  -> {
                return isTimedOut(0.5)
            }
        }
        return false
    }
}