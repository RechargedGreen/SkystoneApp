package org.firstinspires.ftc.teamcode.lib

import com.qualcomm.robotcore.hardware.Gamepad

class Controller(private val gamepad: Gamepad) {
    val leftY: Double
        get() = -gamepad.left_stick_y.toDouble()
    val rightY: Double
        get() = -gamepad.right_stick_y.toDouble()
    val leftX: Double
        get() = gamepad.left_stick_x.toDouble()
    val rightX: Double
        get() = gamepad.right_stick_x.toDouble()

    val leftTrigger: Double
        get() = gamepad.left_trigger.toDouble()
    val rightTrigger: Double
        get() = gamepad.right_trigger.toDouble()

    val rightBumper: Boolean
        get() = gamepad.left_bumper
    val leftBumper: Boolean
        get() = gamepad.right_bumper

    val a: Boolean
        get() = gamepad.a
    val b: Boolean
        get() = gamepad.b
    val x: Boolean
        get() = gamepad.x
    val y: Boolean
        get() = gamepad.y

    val dLeft: Boolean
        get() = gamepad.dpad_left
    val dRight: Boolean
        get() = gamepad.dpad_right
    val dUp: Boolean
        get() = gamepad.dpad_up
    val dDown: Boolean
        get() = gamepad.dpad_down
}