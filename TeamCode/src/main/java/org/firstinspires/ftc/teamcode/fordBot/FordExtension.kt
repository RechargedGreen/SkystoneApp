package org.firstinspires.ftc.teamcode.fordBot

import org.firstinspires.ftc.teamcode.bulkLib.RevHubMotor
import org.firstinspires.ftc.teamcode.lib.hardware.*

class FordExtension {
    var power = 0.0
        set(value) {
            field = value
            motor.power = value
        }
    private val motor = RevHubMotor("extension", Go_3_7::class, FordBot.instance.hardwareMap).brake.reverse
}