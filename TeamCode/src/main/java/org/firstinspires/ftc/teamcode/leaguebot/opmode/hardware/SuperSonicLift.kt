package org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.teamcode.bulkLib.Encoder
import org.firstinspires.ftc.teamcode.bulkLib.MotorEncoder
import org.firstinspires.ftc.teamcode.bulkLib.RevHubMotor
import org.firstinspires.ftc.teamcode.bulkLib.RevHubTouchSensor
import org.firstinspires.ftc.teamcode.lib.Globals.mode
import org.firstinspires.ftc.teamcode.lib.hardware.Go_5_2
import org.firstinspires.ftc.teamcode.util.Clock
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * distance is in inches
 * rail length of 200mm = 7.874"
 * stroke length of 120 mm = 4.724"
 * spool diameter is 1.35"
 * */

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
        var kSlideG: Double = 0.0

        @JvmField
        var kStoneG = 0.0

        @JvmField
        var kConstantG = 0.0

        @JvmField
        var stageStrokeLength = 4.724

        @JvmField
        var staticHeight = 0.0

        var hasBeenCalibrated = false

        var holdingStone = false

        @JvmField
        var kStatic = 0.0

        private const val slides = 8
        private const val maxStages = slides - 1

        private var resetExtension = 0.0

        private var spoolDiameter = 1.968
    }

    var manualTemp = 0.0
    fun updateManualTemp(){
        left.power = manualTemp
        right.power = manualTemp
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
        val ff = calculateCurrentWeight

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
                    0.0
                } else {
                    val p = extensionLeft * kP - speed * kD
                    val s = if (extensionLeft.absoluteValue < 0.2) 0.0 else extensionLeft.sign * kStatic
                    p + s
                }
            }

            controlstates.powerControl -> {
                checkCalibration()
                internalPower = power + ff
            }
        }
    }

    fun checkCalibration() {
        if (false/*downSensor.pressed*/) {
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

            weight += kConstantG

            if (holdingStone)
                weight += kStoneG

            var stages = (extension / stageStrokeLength).toInt()

            if (stages > maxStages)
                stages = maxStages

            weight += stages * kSlideG

            return weight
        }

    val left = RevHubMotor("leftLift", Go_5_2::class).openLoopControl
    val right = RevHubMotor("rightLift", Go_5_2::class).reverse.openLoopControl
    val encoder = Encoder(LeagueBot.lynx2, 3, MotorEncoder.G3_7)
//    val downSensor = RevHubTouchSensor("liftTouch")

    private var internalPower = 0.0
        set(value) {
            val adjustedPower = if (safeMode) Range.clip(value, -0.25, 0.25) else value
            field = adjustedPower
            left.power = adjustedPower
            right.power = adjustedPower
        }
}