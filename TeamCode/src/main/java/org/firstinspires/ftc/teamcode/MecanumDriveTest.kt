package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.*
import com.qualcomm.robotcore.hardware.*
import kotlin.math.*

@TeleOp
class MecanumDriveTest : LinearOpMode() {
    override fun runOpMode() {
        val leftFront = hardwareMap.dcMotor.get("leftFront")
        val leftBack = hardwareMap.dcMotor.get("leftBack")

        val rightFront = hardwareMap.dcMotor.get("rightFront")
        val rightBack = hardwareMap.dcMotor.get("rightBack")

        rightFront.direction = DcMotorSimple.Direction.REVERSE
        rightBack.direction = DcMotorSimple.Direction.REVERSE

        val x = -gamepad1.left_stick_y.toDouble()
        val y = gamepad1.left_stick_x.toDouble()
        val turn = gamepad1.right_stick_y.toDouble()

        var leftFrontPower = y + turn + x
        var leftBackPower = y + turn - x
        var rightFrontPower = y - turn - x
        var rightBackPower = y - turn + x

        val max = listOf(leftFrontPower.absoluteValue, leftBackPower.absoluteValue, rightFrontPower.absoluteValue, rightBackPower.absoluteValue, 1.0).max()!!

        if (max > 1.0) {
            leftFrontPower /= max
            leftBackPower /= max
            rightFrontPower /= max
            rightBackPower /= max
        }

        leftFront.power = leftFrontPower
        leftBack.power = leftBackPower
        rightFront.power = rightFrontPower
        rightBack.power = rightBackPower

        telemetry.addData("y", y)
        telemetry.addData("x", x)
        telemetry.addData("turn", turn)
        telemetry.update()
    }
}