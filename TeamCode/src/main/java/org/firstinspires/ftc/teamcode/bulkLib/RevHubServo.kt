package org.firstinspires.ftc.teamcode.bulkLib

import com.qualcomm.robotcore.hardware.*

class RevHubServo(name: String, hMap: HardwareMap = BlackMagic.hMap) {
    val servo = hMap.servo.get(name)
    var position = Double.NaN
        set(value) {
            if (!value.isNaN() && value != field) {
                servo.position = value
                field = value
            }
        }
}