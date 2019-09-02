package org.firstinspires.ftc.teamcode.sharedhardware

import com.qualcomm.robotcore.hardware.*
import org.firstinspires.ftc.teamcode.bulkLib.BlackMagic.hMap
import org.firstinspires.ftc.teamcode.bulkLib.RevHubMotor
import org.firstinspires.ftc.teamcode.lib.*
import org.firstinspires.ftc.teamcode.lib.hardware.*
import org.firstinspires.ftc.teamcode.movement.*
import org.firstinspires.ftc.teamcode.movement.movementAlgorithms.*
import kotlin.math.*

class Akira(provider: MovementConstantsProvider) {
    private val motorMode: DcMotor.RunMode
        get() = if (Globals.mode.isAutonomous) DcMotor.RunMode.RUN_USING_ENCODER else DcMotor.RunMode.RUN_WITHOUT_ENCODER

    private val leftFront = hMap.dcMotor.get("leftFront").apply {
        zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }
    private val leftBack = hMap.dcMotor.get("leftBack").apply {
        zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }
    private val rightFront = hMap.dcMotor.get("rightFront").apply {
        zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        direction = DcMotorSimple.Direction.REVERSE
    }
    private val rightBack = hMap.dcMotor.get("rightBack").apply {
        zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        direction = DcMotorSimple.Direction.REVERSE
    }

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