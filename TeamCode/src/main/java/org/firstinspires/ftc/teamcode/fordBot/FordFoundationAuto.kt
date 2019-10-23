package org.firstinspires.ftc.teamcode.fordBot

import com.qualcomm.robotcore.eventloop.opmode.*

@Autonomous(group = ".1")
class FordNavigation : FordBot() {
    val delayTime = 1000L
    override fun run() {
        sleep(28000 - delayTime)
        extension.power = 1.0
        sleep(delayTime)
        extension.power = 0.0
    }
}