package org.firstinspires.ftc.teamcode.leaguebot

import org.firstinspires.ftc.teamcode.lib.BaseMode

abstract class LeagueModeBase(isAutonomous: Boolean) : BaseMode(LeagueBot, isAutonomous)
abstract class LeagueBotAutoBase : LeagueModeBase(true)
abstract class LeagueBotTeleOpBase : LeagueModeBase(false)