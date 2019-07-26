package org.firstinspires.ftc.teamcode.roverBot.opMode

import org.firstinspires.ftc.teamcode.lib.BaseMode
import org.firstinspires.ftc.teamcode.roverBot.hardware.RoverBot

abstract class RoverModeBase(isAutonomous: Boolean) : BaseMode(RoverBot, isAutonomous)
abstract class RoverAutoBase : RoverModeBase(true)
abstract class RoverTeleOpBase : RoverModeBase(false)
