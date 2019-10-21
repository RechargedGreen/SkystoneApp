package org.firstinspires.ftc.teamcode.fordBot

import com.acmerobotics.dashboard.config.*
import org.firstinspires.ftc.teamcode.bulkLib.*

@Config
class FordFlip {
    var flippedUp = false

    val leftFlip = RevHubServo("flipL", FordBot.instance.hardwareMap)
    val rightFlip = RevHubServo("flipR", FordBot.instance.hardwareMap)

    fun update() {
        val pos = if (flippedUp) up else down

        rightFlip.position = pos
        leftFlip.position = 1.0 - pos
    }

    companion object {
        @JvmField
        var up = 0.2
        @JvmField
        var down = 0.8
    }
}