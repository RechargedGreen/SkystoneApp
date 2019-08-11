package org.firstinspires.ftc.teamcode.lib

import com.qualcomm.robotcore.util.*
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_angle_unwrapped
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_x
import org.firstinspires.ftc.teamcode.movement.DriveMovement.world_y
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
        get() = hypot(world_x - stateStartX, world_y - stateStartY)
    val angleFromStateStart: Angle
        get() = world_angle - stateStartAngle

    fun nextStage(nextStage: Int = ordinal + 1) {
        ordinal = nextStage
        stateTimer.reset()
        startedStage = true

        stateStartY = world_y
        stateStartX = world_x
        stateStartAngle = world_angle_unwrapped
        stateStartAngleWrap = world_angle
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