package org.firstinspires.ftc.teamcode.fordBot

import RevHubMotor
import org.firstinspires.ftc.teamcode.lib.hardware.*

class FordExtension {
    var power = 0.0
    private val motor = RevHubMotor("extension", Go_3_7::class, FordBot.instance.hardwareMap).brake.reverse
    fun update() {
        motor.power = power
    }
}