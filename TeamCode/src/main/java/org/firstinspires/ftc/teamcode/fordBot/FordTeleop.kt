package org.firstinspires.ftc.teamcode.fordBot

import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.util.*

@TeleOp(group = ".1")
class FordTeleop : FordBot() {
    var flippedUp = false
    override fun run() {
        while (!isStopRequested) {
            val left = -gamepad1.left_stick_y.toDouble() deadZone 0.1
            val right = -gamepad1.right_stick_y.toDouble() deadZone 0.1

            drive.power(left, right)

            val intakeSpeed = (gamepad1.right_trigger - gamepad1.left_trigger).toDouble() deadZone 0.05
            intake.power = intakeSpeed

            when {
                gamepad1.right_bumper -> {
                    flippedUp = false
                }

                gamepad1.left_bumper  -> {
                    flippedUp = true
                }
            }

            flip.flippedUp = flippedUp

            val extensionSpeed = -gamepad2.right_stick_y.toDouble() deadZone 0.05
            extension.power = extensionSpeed

            telemetry.addLine("running")
            telemetry.update()
        }
    }
}