package org.firstinspires.ftc.teamcode.leaguebot.hardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType
import org.firstinspires.ftc.teamcode.bulkLib.RevHubMotor
import org.firstinspires.ftc.teamcode.opmodeLib.Globals
import org.firstinspires.ftc.teamcode.bulkLib.Go_19_2
import org.firstinspires.ftc.teamcode.movement.DriveMovement
import kotlin.math.absoluteValue

class Akira {
    private val motorMode: DcMotor.RunMode = if (Globals.mode.isAutonomous) DcMotor.RunMode.RUN_USING_ENCODER else DcMotor.RunMode.RUN_WITHOUT_ENCODER

    val leftFront = RevHubMotor("leftFront", Go_19_2::class).brake.apply {
        mode = motorMode
    }
    val leftBack = RevHubMotor("leftBack", Go_19_2::class).brake.apply {
        mode = motorMode
    }
    val rightFront = RevHubMotor("rightFront", Go_19_2::class).reverse.brake.apply {
        mode = motorMode
    }
    val rightBack = RevHubMotor("rightBack", Go_19_2::class).reverse.brake.apply {
        mode = motorMode
    }

    val motors = arrayListOf(leftFront, leftBack, rightFront, rightBack)
    val ticksPerRev = MotorConfigurationType.getMotorType(Go_19_2::class.java).ticksPerRev

    val y_drivePos get() = motors.map { it.encoderTicks / ticksPerRev }.average() * Math.PI

    /*private val leftFront = RevHubMotor("leftFront", Go_19_2::class).BRAKE().FORWARD().OPEN_LOOP()
    private val leftBack = RevHubMotor("leftBack", Go_19_2::class).BRAKE().FORWARD().OPEN_LOOP()
    private val rightFront = RevHubMotor("rightFront", Go_19_2::class).BRAKE().REVERSE().OPEN_LOOP()
    private val rightBack = RevHubMotor("rightBack", Go_19_2::class).BRAKE().REVERSE().OPEN_LOOP()*/

    fun update() {
        val x = DriveMovement.movement_x
        val y = DriveMovement.movement_y
        val turn = DriveMovement.movement_turn

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
    }
}