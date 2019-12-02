package org.firstinspires.ftc.teamcode.leaguebot.misc

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.hardware.*
import org.firstinspires.ftc.teamcode.opmodeLib.*

abstract class LeagueModeBase(isAutonomous: Boolean, alliance: Alliance? = null, positionMirror: Pose? = null) : BaseMode(Robot, isAutonomous, alliance, positionMirror)
abstract class LeagueBotAutoBase(alliance: Alliance, positionMirror: Pose) : LeagueModeBase(true, alliance, positionMirror)
abstract class LeagueBotTeleOpBase : LeagueModeBase(false)