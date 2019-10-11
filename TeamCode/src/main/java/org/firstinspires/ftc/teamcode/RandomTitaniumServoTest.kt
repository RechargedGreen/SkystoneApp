package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp
class RandomTitaniumServoTest : LinearOpMode() {
    override fun runOpMode() {
        val s = hardwareMap.servo.get("s")
        waitForStart()
        while (opModeIsActive()) {
            when {
                gamepad1.left_bumper -> s.position = 0.15
                gamepad1.right_bumper -> s.position = 0.75
            }
        }
    }
}