package org.firstinspires.ftc.teamcode.fordBot

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

abstract class FordBot : LinearOpMode() {
    override fun runOpMode() {
        instance = this

        waitForStart()
        if (!isStopRequested)
            run()
    }

    companion object {
        lateinit var instance: LinearOpMode

        lateinit var drive: FordDrive
        lateinit var extension: FordExtension
        lateinit var flip: FordFlip
        lateinit var intake: FordIntake
    }

    abstract fun run()
}