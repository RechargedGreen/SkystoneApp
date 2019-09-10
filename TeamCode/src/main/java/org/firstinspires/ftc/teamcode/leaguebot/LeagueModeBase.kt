package org.firstinspires.ftc.teamcode.leaguebot

import org.firstinspires.ftc.teamcode.leaguebot.opmode.hardware.*
import org.firstinspires.ftc.teamcode.lib.*

abstract class LeagueModeBase(isAutonomous: Boolean, alliance: Alliance? = null) : BaseMode(LeagueBot, isAutonomous, alliance)
abstract class LeagueBotAutoBase(alliance: Alliance) : LeagueModeBase(true, alliance)
abstract class LeagueBotTeleOpBase : LeagueModeBase(false)