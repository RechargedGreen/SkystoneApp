package org.firstinspires.ftc.teamcode.roverBot.hardware

import org.firstinspires.ftc.teamcode.lib.*

object RoverBot : BaseBot {
    lateinit var drive: RoverDrive
    lateinit var lift: RoverLift
    lateinit var autoFeed: RoverAutoFeed
    lateinit var dumper: RoverDumper
    lateinit var extension: RoverExtension
    lateinit var intake: RoverIntake
    lateinit var flip: RoverIntakeFlip
    lateinit var intakeLoadingSensors: RoverIntakeSensors

    override fun setup() {
        drive = RoverDrive()
        lift = RoverLift()
        autoFeed = RoverAutoFeed()
        dumper = RoverDumper()
        extension = RoverExtension()
        intake = RoverIntake()
        intakeLoadingSensors = RoverIntakeSensors()
    }

    override fun update() {
        RoverMovement.updateGyro()
        drive.applyMovement()

        dumper.update()
        intakeLoadingSensors.update()

        autoFeed.update()
    }

    override val teleopName: String = ""
}