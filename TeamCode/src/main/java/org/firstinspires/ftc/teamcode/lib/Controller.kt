package org.firstinspires.ftc.teamcode.lib

import com.qualcomm.robotcore.hardware.*

class Controller(val gamepad: Gamepad) {
    init {
        GamePadMaster.add(this)
    }

    val rightStick = Joystick({ pad -> pad.right_stick_x.toDouble() }, { pad -> pad.right_stick_y.toDouble() })
    val leftStick = Joystick({ pad -> pad.left_stick_x.toDouble() }, { pad -> pad.left_stick_y.toDouble() })

    val leftTrigger: Double
        get() = gamepad.left_trigger.toDouble()
    val rightTrigger: Double
        get() = gamepad.right_trigger.toDouble()

    val rightBumper = Button { pad -> pad.right_bumper }
    val leftBumper = Button { pad -> pad.left_bumper }

    val a = Button { pad -> pad.a }
    val b = Button { pad -> pad.b }
    val x = Button { pad -> pad.x }
    val y = Button { pad -> pad.y }

    val dLeft = Button { pad -> pad.dpad_left }
    val dRight = Button { pad -> pad.dpad_right }
    val dUp = Button { pad -> pad.dpad_up }
    val dDown = Button { pad -> pad.dpad_down }

    private val allParts = arrayOf(rightBumper, leftBumper,
                                   a, b, x, y,
                                   dLeft, dRight, dUp, dDown,
                                   leftStick, rightStick)

    fun update() {
        allParts.forEach { part ->
            part.update(gamepad)
        }
    }
}

class Button(private val getCurrentState: (gamePad: Gamepad) -> Boolean) : GamepadPart {
    var currentState = false
        private set
    var justPressed = false
        private set
    var justReleased = false
        private set

    override fun update(gamePad: Gamepad) {
        val lastState = currentState
        currentState = getCurrentState(gamePad)

        justPressed = currentState && !lastState
        justReleased = !currentState && lastState
    }
}

class Joystick(private val xInput: (Gamepad) -> Double, private val yInput: (Gamepad) -> Double) : GamepadPart {
    var x: Double = 0.0
        private set
    var y: Double = 0.0
        private set

    override fun update(gamePad: Gamepad) {
        x = xInput(gamePad)
        y = yInput(gamePad)
    }
}

object GamePadMaster {
    private val list = ArrayList<Controller>()

    fun add(controller: Controller) {
        list.add(controller)
    }

    fun update() {
        for (controller in list)
            controller.update()
    }

    fun reset() {
        list.clear()
    }
}

private interface GamepadPart {
    fun update(gamePad: Gamepad)
}