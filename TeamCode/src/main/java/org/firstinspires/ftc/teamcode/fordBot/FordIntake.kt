package org.firstinspires.ftc.teamcode.fordBot

import RevHubMotor
import org.firstinspires.ftc.teamcode.lib.hardware.*

class FordIntake {
    var power = 0.0

    private val motor = RevHubMotor("intake", Go_3_7::class, FordBot.instance.hardwareMap).float

    fun update(){
        motor.power = power
    }
}