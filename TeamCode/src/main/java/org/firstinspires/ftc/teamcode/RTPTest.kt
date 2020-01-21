package org.firstinspires.ftc.teamcode

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor

@Config
@TeleOp
class RTPTest : LinearOpMode() {
    companion object {
        @JvmField
        var target = -500
    }

    override fun runOpMode() {
        val motor = hardwareMap.dcMotor.get("leftLift")
        motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        waitForStart()

        val startTicks = motor.currentPosition

        while (opModeIsActive()) {

            val targetTicks = startTicks + target

            motor.mode = DcMotor.RunMode.RUN_TO_POSITION
            motor.targetPosition = targetTicks
            motor.power = 1.0

            val currentTicks = motor.currentPosition
            val error = targetTicks - currentTicks

            telemetry.addData("error", error)
            telemetry.update()
        }
    }
}