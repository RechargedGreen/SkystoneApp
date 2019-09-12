package org.firstinspires.ftc.teamcode.lib

import com.qualcomm.robotcore.util.*
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_unwrapped_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x_raw
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y_raw
import kotlin.math.*

abstract class StateMachine {
    abstract fun onUpdate(): Boolean
    abstract fun onStart()

    var ordinal = 0

    private val stateTimer = ElapsedTime()

    var stateStartY = 0.0
    var stateStartX = 0.0
    var stateStartAngle = Angle.createUnwrappedDeg(0.0)
    var stateStartAngleWrap = Angle.createUnwrappedDeg(0.0)

    val stateSeconds: Double
        get() = stateTimer.seconds()

    var startedStage = true
        private set

    val distanceFromStateStart: Double
        get() = hypot(world_x_raw - stateStartX, world_y_raw - stateStartY)
    val angleFromStateStart: Angle
        get() = world_angle_raw - stateStartAngle

    fun nextStage(nextStage: Int = ordinal + 1) {
        ordinal = nextStage
        stateTimer.reset()
        startedStage = true

        stateStartY = world_y_raw
        stateStartX = world_x_raw
        stateStartAngle = world_angle_unwrapped_raw
        stateStartAngleWrap = world_angle_raw
    }

    fun start() {
        nextStage(0)
    }

    fun isTimedOut(seconds: Double) = stateSeconds > seconds

    fun timeout(seconds: Double, nextStage: Int = ordinal + 1) {
        if (isTimedOut(seconds))
            nextStage(nextStage)
    }


    fun update(): Boolean {
        val done = onUpdate()
        startedStage = false
        return done
    }
}