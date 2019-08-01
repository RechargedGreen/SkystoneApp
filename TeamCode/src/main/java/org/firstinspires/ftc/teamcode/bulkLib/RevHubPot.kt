package org.firstinspires.ftc.teamcode.bulkLib

import org.firstinspires.ftc.teamcode.util.*


open class RevHubPot(name: String) : RevHubAnalogSensor(name) {
    val rangeInDegrees = 270.0

    val radians: Double
        get() = degrees.toRadians()
    val degrees: Double
        get() = (maxVoltage / rangeInDegrees) * voltage
}