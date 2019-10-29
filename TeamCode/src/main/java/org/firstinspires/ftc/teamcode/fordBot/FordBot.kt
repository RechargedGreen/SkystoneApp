package org.firstinspires.ftc.teamcode.fordBot

import com.qualcomm.robotcore.eventloop.opmode.*
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple

abstract class FordBot : LinearOpMode() {
    private lateinit var leftLift:DcMotor
    private lateinit var rightLift:DcMotor

    var lift:Double = 0.0
        set(value) {
            field = value
            leftLift.power = value
            rightLift.power = value
        }

    final override fun runOpMode() {
        instance = this

        drive = FordDrive()
        extension = FordExtension()
        flip = FordFlip()
        intake = FordIntake()

        leftLift = hardwareMap.dcMotor.get("liftL").apply {
            direction = DcMotorSimple.Direction.REVERSE
            zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        }
        rightLift = hardwareMap.dcMotor.get("liftR").apply {
            zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        }

        waitForStart()

        if (!isStopRequested)
            run()
    }

    companion object {
        lateinit var instance: FordBot

        lateinit var drive: FordDrive
        lateinit var extension: FordExtension
        lateinit var flip: FordFlip
        lateinit var intake: FordIntake
    }

    abstract fun run()
}