package org.firstinspires.ftc.teamcode.leaguebot.opmode

import org.firstinspires.ftc.teamcode.leaguebot.*
import org.firstinspires.ftc.teamcode.lib.*

class StateMachineTest : LeagueBotAutoBase(Alliance.RED) {
    enum class progStates {
        a,
        b,
        c,
        d
    }

    var changes = 0
    override fun onMainLoop() {
        if (changedStage)
            changes++

        if (driver.y.justPressed)
            if (stage < progStates.values().size - 1)
                nextStage()
            else
                nextStage(0)

        telemetry.addData("s", progStates.values()[stage])

    }
}