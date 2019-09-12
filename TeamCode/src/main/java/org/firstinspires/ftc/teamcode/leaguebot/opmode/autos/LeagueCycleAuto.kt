package org.firstinspires.ftc.teamcode.leaguebot.opmode.autos

import com.qualcomm.robotcore.eventloop.opmode.*
import org.firstinspires.ftc.teamcode.field.*
import org.firstinspires.ftc.teamcode.leaguebot.*
import org.firstinspires.ftc.teamcode.lib.*

abstract class LeagueCycleAuto(alliance: Alliance) : LeagueBotAutoBase(alliance, Pose(0.0, 0.0, Math.toRadians(-90.0))) {
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