package org.firstinspires.ftc.teamcode.roverBot.hardware

import org.firstinspires.ftc.teamcode.lib.BaseBot

object RoverBot : BaseBot {
    lateinit var drive: RoverDrive
    lateinit var lift: RoverLift
    lateinit var autoFeed: RoverAutoFeed
    lateinit var dumper: RoverDumper
    lateinit var extension: RoverExtension
    lateinit var intake: RoverIntake
    lateinit var flip: RoverIntakeFlip

    override fun setup() {
        drive = RoverDrive()
        lift = RoverLift()
        autoFeed = RoverAutoFeed()
        dumper = RoverDumper()
        extension = RoverExtension()
        intake = RoverIntake()
    }

    override fun update() {
        RoverMovement.updateGyro()
        drive.applyMovement()
    }

    override val teleopName: String = ""
}