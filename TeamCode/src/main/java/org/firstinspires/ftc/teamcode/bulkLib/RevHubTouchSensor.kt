package org.firstinspires.ftc.teamcode.bulkLib

import com.qualcomm.robotcore.hardware.*

open class RevHubTouchSensor(name: String) : RevHubDigitalSensor(name), TouchSensor {
    val released: Boolean
        get() = state
    val pressed: Boolean
        get() = !released

    override fun getValue() = if (pressed) 1.0 else 0.0
    override fun isPressed() = pressed
}