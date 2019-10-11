package org.firstinspires.ftc.teamcode.bulkLib

class RevHubServo(name: String) {
    val servo = BlackMagic.hMap.servo.get(name)
    var position = Double.NaN
        set(value) {
            if (!value.isNaN() && value != field) {
                servo.position = value
                field = value
            }
        }
}