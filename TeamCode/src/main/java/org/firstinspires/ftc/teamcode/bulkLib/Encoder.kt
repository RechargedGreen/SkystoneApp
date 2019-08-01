package org.firstinspires.ftc.teamcode.bulkLib

import com.qualcomm.hardware.lynx.*
import org.firstinspires.ftc.teamcode.util.*

class Encoder(private val module: LynxModule, private val portNumber: Int, private val ticks_per_revolution: Double) {
    val ticks: Int
        get() = module.cachedInput.getEncoder(portNumber)
    val rotations: Double
        get() = ticks / ticks_per_revolution
    val radians: Double
        get() = rotations * MathUtil.TAU
}

object S4T {
    const val CPR_1000 = 4000.0
}