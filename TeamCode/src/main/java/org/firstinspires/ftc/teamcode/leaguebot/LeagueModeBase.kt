package org.firstinspires.ftc.teamcode.leaguebot

import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.*
import org.firstinspires.ftc.teamcode.lib.*

abstract class LeagueModeBase(isAutonomous: Boolean, alliance: Alliance? = null, positionMirror: Pose? = null) : BaseMode(LeagueBot, isAutonomous, alliance, positionMirror)
abstract class LeagueBotAutoBase(alliance: Alliance, positionMirror: Pose) : LeagueModeBase(true, alliance, positionMirror)
abstract class LeagueBotTeleOpBase : LeagueModeBase(false)