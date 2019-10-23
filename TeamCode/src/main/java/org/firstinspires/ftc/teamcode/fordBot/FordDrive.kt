package org.firstinspires.ftc.teamcode.fordBot

import RevHubMotor
import org.firstinspires.ftc.teamcode.lib.hardware.*

class FordDrive {
    private val lf = RevHubMotor("lf", ActualRev20::class, FordBot.instance.hardwareMap).brake.velocityControl
    private val lb = RevHubMotor("lb", ActualRev20::class, FordBot.instance.hardwareMap).brake.velocityControl
    private val rf = RevHubMotor("rf", ActualRev20::class, FordBot.instance.hardwareMap).brake.velocityControl.reverse
    private val rb = RevHubMotor("rb", ActualRev20::class, FordBot.instance.hardwareMap).brake.velocityControl.reverse

    fun power(left: Double, right: Double) {
        lf.power = left
        lb.power = left

        rf.power = right
        rb.power = right
    }
}