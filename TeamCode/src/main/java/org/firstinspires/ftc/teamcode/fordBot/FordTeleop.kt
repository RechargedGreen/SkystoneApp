package org.firstinspires.ftc.teamcode.fordBot

import org.firstinspires.ftc.teamcode.lib.*
import org.firstinspires.ftc.teamcode.util.*

class FordTeleop : FordBot(Alliance.RED, 0.0) {
    override fun run() {
        Companion.loop {
            val left = -gamepad1.left_stick_y.toDouble() deadZone 0.05
            val right = -gamepad1.right_stick_y.toDouble() deadZone 0.05

            drive.movement_turn = (left - right) / 2.0
            drive.movement_y = (left + right) / 2.0

            val intakeSpeed = (gamepad1.right_trigger - gamepad1.left_trigger).toDouble() deadZone 0.05
            intake.power = intakeSpeed

            when {
                gamepad1.right_bumper -> {
                    flip.flippedUp = false
                }

                gamepad1.left_bumper  -> {
                    flip.flippedUp = true
                }
            }

            val extensionSpeed = -gamepad2.right_stick_y.toDouble() deadZone 0.05
            extension.power = extensionSpeed

            val ticks = drive.ticks

            telemetry.addData("y_pos", drive.y_pos)
            telemetry.addData("deg", drive.deg)

            telemetry.addData("lf", ticks[0])
            telemetry.addData("lb", ticks[1])
            telemetry.addData("rf", ticks[2])
            telemetry.addData("rb", ticks[3])

            return@loop false
        }
    }
}