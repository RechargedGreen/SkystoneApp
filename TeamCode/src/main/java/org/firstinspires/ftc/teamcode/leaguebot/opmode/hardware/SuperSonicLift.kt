package org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware

import RevHubMotor
import com.acmerobotics.dashboard.config.*
import org.firstinspires.ftc.teamcode.lib.Globals.mode
import org.firstinspires.ftc.teamcode.lib.hardware.*

@Config
class SuperSonicLift {
    companion object {
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
        var kSegment = 0.0

        var hasBeenCalibrated = false
    }

    init {
        if (mode.isAutonomous)
            hasBeenCalibrated = false
    }

    fun update() {
    }

    val left = RevHubMotor("leftLift", Go_5_2::class)
    val right = RevHubMotor("rightLift", Go_5_2::class)

    private var internalPower = 0.0
        set(value) {
            field = value
            left.power = value
            right.power = value
        }
}