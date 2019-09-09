package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.leaguebot.*
import org.firstinspires.ftc.teamcode.lib.*

abstract class LeagueCycleAuto(alliance: Alliance) : LeagueBotAutoBase(alliance) {
    enum class progStates {
    }

    override fun onMainLoop() {
        val currentStage = progStates.values()[stage]
        telemetry.addData("stage", currentStage)
    }
}

@Autonomous
class RedLeagueCycle : LeagueCycleAuto(Alliance.RED)

@Autonomous
class BlueLeagueCycle : LeagueCycleAuto(Alliance.BLUE)