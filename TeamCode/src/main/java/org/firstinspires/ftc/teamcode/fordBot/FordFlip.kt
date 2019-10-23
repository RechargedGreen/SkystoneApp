package org.firstinspires.ftc.teamcode.fordBot

import com.acmerobotics.dashboard.config.*
import org.firstinspires.ftc.teamcode.bulkLib.*

@Config
class FordFlip {
    var flippedUp = false
        set(value) {
            field = value

            val pos = if (value) up else down

            rightFlip.position = pos
            leftFlip.position = 1.0 - pos
        }

    val leftFlip = RevHubServo("intakeFlipL", FordBot.instance.hardwareMap)
    val rightFlip = RevHubServo("intakeFlipR", FordBot.instance.hardwareMap)

    companion object {
        @JvmField
        var up = 0.2
        @JvmField
        var down = 0.8
    }
}