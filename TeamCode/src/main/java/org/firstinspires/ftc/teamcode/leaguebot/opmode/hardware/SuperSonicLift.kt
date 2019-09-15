package org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware

import RevHubMotor
import com.acmerobotics.dashboard.config.*
import com.qualcomm.robotcore.util.*
import org.firstinspires.ftc.teamcode.bulkLib.*
import org.firstinspires.ftc.teamcode.lib.Globals.mode
import org.firstinspires.ftc.teamcode.lib.hardware.*
import org.firstinspires.ftc.teamcode.util.*
import kotlin.math.*

/**
 * distance is in inches
 * weight is in lbs
 * rail length of 200mm = 7.874"
 * stroke length of 120 mm = 4.724"
 * spool diameter is 50mm = 1.968"
 * slide weight is 0.14 lbs
 */

@Config
class SuperSonicLift {
    companion object {
        @JvmField
        var safeMode = true // this limits the speed to make it safer for testing

        @JvmField
        var kP: Double = 0.0

        @JvmField
        var kD: Double = 0.0

        @JvmField
        var kG: Double = 0.0

        @JvmField
        var kStone = 0.0

        @JvmField
        var kConstantWeight = 0.0

        @JvmField
        var kSlideWeight = 0.14

        @JvmField
        var stageStrokeLength = 4.724

        @JvmField
        var staticHeight = 0.0

        var hasBeenCalibrated = false

        var holdingStone = false

        private const val slides = 8
        private const val maxStages = slides - 1

        private var resetExtension = 0.0

        private var spoolDiameter = 1.968
    }

    var lastTime = Double.NaN
    var lastRawExtension = Double.NaN

    var power = 0.0
        set(value) {
            field = value
            state = controlstates.powerControl
        }

    private var targetExtension = 0.0
        set(value) {
            field = value
            state = controlstates.positionControl
        }

    private var state = controlstates.positionControl

    enum class controlstates {
        positionControl,
        powerControl
    }

    init {
        if (mode.isAutonomous) {
            hasBeenCalibrated = false
            holdingStone = false
        }
    }

    fun update() {
        val ff = calculateCurrentWeight * kG

        var currentExtensionTarget = targetExtension
        var currentState = state

        val extensionLeft = currentExtensionTarget - extension
        val currTime = Clock.seconds
        val speed = if (lastTime.isNaN()) 0.0 else {
            val dt = currTime - lastTime
            val change = rawExtension - lastRawExtension
            change / dt
        }
        lastTime = currTime
        lastRawExtension = rawExtension

        if (!hasBeenCalibrated) {
            currentState = controlstates.positionControl
            currentExtensionTarget = 0.0
        }

        when (currentState) {
            controlstates.positionControl -> {
                internalPower = if (currentExtensionTarget == 0.0) {
                    checkCalibration()
                    -1.0
                } else {
                    extensionLeft * kP - speed * kD
                }
            }

            controlstates.powerControl    -> {
                checkCalibration()
                internalPower = power * ff
            }
        }
    }

    fun checkCalibration() {
        if (downSensor.pressed) {
            hasBeenCalibrated = true
            resetExtension = rawExtension
        }
    }

    val rawExtension: Double get() = encoder.rotations * PI * spoolDiameter
    val extension: Double get() = rawExtension - resetExtension
    val height: Double get() = extension + staticHeight

    val calculateCurrentWeight: Double
        get() {
            var weight = 0.0

            weight += kConstantWeight

            if (holdingStone)
                weight += kStone

            var stages = (extension / stageStrokeLength).toInt()

            if (stages > maxStages)
                stages = maxStages

            weight += stages * kSlideWeight

            return weight
        }

    val left = RevHubMotor("leftLift", Go_5_2::class)
    val right = RevHubMotor("rightLift", Go_5_2::class)
    val encoder = Encoder(LeagueBot.lynx2, 3, MotorEncoder.G5_2)
    val downSensor = RevHubTouchSensor("liftTouch")

    private var internalPower = 0.0
        set(value) {
            val adjustedPower = if (safeMode) Range.clip(value, 0.25, 0.25) else value
            field = adjustedPower
            left.power = adjustedPower
            right.power = adjustedPower
        }
}