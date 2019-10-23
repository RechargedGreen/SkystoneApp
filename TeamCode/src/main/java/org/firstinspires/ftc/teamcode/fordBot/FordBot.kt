package org.firstinspires.ftc.teamcode.fordBot

import com.qualcomm.robotcore.eventloop.opmode.*

abstract class FordBot : LinearOpMode() {
    final override fun runOpMode() {
        instance = this

        drive = FordDrive()
        extension = FordExtension()
        flip = FordFlip()
        intake = FordIntake()

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